package com.freshmart.irs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshmart.irs.entity.InventoryLedgerEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存流水表 Mapper。
 */
@Mapper
public interface InventoryLedgerMapper extends BaseMapper<InventoryLedgerEntity> {
}

