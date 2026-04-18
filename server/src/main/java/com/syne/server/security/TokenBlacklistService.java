package com.syne.server.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String DEFAULT_PREFIX = "auth:blacklist:";

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${auth.token.blacklist-prefix:auth:blacklist:}")
    private String blacklistPrefix;

    @Value("${auth.token.blacklist-ttl-seconds:0}")
    private Long blacklistTtlSeconds;

    public boolean blacklist(String token, Date expiration) {
        if (!StringUtils.hasText(token) || expiration == null) {
            return false;
        }

        long ttlMillis = expiration.getTime() - System.currentTimeMillis();
        if (ttlMillis <= 0) {
            return false;
        }

        long ttlSeconds = Math.max(1, Duration.ofMillis(ttlMillis).toSeconds());
        if (blacklistTtlSeconds != null && blacklistTtlSeconds > 0) {
            ttlSeconds = Math.max(ttlSeconds, blacklistTtlSeconds);
        }

        stringRedisTemplate.opsForValue()
                .set(buildKey(token), "1", Duration.ofSeconds(ttlSeconds));
        return true;
    }

    public boolean isBlacklisted(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Boolean exists = stringRedisTemplate.hasKey(buildKey(token));
        return Boolean.TRUE.equals(exists);
    }

    private String buildKey(String token) {
        String prefix = StringUtils.hasText(blacklistPrefix) ? blacklistPrefix : DEFAULT_PREFIX;
        return prefix + token;
    }
}
