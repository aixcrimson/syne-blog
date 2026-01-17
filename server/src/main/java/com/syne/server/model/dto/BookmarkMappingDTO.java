package com.syne.server.model.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 书签映射DTO
 * 用于导入时的数据传递
 *
 * @author syne
 * @since 2025-12-15
 */
@Data
public class BookmarkMappingDTO {

    /**
     * 文件夹与分类的映射关系
     */
    @NotNull(message = "映射关系不能为空")
    @Valid
    private List<FolderMappingDTO> mappings;

    /**
     * 要导入的书签列表
     */
    @NotNull(message = "书签列表不能为空")
    @Valid
    private List<BookmarkItemDTO> bookmarks;
}