package com.syne.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 * 暂时开放所有接口，后续根据需求添加认证授权
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置 Security 过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF (前后端分离项目通常禁用)
            .csrf(AbstractHttpConfigurer::disable)
            // 配置授权规则
            .authorizeHttpRequests(auth -> auth
                // 放行 Swagger 相关路径
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // 放行静态资源
                .requestMatchers(
                    "/",
                    "/static/**",
                    "/login",
                    "/login.html"
                ).permitAll()
                // 开放所有请求（开发阶段）
                .anyRequest().permitAll()
            );
        return http.build();
    }

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}