package com.freshmart.irs.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * JWT 服务：负责签发与校验 Token（不引入 Spring Security）。
 */
@Service
public class JwtService {
    private static final String ISSUER = "freshmart-irs";
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLES = "roles";

    private final JwtProperties properties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(properties.getSecret());
        this.verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
    }

    /**
     * 签发 Token。
     *
     * @param userId    用户 ID
     * @param username  用户名
     * @param roles     角色编码列表
     * @return JWT 字符串
     */
    public String issueToken(long userId, String username, List<String> roles) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getExpiresSeconds());
        return JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim(CLAIM_USER_ID, userId)
                .withClaim(CLAIM_USERNAME, username)
                .withClaim(CLAIM_ROLES, roles)
                .sign(algorithm);
    }

    /**
     * 获取 Token 过期时间（秒）。
     *
     * @return 过期秒数
     */
    public long getExpiresSeconds() {
        return properties.getExpiresSeconds();
    }

    /**
     * 校验 Token 并提取为认证上下文。
     *
     * @param token JWT 字符串（不包含 Bearer 前缀）
     * @return 认证上下文
     * @throws JWTVerificationException 校验失败
     */
    public AuthContext verifyToContext(String token) throws JWTVerificationException {
        DecodedJWT jwt = verify(token);
        long userId = jwt.getClaim(CLAIM_USER_ID).asLong();
        String username = jwt.getClaim(CLAIM_USERNAME).asString();
        List<String> roles = jwt.getClaim(CLAIM_ROLES).asList(String.class);
        return new AuthContext(userId, username, roles == null ? List.of() : roles);
    }

    /**
     * 校验 Token。
     *
     * @param token JWT 字符串
     * @return 解码后的 JWT
     * @throws JWTVerificationException 校验失败
     */
    public DecodedJWT verify(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }
}
