package com.freshmart.irs.service;

import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.dto.user.UserCreateRequest;
import com.freshmart.irs.dto.user.UserDetailResponse;
import com.freshmart.irs.dto.user.UserListItemResponse;
import com.freshmart.irs.dto.user.UserRolesUpdateRequest;
import com.freshmart.irs.dto.user.UserUpdateRequest;
import com.freshmart.irs.enums.UserStatus;

import java.util.List;

/**
 * 用户服务：提供用户管理（列表/创建/详情/禁用/分配角色）与用户自助更新能力，并记录审计事件。
 */
public interface UsersService {
    PageResponse<UserListItemResponse> list(String keyword, UserStatus status, int page, int size, List<String> sort);

    long create(UserCreateRequest request);

    UserDetailResponse get(long id);

    long update(long id, UserUpdateRequest request);

    long disable(long id);

    long updateRoles(long id, UserRolesUpdateRequest request);
}
