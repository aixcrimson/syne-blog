package com.syne.server.service;

import com.syne.server.config.MinioProperties;
import com.syne.server.exception.BusinessException;
import com.syne.server.model.vo.FileUploadVO;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
     * 构建默认桶下的文件访问 URL
     */
    private String buildFileUrl(String fileName) {
        return buildFileUrl(minioProperties.getBucketName(), fileName);
    }

    /**
     * 构建指定桶下的文件访问 URL
     * 优先使用 minio.public-url（站点 HTTPS 域名），避免 HTTPS 页面加载 HTTP 资源时被浏览器以 Mixed Content 拦截；
     * 未配置时回退到 minio.endpoint（仅适用于纯 HTTP 场景或开发环境）。
     */
    private String buildFileUrl(String bucket, String fileName) {
        String base = minioProperties.getPublicUrl();
        if (base == null || base.isBlank()) {
            base = minioProperties.getEndpoint();
        }
        // 移除末尾的斜杠
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return String.format("%s/%s/%s", base, bucket, fileName);
    }

    /**
     * 从图库中挑选一张图片，返回其公网访问 URL
     * 模仿 https://www.loliapi.com/acg/ 的随机图能力，由调用方以 302 重定向给客户端
     */
    public String pickRandomCoverUrl(String type) {
        String bucket = minioProperties.getCoverBucket();
        if (bucket == null || bucket.isBlank()) {
            throw new BusinessException("未配置图库存储桶");
        }

        String prefix = (type != null && !type.isBlank()) ? type + "/" : "";

        List<String> objectKeys = new ArrayList<>();
        try {
            Iterable<io.minio.Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(prefix)
                            .recursive(true)
                            .build()
            );
            for (io.minio.Result<Item> result : results) {
                Item item = result.get();
                if (item == null || item.isDir()) {
                    continue;
                }
                String name = item.objectName();
                if (name == null || name.isBlank() || name.endsWith("/")) {
                    continue;
                }
                objectKeys.add(name);
            }
        } catch (Exception e) {
            log.error("枚举图库失败: {}", e.getMessage(), e);
            throw new BusinessException("获取图库失败: " + e.getMessage());
        }

        if (objectKeys.isEmpty()) {
            throw new BusinessException("图库为空，请先上传图片 (" + type + ")");
        }

        String pick = objectKeys.get(ThreadLocalRandom.current().nextInt(objectKeys.size()));
        return buildFileUrl(bucket, pick);
    }

    /**
     * 上传图片到图库（syne-cover）
     *
     * @param file 图片文件（预期为前端已处理好的 webp）
     * @param type 类型（pc 或 mobile）
     * @return 上传结果
     */
    public FileUploadVO uploadCover(MultipartFile file, String type) {
        String bucket = minioProperties.getCoverBucket();
        if (bucket == null || bucket.isBlank()) {
            throw new BusinessException("未配置图库存储桶");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        String ext = ".webp";
        // 随机生成 5 位数，如 img12345.webp
        String uuid = "img" + ThreadLocalRandom.current().nextInt(10000, 99999);
        String fileName = type + "/" + uuid + ext;

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType("image/webp")
                            .build()
            );

            String url = buildFileUrl(bucket, fileName);
            log.info("图库文件上传成功: {} -> {}", fileName, url);

            return FileUploadVO.builder()
                    .url(url)
                    .fileName(fileName)
                    .originalName(file.getOriginalFilename())
                    .size(file.getSize())
                    .contentType("image/webp")
                    .build();

        } catch (Exception e) {
            log.error("图库文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException("图库文件上传失败: " + e.getMessage());
        }
    }
    /**
     * 批量上传图片到图库（syne-cover）
     *
     * @param files 图片文件数组（预期为前端已处理好的 webp）
     * @param type  类型（pc 或 mobile）
     * @return 上传结果列表
     */
    public List<FileUploadVO> uploadCoverBatch(MultipartFile[] files, String type) {
        if (files == null || files.length == 0) {
            throw new BusinessException("请选择要上传的文件");
        }

        List<FileUploadVO> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(uploadCover(file, type));
        }
        return results;
    }
}
