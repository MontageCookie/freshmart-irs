package com.freshmart.irs.service.impl;

import com.freshmart.irs.auth.AuthContext;
import com.freshmart.irs.auth.AuthContextHolder;
import com.freshmart.irs.common.TraceIdHolder;
import com.freshmart.irs.entity.AuditEventEntity;
import com.freshmart.irs.mapper.AuditEventMapper;
import com.freshmart.irs.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 审计服务：将关键业务动作落库到 audit_event 表。
 */
@Service
public class AuditServiceImpl implements AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    private final AuditEventMapper auditEventMapper;

    public AuditServiceImpl(AuditEventMapper auditEventMapper) {
        this.auditEventMapper = auditEventMapper;
    }

    @Override
    public void record(String eventType, String targetType, Long targetId, boolean success, String reason) {
        AuthContext actor = AuthContextHolder.getNullable();

        AuditEventEntity entity = new AuditEventEntity();
        entity.setEventType(eventType);
        entity.setActorUserId(actor == null ? null : actor.userId());
        entity.setTargetType(targetType);
        entity.setTargetId(targetId);
        entity.setSuccess(success ? 1 : 0);
        entity.setReason(reason);
        entity.setTraceId(TraceIdHolder.getOrCreate());

        try {
            auditEventMapper.insert(entity);
        } catch (Exception ex) {
            log.debug("audit_event insert failed, eventType={}, targetType={}, targetId={}, traceId={}",
                    eventType, targetType, targetId, TraceIdHolder.getOrCreate(), ex);
        }
    }
}
