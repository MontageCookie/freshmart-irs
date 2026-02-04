package com.freshmart.irs.service;

/**
 * 审计服务：将关键业务动作落库到 audit_event 表。
 */
public interface AuditService {
    /**
     * 记录审计事件。
     *
     * @param eventType   事件类型
     * @param targetType  目标类型
     * @param targetId    目标 ID（可空）
     * @param success     是否成功
     * @param reason      失败原因（可空）
     */
    void record(String eventType, String targetType, Long targetId, boolean success, String reason);
}
