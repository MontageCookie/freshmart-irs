package com.freshmart.irs.dto.replenishment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ReplenishmentGenerateRequest(
        @Schema(description = "补货周期天数") @NotNull @Min(1) Integer cycleDays,
        @Schema(description = "安全库存系数（可选，默认 1.0）") BigDecimal safetyStockFactor,
        @Schema(description = "商品 ID 列表（可选：为空表示全量）") List<Long> productIds
) {
}
