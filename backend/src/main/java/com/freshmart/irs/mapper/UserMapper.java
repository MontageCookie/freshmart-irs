package com.freshmart.irs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshmart.irs.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper。
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
