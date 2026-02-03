package com.freshmart.irs.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemSetRequest(
        @Schema(description = "数量（覆盖式设置）") @NotNull @Min(0) Integer qty
) {
}
