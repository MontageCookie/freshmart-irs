package com.freshmart.irs.dto.customerorder;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record CustomerOrderItemResponse(
        @Schema(description = "商品 ID") long productId,
        @Schema(description = "库存批次 ID（可选）") Long inventoryBatchId,
        @Schema(description = "数量") int qty,
        @Schema(description = "单价") BigDecimal unitPrice,
        @Schema(description = "行金额") BigDecimal lineAmount
) {
}
