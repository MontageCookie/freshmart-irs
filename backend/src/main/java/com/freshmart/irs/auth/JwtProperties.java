package com.freshmart.irs.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
    private String secret;

    private long expiresSeconds;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiresSeconds() {
        return expiresSeconds;
    }

    public void setExpiresSeconds(long expiresSeconds) {
        this.expiresSeconds = expiresSeconds;
    }
}
