package com.freshmart.irs.dto.replenishment;

import com.freshmart.irs.enums.ReplenishmentApprovalStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record ReplenishmentApproveResponse(
        @Schema(description = "建议单 ID") long id,
        @Schema(description = "审批状态") ReplenishmentApprovalStatus approvalStatus,
        @Schema(description = "审批人 userId") long approvedBy,
        @Schema(description = "审批时间（ISO-8601）") Instant approvedAt
) {
}
