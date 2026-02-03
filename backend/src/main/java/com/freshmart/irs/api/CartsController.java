package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.cart.CartItemRemoveResponse;
import com.freshmart.irs.dto.cart.CartItemResponse;
import com.freshmart.irs.dto.cart.CartItemSetRequest;
import com.freshmart.irs.dto.cart.CartItemSetResponse;
import com.freshmart.irs.dto.cart.CartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Customer Orders & Payment")
@RestController
@RequestMapping("/api/v1/carts/me")
public class CartsController {

    @Operation(summary = "获取我的购物车（占位）", description = "鉴权：JWT；角色：CUSTOMER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<CartResponse> getMyCart() {
        CartResponse data = new CartResponse(
                1,
                "ACTIVE",
                List.of(new CartItemResponse(1, 2))
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "设置购物车商品数量（覆盖式，占位）", description = "鉴权：JWT；角色：CUSTOMER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PutMapping("/items/{productId}")
    public ApiResponse<CartItemSetResponse> setItemQty(@PathVariable long productId, @Valid @RequestBody CartItemSetRequest request) {
        CartItemSetResponse data = new CartItemSetResponse(1, productId, request.qty());
        return ApiResponse.ok(data);
    }

    @Operation(summary = "移除购物车商品（占位）", description = "鉴权：JWT；角色：CUSTOMER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @DeleteMapping("/items/{productId}")
    public ApiResponse<CartItemRemoveResponse> removeItem(@PathVariable long productId) {
        return ApiResponse.ok(new CartItemRemoveResponse(1, productId));
    }
}
