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
import com.freshmart.irs.service.UsersService;
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

import java.util.List;

@Tag(name = "Users & Roles")
@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @Operation(summary = "用户列表（分页）", description = "鉴权：JWT；角色：ADMIN；支持 keyword/status 过滤与 sort 排序")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<PageResponse<UserListItemResponse>> list(
            @Parameter(description = "关键字（用户名/手机号模糊）") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) UserStatus status,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=field,asc|desc，可重复传参") @RequestParam(required = false) List<String> sort
    ) {
        return ApiResponse.ok(usersService.list(keyword, status, page, size, sort));
    }

    @Operation(summary = "创建用户", description = "鉴权：JWT；角色：ADMIN；phone 需中国大陆 11 位；email 需满足邮箱格式；创建后覆盖式分配角色")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping
    public ApiResponse<IdResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.ok(new IdResponse(usersService.create(request)));
    }

    @Operation(summary = "用户详情", description = "鉴权：JWT；角色：ADMIN；返回用户基本信息与角色编码列表")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/{id}")
    public ApiResponse<UserDetailResponse> get(@PathVariable long id) {
        return ApiResponse.ok(usersService.get(id));
    }

    @Operation(summary = "更新用户（PATCH）", description = "鉴权：JWT；管理员可更新任意用户；管理员更新他人用户时不要求 currentPassword；其余场景（包含管理员自助/普通用户自助）如有任何更新均需提供 currentPassword；phone/email 会做格式校验")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PatchMapping("/{id}")
    public ApiResponse<IdResponse> update(@PathVariable long id, @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.ok(new IdResponse(usersService.update(id, request)));
    }

    @Operation(summary = "禁用用户（DELETE 语义固定：禁用）", description = "鉴权：JWT；角色：ADMIN；将 status 置为 DISABLED")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @DeleteMapping("/{id}")
    public ApiResponse<IdResponse> disable(@PathVariable long id) {
        return ApiResponse.ok(new IdResponse(usersService.disable(id)));
    }

    @Operation(summary = "为用户分配角色（覆盖式）", description = "鉴权：JWT；角色：ADMIN；覆盖式更新 user_role 绑定")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PutMapping("/{id}/roles")
    public ApiResponse<IdResponse> updateRoles(@PathVariable long id, @Valid @RequestBody UserRolesUpdateRequest request) {
        return ApiResponse.ok(new IdResponse(usersService.updateRoles(id, request)));
    }
}
