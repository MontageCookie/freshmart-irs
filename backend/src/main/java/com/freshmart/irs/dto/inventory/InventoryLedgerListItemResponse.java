package com.freshmart.irs.dto.inventory;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record InventoryLedgerListItemResponse(
        @Schema(description = "流水 ID", example = "1") long id,
        @Schema(description = "商品 ID", example = "1") long productId,
        @Schema(description = "批次 ID", example = "1") long inventoryBatchId,
        @Schema(description = "流水类型：STOCK_IN/SALE_POS/SALE_ONLINE/ADJUST", example = "STOCK_IN") String changeType,
        @Schema(description = "变更数量（入库为正，扣减为负）", example = "50") int changeQty,
        @Schema(description = "变更后批次可用量快照", example = "50") int qtyAfter,
        @Schema(description = "来源单据类型", example = "STOCK_IN_ITEM") String sourceType,
        @Schema(description = "来源单据 ID", example = "1") long sourceId,
        @Schema(description = "业务发生时间") Instant eventTime
) {
}

