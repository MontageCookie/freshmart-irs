package com.freshmart.irs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshmart.irs.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-角色绑定表 Mapper。
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {
}
