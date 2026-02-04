package com.freshmart.irs.auth;

public final class AuthContextHolder {
    private static final ThreadLocal<AuthContext> CONTEXT = new ThreadLocal<>();

    private AuthContextHolder() {
    }

    public static void set(AuthContext context) {
        CONTEXT.set(context);
    }

    public static AuthContext getRequired() {
        AuthContext ctx = CONTEXT.get();
        if (ctx == null) {
            throw new IllegalStateException("AuthContext is missing");
        }
        return ctx;
    }

    public static AuthContext getNullable() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
