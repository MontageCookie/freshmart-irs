package com.freshmart.irs.service;

import com.freshmart.irs.dto.role.RoleResponse;

import java.util.List;

/**
 * 角色服务：提供角色查询与关键角色定位能力。
 */
public interface RolesService {
    /**
     * 获取所有启用角色列表。
     *
     * @return 角色列表
     */
    List<RoleResponse> listAllEnabled();

    /**
     * 获取 CUSTOMER 角色 ID，不存在则抛出异常。
     *
     * @return CUSTOMER 角色 ID
     */
    long getCustomerRoleIdOrThrow();
}
