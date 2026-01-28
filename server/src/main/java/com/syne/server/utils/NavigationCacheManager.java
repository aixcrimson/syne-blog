package com.syne.server.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syne.server.model.vo.NavigationCategoryWithSitesVO;
import com.syne.server.model.vo.NavigationSiteShowVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NavigationCacheManager {

    private static final String DEFAULT_PREFIX = "navigation:";
    private static final String KEY_SITES_ALL = "sites:all";
    private static final String KEY_CATEGORY_WITH_SITES = "categories:with-sites";

    private static final TypeReference<List<NavigationSiteShowVO>> SITE_SHOW_LIST_TYPE =
            new TypeReference<>() {};
    private static final TypeReference<List<NavigationCategoryWithSitesVO>> CATEGORY_WITH_SITES_LIST_TYPE =
            new TypeReference<>() {};

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${navigation.cache.key-prefix:navigation:}")
    private String cacheKeyPrefix;

    @Value("${navigation.cache.ttl-seconds:300}")
    private Long cacheTtlSeconds;

    public List<NavigationSiteShowVO> getAllSitesCache() {
        if (!isCacheEnabled()) {
            return null;
        }
        return readList(buildKey(KEY_SITES_ALL), SITE_SHOW_LIST_TYPE);
    }

    public void setAllSitesCache(List<NavigationSiteShowVO> data) {
        if (!isCacheEnabled()) {
            return;
        }
        writeList(buildKey(KEY_SITES_ALL), data);
    }

    public List<NavigationCategoryWithSitesVO> getAllCategoryWithSitesCache() {
        if (!isCacheEnabled()) {
            return null;
        }
        return readList(buildKey(KEY_CATEGORY_WITH_SITES), CATEGORY_WITH_SITES_LIST_TYPE);
    }

    public void setAllCategoryWithSitesCache(List<NavigationCategoryWithSitesVO> data) {
        if (!isCacheEnabled()) {
            return;
        }
        writeList(buildKey(KEY_CATEGORY_WITH_SITES), data);
    }

    public void invalidateAll() {
        if (!isCacheEnabled()) {
            return;
        }
        stringRedisTemplate.delete(buildKey(KEY_SITES_ALL));
        stringRedisTemplate.delete(buildKey(KEY_CATEGORY_WITH_SITES));
    }

    private boolean isCacheEnabled() {
        return cacheTtlSeconds != null && cacheTtlSeconds > 0;
    }

    private String buildKey(String suffix) {
        String prefix = StringUtils.hasText(cacheKeyPrefix) ? cacheKeyPrefix : DEFAULT_PREFIX;
        return prefix + suffix;
    }

    private <T> List<T> readList(String key, TypeReference<List<T>> typeReference) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (!StringUtils.hasText(json)) {
                return null;
            }
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.warn("Failed to read navigation cache key: {}", key, e);
            return null;
        }
    }

    private void writeList(String key, Object data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            stringRedisTemplate.opsForValue().set(key, json, Duration.ofSeconds(cacheTtlSeconds));
        } catch (Exception e) {
            log.warn("Failed to write navigation cache key: {}", key, e);
        }
    }
}
