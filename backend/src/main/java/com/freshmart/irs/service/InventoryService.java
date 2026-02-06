package com.freshmart.irs.service;

import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.dto.inventory.InventoryLedgerListItemResponse;
import com.freshmart.irs.dto.inventory.InventorySnapshotItemResponse;

import java.time.Instant;
import java.util.List;

/**
 * 库存服务：提供库存快照与库存流水的只读查询能力。
 */
public interface InventoryService {
    /**
     * 查询库存快照（管理端）。
     *
     * @param productId 可选商品 ID（传入时返回该商品并包含批次明细）
     * @param keyword   关键字（商品名称/sku 模糊）
     * @param page      页码，从 1 开始
     * @param size      每页大小
     * @param sort      排序参数（sort=field,asc|desc，可重复传参）
     * @return 分页结果
     */
    PageResponse<InventorySnapshotItemResponse> snapshot(Long productId, String keyword, int page, int size, List<String> sort);

    /**
     * 查询库存流水（管理端）。
     *
     * @param productId  商品 ID
     * @param batchId    批次 ID
     * @param changeType 流水类型
     * @param sourceType 来源单据类型
     * @param eventFrom  发生时间起（ISO-8601）
     * @param eventTo    发生时间止（ISO-8601）
     * @param page       页码，从 1 开始
     * @param size       每页大小
     * @param sort       排序参数
     * @return 分页结果
     */
    PageResponse<InventoryLedgerListItemResponse> ledgers(
            Long productId,
            Long batchId,
            String changeType,
            String sourceType,
            Instant eventFrom,
            Instant eventTo,
            int page,
            int size,
            List<String> sort
    );
}

