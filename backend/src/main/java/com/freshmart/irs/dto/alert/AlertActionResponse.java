package com.freshmart.irs.dto.alert;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record AlertActionResponse(
        @Schema(description = "动作 ID") long id,
        @Schema(description = "预警 ID") long alertId,
        @Schema(description = "操作人 userId") long actorUserId,
        @Schema(description = "动作类型") String actionType,
        @Schema(description = "处置结果/说明") String actionResult,
        @Schema(description = "动作时间（ISO-8601）") Instant actionTime
) {
}
