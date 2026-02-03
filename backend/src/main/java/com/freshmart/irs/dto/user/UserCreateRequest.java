package com.freshmart.irs.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserCreateRequest(
        @Schema(description = "用户名（唯一）", example = "manager") @NotBlank String username,
        @Schema(description = "初始密码（明文，仅占位）", example = "123456") @NotBlank String password,
        @Schema(description = "手机号", example = "13800000002") String phone,
        @Schema(description = "邮箱") @Email String email,
        @Schema(description = "角色 ID 列表（可选）") List<Long> roleIds
) {
}

