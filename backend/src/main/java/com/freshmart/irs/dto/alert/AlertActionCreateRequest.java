package com.freshmart.irs.dto.alert;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AlertActionCreateRequest(
        @Schema(description = "动作类型：ACK/DISPOSE/PROMOTE/RETURN/OTHER", example = "ACK") @NotBlank String actionType,
        @Schema(description = "处置结果/说明") String actionResult
) {
}
