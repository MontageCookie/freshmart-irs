package com.freshmart.irs.dto.customerorder;

import com.freshmart.irs.enums.CustomerOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

public record CustomerOrderListItemResponse(
        @Schema(description = "订单 ID") long id,
        @Schema(description = "订单号") String bizNo,
        @Schema(description = "订单状态") CustomerOrderStatus orderStatus,
        @Schema(description = "总金额") BigDecimal totalAmount,
        @Schema(description = "下单时间（ISO-8601）") Instant placedAt
) {
}
