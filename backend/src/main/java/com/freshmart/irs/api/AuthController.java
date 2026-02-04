package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.common.IdResponse;
import com.freshmart.irs.dto.auth.LoginRequest;
import com.freshmart.irs.dto.auth.LoginResponse;
import com.freshmart.irs.dto.auth.MeResponse;
import com.freshmart.irs.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public record PermissionsResponse(List<String> roles) {
    }

    public record RegisterRequest(
            @NotBlank String username,
            @NotBlank String password,
            String phone,
            @Email String email
    ) {
    }

    @Operation(summary = "登录并获取 JWT", description = "用户名/密码登录，返回 Bearer Token；失败返回 401/403")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @Operation(summary = "顾客注册", description = "默认赋予 CUSTOMER 角色；phone 需中国大陆 11 位；email 需满足邮箱格式")
    @PostMapping("/register")
    public ApiResponse<IdResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(new IdResponse(authService.register(request.username(), request.password(), request.phone(), request.email())));
    }

    @Operation(summary = "登出", description = "鉴权：JWT；服务端不做 Token 黑名单，仅记录审计事件")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.ok(null);
    }

    @Operation(summary = "获取当前登录用户信息", description = "鉴权：JWT；返回 userId/username/roles，并校验用户状态是否启用")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/me")
    public ApiResponse<MeResponse> me() {
        return ApiResponse.ok(authService.me());
    }

    @Operation(summary = "获取当前登录用户角色列表", description = "鉴权：JWT；返回 token 内角色编码列表")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/permissions")
    public ApiResponse<PermissionsResponse> permissions() {
        return ApiResponse.ok(new PermissionsResponse(authService.permissions()));
    }
}
