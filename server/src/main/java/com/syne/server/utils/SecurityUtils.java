package com.syne.server.utils;

import com.syne.server.security.LoginUser;
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

        // 从认证信息中获取用户id
        if (authentication.getPrincipal() instanceof LoginUser) {
            return ((LoginUser) authentication.getPrincipal()).getId();
        }

        // 如果上述方式获取不到，返回 null
        return null;
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