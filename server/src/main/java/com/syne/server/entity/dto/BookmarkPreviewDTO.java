package com.syne.server.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 书签预览响应DTO
 *
 * @author syne
 * @since 2025-12-15
 */
@Data
public class BookmarkPreviewDTO {

    /**
     * 文件夹统计信息
     */
    private List<FolderStatsDTO> categories;

    /**
     * 书签列表
     */
    private List<BookmarkItemDTO> bookmarks;

    /**
     * 书签总数
     */
    private Integer totalBookmarks;

    /**
     * 文件夹总数
     */
    private Integer totalFolders;

    /**
     * 文件夹统计DTO
     */
    @Data
    public static class FolderStatsDTO {
        /**
         * 文件夹名称
         */
        private String name;

        /**
         * 文件夹路径
         */
        private String path;

        /**
         * 书签数量
         */
        private Integer count;
    }
}