package com.freshmart.irs.dto.stockin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockInOrderItemResponse(
        @Schema(description = "入库明细 ID", example = "1") long id,
        @Schema(description = "商品 ID", example = "1") long productId,
        @Schema(description = "库存批次 ID", example = "1") long inventoryBatchId,
        @Schema(description = "入库数量", example = "50") int qty,
        @Schema(description = "入库成本单价", example = "8.00") BigDecimal unitCost,
        @Schema(description = "效期日期", example = "2026-02-20") LocalDate expiryDate
) {
}

