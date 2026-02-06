package com.freshmart.irs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体，对应表：product。
 */
@TableName("product")
public class ProductEntity {
    /**
     * 商品 ID（主键，自增）。
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * SKU（唯一）。
     */
    private String sku;

    /**
     * 商品名称。
     */
    private String name;

    /**
     * 计量单位（如 kg/盒/袋）。
     */
    private String unit;

    /**
     * 销售价（单价）。
     */
    private BigDecimal price;

    /**
     * 安全库存阈值。
     */
    @TableField("safety_stock_qty")
    private Integer safetyStockQty;

    /**
     * 商品状态：1=ON_SALE,0=OFF_SALE。
     */
    private Integer status;

    /**
     * 创建时间。
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getSafetyStockQty() {
        return safetyStockQty;
    }

    public void setSafetyStockQty(Integer safetyStockQty) {
        this.safetyStockQty = safetyStockQty;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

