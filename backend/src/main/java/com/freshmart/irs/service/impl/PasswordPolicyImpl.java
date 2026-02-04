package com.freshmart.irs.service.impl;

import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.service.PasswordPolicy;
import org.springframework.stereotype.Component;

/**
 * 默认密码策略实现。
 */
@Component
public class PasswordPolicyImpl implements PasswordPolicy {
    private static final int MIN_LENGTH = 8;

    @Override
    public void validateOrThrow(String password) {
        if (password == null || password.isBlank()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "密码不能为空");
        }
        if (password.length() < MIN_LENGTH) {
            throw new BizException(ErrorCode.PARAM_INVALID, "密码至少8位，且包含字母和数字");
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        if (!hasLetter || !hasDigit) {
            throw new BizException(ErrorCode.PARAM_INVALID, "密码至少8位，且包含字母和数字");
        }
    }
}
