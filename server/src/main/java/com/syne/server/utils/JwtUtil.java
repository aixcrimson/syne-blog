package com.syne.server.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil{

    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:86400}")
    private Long  expiration;

    /**
     * 生成密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成token
     */
    public String generateToken(Long userId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try{
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        }catch (Exception e) {
            log.error("从Token中获取用户ID失败", e);
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try{
            Claims claims = getClaimsFromToken(token);
            return Long.valueOf(claims.get("userId").toString());
        }catch (Exception e) {
            log.error("从Token中获取用户名失败", e);
            return null;
        }
    }

    /**
     * 从Token中获取用户角色
     */
    public Integer getRoleFromToken(String token) {
        try{
            Claims claims = getClaimsFromToken(token);
            return Integer.valueOf(claims.get("role").toString());
        }catch (Exception e) {
            log.error("从Token中获取用户角色失败", e);
            return null;
        }
    }

    /**
     * 从Token中获取过期时间
     */
    public Date getExpirationFromToken(String token) {
        try{
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();
        }catch (Exception e) {
            log.error("从Token中获取过期时间失败", e);
            return null;
        }
    }

    /**
     * 从Token中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证Token是否过期
     */
    public Boolean isTokenExpired(String token) {
        try{
            Date expiration = getExpirationFromToken(token);
            return expiration != null && expiration.before(new Date());
        }catch(Exception e) {
            log.error("验证Token是否过期失败", e);
            return false;
        }
    }

    /**
     * 验证Token
     */
    public Boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return tokenUsername != null && tokenUsername.equals(username) && !isTokenExpired(token);
        }catch (Exception e) {
            log.error("验证Token失败", e);
            return false;
        }
    }

    /** 
     * 刷新Token
    */
    public String refreshToken(String token) {
        try{
            Claims claims = getClaimsFromToken(token);
            Long userId = Long.valueOf(claims.get("userId").toString());
            String username = claims.getSubject();
            Integer role = Integer.valueOf(claims.get("role").toString());

            return generateToken(userId, username, role);
        }catch(Exception e) {
            log.error("刷新Token失败", e);
            return null;
        }
    }
}