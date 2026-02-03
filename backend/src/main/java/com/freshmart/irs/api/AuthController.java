package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.auth.LoginRequest;
import com.freshmart.irs.dto.auth.LoginResponse;
import com.freshmart.irs.dto.auth.MeResponse;
import com.freshmart.irs.enums.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Operation(summary = "登录并获取 JWT（占位）")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse data = new LoginResponse(
                "mock.jwt.token",
                "Bearer",
                3600,
                1,
                request.username(),
                List.of("ADMIN")
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "获取当前登录用户信息（占位）", description = "鉴权：JWT；角色：ADMIN/STORE_MANAGER/WAREHOUSE/CASHIER/CUSTOMER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/me")
    public ApiResponse<MeResponse> me() {
        MeResponse data = new MeResponse(
                1,
                "admin",
                "13800000001",
                "admin@freshmart.test",
                UserStatus.ENABLED,
                List.of("ADMIN")
        );
        return ApiResponse.ok(data);
    }
}

