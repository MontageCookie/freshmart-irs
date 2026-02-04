package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.role.RoleResponse;
import com.freshmart.irs.service.RolesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Users & Roles")
@RestController
@RequestMapping("/api/v1/roles")
public class RolesController {
    private final RolesService rolesService;

    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @Operation(summary = "角色列表", description = "鉴权：JWT；角色：ADMIN；返回所有启用角色")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<List<RoleResponse>> list() {
        return ApiResponse.ok(rolesService.listAllEnabled());
    }
}
