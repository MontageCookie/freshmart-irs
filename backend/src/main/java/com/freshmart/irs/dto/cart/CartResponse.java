package com.freshmart.irs.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CartResponse(
        @Schema(description = "购物车 ID") long id,
        @Schema(description = "状态") String status,
        @Schema(description = "购物车明细") List<CartItemResponse> items
) {
}
