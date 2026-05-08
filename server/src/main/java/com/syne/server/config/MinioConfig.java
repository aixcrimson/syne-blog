package com.syne.server.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperties minioProperties;

    /**
     * 创建 MinioClient Bean
     */
    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();

        // 初始化时检查并创建默认桶与封面图库桶
        initBucket(client, minioProperties.getBucketName());
        String coverBucket = minioProperties.getCoverBucket();
        if (coverBucket != null && !coverBucket.isBlank()) {
            initBucket(client, coverBucket);
        }

        return client;
    }

    /**
     * 初始化存储桶（如果不存在则创建并设为公开读）
     */
    private void initBucket(MinioClient client, String bucketName) {
        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!exists) {
                // 创建存储桶
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("MinIO bucket '{}' 创建成功", bucketName);

                // 设置公开读取策略（图片需要公开访问）
                String policy = buildPublicReadPolicy(bucketName);
                client.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policy)
                                .build()
                );
                log.info("MinIO bucket '{}' 已设置为公开读取", bucketName);
            } else {
                log.info("MinIO bucket '{}' 已存在", bucketName);
            }
        } catch (Exception e) {
            log.error("MinIO bucket '{}' 初始化失败: {}", bucketName, e.getMessage());
            throw new RuntimeException("MinIO 初始化失败: " + bucketName, e);
        }
    }

    /**
     * 构建公开读取策略 JSON
     */
    private String buildPublicReadPolicy(String bucketName) {
        return """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": {"AWS": ["*"]},
                            "Action": ["s3:GetObject"],
                            "Resource": ["arn:aws:s3:::%s/*"]
                        }
                    ]
                }
                """.formatted(bucketName);
    }
}
