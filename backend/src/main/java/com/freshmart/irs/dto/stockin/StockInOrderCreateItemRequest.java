package com.freshmart.irs.dto.stockin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockInOrderCreateItemRequest(
        @Schema(description = "商品 ID", example = "1") @NotNull Long productId,
        @Schema(description = "入库数量", example = "50") @NotNull @Min(1) Integer qty,
        @Schema(description = "入库成本单价（可选业务视图）", example = "8.00") @NotNull @DecimalMin("0.00") BigDecimal unitCost,
        @Schema(description = "生产日期（可空）", example = "2026-02-01") LocalDate productionDate,
        @Schema(description = "效期日期", example = "2026-02-20") @NotNull LocalDate expiryDate
) {
}

