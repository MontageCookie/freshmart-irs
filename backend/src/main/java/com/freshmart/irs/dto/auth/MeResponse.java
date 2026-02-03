package com.freshmart.irs.dto.auth;

import com.freshmart.irs.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MeResponse(
        @Schema(description = "用户 ID", example = "1") long id,
        @Schema(description = "用户名", example = "admin") String username,
        @Schema(description = "手机号", example = "13800000001") String phone,
        @Schema(description = "邮箱", example = "admin@freshmart.test") String email,
        @Schema(description = "用户状态", allowableValues = {"ENABLED", "DISABLED"}) UserStatus status,
        @Schema(description = "角色编码列表") List<String> roles
) {
}

