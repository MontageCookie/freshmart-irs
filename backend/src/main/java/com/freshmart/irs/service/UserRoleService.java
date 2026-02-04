package com.freshmart.irs.service;

import java.util.List;
import java.util.Map;

/**
 * 用户-角色服务：查询用户角色、覆盖式分配用户角色。
 */
public interface UserRoleService {
    /**
     * 查询用户的角色编码列表。
     *
     * @param userId 用户 ID
     * @return 角色编码列表
     */
    List<String> getRoleCodesByUserId(long userId);

    /**
     * 覆盖式设置用户角色：先删后插，且要求角色均为启用状态。
     *
     * @param userId  用户 ID
     * @param roleIds 角色 ID 列表
     */
    void replaceUserRoles(long userId, List<Long> roleIds);

    /**
     * 批量查询多个用户的角色编码列表。
     *
     * @param userIds 用户 ID 列表
     * @return key=userId, value=角色编码列表
     */
    Map<Long, List<String>> getRoleCodesByUserIds(List<Long> userIds);
}
