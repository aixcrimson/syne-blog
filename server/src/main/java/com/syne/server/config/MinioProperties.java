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
     * MinIO 服务端点地址
     */
    private String endpoint;

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
