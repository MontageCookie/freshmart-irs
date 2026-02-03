package com.freshmart.irs.dto.sale;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record SaleOrderCreateRequest(
        @Schema(description = "销售时间（ISO-8601）") @NotNull Instant saleTime,
        @Schema(description = "销售明细") @NotEmpty List<SaleOrderCreateItemRequest> items
) {
}
