package com.freshmart.irs.dto.replenishment;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReplenishmentItemResponse(
        @Schema(description = "商品 ID") long productId,
        @Schema(description = "预测销量") int predictedQty,
        @Schema(description = "补货周期天数") int cycleDays,
        @Schema(description = "安全库存数量") int safetyStockQty,
        @Schema(description = "当前库存数量") int currentStockQty,
        @Schema(description = "建议采购数量") int suggestedQty
) {
}
