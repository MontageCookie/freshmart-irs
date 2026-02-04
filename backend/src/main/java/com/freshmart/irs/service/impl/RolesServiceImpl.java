package com.freshmart.irs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.dto.role.RoleResponse;
import com.freshmart.irs.entity.RoleEntity;
import com.freshmart.irs.mapper.RoleMapper;
import com.freshmart.irs.service.RolesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务：提供角色查询与关键角色定位能力。
 */
@Service
public class RolesServiceImpl implements RolesService {
    private final RoleMapper roleMapper;

    public RolesServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public List<RoleResponse> listAllEnabled() {
        List<RoleEntity> roles = roleMapper.selectList(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getStatus, 1)
                .orderByAsc(RoleEntity::getId));
        return roles.stream()
                .map(r -> new RoleResponse(r.getId(), r.getCode(), r.getName(), r.getStatus() != null && r.getStatus() == 1))
                .toList();
    }

    @Override
    public long getCustomerRoleIdOrThrow() {
        RoleEntity role = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getCode, "CUSTOMER")
                .eq(RoleEntity::getStatus, 1));
        if (role == null) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "CUSTOMER 角色未初始化");
        }
        return role.getId();
    }
}
