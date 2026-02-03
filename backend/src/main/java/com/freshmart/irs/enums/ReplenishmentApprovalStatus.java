package com.freshmart.irs.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "补货建议审批状态枚举（冻结语义）")
public enum ReplenishmentApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED
}

