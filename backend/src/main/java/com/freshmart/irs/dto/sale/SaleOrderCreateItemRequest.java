package com.freshmart.irs.dto.sale;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaleOrderCreateItemRequest(
        @Schema(description = "商品 ID") @NotNull Long productId,
        @Schema(description = "库存批次 ID（可选：指定从哪个批次扣减）") Long inventoryBatchId,
        @Schema(description = "数量") @NotNull @Min(1) Integer qty,
        @Schema(description = "单价") @NotNull BigDecimal unitPrice
) {
}
