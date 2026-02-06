package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.inventory.InventoryLedgerListItemResponse;
import com.freshmart.irs.dto.inventory.InventorySnapshotItemResponse;
import com.freshmart.irs.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@Tag(name = "Inventory")
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "库存快照", description = "鉴权：JWT；角色：STORE_MANAGER/WAREHOUSE；可选指定 productId 返回批次明细")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/snapshot")
    public ApiResponse<PageResponse<InventorySnapshotItemResponse>> snapshot(
            @Parameter(description = "商品 ID") @RequestParam(required = false) Long productId,
            @Parameter(description = "关键字（商品名称/SKU 模糊）") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=field,asc|desc，可重复传参") @RequestParam(required = false) List<String> sort
    ) {
        return ApiResponse.ok(inventoryService.snapshot(productId, keyword, page, size, sort));
    }

    @Operation(summary = "库存流水", description = "鉴权：JWT；角色：STORE_MANAGER/WAREHOUSE")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/ledgers")
    public ApiResponse<PageResponse<InventoryLedgerListItemResponse>> ledgers(
            @Parameter(description = "商品 ID") @RequestParam(required = false) Long productId,
            @Parameter(description = "批次 ID") @RequestParam(required = false) Long batchId,
            @Parameter(description = "流水类型") @RequestParam(required = false) String changeType,
            @Parameter(description = "来源单据类型") @RequestParam(required = false) String sourceType,
            @Parameter(description = "发生时间起（ISO-8601）") @RequestParam(required = false) Instant eventFrom,
            @Parameter(description = "发生时间止（ISO-8601）") @RequestParam(required = false) Instant eventTo,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=field,asc|desc，可重复传参") @RequestParam(required = false) List<String> sort
    ) {
        return ApiResponse.ok(inventoryService.ledgers(productId, batchId, changeType, sourceType, eventFrom, eventTo, page, size, sort));
    }
}
