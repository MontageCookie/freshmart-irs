package com.freshmart.irs.service;

import com.freshmart.irs.auth.AuthContext;
import com.freshmart.irs.auth.AuthContextHolder;
import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.dto.product.ProductDetailResponse;
import com.freshmart.irs.dto.product.ProductUpsertRequest;
import com.freshmart.irs.entity.ProductEntity;
import com.freshmart.irs.enums.ProductStatus;
import com.freshmart.irs.mapper.InventoryBatchMapper;
import com.freshmart.irs.mapper.ProductMapper;
import com.freshmart.irs.service.impl.ProductsServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {
    @Mock
    private ProductMapper productMapper;

    @Mock
    private InventoryBatchMapper inventoryBatchMapper;

    @AfterEach
    void tearDown() {
        AuthContextHolder.clear();
    }

    @Test
    void create_notStoreManager_returnsForbidden() {
        ProductsService service = new ProductsServiceImpl(productMapper, inventoryBatchMapper);
        AuthContextHolder.set(new AuthContext(1L, "u1", List.of("CUSTOMER")));

        ProductUpsertRequest request = new ProductUpsertRequest(
                "APPLE-001",
                "苹果",
                "kg",
                new BigDecimal("12.50"),
                10,
                ProductStatus.ON_SALE
        );

        BizException ex = Assertions.assertThrows(BizException.class, () -> service.create(request));
        Assertions.assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void create_skuExists_returnsParamInvalid() {
        ProductsService service = new ProductsServiceImpl(productMapper, inventoryBatchMapper);
        AuthContextHolder.set(new AuthContext(1L, "manager", List.of("STORE_MANAGER")));
        when(productMapper.selectCount(any())).thenReturn(1L);

        ProductUpsertRequest request = new ProductUpsertRequest(
                "APPLE-001",
                "苹果",
                "kg",
                new BigDecimal("12.50"),
                10,
                ProductStatus.ON_SALE
        );

        BizException ex = Assertions.assertThrows(BizException.class, () -> service.create(request));
        Assertions.assertEquals(ErrorCode.PARAM_INVALID, ex.getErrorCode());
    }

    @Test
    void create_negativeSafetyStock_returnsParamInvalid() {
        ProductsService service = new ProductsServiceImpl(productMapper, inventoryBatchMapper);
        AuthContextHolder.set(new AuthContext(1L, "manager", List.of("STORE_MANAGER")));

        ProductUpsertRequest request = new ProductUpsertRequest(
                "APPLE-001",
                "苹果",
                "kg",
                new BigDecimal("12.50"),
                -1,
                ProductStatus.ON_SALE
        );

        BizException ex = Assertions.assertThrows(BizException.class, () -> service.create(request));
        Assertions.assertEquals(ErrorCode.PARAM_INVALID, ex.getErrorCode());
    }

    @Test
    void get_anonymousOffSale_returnsNotFound() {
        ProductsService service = new ProductsServiceImpl(productMapper, inventoryBatchMapper);

        ProductEntity p = new ProductEntity();
        p.setId(1L);
        p.setSku("APPLE-001");
        p.setName("苹果");
        p.setUnit("kg");
        p.setPrice(new BigDecimal("12.50"));
        p.setSafetyStockQty(20);
        p.setStatus(0);
        when(productMapper.selectById(1L)).thenReturn(p);

        BizException ex = Assertions.assertThrows(BizException.class, () -> service.get(1L));
        Assertions.assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void get_warehouseCanViewOffSale_returnsOk() {
        ProductsService service = new ProductsServiceImpl(productMapper, inventoryBatchMapper);
        AuthContextHolder.set(new AuthContext(2L, "w1", List.of("WAREHOUSE")));

        ProductEntity p = new ProductEntity();
        p.setId(1L);
        p.setSku("APPLE-001");
        p.setName("苹果");
        p.setUnit("kg");
        p.setPrice(new BigDecimal("12.50"));
        p.setSafetyStockQty(20);
        p.setStatus(0);
        when(productMapper.selectById(1L)).thenReturn(p);

        when(inventoryBatchMapper.selectMaps(any())).thenReturn(List.of(
                Map.of("product_id", 1L, "availableQty", 10)
        ));

        ProductDetailResponse resp = service.get(1L);
        Assertions.assertEquals(ProductStatus.OFF_SALE, resp.status());
        Assertions.assertEquals(10, resp.availableQty());
    }

    @Test
    void offSale_notFound_returnsNotFound() {
        ProductsService service = new ProductsServiceImpl(productMapper, inventoryBatchMapper);
        AuthContextHolder.set(new AuthContext(1L, "manager", List.of("STORE_MANAGER")));
        when(productMapper.selectById(1L)).thenReturn(null);

        BizException ex = Assertions.assertThrows(BizException.class, () -> service.offSale(1L));
        Assertions.assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void list_availableQtyUsesStrictExpiryGtToday() {
        ProductsService service = new ProductsServiceImpl(productMapper, inventoryBatchMapper);

        ProductEntity p = new ProductEntity();
        p.setId(1L);
        p.setSku("APPLE-001");
        p.setName("苹果");
        p.setUnit("kg");
        p.setPrice(new BigDecimal("12.50"));
        p.setSafetyStockQty(20);
        p.setStatus(1);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProductEntity> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        page.setTotal(1);
        page.setRecords(List.of(p));
        when(productMapper.selectPage(any(com.baomidou.mybatisplus.extension.plugins.pagination.Page.class), any())).thenReturn(page);

        when(inventoryBatchMapper.selectMaps(any())).thenReturn(List.of(
                Map.of("product_id", 1L, "availableQty", 8)
        ));

        var resp = service.list(null, null, 1, 10, List.of());
        Assertions.assertEquals(1, resp.items().size());
        Assertions.assertEquals(8, resp.items().get(0).availableQty());
    }
}
