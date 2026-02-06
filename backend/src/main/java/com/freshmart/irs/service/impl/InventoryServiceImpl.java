package com.freshmart.irs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshmart.irs.auth.AuthContext;
import com.freshmart.irs.auth.AuthContextHolder;
import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.dto.inventory.InventoryBatchSnapshotResponse;
import com.freshmart.irs.dto.inventory.InventoryLedgerListItemResponse;
import com.freshmart.irs.dto.inventory.InventorySnapshotItemResponse;
import com.freshmart.irs.entity.InventoryBatchEntity;
import com.freshmart.irs.entity.InventoryLedgerEntity;
import com.freshmart.irs.entity.ProductEntity;
import com.freshmart.irs.enums.ProductStatus;
import com.freshmart.irs.mapper.InventoryBatchMapper;
import com.freshmart.irs.mapper.InventoryLedgerMapper;
import com.freshmart.irs.mapper.ProductMapper;
import com.freshmart.irs.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存服务实现：对齐“商品与库存模块”的冻结契约，提供库存快照与流水查询能力（只读）。
 */
@Service
public class InventoryServiceImpl implements InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private static final int DB_PRODUCT_STATUS_ON_SALE = 1;
    private static final int DB_BATCH_STATUS_AVAILABLE = 1;
    private static final int DB_BATCH_STATUS_BLOCKED = 0;
    private static final int DB_BATCH_STATUS_EXPIRED = 2;

    private final ProductMapper productMapper;
    private final InventoryBatchMapper inventoryBatchMapper;
    private final InventoryLedgerMapper inventoryLedgerMapper;

    public InventoryServiceImpl(
            ProductMapper productMapper,
            InventoryBatchMapper inventoryBatchMapper,
            InventoryLedgerMapper inventoryLedgerMapper
    ) {
        this.productMapper = productMapper;
        this.inventoryBatchMapper = inventoryBatchMapper;
        this.inventoryLedgerMapper = inventoryLedgerMapper;
    }

    @Override
    public PageResponse<InventorySnapshotItemResponse> snapshot(Long productId, String keyword, int page, int size, List<String> sort) {
        requireStoreManagerOrWarehouse();

        if (page < 1) {
            throw new BizException(ErrorCode.PARAM_INVALID, "page 必须从 1 开始");
        }
        if (size < 1 || size > 100) {
            throw new BizException(ErrorCode.PARAM_INVALID, "size 必须在 1~100");
        }

        try {
            if (productId != null) {
                ProductEntity product = productMapper.selectById(productId);
                if (product == null) {
                    throw new BizException(ErrorCode.NOT_FOUND, "商品不存在");
                }

                List<InventoryBatchSnapshotResponse> batches = listBatchesByProductId(productId);
                int totalQty = sumQtyAvailableAll(productId);
                int availableQty = sumAvailableQty(productId, LocalDate.now());

                InventorySnapshotItemResponse item = new InventorySnapshotItemResponse(
                        product.getId(),
                        product.getSku(),
                        product.getName(),
                        product.getUnit(),
                        product.getPrice() == null ? BigDecimal.ZERO : product.getPrice(),
                        fromDbProductStatus(product.getStatus()),
                        product.getSafetyStockQty() == null ? 0 : product.getSafetyStockQty(),
                        availableQty,
                        totalQty,
                        batches
                );
                return new PageResponse<>(1, 1, 1, List.of(item));
            }

            LambdaQueryWrapper<ProductEntity> qw = new LambdaQueryWrapper<>();
            if (keyword != null && !keyword.isBlank()) {
                qw.and(w -> w.like(ProductEntity::getName, keyword).or().like(ProductEntity::getSku, keyword));
            }
            applyProductSort(qw, sort);

            Page<ProductEntity> p = productMapper.selectPage(new Page<>(page, size), qw);
            List<ProductEntity> products = p.getRecords();
            if (products.isEmpty()) {
                return PageResponse.empty(page, size);
            }

            List<Long> productIds = products.stream().map(ProductEntity::getId).toList();
            LocalDate today = LocalDate.now();
            Map<Long, Integer> availableQtyMap = sumAvailableQtyMap(productIds, today);
            Map<Long, Integer> totalQtyMap = sumTotalQtyMap(productIds);

            List<InventorySnapshotItemResponse> items = new ArrayList<>(products.size());
            for (ProductEntity product : products) {
                long pid = product.getId();
                items.add(new InventorySnapshotItemResponse(
                        pid,
                        product.getSku(),
                        product.getName(),
                        product.getUnit(),
                        product.getPrice() == null ? BigDecimal.ZERO : product.getPrice(),
                        fromDbProductStatus(product.getStatus()),
                        product.getSafetyStockQty() == null ? 0 : product.getSafetyStockQty(),
                        availableQtyMap.getOrDefault(pid, 0),
                        totalQtyMap.getOrDefault(pid, 0),
                        List.of()
                ));
            }

            return new PageResponse<>(page, size, p.getTotal(), items);
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            log.debug("snapshot query failed", ex);
            throw ex;
        }
    }

    @Override
    public PageResponse<InventoryLedgerListItemResponse> ledgers(
            Long productId,
            Long batchId,
            String changeType,
            String sourceType,
            Instant eventFrom,
            Instant eventTo,
            int page,
            int size,
            List<String> sort
    ) {
        requireStoreManagerOrWarehouse();

        if (page < 1) {
            throw new BizException(ErrorCode.PARAM_INVALID, "page 必须从 1 开始");
        }
        if (size < 1 || size > 100) {
            throw new BizException(ErrorCode.PARAM_INVALID, "size 必须在 1~100");
        }

        try {
            LambdaQueryWrapper<InventoryLedgerEntity> qw = new LambdaQueryWrapper<>();
            if (productId != null) {
                qw.eq(InventoryLedgerEntity::getProductId, productId);
            }
            if (batchId != null) {
                qw.eq(InventoryLedgerEntity::getInventoryBatchId, batchId);
            }
            if (changeType != null && !changeType.isBlank()) {
                qw.eq(InventoryLedgerEntity::getChangeType, changeType);
            }
            if (sourceType != null && !sourceType.isBlank()) {
                qw.eq(InventoryLedgerEntity::getSourceType, sourceType);
            }
            if (eventFrom != null) {
                qw.ge(InventoryLedgerEntity::getEventTime, toLocalDateTime(eventFrom));
            }
            if (eventTo != null) {
                qw.le(InventoryLedgerEntity::getEventTime, toLocalDateTime(eventTo));
            }
            applyLedgerSort(qw, sort);

            Page<InventoryLedgerEntity> p = inventoryLedgerMapper.selectPage(new Page<>(page, size), qw);
            List<InventoryLedgerEntity> records = p.getRecords();
            if (records.isEmpty()) {
                return PageResponse.empty(page, size);
            }

            List<InventoryLedgerListItemResponse> items = new ArrayList<>(records.size());
            for (InventoryLedgerEntity e : records) {
                items.add(new InventoryLedgerListItemResponse(
                        e.getId(),
                        e.getProductId() == null ? 0L : e.getProductId(),
                        e.getInventoryBatchId() == null ? 0L : e.getInventoryBatchId(),
                        e.getChangeType(),
                        e.getChangeQty() == null ? 0 : e.getChangeQty(),
                        e.getQtyAfter() == null ? 0 : e.getQtyAfter(),
                        e.getSourceType(),
                        e.getSourceId() == null ? 0L : e.getSourceId(),
                        e.getEventTime() == null ? null : e.getEventTime().atZone(ZoneId.systemDefault()).toInstant()
                ));
            }

            return new PageResponse<>(page, size, p.getTotal(), items);
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            log.debug("ledger query failed", ex);
            throw ex;
        }
    }

    private void requireStoreManagerOrWarehouse() {
        AuthContext ctx = AuthContextHolder.getRequired();
        List<String> roles = ctx.roles();
        if (!roles.contains("STORE_MANAGER") && !roles.contains("WAREHOUSE")) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限：需要角色 STORE_MANAGER/WAREHOUSE");
        }
    }

    private List<InventoryBatchSnapshotResponse> listBatchesByProductId(long productId) {
        LambdaQueryWrapper<InventoryBatchEntity> qw = new LambdaQueryWrapper<InventoryBatchEntity>()
                .eq(InventoryBatchEntity::getProductId, productId)
                .orderByDesc(InventoryBatchEntity::getReceivedAt)
                .orderByDesc(InventoryBatchEntity::getId);
        List<InventoryBatchEntity> batches = inventoryBatchMapper.selectList(qw);
        if (batches.isEmpty()) {
            return List.of();
        }
        List<InventoryBatchSnapshotResponse> list = new ArrayList<>(batches.size());
        for (InventoryBatchEntity b : batches) {
            list.add(new InventoryBatchSnapshotResponse(
                    b.getId(),
                    b.getBatchNo(),
                    b.getExpiryDate(),
                    b.getQtyInitial() == null ? 0 : b.getQtyInitial(),
                    b.getQtyAvailable() == null ? 0 : b.getQtyAvailable(),
                    fromDbBatchStatus(b.getStatus()),
                    b.getReceivedAt() == null ? null : b.getReceivedAt().atZone(ZoneId.systemDefault()).toInstant()
            ));
        }
        return list;
    }

    private int sumQtyAvailableAll(long productId) {
        QueryWrapper<InventoryBatchEntity> qw = new QueryWrapper<InventoryBatchEntity>()
                .select("SUM(qty_available) AS totalQty")
                .eq("product_id", productId);
        List<Map<String, Object>> rows = inventoryBatchMapper.selectMaps(qw);
        if (rows.isEmpty()) {
            return 0;
        }
        Map<String, Object> row0 = rows.get(0);
        if (row0 == null) {
            return 0;
        }
        Object v = row0.get("totalQty");
        return v instanceof Number n ? n.intValue() : 0;
    }

    private int sumAvailableQty(long productId, LocalDate today) {
        Map<Long, Integer> map = sumAvailableQtyMap(List.of(productId), today);
        return map.getOrDefault(productId, 0);
    }

    private Map<Long, Integer> sumAvailableQtyMap(List<Long> productIds, LocalDate today) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }

        QueryWrapper<InventoryBatchEntity> qw = new QueryWrapper<InventoryBatchEntity>()
                .select("product_id", "SUM(qty_available) AS availableQty")
                .eq("status", DB_BATCH_STATUS_AVAILABLE)
                .gt("expiry_date", today)
                .in("product_id", productIds)
                .groupBy("product_id");

        List<Map<String, Object>> rows = inventoryBatchMapper.selectMaps(qw);
        Map<Long, Integer> map = new HashMap<>();
        for (Map<String, Object> row : rows) {
            if (row == null) {
                continue;
            }
            Object productIdObj = row.get("product_id");
            if (productIdObj == null) {
                productIdObj = row.get("productId");
            }
            Object qtyObj = row.get("availableQty");
            if (productIdObj instanceof Number pn) {
                long pid = pn.longValue();
                int qty = 0;
                if (qtyObj instanceof Number qn) {
                    qty = qn.intValue();
                }
                map.put(pid, qty);
            }
        }
        return map;
    }

    private Map<Long, Integer> sumTotalQtyMap(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }

        QueryWrapper<InventoryBatchEntity> qw = new QueryWrapper<InventoryBatchEntity>()
                .select("product_id", "SUM(qty_available) AS totalQty")
                .in("product_id", productIds)
                .groupBy("product_id");
        List<Map<String, Object>> rows = inventoryBatchMapper.selectMaps(qw);
        Map<Long, Integer> map = new HashMap<>();
        for (Map<String, Object> row : rows) {
            if (row == null) {
                continue;
            }
            Object productIdObj = row.get("product_id");
            if (productIdObj == null) {
                productIdObj = row.get("productId");
            }
            Object qtyObj = row.get("totalQty");
            if (productIdObj instanceof Number pn) {
                long pid = pn.longValue();
                int qty = 0;
                if (qtyObj instanceof Number qn) {
                    qty = qn.intValue();
                }
                map.put(pid, qty);
            }
        }
        return map;
    }

    private ProductStatus fromDbProductStatus(Integer status) {
        return status != null && status == DB_PRODUCT_STATUS_ON_SALE ? ProductStatus.ON_SALE : ProductStatus.OFF_SALE;
    }

    private String fromDbBatchStatus(Integer status) {
        if (status == null) {
            return "BLOCKED";
        }
        if (status == DB_BATCH_STATUS_AVAILABLE) {
            return "AVAILABLE";
        }
        if (status == DB_BATCH_STATUS_EXPIRED) {
            return "EXPIRED";
        }
        if (status == DB_BATCH_STATUS_BLOCKED) {
            return "BLOCKED";
        }
        return "BLOCKED";
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    private void applyProductSort(LambdaQueryWrapper<ProductEntity> qw, List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            qw.orderByDesc(ProductEntity::getId);
            return;
        }

        String field = sort.get(0).trim();
        boolean asc = true;

        if (sort.size() >= 2) {
            asc = !"desc".equalsIgnoreCase(sort.get(1).trim());
        } else if (field.contains(",")) {
            String[] parts = field.split(",");
            field = parts[0].trim();
            asc = parts.length < 2 || !"desc".equalsIgnoreCase(parts[1].trim());
        }

        field = field.replace("[", "").replace("\"", "");

        switch (field) {
            case "sku" -> qw.orderBy(true, asc, ProductEntity::getSku);
            case "name" -> qw.orderBy(true, asc, ProductEntity::getName);
            case "price" -> qw.orderBy(true, asc, ProductEntity::getPrice);
            default -> qw.orderBy(true, asc, ProductEntity::getId);
        }
    }

    private void applyLedgerSort(LambdaQueryWrapper<InventoryLedgerEntity> qw, List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            qw.orderByDesc(InventoryLedgerEntity::getEventTime).orderByDesc(InventoryLedgerEntity::getId);
            return;
        }

        String field = sort.get(0).trim();
        boolean asc = true;

        if (sort.size() >= 2) {
            asc = !"desc".equalsIgnoreCase(sort.get(1).trim());
        } else if (field.contains(",")) {
            String[] parts = field.split(",");
            field = parts[0].trim();
            asc = parts.length < 2 || !"desc".equalsIgnoreCase(parts[1].trim());
        }

        field = field.replace("[", "").replace("\"", "");

        switch (field) {
            case "eventTime" -> qw.orderBy(true, asc, InventoryLedgerEntity::getEventTime);
            default -> qw.orderBy(true, asc, InventoryLedgerEntity::getId);
        }
    }
}
