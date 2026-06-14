package com.d1.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final ServerApiKeyInterceptor serverApiKeyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 클라이언트 JWT 인증: /api/** (auth, server 제외)
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**", "/api/server/**");

        // 데디서버 API Key 인증: /api/server/**
        registry.addInterceptor(serverApiKeyInterceptor)
                .addPathPatterns("/api/server/**");
    }
}
