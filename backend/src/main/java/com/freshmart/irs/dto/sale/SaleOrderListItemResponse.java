package com.freshmart.irs.dto.sale;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

public record SaleOrderListItemResponse(
        @Schema(description = "销售单 ID") long id,
        @Schema(description = "销售单号") String bizNo,
        @Schema(description = "收银员 userId") long cashierId,
        @Schema(description = "状态", example = "COMPLETED") String status,
        @Schema(description = "总金额") BigDecimal totalAmount,
        @Schema(description = "销售时间（ISO-8601）") Instant saleTime
) {
}
