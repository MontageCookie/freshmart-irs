package com.freshmart.irs.dto.inventory;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

/**
 * 库存批次快照响应（管理端只读）。
 */
public record InventoryBatchSnapshotResponse(
        @Schema(description = "批次 ID", example = "1") long id,
        @Schema(description = "批次号", example = "BATCH-20260203-0001") String batchNo,
        @Schema(description = "效期日期", example = "2026-03-01") LocalDate expiryDate,
        @Schema(description = "初始数量", example = "100") int qtyInitial,
        @Schema(description = "可用数量", example = "80") int qtyAvailable,
        @Schema(description = "批次状态", allowableValues = {"AVAILABLE", "BLOCKED", "EXPIRED"}) String status,
        @Schema(description = "入库时间") Instant receivedAt
) {
}

