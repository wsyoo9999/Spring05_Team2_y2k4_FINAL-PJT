package com.multi.y2k4.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantContextInterceptor())
                .addPathPatterns("/**")        // 모든 요청에 적용
                .excludePathPatterns("/login", "/logout", "/addUser", "/alerts"); // 로그인/정적 리소스는 제외
    }
}
