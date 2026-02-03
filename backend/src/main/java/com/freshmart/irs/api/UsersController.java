package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.common.IdResponse;
import com.freshmart.irs.dto.user.UserCreateRequest;
import com.freshmart.irs.dto.user.UserDetailResponse;
import com.freshmart.irs.dto.user.UserListItemResponse;
import com.freshmart.irs.dto.user.UserRolesUpdateRequest;
import com.freshmart.irs.dto.user.UserUpdateRequest;
import com.freshmart.irs.enums.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@Tag(name = "Users & Roles")
@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    @Operation(summary = "用户列表（分页，占位）", description = "鉴权：JWT；角色：ADMIN")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<PageResponse<UserListItemResponse>> list(
            @Parameter(description = "关键字（用户名/手机号模糊）") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) UserStatus status,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=field,asc|desc，可重复传参") @RequestParam(required = false) List<String> sort
    ) {
        PageResponse<UserListItemResponse> data = new PageResponse<>(
                page,
                size,
                1,
                List.of(new UserListItemResponse(1, "admin", "13800000001", "admin@freshmart.test", UserStatus.ENABLED, Instant.now()))
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "创建用户（占位）", description = "鉴权：JWT；角色：ADMIN")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping
    public ApiResponse<IdResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.ok(new IdResponse(1));
    }

    @Operation(summary = "用户详情（占位）", description = "鉴权：JWT；角色：ADMIN")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/{id}")
    public ApiResponse<UserDetailResponse> get(@PathVariable long id) {
        UserDetailResponse data = new UserDetailResponse(
                id,
                "admin",
                "13800000001",
                "admin@freshmart.test",
                UserStatus.ENABLED,
                List.of("ADMIN")
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "更新用户（PATCH，占位）", description = "鉴权：JWT；角色：ADMIN")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PatchMapping("/{id}")
    public ApiResponse<IdResponse> update(@PathVariable long id, @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.ok(new IdResponse(id));
    }

    @Operation(summary = "禁用用户（DELETE 语义固定：禁用，占位）", description = "鉴权：JWT；角色：ADMIN；DELETE 语义：禁用（status=DISABLED）")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @DeleteMapping("/{id}")
    public ApiResponse<IdResponse> disable(@PathVariable long id) {
        return ApiResponse.ok(new IdResponse(id));
    }

    @Operation(summary = "为用户分配角色（覆盖式，占位）", description = "鉴权：JWT；角色：ADMIN")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PutMapping("/{id}/roles")
    public ApiResponse<IdResponse> updateRoles(@PathVariable long id, @Valid @RequestBody UserRolesUpdateRequest request) {
        return ApiResponse.ok(new IdResponse(id));
    }
}
