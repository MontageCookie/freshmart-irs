package com.freshmart.irs.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CustomerOrderPaymentRequest(
        @Schema(description = "支付金额") @NotNull BigDecimal amount,
        @Schema(description = "支付流水号（可选）") String payRef
) {
}
