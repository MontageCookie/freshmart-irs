package com.freshmart.irs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 库存流水实体，对应表：inventory_ledger。
 */
@TableName("inventory_ledger")
public class InventoryLedgerEntity {
    /**
     * 流水 ID（主键，自增）。
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品 ID。
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 库存批次 ID。
     */
    @TableField("inventory_batch_id")
    private Long inventoryBatchId;

    /**
     * 流水类型：STOCK_IN/SALE_POS/SALE_ONLINE/ADJUST。
     */
    @TableField("change_type")
    private String changeType;

    /**
     * 变更数量（入库为正，扣减为负）。
     */
    @TableField("change_qty")
    private Integer changeQty;

    /**
     * 变更后批次可用量快照。
     */
    @TableField("qty_after")
    private Integer qtyAfter;

    /**
     * 来源单据类型。
     */
    @TableField("source_type")
    private String sourceType;

    /**
     * 来源单据 ID。
     */
    @TableField("source_id")
    private Long sourceId;

    /**
     * 业务发生时间。
     */
    @TableField("event_time")
    private LocalDateTime eventTime;

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

    public Long getInventoryBatchId() {
        return inventoryBatchId;
    }

    public void setInventoryBatchId(Long inventoryBatchId) {
        this.inventoryBatchId = inventoryBatchId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public Integer getChangeQty() {
        return changeQty;
    }

    public void setChangeQty(Integer changeQty) {
        this.changeQty = changeQty;
    }

    public Integer getQtyAfter() {
        return qtyAfter;
    }

    public void setQtyAfter(Integer qtyAfter) {
        this.qtyAfter = qtyAfter;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
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

