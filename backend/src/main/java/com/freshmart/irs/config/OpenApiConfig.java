package com.freshmart.irs.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class OpenApiConfig {
    // 之前解决过的 Swagger Header 显示问题：将组件 ID 设为 Authorization
    public static final String SECURITY_SCHEME_BEARER = HttpHeaders.AUTHORIZATION;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Freshmart IRS API").version("v1"))
                .components(new Components().addSecuritySchemes(
                        SECURITY_SCHEME_BEARER,
                        new SecurityScheme()
                                .name(HttpHeaders.AUTHORIZATION)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)));
    }

    /**
     * 最高优先级的跨域配置，确保在所有业务 Filter (如 JwtAuthFilter) 之前执行
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许跨域携带 Cookie (Vue3 开发常用)
        config.setAllowCredentials(true); 
        // 允许的源，使用 Pattern 模式支持通配符且兼容 allowCredentials
        config.addAllowedOriginPattern("*"); 
        // 允许所有的请求头 (包括 Authorization, Content-Type 等)
        config.addAllowedHeader("*"); 
        // 允许所有的 HTTP 方法
        config.addAllowedMethod("*"); 
        // 预检请求 (OPTIONS) 缓存时间：1 小时
        config.setMaxAge(3600L); 

        source.registerCorsConfiguration("/**", config);
        
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        // 设置为最高优先级，由于 Spring Boot 内部过滤器顺序，它会跑在所有自定义 Filter 之前
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); 
        return bean;
    }
}