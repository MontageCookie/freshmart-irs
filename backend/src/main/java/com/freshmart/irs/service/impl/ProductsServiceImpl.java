package com.freshmart.irs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshmart.irs.auth.AuthContext;
import com.freshmart.irs.auth.AuthContextHolder;
import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.dto.product.ProductDetailResponse;
import com.freshmart.irs.dto.product.ProductListItemResponse;
import com.freshmart.irs.dto.product.ProductUpsertRequest;
import com.freshmart.irs.entity.InventoryBatchEntity;
import com.freshmart.irs.entity.ProductEntity;
import com.freshmart.irs.enums.ProductStatus;
import com.freshmart.irs.mapper.InventoryBatchMapper;
import com.freshmart.irs.mapper.ProductMapper;
import com.freshmart.irs.service.ProductsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品服务实现：对齐“商品与库存模块”的冻结契约，提供商品查询与店长维护能力。
 */
@Service
public class ProductsServiceImpl implements ProductsService {
    private static final Logger log = LoggerFactory.getLogger(ProductsServiceImpl.class);

    private static final int DB_PRODUCT_STATUS_ON_SALE = 1;
    private static final int DB_PRODUCT_STATUS_OFF_SALE = 0;
    private static final int DB_BATCH_STATUS_AVAILABLE = 1;

    private final ProductMapper productMapper;
    private final InventoryBatchMapper inventoryBatchMapper;

    public ProductsServiceImpl(ProductMapper productMapper, InventoryBatchMapper inventoryBatchMapper) {
        this.productMapper = productMapper;
        this.inventoryBatchMapper = inventoryBatchMapper;
    }

    @Override
    public PageResponse<ProductListItemResponse> list(String keyword, ProductStatus status, int page, int size, List<String> sort) {
        if (page < 1) {
            throw new BizException(ErrorCode.PARAM_INVALID, "page 必须从 1 开始");
        }
        if (size < 1 || size > 100) {
            throw new BizException(ErrorCode.PARAM_INVALID, "size 必须在 1~100");
        }

        boolean allowManageView = isStoreManagerOrWarehouseOrAdmin();

        LambdaQueryWrapper<ProductEntity> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.and(w -> w.like(ProductEntity::getName, keyword).or().like(ProductEntity::getSku, keyword));
        }
        if (allowManageView) {
            if (status != null) {
                qw.eq(ProductEntity::getStatus, toDbStatus(status));
            }
        } else {
            qw.eq(ProductEntity::getStatus, DB_PRODUCT_STATUS_ON_SALE);
        }
        applySort(qw, sort);

        Page<ProductEntity> p = productMapper.selectPage(new Page<>(page, size), qw);
        List<ProductEntity> records = p.getRecords();
        if (records.isEmpty()) {
            return PageResponse.empty(page, size);
        }

        List<Long> productIds = records.stream().map(ProductEntity::getId).toList();
        Map<Long, Integer> availableQtyMap = getAvailableQtyMap(productIds, LocalDate.now());

        List<ProductListItemResponse> items = new ArrayList<>(records.size());
        for (ProductEntity e : records) {
            int availableQty = availableQtyMap.getOrDefault(e.getId(), 0);
            items.add(new ProductListItemResponse(
                    e.getId(),
                    e.getSku(),
                    e.getName(),
                    e.getUnit(),
                    e.getPrice(),
                    fromDbStatus(e.getStatus()),
                    availableQty
            ));
        }

