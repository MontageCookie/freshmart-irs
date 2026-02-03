package com.freshmart.irs.dto.replenishment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ReplenishmentRejectRequest(
        @Schema(description = "驳回原因") @NotBlank String rejectReason
) {
}
