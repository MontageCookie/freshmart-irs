package com.freshmart.irs.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UserRolesUpdateRequest(
        @Schema(description = "角色 ID 列表（覆盖式）") @NotEmpty List<Long> roleIds
) {
}

