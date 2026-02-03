package com.freshmart.irs.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartItemResponse(
        @Schema(description = "商品 ID") long productId,
        @Schema(description = "数量") int qty
) {
}
