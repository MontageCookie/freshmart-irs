package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.role.RoleResponse;
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

    @Operation(summary = "角色列表（占位）", description = "鉴权：JWT；角色：ADMIN")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<List<RoleResponse>> list() {
        List<RoleResponse> data = List.of(
                new RoleResponse(1, "ADMIN", "系统管理员", true),
                new RoleResponse(2, "STORE_MANAGER", "店长", true),
                new RoleResponse(3, "WAREHOUSE", "库管", true),
                new RoleResponse(4, "CASHIER", "收银", true),
                new RoleResponse(5, "CUSTOMER", "顾客", true)
        );
        return ApiResponse.ok(data);
    }
}
