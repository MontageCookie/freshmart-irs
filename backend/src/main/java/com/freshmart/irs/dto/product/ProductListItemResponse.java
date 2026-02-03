package com.freshmart.irs.dto.product;

import com.freshmart.irs.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ProductListItemResponse(
        @Schema(description = "商品 ID", example = "1") long id,
        @Schema(description = "SKU", example = "APPLE-001") String sku,
        @Schema(description = "商品名称", example = "苹果") String name,
        @Schema(description = "计量单位", example = "kg") String unit,
        @Schema(description = "单价（销售价）", example = "12.50") BigDecimal price,
        @Schema(description = "状态", allowableValues = {"ON_SALE", "OFF_SALE"}) ProductStatus status
) {
}

