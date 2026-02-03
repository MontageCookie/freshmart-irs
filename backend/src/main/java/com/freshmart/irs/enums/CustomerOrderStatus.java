package com.freshmart.irs.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "顾客订单状态枚举（冻结语义）")
public enum CustomerOrderStatus {
    PLACED,
    PAID,
    CONFIRMED,
    CANCELLED
}
