package com.freshmart.irs.dto.stockin;

import io.swagger.v3.oas.annotations.media.Schema;

public record StockInOrderCreateResponse(
        @Schema(description = "入库单 ID", example = "1") long id,
        @Schema(description = "入库单号", example = "SI-20260203-0001") String bizNo,
        @Schema(description = "状态：DRAFT/CONFIRMED/CANCELLED", example = "CONFIRMED") String status
) {
}

