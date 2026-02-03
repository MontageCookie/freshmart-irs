package com.freshmart.irs.dto.stockin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record StockInOrderCreateRequest(
        @Schema(description = "入库单状态：DRAFT/CONFIRMED/CANCELLED", example = "CONFIRMED") @NotNull String status,
        @Schema(description = "到货/入库确认时间（status=CONFIRMED 时建议填写）") Instant receivedAt,
        @Schema(description = "备注") String remark,
        @Schema(description = "入库明细") @NotEmpty List<StockInOrderCreateItemRequest> items
) {
}

