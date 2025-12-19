package com.syne.server.entity.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 书签导入请求DTO
 *
 * @author syne
 * @since 2025-12-15
 */
@Data
public class BookmarkImportDTO {

    /**
     * 书签列表
     */
    @NotNull(message = "书签列表不能为空")
    private List<BookmarkItemDTO> bookmarks;

    /**
     * 文件夹与分类的映射关系
     */
    @NotNull(message = "映射关系不能为空")
    private List<FolderMappingDTO> mappings;
}