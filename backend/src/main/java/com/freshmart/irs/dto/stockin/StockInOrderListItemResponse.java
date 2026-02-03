package com.freshmart.irs.dto.stockin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record StockInOrderListItemResponse(
        @Schema(description = "入库单 ID", example = "1") long id,
        @Schema(description = "入库单号", example = "SI-20260203-0001") String bizNo,
        @Schema(description = "创建人 userId", example = "3") long createdBy,
        @Schema(description = "状态：DRAFT/CONFIRMED/CANCELLED", example = "CONFIRMED") String status,
        @Schema(description = "到货/入库确认时间") Instant receivedAt,
        @Schema(description = "创建时间") Instant createdAt
) {
}

