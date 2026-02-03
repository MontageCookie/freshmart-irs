package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.common.IdResponse;
import com.freshmart.irs.dto.product.ProductDetailResponse;
import com.freshmart.irs.dto.product.ProductListItemResponse;
import com.freshmart.irs.dto.product.ProductUpsertRequest;
import com.freshmart.irs.enums.ProductStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {

    @Operation(summary = "商品列表（分页，占位）", description = "鉴权：否")
    @GetMapping
    public ApiResponse<PageResponse<ProductListItemResponse>> list(
            @Parameter(description = "关键字（名称模糊）") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) ProductStatus status,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=field,asc|desc，可重复传参") @RequestParam(required = false) List<String> sort
    ) {
        PageResponse<ProductListItemResponse> data = new PageResponse<>(
                page,
                size,
                2,
                List.of(
                        new ProductListItemResponse(1, "APPLE-001", "苹果", "kg", new BigDecimal("12.50"), ProductStatus.ON_SALE),
                        new ProductListItemResponse(2, "MILK-001", "牛奶", "盒", new BigDecimal("4.80"), ProductStatus.ON_SALE)
                )
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "创建商品（占位）", description = "鉴权：JWT；角色：ADMIN/STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping
    public ApiResponse<IdResponse> create(@Valid @RequestBody ProductUpsertRequest request) {
        return ApiResponse.ok(new IdResponse(1));
    }

    @Operation(summary = "商品详情（占位）", description = "鉴权：否")
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> get(@PathVariable long id) {
        ProductDetailResponse data = new ProductDetailResponse(
                id,
                "APPLE-001",
                "苹果",
                "kg",
                new BigDecimal("12.50"),
                20,
                ProductStatus.ON_SALE
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "更新商品（占位）", description = "鉴权：JWT；角色：ADMIN/STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PutMapping("/{id}")
    public ApiResponse<IdResponse> update(@PathVariable long id, @Valid @RequestBody ProductUpsertRequest request) {
        return ApiResponse.ok(new IdResponse(id));
    }

    @Operation(summary = "下架商品（DELETE 语义固定：下架，占位）", description = "鉴权：JWT；角色：ADMIN/STORE_MANAGER；DELETE 语义：下架（status=OFF_SALE）")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @DeleteMapping("/{id}")
    public ApiResponse<IdResponse> offSale(@PathVariable long id) {
        return ApiResponse.ok(new IdResponse(id));
    }
}

