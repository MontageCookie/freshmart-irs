package com.freshmart.irs.dto.user;

import com.freshmart.irs.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record UserDetailResponse(
        @Schema(description = "用户 ID", example = "1") long id,
        @Schema(description = "用户名", example = "admin") String username,
        @Schema(description = "手机号") String phone,
        @Schema(description = "邮箱") String email,
        @Schema(description = "状态", allowableValues = {"ENABLED", "DISABLED"}) UserStatus status,
        @Schema(description = "角色编码列表") List<String> roles
) {
}

