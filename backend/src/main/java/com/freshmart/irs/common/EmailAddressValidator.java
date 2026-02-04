package com.freshmart.irs.common;

import java.util.regex.Pattern;

/**
 * 邮箱格式校验工具：
 * - 仅做常规格式校验（user@domain.tld），不做域名/可达性校验
 */
public final class EmailAddressValidator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]{1,255}\\.[A-Za-z]{2,63}$");

    private EmailAddressValidator() {
    }

    public static String normalizeOrNull(String email) {
        if (email == null) {
            return null;
        }
        String normalized = email.trim();
        return normalized.isBlank() ? null : normalized;
    }

    public static void validateOrThrow(String email) {
        String normalized = normalizeOrNull(email);
        if (normalized == null) {
            return;
        }
        if (!EMAIL.matcher(normalized).matches()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "邮箱格式不正确");
        }
    }
}
