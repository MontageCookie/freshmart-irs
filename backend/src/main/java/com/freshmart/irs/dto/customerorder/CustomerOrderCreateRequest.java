package com.freshmart.irs.dto.customerorder;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CustomerOrderCreateRequest(
        @Schema(description = "来源（固定 CART）", example = "CART") String source,
        @Schema(description = "下单明细（可选：为空表示直接取购物车）") List<CustomerOrderCreateItemRequest> items
) {
}
