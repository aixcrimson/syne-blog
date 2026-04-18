package com.syne.server.service;

import com.syne.server.config.MinioProperties;
import com.syne.server.exception.BusinessException;
import com.syne.server.model.vo.FileUploadVO;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * MinIO 文件服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 上传结果
     */
    public FileUploadVO uploadImage(MultipartFile file) {
        // 1. 校验文件
        validateFile(file);

        // 2. 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String fileName = generateFileName(originalName);

        // 3. 上传文件
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 4. 构建访问 URL
            String url = buildFileUrl(fileName);

            log.info("文件上传成功: {} -> {}", originalName, url);

            return FileUploadVO.builder()
                    .url(url)
                    .fileName(fileName)
                    .originalName(originalName)
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .build();

        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名（包含路径）
     */
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .build()
            );
            log.info("文件删除成功: {}", fileName);
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件临时访问 URL（适用于私有桶）
     *
     * @param fileName 文件名
     * @param expiry   过期时间（秒）
     * @return 临时访问 URL
     */
    public String getPresignedUrl(String fileName, int expiry) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .method(Method.GET)
                            .expiry(expiry)
                            .build()
            );
        } catch (Exception e) {
            log.error("获取预签名 URL 失败: {}", e.getMessage(), e);
            throw new BusinessException("获取文件访问链接失败");
        }
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        if (!minioProperties.getAllowedTypeList().contains(contentType)) {
            throw new BusinessException("不支持的文件类型: " + contentType);
        }

        // 校验文件大小
        long maxSize = minioProperties.getMaxSize() * 1024L * 1024L;
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小超过限制: " + minioProperties.getMaxSize() + "MB");
        }
    }

    /**
     * 生成唯一文件名
     * 格式: images/2025/01/uuid.ext
     */
    private String generateFileName(String originalName) {
        // 获取文件扩展名
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        // 按日期分目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

        // 生成 UUID 文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return String.format("images/%s/%s%s", datePath, uuid, ext);
    }

    /**
     * 构建文件访问 URL
     */
    private String buildFileUrl(String fileName) {
        String endpoint = minioProperties.getEndpoint();
        // 移除末尾的斜杠
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return String.format("%s/%s/%s", endpoint, minioProperties.getBucketName(), fileName);
    }
}
