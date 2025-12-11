package com.syne.server.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全工具类
 * 用于获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户id
     * @return 用户id，未登录返回null
     */
    public static Long getCurrentUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // 如果是匿名用户，返回null
        if (authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        // 从认证信息中获取用户名
        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        if(username == null) {
            return null;
        }

        // TODO: 根据用户名查询用户ID
        // 这里需要注入UserService来查询用户ID
        // 由于在MetaObjectHandler中无法直接注入Service，建议使用以下方案之一：
        // 1. 使用SpringContextHolder工具类获取Bean
        // 2. 使用ThreadLocal在请求开始时存储用户信息
        // 3. 使用JWT解析获取用户ID

        // 暂时返回固定值，实际使用时需要替换为真实的用户ID获取逻辑
        return 1L;
    }

    /**
     * 获取当前登录用户名
     * @return 用户名，未登录返回null
     */
    public static String getCurrentUsername() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // 从认证信息中获取用户名
        if (authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }

        return null;
    }

    /**
     * 判断当前用户是否已登录
     * @return true 已登录，false 未登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }
}