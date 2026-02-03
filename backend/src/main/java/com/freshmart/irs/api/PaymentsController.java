package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.payment.PaymentDetailResponse;
import com.freshmart.irs.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

@Tag(name = "Customer Orders & Payment")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentsController {

    @Operation(summary = "查询支付记录（占位）", description = "鉴权：JWT；角色：CUSTOMER/STORE_MANAGER/ADMIN")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/{id}")
    public ApiResponse<PaymentDetailResponse> get(@PathVariable long id) {
        PaymentDetailResponse data = new PaymentDetailResponse(
                id,
                1,
                PaymentStatus.SUCCESS,
                new BigDecimal("25.00"),
                Instant.now(),
                "PAY-REF-0001"
        );
        return ApiResponse.ok(data);
    }
}
