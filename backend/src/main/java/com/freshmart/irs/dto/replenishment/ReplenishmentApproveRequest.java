package com.freshmart.irs.dto.replenishment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record ReplenishmentApproveRequest(
        @Schema(description = "审批时间（可选：默认当前时间）") Instant approvedAt
) {
}
