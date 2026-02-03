package com.freshmart.irs.dto.sale;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record SaleOrderCreateResponse(
        @Schema(description = "销售单 ID") long id,
        @Schema(description = "销售单号") String bizNo,
        @Schema(description = "状态", example = "COMPLETED") String status,
        @Schema(description = "总金额") BigDecimal totalAmount
) {
}
