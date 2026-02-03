package com.freshmart.irs.dto.user;

import com.freshmart.irs.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
        @Schema(description = "手机号") String phone,
        @Schema(description = "邮箱") @Email String email,
        @Schema(description = "状态", allowableValues = {"ENABLED", "DISABLED"}) UserStatus status
) {
}

