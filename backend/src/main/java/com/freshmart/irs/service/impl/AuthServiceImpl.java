package com.freshmart.irs.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.freshmart.irs.auth.AuthContext;
import com.freshmart.irs.auth.AuthContextHolder;
import com.freshmart.irs.auth.JwtService;
import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.EmailAddressValidator;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.common.PhoneNumberValidator;
import com.freshmart.irs.dto.auth.LoginRequest;
import com.freshmart.irs.dto.auth.LoginResponse;
import com.freshmart.irs.dto.auth.MeResponse;
import com.freshmart.irs.entity.UserEntity;
import com.freshmart.irs.enums.UserStatus;
import com.freshmart.irs.mapper.UserMapper;
import com.freshmart.irs.service.AuditService;
import com.freshmart.irs.service.AuthService;
import com.freshmart.irs.service.PasswordPolicy;
import com.freshmart.irs.service.RolesService;
import com.freshmart.irs.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务：处理登录、注册、登出与当前用户信息查询，并记录审计事件。
 */
@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final RolesService rolesService;
    private final JwtService jwtService;
    private final PasswordPolicy passwordPolicy;
    private final AuditService auditService;

    public AuthServiceImpl(
            UserMapper userMapper,
            UserRoleService userRoleService,
            RolesService rolesService,
            JwtService jwtService,
            PasswordPolicy passwordPolicy,
            AuditService auditService
    ) {
        this.userMapper = userMapper;
        this.userRoleService = userRoleService;
        this.rolesService = rolesService;
        this.jwtService = jwtService;
        this.passwordPolicy = passwordPolicy;
        this.auditService = auditService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getUsername, request.username()));
            if (user == null) {
                throw new BizException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
            }
            if (user.getStatus() == null || user.getStatus() != 1) {
                throw new BizException(ErrorCode.FORBIDDEN, "用户已被禁用");
            }
            boolean verified = BCrypt.verifyer().verify(request.password().toCharArray(), user.getPasswordHash()).verified;
            if (!verified) {
                throw new BizException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
            }

            List<String> roleCodes = userRoleService.getRoleCodesByUserId(user.getId());
            String token = jwtService.issueToken(user.getId(), user.getUsername(), roleCodes);
            auditService.record("AUTH_LOGIN", "USER", user.getId(), true, null);
            return new LoginResponse(token, "Bearer", jwtService.getExpiresSeconds(), user.getId(), user.getUsername(), roleCodes);
        } catch (BizException ex) {
            auditService.record("AUTH_LOGIN", "USER", null, false, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            auditService.record("AUTH_LOGIN", "USER", null, false, "INTERNAL_ERROR");
            log.debug("login failed", ex);
            throw ex;
        }
    }

    @Override
    public long register(String username, String password, String phone, String email) {
        try {
            passwordPolicy.validateOrThrow(password);

            Long exists = userMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getUsername, username));
            if (exists != null && exists > 0) {
                throw new BizException(ErrorCode.PARAM_INVALID, "用户名已存在");
            }

            String normalizedPhone = PhoneNumberValidator.normalizeOrNull(phone);
            PhoneNumberValidator.validateChinaMainland11OrThrow(normalizedPhone);
            String normalizedEmail = EmailAddressValidator.normalizeOrNull(email);
            EmailAddressValidator.validateOrThrow(normalizedEmail);

            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setPasswordHash(BCrypt.withDefaults().hashToString(12, password.toCharArray()));
            user.setPhone(normalizedPhone);
            user.setEmail(normalizedEmail);
            user.setStatus(1);
            userMapper.insert(user);

            long customerRoleId = rolesService.getCustomerRoleIdOrThrow();
            userRoleService.replaceUserRoles(user.getId(), List.of(customerRoleId));

            auditService.record("USER_REGISTER", "USER", user.getId(), true, null);
            return user.getId();
        } catch (BizException ex) {
            auditService.record("USER_REGISTER", "USER", null, false, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            auditService.record("USER_REGISTER", "USER", null, false, "INTERNAL_ERROR");
            log.debug("register failed", ex);
            throw ex;
        }
    }

    @Override
    public MeResponse me() {
        AuthContext ctx = AuthContextHolder.getRequired();
        UserEntity statusOnly = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .select("id", "status")
                .eq("id", ctx.userId()));
        if (statusOnly == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (statusOnly.getStatus() == null || statusOnly.getStatus() != 1) {
            throw new BizException(ErrorCode.FORBIDDEN, "用户已被禁用");
        }
        return new MeResponse(
                ctx.userId(),
                ctx.username(),
                null,
                null,
                UserStatus.ENABLED,
                ctx.roles()
        );
    }

    @Override
    public List<String> permissions() {
        AuthContext ctx = AuthContextHolder.getRequired();
        return ctx.roles();
    }

    @Override
    public void logout() {
        AuthContext ctx = AuthContextHolder.getRequired();
        auditService.record("AUTH_LOGOUT", "USER", ctx.userId(), true, null);
    }
}
