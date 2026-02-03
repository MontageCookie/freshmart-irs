package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.stockin.StockInOrderCreateRequest;
import com.freshmart.irs.dto.stockin.StockInOrderCreateResponse;
import com.freshmart.irs.dto.stockin.StockInOrderDetailResponse;
import com.freshmart.irs.dto.stockin.StockInOrderItemResponse;
import com.freshmart.irs.dto.stockin.StockInOrderListItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "StockIn")
@RestController
@RequestMapping("/api/v1/stock-in-orders")
public class StockInController {

    @Operation(summary = "创建入库单（占位）", description = "鉴权：JWT；角色：WAREHOUSE/STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping
    public ApiResponse<StockInOrderCreateResponse> create(@Valid @RequestBody StockInOrderCreateRequest request) {
        StockInOrderCreateResponse data = new StockInOrderCreateResponse(1, "SI-20260203-0001", request.status());
        return ApiResponse.ok(data);
    }

    @Operation(summary = "入库单列表（分页，占位）", description = "鉴权：JWT；角色：WAREHOUSE/STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<PageResponse<StockInOrderListItemResponse>> list(
            @Parameter(description = "状态：DRAFT/CONFIRMED/CANCELLED") @RequestParam(required = false) String status,
            @Parameter(description = "入库单号") @RequestParam(required = false) String bizNo,
            @Parameter(description = "创建人 userId") @RequestParam(required = false) Long createdBy,
            @Parameter(description = "到货/入库确认时间起（ISO-8601）") @RequestParam(required = false) Instant receivedFrom,
            @Parameter(description = "到货/入库确认时间止（ISO-8601）") @RequestParam(required = false) Instant receivedTo,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=receivedAt,desc&sort=id,asc") @RequestParam(required = false) List<String> sort
    ) {
        PageResponse<StockInOrderListItemResponse> data = new PageResponse<>(
                page,
                size,
                1,
                List.of(new StockInOrderListItemResponse(1, "SI-20260203-0001", 3, "CONFIRMED", Instant.now(), Instant.now()))
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "入库单详情（占位）", description = "鉴权：JWT；角色：WAREHOUSE/STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/{id}")
    public ApiResponse<StockInOrderDetailResponse> get(@PathVariable long id) {
        List<StockInOrderItemResponse> items = List.of(
                new StockInOrderItemResponse(1, 1, 1, 50, new BigDecimal("8.00"), LocalDate.now().plusDays(20))
        );
        StockInOrderDetailResponse data = new StockInOrderDetailResponse(
                id,
                "SI-20260203-0001",
                3,
                "CONFIRMED",
                Instant.now(),
                items
        );
        return ApiResponse.ok(data);
    }
}

