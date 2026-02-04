package com.freshmart.irs.service;

/**
 * 密码策略校验：
 * - 至少 8 位
 * - 必须同时包含字母和数字
 */
public interface PasswordPolicy {
    /**
     * 校验密码是否满足策略，不满足则抛出业务异常（40001）。
     *
     * @param password 明文密码
     */
    void validateOrThrow(String password);
}
