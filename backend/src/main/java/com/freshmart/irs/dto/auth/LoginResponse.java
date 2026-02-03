package com.freshmart.irs.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record LoginResponse(
        @Schema(description = "JWT Token（占位）", example = "mock.jwt.token") String token,
        @Schema(description = "Token 类型", example = "Bearer") String tokenType,
        @Schema(description = "过期秒数（占位）", example = "3600") long expiresIn,
        @Schema(description = "用户 ID", example = "1") long userId,
        @Schema(description = "用户名", example = "admin") String username,
        @Schema(description = "角色编码列表", example = "[\"ADMIN\"]") List<String> roles
) {
}

