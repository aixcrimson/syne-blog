package com.syne.server.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 文件夹映射DTO
 *
 * @author syne
 * @since 2025-12-15
 */
@Data
public class FolderMappingDTO {

    /**
     * 文件夹名称
     */
    @NotNull(message = "文件夹名称不能为空")
    private String folder;

    /**
     * 映射的分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 是否创建新分类（当categoryId为null时使用）
     */
    private Boolean createNew;

    /**
     * 新分类名称（当createNew为true时使用）
     */
    private String newCategoryName;

    /**
     * 新分类图标（当createNew为true时使用）
     */
    private String newCategoryIcon;
}