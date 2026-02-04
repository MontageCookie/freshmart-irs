package com.freshmart.irs.common;

import java.util.regex.Pattern;

/**
 * 手机号校验工具：
 * - 中国大陆 11 位手机号：以 1 开头，第二位 3~9，其余为数字
 */
public final class PhoneNumberValidator {
    private static final Pattern CHINA_MAINLAND_11 = Pattern.compile("^1[3-9]\\d{9}$");

    private PhoneNumberValidator() {
    }

    public static String normalizeOrNull(String phone) {
        if (phone == null) {
            return null;
        }
        String normalized = phone.trim();
        return normalized.isBlank() ? null : normalized;
    }

    public static void validateChinaMainland11OrThrow(String phone) {
        String normalized = normalizeOrNull(phone);
        if (normalized == null) {
            return;
        }
        if (!CHINA_MAINLAND_11.matcher(normalized).matches()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "手机号格式不正确（需中国大陆11位）");
        }
    }
}
