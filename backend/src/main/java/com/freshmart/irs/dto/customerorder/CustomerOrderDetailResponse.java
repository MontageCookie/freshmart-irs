package com.freshmart.irs.dto.customerorder;

import com.freshmart.irs.dto.payment.PaymentSummaryResponse;
import com.freshmart.irs.enums.CustomerOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CustomerOrderDetailResponse(
        @Schema(description = "订单 ID") long id,
        @Schema(description = "订单号") String bizNo,
        @Schema(description = "下单用户 userId") long userId,
        @Schema(description = "订单状态") CustomerOrderStatus orderStatus,
        @Schema(description = "总金额") BigDecimal totalAmount,
        @Schema(description = "下单时间（ISO-8601）") Instant placedAt,
        @Schema(description = "确认时间（ISO-8601）") Instant confirmedAt,
        @Schema(description = "订单明细") List<CustomerOrderItemResponse> items,
        @Schema(description = "支付信息") PaymentSummaryResponse payment
) {
}
