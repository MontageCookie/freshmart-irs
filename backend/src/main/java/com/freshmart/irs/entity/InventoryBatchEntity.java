package com.freshmart.irs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存批次实体，对应表：inventory_batch。
 */
@TableName("inventory_batch")
public class InventoryBatchEntity {
    /**
     * 批次 ID（主键，自增）。
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品 ID。
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 批次号（业务号）。
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 生产日期（可空）。
     */
    @TableField("production_date")
    private LocalDate productionDate;

    /**
     * 效期日期。
     */
    @TableField("expiry_date")
    private LocalDate expiryDate;

    /**
     * 批次初始数量。
     */
    @TableField("qty_initial")
    private Integer qtyInitial;

    /**
     * 批次当前可用数量。
     */
    @TableField("qty_available")
    private Integer qtyAvailable;

    /**
     * 批次状态：1=AVAILABLE,0=BLOCKED,2=EXPIRED。
     */
    private Integer status;

    /**
     * 入库时间。
     */
    @TableField("received_at")
    private LocalDateTime receivedAt;

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getQtyInitial() {
        return qtyInitial;
    }

    public void setQtyInitial(Integer qtyInitial) {
        this.qtyInitial = qtyInitial;
    }

    public Integer getQtyAvailable() {
        return qtyAvailable;
    }

    public void setQtyAvailable(Integer qtyAvailable) {
        this.qtyAvailable = qtyAvailable;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
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

