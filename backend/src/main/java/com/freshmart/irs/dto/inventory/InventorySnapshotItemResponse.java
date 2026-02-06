package com.freshmart.irs.dto.inventory;

import com.freshmart.irs.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存快照（商品维度）响应。
 */
public record InventorySnapshotItemResponse(
        @Schema(description = "商品 ID", example = "1") long productId,
        @Schema(description = "SKU", example = "APPLE-001") String sku,
        @Schema(description = "商品名称", example = "苹果") String name,
        @Schema(description = "计量单位", example = "kg") String unit,
        @Schema(description = "销售价", example = "12.50") BigDecimal price,
        @Schema(description = "商品状态", allowableValues = {"ON_SALE", "OFF_SALE"}) ProductStatus status,
        @Schema(description = "安全库存阈值", example = "20") int safetyStockQty,
        @Schema(description = "可售数量（AVAILABLE 且未过期）", example = "80") int availableQty,
        @Schema(description = "当前数量（批次可用量汇总）", example = "100") int totalQty,
        @Schema(description = "批次明细（仅在指定 productId 时返回）") List<InventoryBatchSnapshotResponse> batches
) {
}

