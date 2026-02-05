package com.freshmart.irs.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshmart.irs.auth.AuthContext;
import com.freshmart.irs.auth.AuthContextHolder;
import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.EmailAddressValidator;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.common.PhoneNumberValidator;
import com.freshmart.irs.dto.user.UserCreateRequest;
import com.freshmart.irs.dto.user.UserDetailResponse;
import com.freshmart.irs.dto.user.UserListItemResponse;
import com.freshmart.irs.dto.user.UserRolesUpdateRequest;
import com.freshmart.irs.dto.user.UserUpdateRequest;
import com.freshmart.irs.entity.UserEntity;
import com.freshmart.irs.enums.UserStatus;
import com.freshmart.irs.mapper.UserMapper;
import com.freshmart.irs.service.AuditService;
import com.freshmart.irs.service.PasswordPolicy;
import com.freshmart.irs.service.UserRoleService;
import com.freshmart.irs.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务：提供用户管理（列表/创建/详情/禁用/分配角色）与用户自助更新能力，并记录审计事件。
 */
@Service
public class UsersServiceImpl implements UsersService {
    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);

    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordPolicy passwordPolicy;
    private final AuditService auditService;

    public UsersServiceImpl(
            UserMapper userMapper,
            UserRoleService userRoleService,
            PasswordPolicy passwordPolicy,
            AuditService auditService
    ) {
        this.userMapper = userMapper;
        this.userRoleService = userRoleService;
        this.passwordPolicy = passwordPolicy;
        this.auditService = auditService;
    }

    @Override
    public PageResponse<UserListItemResponse> list(String keyword, UserStatus status, int page, int size, List<String> sort) {
        requireAdmin();

        if (page < 1) {
            throw new BizException(ErrorCode.PARAM_INVALID, "page 必须从 1 开始");
        }
        if (size < 1 || size > 100) {
            throw new BizException(ErrorCode.PARAM_INVALID, "size 必须在 1~100");
        }

        LambdaQueryWrapper<UserEntity> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.and(w -> w.like(UserEntity::getUsername, keyword).or().like(UserEntity::getPhone, keyword));
        }
        if (status != null) {
            qw.eq(UserEntity::getStatus, status == UserStatus.ENABLED ? 1 : 0);
        }
        applySort(qw, sort);

        Page<UserEntity> p = userMapper.selectPage(new Page<>(page, size), qw);
        List<UserEntity> records = p.getRecords();

        List<UserListItemResponse> items = new ArrayList<>(records.size());
        for (UserEntity u : records) {
            items.add(new UserListItemResponse(
                    u.getId(),
                    u.getUsername(),
                    u.getPhone(),
                    u.getEmail(),
                    u.getStatus() != null && u.getStatus() == 1 ? UserStatus.ENABLED : UserStatus.DISABLED,
                    u.getCreatedAt() == null ? null : u.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
            ));
        }

        return new PageResponse<>(page, size, p.getTotal(), items);
    }

    @Override
    @Transactional
    public long create(UserCreateRequest request) {
        requireAdmin();
        try {
            passwordPolicy.validateOrThrow(request.password());

            Long exists = userMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getUsername, request.username()));
            if (exists != null && exists > 0) {
                throw new BizException(ErrorCode.PARAM_INVALID, "用户名已存在");
            }

            if (request.roleIds() == null || request.roleIds().isEmpty()) {
                throw new BizException(ErrorCode.PARAM_INVALID, "角色不能为空");
            }

            String normalizedPhone = PhoneNumberValidator.normalizeOrNull(request.phone());
            PhoneNumberValidator.validateChinaMainland11OrThrow(normalizedPhone);
            String normalizedEmail = EmailAddressValidator.normalizeOrNull(request.email());
            EmailAddressValidator.validateOrThrow(normalizedEmail);

            UserEntity user = new UserEntity();
            user.setUsername(request.username());
            user.setPasswordHash(BCrypt.withDefaults().hashToString(12, request.password().toCharArray()));
            user.setPhone(normalizedPhone);
            user.setEmail(normalizedEmail);
            user.setStatus(1);
            userMapper.insert(user);

            userRoleService.replaceUserRoles(user.getId(), request.roleIds());
            auditService.record("USER_CREATE", "USER", user.getId(), true, null);
            return user.getId();
        } catch (BizException ex) {
            auditService.record("USER_CREATE", "USER", null, false, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            auditService.record("USER_CREATE", "USER", null, false, "INTERNAL_ERROR");
            log.debug("create user failed", ex);
            throw ex;
        }
    }

    @Override
    public UserDetailResponse get(long id) {
        requireAdmin();
        UserEntity user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        List<String> roles = userRoleService.getRoleCodesByUserId(id);
        return new UserDetailResponse(
                user.getId(),
                user.getUsername(),
                user.getPhone(),
                user.getEmail(),
                user.getStatus() != null && user.getStatus() == 1 ? UserStatus.ENABLED : UserStatus.DISABLED,
                roles
        );
    }

    @Override
    @Transactional
    public long update(long id, UserUpdateRequest request) {
        AuthContext ctx = AuthContextHolder.getRequired();
        boolean isAdmin = ctx.roles().contains("ADMIN");
        boolean isSelf = ctx.userId() == id;
        if (!isAdmin && !isSelf) {
            throw new BizException(ErrorCode.FORBIDDEN, "只能修改自己的用户信息");
        }

        try {
            UserEntity user = userMapper.selectById(id);
            if (user == null) {
                throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
            }

            if (!isAdmin && request.status() != null) {
                throw new BizException(ErrorCode.FORBIDDEN, "无权限修改用户状态");
            }

            boolean isAdminUpdatingOther = isAdmin && !isSelf;
            boolean hasAnyChange =
                    (request.username() != null && !request.username().isBlank()) ||
                            (request.phone() != null && !request.phone().isBlank()) ||
                            (request.email() != null && !request.email().isBlank()) ||
                            request.status() != null ||
                            (request.newPassword() != null && !request.newPassword().isBlank());

            if (!isAdminUpdatingOther && hasAnyChange) {
                if (request.currentPassword() == null || request.currentPassword().isBlank()) {
                    throw new BizException(ErrorCode.PARAM_INVALID, "currentPassword 不能为空");
                }
                boolean verified = BCrypt.verifyer().verify(request.currentPassword().toCharArray(), user.getPasswordHash()).verified;
                if (!verified) {
                    throw new BizException(ErrorCode.PARAM_INVALID, "当前密码错误");
                }
            }

            if (request.username() != null && !request.username().isBlank()) {
                if (!request.username().equals(user.getUsername())) {
                    Long exists = userMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                            .eq(UserEntity::getUsername, request.username()));
                    if (exists != null && exists > 0) {
                        throw new BizException(ErrorCode.PARAM_INVALID, "用户名已存在");
                    }
                    user.setUsername(request.username());
                }
            }
            if (request.phone() != null && !request.phone().isBlank()) {
                String normalizedPhone = PhoneNumberValidator.normalizeOrNull(request.phone());
                PhoneNumberValidator.validateChinaMainland11OrThrow(normalizedPhone);
                user.setPhone(normalizedPhone);
            }
            if (request.email() != null && !request.email().isBlank()) {
                String normalizedEmail = EmailAddressValidator.normalizeOrNull(request.email());
                EmailAddressValidator.validateOrThrow(normalizedEmail);
                user.setEmail(normalizedEmail);
            }

            if (request.status() != null) {
                user.setStatus(request.status() == UserStatus.ENABLED ? 1 : 0);
            }

            if (request.newPassword() != null && !request.newPassword().isBlank()) {
                passwordPolicy.validateOrThrow(request.newPassword());
                user.setPasswordHash(BCrypt.withDefaults().hashToString(12, request.newPassword().toCharArray()));
            }

            userMapper.updateById(user);
            auditService.record("USER_UPDATE", "USER", id, true, null);
            return id;
        } catch (BizException ex) {
            auditService.record("USER_UPDATE", "USER", id, false, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            auditService.record("USER_UPDATE", "USER", id, false, "INTERNAL_ERROR");
            log.debug("update user failed", ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public long disable(long id) {
        requireAdmin();
        try {
            UserEntity user = userMapper.selectById(id);
            if (user == null) {
                throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
            }
            user.setStatus(0);
            userMapper.updateById(user);
            auditService.record("USER_DISABLE", "USER", id, true, null);
            return id;
        } catch (BizException ex) {
            auditService.record("USER_DISABLE", "USER", id, false, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            auditService.record("USER_DISABLE", "USER", id, false, "INTERNAL_ERROR");
            log.debug("disable user failed", ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public long updateRoles(long id, UserRolesUpdateRequest request) {
        requireAdmin();
        try {
            UserEntity user = userMapper.selectById(id);
            if (user == null) {
                throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
            }
            userRoleService.replaceUserRoles(id, request.roleIds());
            auditService.record("ROLE_ASSIGN", "USER", id, true, null);
            return id;
        } catch (BizException ex) {
            auditService.record("ROLE_ASSIGN", "USER", id, false, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            auditService.record("ROLE_ASSIGN", "USER", id, false, "INTERNAL_ERROR");
            log.debug("update user roles failed", ex);
            throw ex;
        }
    }

    private void requireAdmin() {
        AuthContext ctx = AuthContextHolder.getRequired();
        if (!ctx.roles().contains("ADMIN")) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限");
        }
    }

    private void applySort(LambdaQueryWrapper<UserEntity> qw, List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            qw.orderByDesc(UserEntity::getId);
            return;
        }

        String field = sort.get(0).trim();
        boolean asc = true;

        if (sort.size() >= 2) {
            asc = !"desc".equalsIgnoreCase(sort.get(1).trim());
        } else if (field.contains(",")) {
            String[] parts = field.split(",");
            field = parts[0].trim();
            asc = parts.length < 2 || !"desc".equalsIgnoreCase(parts[1].trim());
        }

        field = field.replace("[", "").replace("\"", "");

        switch (field) {
            case "username" -> qw.orderBy(true, asc, UserEntity::getUsername);
            case "createdAt" -> qw.orderBy(true, asc, UserEntity::getCreatedAt);
            case "updatedAt" -> qw.orderBy(true, asc, UserEntity::getUpdatedAt);
            default -> qw.orderBy(true, asc, UserEntity::getId);
        }
    }
}
