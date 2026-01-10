package com.syne.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.syne.server.security.JwtAuthenticationFilter;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置 Security 过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF (前后端分离项目通常禁用)
                .csrf(AbstractHttpConfigurer::disable)
                // 开启 CORS 跨域支持
                .cors(org.springframework.security.config.Customizer.withDefaults())
                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 放行认证相关接口
                        .requestMatchers("/auth/login", "/auth/refresh").permitAll()
                        // 放行用户端文章接口（允许匿名访问）
                        .requestMatchers(
                                "/auth/**",
                                "/articles",
                                "/articles/**",
                                "/categories",
                                "/navigations",
                                "/tags",
                                "/stats",
                                "/site/**",
                                "/comments",
                                "/comments/**"
                        ).permitAll()
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
                                "/admin/auth/login",
                                "/login.html"
                        ).permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    /**
     * CORS 配置
     */
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        // 允许所有来源（生产环境建议指定具体域名）
        configuration.addAllowedOriginPattern("*");
        // 允许所有请求头
        configuration.addAllowedHeader("*");
        // 允许所有请求方法 (GET, POST, PUT, DELETE, OPTIONS等)
        configuration.addAllowedMethod("*");
        // 允许携带凭证 (Cookie等)
        configuration.setAllowCredentials(true);
        // 暴露响应头
        configuration.addExposedHeader("Authorization");

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
