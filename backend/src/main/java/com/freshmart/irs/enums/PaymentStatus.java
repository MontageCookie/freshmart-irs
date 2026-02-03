package com.freshmart.irs.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "支付状态枚举（冻结语义）")
public enum PaymentStatus {
    SUCCESS,
    FAILED
}
