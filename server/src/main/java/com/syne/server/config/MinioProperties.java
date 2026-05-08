package com.syne.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * MinIO 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /**
     * MinIO 服务端点地址（用于 SDK 上传/删除等内部调用）
     */
    private String endpoint;

    /**
     * 对外公开访问 URL 前缀（用于生成入库的资源 URL）
     * 推荐配置为站点 HTTPS 域名，例如 https://cyneblog.top
     * 配合 nginx 的 location ^~ /{bucket}/ 反代到 MinIO，避免 HTTPS 页面加载 HTTP 资源时
     * 触发浏览器 Mixed Content 拦截。
     * 留空时回退到 endpoint。
     */
    private String publicUrl;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 允许上传的文件类型（逗号分隔）
     */
    private String allowedTypes;

    /**
     * 最大文件大小（MB）
     */
    private Integer maxSize = 10;

    /**
     * 获取允许的类型列表
     */
    public List<String> getAllowedTypeList() {
        if (allowedTypes == null || allowedTypes.isEmpty()) {
            return List.of("image/jpeg", "image/png", "image/gif", "image/webp");
        }
        return Arrays.asList(allowedTypes.split(","));
    }
}
