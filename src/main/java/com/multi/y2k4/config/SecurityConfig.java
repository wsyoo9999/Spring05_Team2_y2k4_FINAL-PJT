package com.multi.y2k4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. 암호화 인코더 빈 등록 (UserService에서 주입받아 사용됨)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. HTTP 보안 설정 (기존 로직 방해 안 되게 설정)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보안 비활성화 (기존 AJAX 통신 등을 위해)
                .csrf(AbstractHttpConfigurer::disable)

                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 모든 요청 허용 (로그인 체크는 이미 Interceptor에서 하고 있으므로 여기선 품)
                        .anyRequest().permitAll()
                )

                // 스프링 시큐리티 기본 로그인 페이지 비활성화 (직접 만든 login.html 사용)
                .formLogin(AbstractHttpConfigurer::disable)

                // 스프링 시큐리티 기본 로그아웃 비활성화 (직접 만든 로그아웃 사용)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
}