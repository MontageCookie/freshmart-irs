package com.freshmart.irs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 审计事件实体，对应表：audit_event（以 sql/01_schema_update_v1.sql 为准）。
 */
@TableName("audit_event")
public class AuditEventEntity {
    /**
     * 审计事件 ID（主键，自增）。
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 事件类型（如 AUTH_LOGIN/USER_UPDATE 等）。
     */
    @TableField("event_type")
    private String eventType;

    /**
     * 操作人用户 ID（匿名时可空）。
     */
    @TableField("actor_user_id")
    private Long actorUserId;

    /**
     * 目标资源类型（如 USER）。
     */
    @TableField("target_type")
    private String targetType;

    /**
     * 目标资源 ID（可空）。
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 是否成功：1=成功，0=失败。
     */
    @TableField("success")
    private Integer success;

    /**
     * 失败原因（可空）。
     */
    @TableField("reason")
    private String reason;

    /**
     * traceId，用于链路追踪排障。
     */
    @TableField("trace_id")
    private String traceId;

    /**
     * 创建时间。
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
