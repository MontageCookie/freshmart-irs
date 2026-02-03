package com.freshmart.irs.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;

public record RoleResponse(
        @Schema(description = "角色 ID", example = "1") long id,
        @Schema(description = "角色编码", example = "ADMIN") String code,
        @Schema(description = "角色名称", example = "系统管理员") String name,
        @Schema(description = "是否启用", example = "true") boolean enabled
) {
}