        return new PageResponse<>(page, size, p.getTotal(), items);
    }

    @Override
    public ProductDetailResponse get(long id) {
        boolean allowManageView = isStoreManagerOrWarehouseOrAdmin();

        ProductEntity product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "商品不存在");
        }

        if (!allowManageView && (product.getStatus() == null || product.getStatus() != DB_PRODUCT_STATUS_ON_SALE)) {
            throw new BizException(ErrorCode.NOT_FOUND, "商品不存在");
        }

        int safetyStockQty = product.getSafetyStockQty() == null ? 0 : product.getSafetyStockQty();
        int availableQty = getAvailableQtyByProductId(id, LocalDate.now());

        return new ProductDetailResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getUnit(),
                product.getPrice(),
                safetyStockQty,
                fromDbStatus(product.getStatus()),
                availableQty
        );
    }

    @Override
    @Transactional
    public long create(ProductUpsertRequest request) {
        requireStoreManager();

        try {
            int safetyStockQty = request.safetyStockQty() == null ? 0 : request.safetyStockQty();
            if (safetyStockQty < 0) {
                throw new BizException(ErrorCode.PARAM_INVALID, "safetyStockQty 必须 >= 0");
            }

            Long exists = productMapper.selectCount(new LambdaQueryWrapper<ProductEntity>()
                    .eq(ProductEntity::getSku, request.sku()));
            if (exists != null && exists > 0) {
                throw new BizException(ErrorCode.PARAM_INVALID, "SKU 已存在");
            }

            ProductEntity e = new ProductEntity();
            e.setSku(request.sku());
            e.setName(request.name());
            e.setUnit(request.unit());
            e.setPrice(request.price());
            e.setSafetyStockQty(safetyStockQty);
            e.setStatus(toDbStatus(request.status() == null ? ProductStatus.ON_SALE : request.status()));
            productMapper.insert(e);
            return e.getId();
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            log.debug("create product failed", ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public long update(long id, ProductUpsertRequest request) {
        requireStoreManager();

        try {
            ProductEntity current = productMapper.selectById(id);
            if (current == null) {
                throw new BizException(ErrorCode.NOT_FOUND, "商品不存在");
            }

            if (request.safetyStockQty() != null && request.safetyStockQty() < 0) {
                throw new BizException(ErrorCode.PARAM_INVALID, "safetyStockQty 必须 >= 0");
            }

            if (request.sku() != null && !request.sku().equals(current.getSku())) {
                Long exists = productMapper.selectCount(new LambdaQueryWrapper<ProductEntity>()
                        .eq(ProductEntity::getSku, request.sku())
                        .ne(ProductEntity::getId, id));
                if (exists != null && exists > 0) {
                    throw new BizException(ErrorCode.PARAM_INVALID, "SKU 已存在");
                }
            }

            current.setSku(request.sku());
            current.setName(request.name());
            current.setUnit(request.unit());
            current.setPrice(request.price());
            if (request.safetyStockQty() != null) {
                current.setSafetyStockQty(request.safetyStockQty());
            }
            if (request.status() != null) {
                current.setStatus(toDbStatus(request.status()));
            }

            productMapper.updateById(current);
            return id;
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            log.debug("update product failed", ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public long offSale(long id) {
        requireStoreManager();

        try {
            ProductEntity current = productMapper.selectById(id);
            if (current == null) {
                throw new BizException(ErrorCode.NOT_FOUND, "商品不存在");
            }
            current.setStatus(DB_PRODUCT_STATUS_OFF_SALE);
            productMapper.updateById(current);
            return id;
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            log.debug("offSale product failed", ex);
            throw ex;
        }
    }

    private int getAvailableQtyByProductId(long productId, LocalDate today) {
        Map<Long, Integer> map = getAvailableQtyMap(List.of(productId), today);
        return map.getOrDefault(productId, 0);
    }

    private Map<Long, Integer> getAvailableQtyMap(List<Long> productIds, LocalDate today) {
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

    private boolean isStoreManagerOrWarehouseOrAdmin() {
        AuthContext ctx = AuthContextHolder.getNullable();
        if (ctx == null) {
            return false;
        }
        List<String> roles = ctx.roles();
        return roles.contains("STORE_MANAGER") || roles.contains("WAREHOUSE") || roles.contains("ADMIN");
    }

    private void requireStoreManager() {
        AuthContext ctx = AuthContextHolder.getRequired();
        if (!ctx.roles().contains("STORE_MANAGER")) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限：需要角色 STORE_MANAGER");
        }
    }

    private ProductStatus fromDbStatus(Integer status) {
        return status != null && status == DB_PRODUCT_STATUS_ON_SALE ? ProductStatus.ON_SALE : ProductStatus.OFF_SALE;
    }

    private int toDbStatus(ProductStatus status) {
        return status == ProductStatus.ON_SALE ? DB_PRODUCT_STATUS_ON_SALE : DB_PRODUCT_STATUS_OFF_SALE;
    }

    private void applySort(LambdaQueryWrapper<ProductEntity> qw, List<String> sort) {
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
            case "createdAt" -> qw.orderBy(true, asc, ProductEntity::getCreatedAt);
            case "updatedAt" -> qw.orderBy(true, asc, ProductEntity::getUpdatedAt);
            default -> qw.orderBy(true, asc, ProductEntity::getId);
        }
    }
}

