package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.inventory.InventoryBatchListItemResponse;
import com.freshmart.irs.dto.inventory.InventoryLedgerListItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Inventory")
@RestController
@RequestMapping("/api/v1")
public class InventoryController {

    @Operation(summary = "库存批次列表（分页，占位）", description = "鉴权：JWT；角色：STORE_MANAGER/WAREHOUSE/CASHIER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/inventory-batches")
    public ApiResponse<PageResponse<InventoryBatchListItemResponse>> listBatches(
            @Parameter(description = "商品 ID") @RequestParam(required = false) Long productId,
            @Parameter(description = "批次状态：0=BLOCKED,1=AVAILABLE,2=EXPIRED") @RequestParam(required = false) Integer status,
            @Parameter(description = "效期起（YYYY-MM-DD）") @RequestParam(required = false) LocalDate expiryFrom,
            @Parameter(description = "效期止（YYYY-MM-DD）") @RequestParam(required = false) LocalDate expiryTo,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=createdAt,desc&sort=id,asc") @RequestParam(required = false) List<String> sort
    ) {
        PageResponse<InventoryBatchListItemResponse> data = new PageResponse<>(
                page,
                size,
                1,
                List.of(new InventoryBatchListItemResponse(
                        1,
                        1,
                        "BATCH-20260203-0001",
                        LocalDate.now().plusDays(20),
                        100,
                        80,
                        1,
                        Instant.now()
                ))
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "库存流水列表（分页，占位）", description = "鉴权：JWT；角色：STORE_MANAGER/WAREHOUSE")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/inventory-ledgers")
    public ApiResponse<PageResponse<InventoryLedgerListItemResponse>> listLedger(
            @Parameter(description = "商品 ID") @RequestParam(required = false) Long productId,
            @Parameter(description = "批次 ID") @RequestParam(required = false) Long batchId,
            @Parameter(description = "流水类型") @RequestParam(required = false) String changeType,
            @Parameter(description = "来源单据类型") @RequestParam(required = false) String sourceType,
            @Parameter(description = "发生时间起（ISO-8601）") @RequestParam(required = false) Instant eventFrom,
            @Parameter(description = "发生时间止（ISO-8601）") @RequestParam(required = false) Instant eventTo,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=eventTime,desc&sort=id,asc") @RequestParam(required = false) List<String> sort
    ) {
        PageResponse<InventoryLedgerListItemResponse> data = new PageResponse<>(
                page,
                size,
                1,
                List.of(new InventoryLedgerListItemResponse(
                        1,
                        1,
                        1,
                        "STOCK_IN",
                        50,
                        50,
                        "STOCK_IN_ITEM",
                        1,
                        Instant.now()
                ))
        );
        return ApiResponse.ok(data);
    }
}
