package com.syne.server.controller.web;

import com.syne.server.common.Result;
import com.syne.server.model.vo.FileUploadVO;
import com.syne.server.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

/**
 * 文件上传控制器
 */
@Tag(name = "用户端文件管理", description = "用户端文件上传相关接口")
@RestController("webFileController")
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;

    /**
     * 上传图片
     */
    @Operation(summary = "上传图片")
    @PostMapping("/upload/image")
    public Result<FileUploadVO> uploadImage(@RequestParam("file") MultipartFile file) {
        FileUploadVO result = minioService.uploadImage(file);
        return Result.success(result);
    }

    /**
     * 随机封面图（302 重定向到真实图片地址，模仿 https://www.loliapi.com/acg/ 行为）
     * 图片来源于独立的封面图库桶（minio.cover-bucket，默认 syne-cover），由运营方手动上传维护。
     */
    @Operation(summary = "随机封面图（302 重定向到真实图片地址）")
    @GetMapping("/cover/random")
    public ResponseEntity<Void> randomCover() {
        String url = minioService.pickRandomCoverUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        // 不缓存，确保每次都拿到不同图片
        headers.setCacheControl("no-store");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
