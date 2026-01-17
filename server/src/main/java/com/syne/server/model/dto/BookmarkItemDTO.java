package com.syne.server.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 书签项DTO
 *
 * @author syne
 * @since 2025-12-15
 */
@Data
public class BookmarkItemDTO {

    /**
     * 书签名称
     */
    @NotBlank(message = "书签名称不能为空")
    private String name;

    /**
     * 书签URL
     */
    @NotBlank(message = "书签URL不能为空")
    private String url;

    /**
     * 所属文件夹
     */
    private String folder;

    /**
     * 书签描述
     */
    private String description;
}