package com.freshmart.irs.dto.product;

import com.freshmart.irs.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductUpsertRequest(
        @Schema(description = "SKU（唯一）", example = "APPLE-001") @NotBlank String sku,
        @Schema(description = "商品名称", example = "苹果") @NotBlank String name,
        @Schema(description = "计量单位", example = "kg") @NotBlank String unit,
        @Schema(description = "单价（销售价）", example = "12.50") @NotNull @DecimalMin("0.00") BigDecimal price,
        @Schema(description = "安全库存阈值", example = "20") Integer safetyStockQty,
        @Schema(description = "商品状态", allowableValues = {"ON_SALE", "OFF_SALE"}) ProductStatus status
) {
}

