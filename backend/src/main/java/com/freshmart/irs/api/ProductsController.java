package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.common.IdResponse;
import com.freshmart.irs.dto.product.ProductDetailResponse;
import com.freshmart.irs.dto.product.ProductListItemResponse;
import com.freshmart.irs.dto.product.ProductUpsertRequest;
import com.freshmart.irs.enums.ProductStatus;
import com.freshmart.irs.service.ProductsService;
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

import java.util.List;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {
    private final ProductsService productsService;

    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Operation(summary = "商品列表（分页）", description = "鉴权：否（匿名可访问）；匿名/顾客仅返回 ON_SALE 商品并包含 availableQty")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<PageResponse<ProductListItemResponse>> list(
            @Parameter(description = "关键字（名称模糊）") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) ProductStatus status,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=field,asc|desc，可重复传参") @RequestParam(required = false) List<String> sort) {
        return ApiResponse.ok(productsService.list(keyword, status, page, size, sort));
    }

    @Operation(summary = "创建商品", description = "鉴权：JWT；角色：STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping
    public ApiResponse<IdResponse> create(@Valid @RequestBody ProductUpsertRequest request) {
        return ApiResponse.ok(new IdResponse(productsService.create(request)));
    }

    @Operation(summary = "商品详情", description = "鉴权：否（匿名可访问）；匿名/顾客访问下架商品返回 404")
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> get(@PathVariable long id) {
        return ApiResponse.ok(productsService.get(id));
    }

    @Operation(summary = "更新商品", description = "鉴权：JWT；角色：STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PutMapping("/{id}")
    public ApiResponse<IdResponse> update(@PathVariable long id, @Valid @RequestBody ProductUpsertRequest request) {
        return ApiResponse.ok(new IdResponse(productsService.update(id, request)));
    }

    @Operation(summary = "下架商品（DELETE 语义固定：下架）", description = "鉴权：JWT；角色：STORE_MANAGER；DELETE 语义：下架（status=OFF_SALE）")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @DeleteMapping("/{id}")
    public ApiResponse<IdResponse> offSale(@PathVariable long id) {
        return ApiResponse.ok(new IdResponse(productsService.offSale(id)));
    }
}
