package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.sale.SaleOrderCreateRequest;
import com.freshmart.irs.dto.sale.SaleOrderCreateResponse;
import com.freshmart.irs.dto.sale.SaleOrderDetailResponse;
import com.freshmart.irs.dto.sale.SaleOrderItemResponse;
import com.freshmart.irs.dto.sale.SaleOrderListItemResponse;
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
import java.util.List;

@Tag(name = "Sales")
@RestController
@RequestMapping("/api/v1/sale-orders")
public class SalesController {

    @Operation(summary = "创建销售单（占位）", description = "鉴权：JWT；角色：CASHIER/STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping
    public ApiResponse<SaleOrderCreateResponse> create(@Valid @RequestBody SaleOrderCreateRequest request) {
        SaleOrderCreateResponse data = new SaleOrderCreateResponse(
                1,
                "SO-20260204-0001",
                "COMPLETED",
                new BigDecimal("25.00")
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "销售单列表（分页，占位）", description = "鉴权：JWT；角色：CASHIER/STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<PageResponse<SaleOrderListItemResponse>> list(
            @Parameter(description = "销售单号") @RequestParam(required = false) String bizNo,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "收银员 userId") @RequestParam(required = false) Long cashierId,
            @Parameter(description = "销售时间起（ISO-8601）") @RequestParam(required = false) Instant saleFrom,
            @Parameter(description = "销售时间止（ISO-8601）") @RequestParam(required = false) Instant saleTo,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=field,asc|desc，可重复传参") @RequestParam(required = false) List<String> sort
    ) {
        PageResponse<SaleOrderListItemResponse> data = new PageResponse<>(
                page,
                size,
                1,
                List.of(new SaleOrderListItemResponse(
                        1,
                        "SO-20260204-0001",
                        4,
                        "COMPLETED",
                        new BigDecimal("25.00"),
                        Instant.now()
                ))
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "销售单详情（占位）", description = "鉴权：JWT；角色：CASHIER/STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/{id}")
    public ApiResponse<SaleOrderDetailResponse> get(@PathVariable long id) {
        List<SaleOrderItemResponse> items = List.of(
                new SaleOrderItemResponse(1, 1, 1L, 2, new BigDecimal("12.50"), new BigDecimal("25.00"))
        );
        SaleOrderDetailResponse data = new SaleOrderDetailResponse(
                id,
                "SO-20260204-0001",
                4,
                "COMPLETED",
                new BigDecimal("25.00"),
                Instant.now(),
                items
        );
        return ApiResponse.ok(data);
    }
}
