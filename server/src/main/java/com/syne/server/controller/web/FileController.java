package com.syne.server.controller.web;

import com.syne.server.common.Result;
import com.syne.server.model.vo.FileUploadVO;
import com.syne.server.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}
