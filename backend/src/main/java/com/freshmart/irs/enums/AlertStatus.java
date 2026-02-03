package com.freshmart.irs.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "预警状态枚举（冻结语义）")
public enum AlertStatus {
    OPEN,
    ACKED,
    RESOLVED,
    CLOSED
}
