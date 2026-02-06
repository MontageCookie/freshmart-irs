package com.freshmart.irs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshmart.irs.entity.ProductEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品表 Mapper。
 */
@Mapper
public interface ProductMapper extends BaseMapper<ProductEntity> {
}

