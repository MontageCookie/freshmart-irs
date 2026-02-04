package com.freshmart.irs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshmart.irs.entity.AuditEventEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计事件表 Mapper。
 */
@Mapper
public interface AuditEventMapper extends BaseMapper<AuditEventEntity> {
}
