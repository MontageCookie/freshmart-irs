package com.freshmart.irs.service;

import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.dto.product.ProductDetailResponse;
import com.freshmart.irs.dto.product.ProductListItemResponse;
import com.freshmart.irs.dto.product.ProductUpsertRequest;
import com.freshmart.irs.enums.ProductStatus;

import java.util.List;

/**
 * 商品服务：提供商品查询与店长维护能力。
 */
public interface ProductsService {
    /**
     * 分页查询商品列表。
     *
     * @param keyword 关键字（名称/sku 模糊）
     * @param status  状态过滤（仅管理端可用；顾客端/匿名固定仅看 ON_SALE）
     * @param page    页码，从 1 开始
     * @param size    每页大小
     * @param sort    排序参数（sort=field,asc|desc，可重复传参）
     * @return 分页结果
     */
    PageResponse<ProductListItemResponse> list(String keyword, ProductStatus status, int page, int size, List<String> sort);

    /**
     * 查询商品详情。
     *
     * @param id 商品 ID
     * @return 商品详情
     */
    ProductDetailResponse get(long id);

    /**
     * 创建商品（仅店长）。
     *
     * @param request 创建请求
     * @return 新商品 ID
     */
    long create(ProductUpsertRequest request);

    /**
     * 更新商品（仅店长）。
     *
     * @param id      商品 ID
     * @param request 更新请求
     * @return 商品 ID
     */
    long update(long id, ProductUpsertRequest request);

    /**
     * 下架商品（仅店长；DELETE 语义固定为逻辑下架）。
     *
     * @param id 商品 ID
     * @return 商品 ID
     */
    long offSale(long id);
}

