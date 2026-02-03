package com.freshmart.irs.dto.inventory;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

public record InventoryBatchListItemResponse(
        @Schema(description = "批次 ID", example = "1") long id,
        @Schema(description = "商品 ID", example = "1") long productId,
        @Schema(description = "批次号", example = "BATCH-20260203-0001") String batchNo,
        @Schema(description = "效期日期", example = "2026-03-01") LocalDate expiryDate,
        @Schema(description = "初始数量", example = "100") int qtyInitial,
        @Schema(description = "可用数量", example = "80") int qtyAvailable,
        @Schema(description = "批次状态：0=BLOCKED,1=AVAILABLE,2=EXPIRED", example = "1") int status,
        @Schema(description = "入库时间") Instant receivedAt
) {
}

