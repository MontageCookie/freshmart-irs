package com.freshmart.irs.dto.alert;

import com.freshmart.irs.enums.AlertStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record AlertDetailResponse(
        @Schema(description = "预警 ID") long id,
        @Schema(description = "预警类型", example = "LOW_STOCK") String alertType,
        @Schema(description = "预警状态") AlertStatus alertStatus,
        @Schema(description = "商品 ID") Long productId,
        @Schema(description = "库存批次 ID") Long inventoryBatchId,
        @Schema(description = "阈值数量") Integer thresholdQty,
        @Schema(description = "效期日期（可空）") LocalDate expiryDate,
        @Schema(description = "触发时间（ISO-8601）") Instant triggeredAt,
        @Schema(description = "处置完成时间（ISO-8601，可空）") Instant resolvedAt,
        @Schema(description = "处置动作列表") List<AlertActionResponse> actions
) {
}
