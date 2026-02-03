package com.freshmart.irs.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartItemSetResponse(
        @Schema(description = "购物车 ID") long cartId,
        @Schema(description = "商品 ID") long productId,
        @Schema(description = "数量") int qty
) {
}
