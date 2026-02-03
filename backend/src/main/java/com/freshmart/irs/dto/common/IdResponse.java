package com.freshmart.irs.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

public record IdResponse(
        @Schema(description = "ID", example = "1") long id
) {
}

