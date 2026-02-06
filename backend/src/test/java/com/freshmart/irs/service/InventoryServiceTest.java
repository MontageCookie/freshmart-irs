package com.freshmart.irs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshmart.irs.auth.AuthContext;
import com.freshmart.irs.auth.AuthContextHolder;
import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.dto.inventory.InventoryLedgerListItemResponse;
import com.freshmart.irs.entity.InventoryBatchEntity;
import com.freshmart.irs.entity.InventoryLedgerEntity;
import com.freshmart.irs.entity.ProductEntity;
import com.freshmart.irs.mapper.InventoryBatchMapper;
import com.freshmart.irs.mapper.InventoryLedgerMapper;
import com.freshmart.irs.mapper.ProductMapper;
import com.freshmart.irs.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock
    private ProductMapper productMapper;

    @Mock
    private InventoryBatchMapper inventoryBatchMapper;

    @Mock
    private InventoryLedgerMapper inventoryLedgerMapper;

    @AfterEach
    void tearDown() {
        AuthContextHolder.clear();
    }

    @Test
    void snapshot_customer_returnsForbidden() {
        InventoryService service = new InventoryServiceImpl(productMapper, inventoryBatchMapper, inventoryLedgerMapper);
        AuthContextHolder.set(new AuthContext(1L, "c1", List.of("CUSTOMER")));

        BizException ex = Assertions.assertThrows(BizException.class, () -> service.snapshot(null, null, 1, 10, List.of()));
        Assertions.assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void snapshot_withProductId_includesBatches() {
        InventoryService service = new InventoryServiceImpl(productMapper, inventoryBatchMapper, inventoryLedgerMapper);
        AuthContextHolder.set(new AuthContext(2L, "m1", List.of("STORE_MANAGER")));

        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setSku("APPLE-001");
        product.setName("苹果");
        product.setUnit("kg");
        product.setPrice(new BigDecimal("12.50"));
        product.setSafetyStockQty(20);
        product.setStatus(1);
        when(productMapper.selectById(1L)).thenReturn(product);

        InventoryBatchEntity b1 = new InventoryBatchEntity();
        b1.setId(11L);
        b1.setProductId(1L);
        b1.setBatchNo("B1");
        b1.setExpiryDate(LocalDate.now().plusDays(1));
        b1.setQtyInitial(100);
        b1.setQtyAvailable(80);
        b1.setStatus(1);
        b1.setReceivedAt(LocalDateTime.now().plusMinutes(1));

        InventoryBatchEntity b2 = new InventoryBatchEntity();
        b2.setId(12L);
        b2.setProductId(1L);
        b2.setBatchNo("B2");
        b2.setExpiryDate(LocalDate.now().plusDays(10));
        b2.setQtyInitial(50);
        b2.setQtyAvailable(20);
        b2.setStatus(0);
        b2.setReceivedAt(LocalDateTime.now());

        when(inventoryBatchMapper.selectList(any())).thenReturn(List.of(b1, b2));

        when(inventoryBatchMapper.selectMaps(any())).thenReturn(List.of(
                Map.of("totalQty", 100),
                Map.of("product_id", 1L, "availableQty", 80)
        ));

        var resp = service.snapshot(1L, null, 1, 10, List.of());
        Assertions.assertEquals(1, resp.items().size());
        var item = resp.items().get(0);
        Assertions.assertEquals(2, item.batches().size());
        Assertions.assertEquals("AVAILABLE", item.batches().get(0).status());
    }

    @Test
    void snapshot_withProductId_handlesNullAggregateRow() {
        InventoryService service = new InventoryServiceImpl(productMapper, inventoryBatchMapper, inventoryLedgerMapper);
        AuthContextHolder.set(new AuthContext(4L, "w2", List.of("WAREHOUSE")));

        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setSku("APPLE-001");
        product.setName("苹果");
        product.setUnit("kg");
        product.setPrice(new BigDecimal("12.50"));
        product.setSafetyStockQty(20);
        product.setStatus(1);
        when(productMapper.selectById(1L)).thenReturn(product);

        when(inventoryBatchMapper.selectList(any())).thenReturn(List.of());

        when(inventoryBatchMapper.selectMaps(any())).thenReturn(
                Collections.singletonList(null),
                List.of()
        );

        var resp = service.snapshot(1L, null, 1, 10, List.of());
        Assertions.assertEquals(1, resp.items().size());
        var item = resp.items().get(0);
        Assertions.assertEquals(0, item.totalQty());
        Assertions.assertEquals(0, item.availableQty());
        Assertions.assertEquals(0, item.batches().size());
    }

    @Test
    void ledgers_warehouse_returnsOk() {
        InventoryService service = new InventoryServiceImpl(productMapper, inventoryBatchMapper, inventoryLedgerMapper);
        AuthContextHolder.set(new AuthContext(3L, "w1", List.of("WAREHOUSE")));

        InventoryLedgerEntity e = new InventoryLedgerEntity();
        e.setId(1L);
        e.setProductId(2L);
        e.setInventoryBatchId(3L);
        e.setChangeType("STOCK_IN");
        e.setChangeQty(10);
        e.setQtyAfter(10);
        e.setSourceType("STOCK_IN_ITEM");
        e.setSourceId(99L);
        e.setEventTime(LocalDateTime.of(2026, 2, 1, 10, 0));

        Page<InventoryLedgerEntity> p = new Page<>(1, 10);
        p.setTotal(1);
        p.setRecords(List.of(e));
        when(inventoryLedgerMapper.selectPage(any(Page.class), any())).thenReturn(p);

        var resp = service.ledgers(null, null, null, null, null, null, 1, 10, List.of());
        Assertions.assertEquals(1, resp.items().size());
        InventoryLedgerListItemResponse item = resp.items().get(0);
        Assertions.assertEquals(1L, item.id());
        Instant expected = e.getEventTime().atZone(ZoneId.systemDefault()).toInstant();
        Assertions.assertEquals(expected, item.eventTime());
    }
}
