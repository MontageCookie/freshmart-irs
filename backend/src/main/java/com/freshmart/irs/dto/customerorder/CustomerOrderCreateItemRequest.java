package com.freshmart.irs.dto.customerorder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CustomerOrderCreateItemRequest(
        @Schema(description = "商品 ID") @NotNull Long productId,
        @Schema(description = "数量") @NotNull @Min(1) Integer qty
) {
}
