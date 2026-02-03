package com.freshmart.irs.dto.sale;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record SaleOrderItemResponse(
        @Schema(description = "明细 ID") long id,
        @Schema(description = "商品 ID") long productId,
        @Schema(description = "库存批次 ID") Long inventoryBatchId,
        @Schema(description = "数量") int qty,
        @Schema(description = "单价") BigDecimal unitPrice,
        @Schema(description = "行金额") BigDecimal lineAmount
) {
}
