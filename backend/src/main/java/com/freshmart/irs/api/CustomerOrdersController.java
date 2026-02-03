package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.customerorder.CustomerOrderCreateRequest;
import com.freshmart.irs.dto.customerorder.CustomerOrderCreateResponse;
import com.freshmart.irs.dto.customerorder.CustomerOrderDetailResponse;
import com.freshmart.irs.dto.customerorder.CustomerOrderItemResponse;
import com.freshmart.irs.dto.customerorder.CustomerOrderListItemResponse;
import com.freshmart.irs.dto.payment.CustomerOrderPaymentRequest;
import com.freshmart.irs.dto.payment.CustomerOrderPaymentResponse;
import com.freshmart.irs.dto.payment.PaymentSummaryResponse;
import com.freshmart.irs.enums.CustomerOrderStatus;
import com.freshmart.irs.enums.PaymentStatus;
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

@Tag(name = "Customer Orders & Payment")
@RestController
@RequestMapping("/api/v1/customer-orders")
public class CustomerOrdersController {

    @Operation(summary = "创建顾客订单（占位）", description = "鉴权：JWT；角色：CUSTOMER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping
    public ApiResponse<CustomerOrderCreateResponse> create(@Valid @RequestBody CustomerOrderCreateRequest request) {
        CustomerOrderCreateResponse data = new CustomerOrderCreateResponse(
                1,
                "CO-20260204-0001",
                CustomerOrderStatus.PLACED,
                new BigDecimal("25.00"),
                Instant.now()
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "我的订单列表（分页，占位）", description = "鉴权：JWT；角色：CUSTOMER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<PageResponse<CustomerOrderListItemResponse>> list(
            @Parameter(description = "状态：PLACED/PAID/CONFIRMED/CANCELLED") @RequestParam(required = false) CustomerOrderStatus status,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=field,asc|desc，可重复传参") @RequestParam(required = false) List<String> sort
    ) {
        PageResponse<CustomerOrderListItemResponse> data = new PageResponse<>(
                page,
                size,
                1,
                List.of(new CustomerOrderListItemResponse(
                        1,
                        "CO-20260204-0001",
                        CustomerOrderStatus.PLACED,
                        new BigDecimal("25.00"),
                        Instant.now()
                ))
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "订单详情（占位）", description = "鉴权：JWT；角色：CUSTOMER/STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/{id}")
    public ApiResponse<CustomerOrderDetailResponse> get(@PathVariable long id) {
        List<CustomerOrderItemResponse> items = List.of(
                new CustomerOrderItemResponse(1, 1L, 2, new BigDecimal("12.50"), new BigDecimal("25.00"))
        );
        PaymentSummaryResponse payment = new PaymentSummaryResponse(
                1,
                PaymentStatus.SUCCESS,
                new BigDecimal("25.00"),
                Instant.now()
        );
        CustomerOrderDetailResponse data = new CustomerOrderDetailResponse(
                id,
                "CO-20260204-0001",
                5,
                CustomerOrderStatus.PLACED,
                new BigDecimal("25.00"),
                Instant.now(),
                null,
                items,
                payment
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "模拟支付确认（占位）", description = "鉴权：JWT；角色：CUSTOMER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping("/{id}/payment")
    public ApiResponse<CustomerOrderPaymentResponse> pay(@PathVariable long id, @Valid @RequestBody CustomerOrderPaymentRequest request) {
        CustomerOrderPaymentResponse data = new CustomerOrderPaymentResponse(
                1,
                PaymentStatus.SUCCESS,
                CustomerOrderStatus.PAID
        );
        return ApiResponse.ok(data);
    }
}
