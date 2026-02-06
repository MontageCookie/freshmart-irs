package com.freshmart.irs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshmart.irs.entity.InventoryBatchEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存批次表 Mapper。
 */
@Mapper
public interface InventoryBatchMapper extends BaseMapper<InventoryBatchEntity> {
}

