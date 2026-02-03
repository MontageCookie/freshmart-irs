package com.freshmart.irs.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户状态枚举（冻结语义）")
public enum UserStatus {
    ENABLED,
    DISABLED
}

