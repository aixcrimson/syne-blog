package com.syne.server.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadVO {

    /**
     * 文件访问 URL
     */
    private String url;

    /**
     * 文件名（存储后的名称）
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文件类型
     */
    private String contentType;
}
