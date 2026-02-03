package com.freshmart.irs.dto.payment;

import com.freshmart.irs.enums.CustomerOrderStatus;
import com.freshmart.irs.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record CustomerOrderPaymentResponse(
        @Schema(description = "支付记录 ID") long paymentId,
        @Schema(description = "支付状态") PaymentStatus payStatus,
        @Schema(description = "订单状态") CustomerOrderStatus orderStatus
) {
}
