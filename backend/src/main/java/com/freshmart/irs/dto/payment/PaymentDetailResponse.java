package com.freshmart.irs.dto.payment;

import com.freshmart.irs.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentDetailResponse(
        @Schema(description = "支付记录 ID") long id,
        @Schema(description = "顾客订单 ID") long customerOrderId,
        @Schema(description = "支付状态") PaymentStatus payStatus,
        @Schema(description = "支付金额") BigDecimal amount,
        @Schema(description = "支付时间（ISO-8601）") Instant paidAt,
        @Schema(description = "支付流水号") String payRef
) {
}
