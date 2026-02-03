package com.freshmart.irs.dto.replenishment;

import com.freshmart.irs.enums.ReplenishmentApprovalStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record ReplenishmentGenerateResponse(
        @Schema(description = "建议单 ID") long id,
        @Schema(description = "建议单号") String bizNo,
        @Schema(description = "审批状态") ReplenishmentApprovalStatus approvalStatus,
        @Schema(description = "生成时间（ISO-8601）") Instant generatedAt
) {
}
