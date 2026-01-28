package com.syne.server.controller.admin;

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
@Tag(name = "文件管理", description = "文件上传相关接口")
@RestController
@RequestMapping("/admin/file")
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
     * 删除文件
     */
    @Operation(summary = "删除文件")
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam("fileName") String fileName) {
        minioService.deleteFile(fileName);
        return Result.success();
    }
}
