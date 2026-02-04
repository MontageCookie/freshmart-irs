package com.freshmart.irs.dto.user;

import com.freshmart.irs.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
                @Schema(description = "用户名（唯一）") String username,
                @Schema(description = "手机号") String phone,
                @Schema(description = "邮箱") @Email String email,
                @Schema(description = "当前密码（用户自助更新时必填）") String currentPassword,
                @Schema(description = "新密码（管理员重置/用户自助修改时填写）") String newPassword,
                @Schema(description = "状态", allowableValues = {
                                "ENABLED", "DISABLED" }) UserStatus status) {
}
