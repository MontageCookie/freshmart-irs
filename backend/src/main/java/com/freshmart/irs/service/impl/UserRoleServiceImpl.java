package com.freshmart.irs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.entity.RoleEntity;
import com.freshmart.irs.entity.UserRoleEntity;
import com.freshmart.irs.mapper.RoleMapper;
import com.freshmart.irs.mapper.UserRoleMapper;
import com.freshmart.irs.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户-角色服务：查询用户角色、覆盖式分配用户角色。
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public UserRoleServiceImpl(UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<String> getRoleCodesByUserId(long userId) {
        List<UserRoleEntity> bindings = userRoleMapper.selectList(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, userId));
        if (bindings.isEmpty()) {
            return List.of();
        }
        List<Long> roleIds = bindings.stream()
                .map(UserRoleEntity::getRoleId)
                .distinct()
                .toList();
        List<RoleEntity> roles = roleMapper.selectBatchIds(roleIds);
        return roles.stream().map(RoleEntity::getCode).toList();
    }

    @Override
    @Transactional
    public void replaceUserRoles(long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "角色不能为空");
        }

        List<RoleEntity> roles = roleMapper.selectBatchIds(roleIds);
        if (roles.size() != roleIds.stream().distinct().count()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "存在无效的角色ID");
        }
        for (RoleEntity role : roles) {
            if (role.getStatus() == null || role.getStatus() != 1) {
                throw new BizException(ErrorCode.PARAM_INVALID, "包含已禁用的角色");
            }
        }

        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleEntity>().eq(UserRoleEntity::getUserId, userId));

        Map<Long, RoleEntity> unique = new HashMap<>();
        for (RoleEntity role : roles) {
            unique.put(role.getId(), role);
        }
        List<UserRoleEntity> inserts = new ArrayList<>(unique.size());
        for (Long roleId : unique.keySet()) {
            UserRoleEntity e = new UserRoleEntity();
            e.setUserId(userId);
            e.setRoleId(roleId);
            inserts.add(e);
        }
        for (UserRoleEntity e : inserts) {
            userRoleMapper.insert(e);
        }
    }

    @Override
    public Map<Long, List<String>> getRoleCodesByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        List<UserRoleEntity> bindings = userRoleMapper.selectList(new LambdaQueryWrapper<UserRoleEntity>()
                .in(UserRoleEntity::getUserId, userIds));
        if (bindings.isEmpty()) {
            return Map.of();
        }

        Set<Long> roleIds = bindings.stream().map(UserRoleEntity::getRoleId).collect(Collectors.toSet());
        List<RoleEntity> roles = roleMapper.selectBatchIds(roleIds);
        Map<Long, String> roleIdToCode = roles.stream().collect(Collectors.toMap(RoleEntity::getId, RoleEntity::getCode, (a, b) -> a));

        Map<Long, List<String>> result = new HashMap<>();
        for (UserRoleEntity binding : bindings) {
            String code = roleIdToCode.get(binding.getRoleId());
            if (code == null) {
                continue;
            }
            result.computeIfAbsent(binding.getUserId(), k -> new ArrayList<>()).add(code);
        }
        return result;
    }
}
