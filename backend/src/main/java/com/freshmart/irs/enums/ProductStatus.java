package com.freshmart.irs.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商品状态枚举（冻结语义）")
public enum ProductStatus {
    ON_SALE,
    OFF_SALE
}

