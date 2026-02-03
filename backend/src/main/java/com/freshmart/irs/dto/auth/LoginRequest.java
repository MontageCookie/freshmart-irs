package com.freshmart.irs.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "用户名", example = "admin") @NotBlank String username,
        @Schema(description = "密码（明文，仅用于示例）", example = "123456") @NotBlank String password
) {
}

