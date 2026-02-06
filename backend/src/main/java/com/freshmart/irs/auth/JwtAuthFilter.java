package com.freshmart.irs.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 鉴权过滤器：负责提取并校验 Bearer Token，写入线程上下文并做最小角色拦截。
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isAnonymousAllowed(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = resolveBearerToken(request);
            if (token == null) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorCode.UNAUTHORIZED, null);
                return;
            }

            AuthContext context;
            try {
                context = jwtService.verifyToContext(token);
            } catch (JWTVerificationException ex) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorCode.UNAUTHORIZED, null);
                return;
            }

            AuthContextHolder.set(context);

            if (isAdminOnlyEndpoint(request) && !context.roles().contains("ADMIN")) {
                writeError(response, HttpServletResponse.SC_FORBIDDEN, ErrorCode.FORBIDDEN, null);
                return;
            }

            filterChain.doFilter(request, response);
        } finally {
            AuthContextHolder.clear();
        }
    }

    private boolean isAnonymousAllowed(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.equals("/doc.html")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars")) {
            return true;
        }

        if ("POST".equals(method) && path.equals("/api/v1/auth/login")) {
            return true;
        }
        if ("POST".equals(method) && path.equals("/api/v1/auth/register")) {
            return true;
        }

        // return "GET".equals(method) && path.startsWith("/api/v1/products");
        return false;
    }

    private boolean isAdminOnlyEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.equals("/api/v1/roles")) {
            return true;
        }
        if (path.equals("/api/v1/users")) {
            return true;
        }
        if (path.matches("^/api/v1/users/\\d+$")) {
            return "GET".equals(method) || "DELETE".equals(method);
        }
        return path.matches("^/api/v1/users/\\d+/roles$") && "PUT".equals(method);
    }

    private String resolveBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isBlank()) {
            return null;
        }
        if (!authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        return token.isBlank() ? null : token;
    }

    private void writeError(HttpServletResponse response, int httpStatus, ErrorCode errorCode, String message)
            throws IOException {
        response.setStatus(httpStatus);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse<Object> body = ApiResponse.fail(errorCode, message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
