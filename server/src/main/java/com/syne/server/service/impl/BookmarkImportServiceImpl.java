package com.syne.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.syne.server.model.dto.*;
import com.syne.server.model.entity.NavigationCategory;
import com.syne.server.model.entity.NavigationSite;
import com.syne.server.service.BookmarkImportService;
import com.syne.server.service.NavigationCategoryService;
import com.syne.server.service.NavigationSiteService;
import com.syne.server.utils.BookmarkParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 书签导入服务实现
 *
 * @author syne
 * @since 2025-12-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkImportServiceImpl implements BookmarkImportService {

    private final NavigationCategoryService categoryService;
    private final NavigationSiteService siteService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public BookmarkPreviewDTO parseBookmarkFile(MultipartFile file) throws IOException {
        // 验证文件
        validateFile(file);

        // 解析书签文件
        BookmarkParser.BookmarkParseResult parseResult;
        try {
            parseResult = BookmarkParser.parse(file.getInputStream());
        } catch (Exception e) {
            log.error("解析书签文件失败", e);
            throw new IllegalArgumentException("书签文件格式错误：" + e.getMessage());
        }

        // 转换为DTO
        BookmarkPreviewDTO previewDTO = new BookmarkPreviewDTO();
        previewDTO.setTotalBookmarks(parseResult.getTotalBookmarks());
        previewDTO.setTotalFolders(parseResult.getFolderCount());

        // 转换文件夹统计
        List<BookmarkPreviewDTO.FolderStatsDTO> categories = parseResult.getFolderCounts().entrySet().stream()
                .map(entry -> {
                    BookmarkPreviewDTO.FolderStatsDTO stats = new BookmarkPreviewDTO.FolderStatsDTO();
                    stats.setName(entry.getKey());
                    stats.setPath(entry.getKey());
                    stats.setCount(entry.getValue());
                    return stats;
                })
                .sorted(Comparator.comparing(BookmarkPreviewDTO.FolderStatsDTO::getName))
                .collect(Collectors.toList());
        previewDTO.setCategories(categories);

        // 转换书签列表
        List<BookmarkItemDTO> bookmarks = parseResult.getBookmarks().stream()
                .map(item -> {
                    BookmarkItemDTO dto = new BookmarkItemDTO();
                    dto.setName(item.getName());
                    dto.setUrl(item.getUrl());
                    dto.setFolder(item.getFolder());
                    return dto;
                })
                .collect(Collectors.toList());
        previewDTO.setBookmarks(bookmarks);

        return previewDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResult importBookmarks(BookmarkMappingDTO mappingDTO) {
        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        try {
            // 构建文件夹到分类的映射
            Map<String, Long> folderToCategoryMap = new HashMap<>();
            List<NavigationCategory> newCategories = new ArrayList<>();

            // 处理映射关系
            for (FolderMappingDTO mapping : mappingDTO.getMappings()) {
                if (Boolean.TRUE.equals(mapping.getCreateNew())) {
                    // 创建新分类
                    NavigationCategory category = new NavigationCategory();
                    category.setName(mapping.getNewCategoryName());
                    category.setSortOrder((int) (categoryService.count() + newCategories.size()));


                    newCategories.add(category);
                } else {
                    // 使用现有分类
                    folderToCategoryMap.put(mapping.getFolder(), mapping.getCategoryId());
                }
            }

            // 批量保存新分类
            if (!newCategories.isEmpty()) {
                for (NavigationCategory category : newCategories) {
                    NavigationCategoryDTO dto = new NavigationCategoryDTO();
                    dto.setName(category.getName());
                    dto.setSortOrder(category.getSortOrder());
                    NavigationCategory created = categoryService.createNavigationCategory(dto);
                    folderToCategoryMap.put(created.getName(), created.getId());
                }
            }

            // 获取所有现有站点的URL（用于检测重复）
            Set<String> existingUrls = siteService.listAllSites().stream()
                    .map(NavigationSite::getUrl)
                    .collect(Collectors.toSet());

            // 获取所有分类
            Map<Long, NavigationCategory> categoryMap = categoryService.listAllCategories().stream()
                    .collect(Collectors.toMap(NavigationCategory::getId, c -> c));

            // 准备要创建的站点列表
            List<NavigationSite> sitesToCreate = new ArrayList<>();
            Map<Long, Integer> categorySiteCount = new HashMap<>();

            // 处理书签
            for (BookmarkItemDTO bookmark : mappingDTO.getBookmarks()) {
                try {
                    // 验证URL
                    if (!isValidUrl(bookmark.getUrl())) {
                        log.warn("跳过无效URL: {}", bookmark.getUrl());
                        skipCount++;
                        continue;
                    }

                    // 检查重复
                    if (existingUrls.contains(bookmark.getUrl())) {
                        log.info("跳过重复书签: {}", bookmark.getUrl());
                        skipCount++;
                        continue;
                    }

                    // 获取分类ID
                    Long categoryId = folderToCategoryMap.get(bookmark.getFolder());
                    if (categoryId == null) {
                        log.warn("找不到对应分类，跳过书签: {} - {}", bookmark.getFolder(), bookmark.getName());
                        skipCount++;
                        continue;
                    }

                    // 创建站点
                    NavigationSite site = new NavigationSite();
                    site.setCategoryId(categoryId);
                    site.setName(StrUtil.sub(bookmark.getName(), 0, 100)); // 限制长度
                    site.setUrl(bookmark.getUrl());
                    site.setDescription(bookmark.getDescription());
                    site.setSortOrder(categorySiteCount.getOrDefault(categoryId, 0));

                    sitesToCreate.add(site);
                    existingUrls.add(bookmark.getUrl()); // 添加到已存在集合，防止本次导入重复

                    // 更新分类站点计数
                    categorySiteCount.merge(categoryId, 1, Integer::sum);
                    successCount++;

                } catch (Exception e) {
                    log.error("处理书签时出错: {}", bookmark.getName(), e);
                    errorCount++;
                }
            }

            // 批量保存站点
            if (!sitesToCreate.isEmpty()) {
                for (NavigationSite site : sitesToCreate) {
                    NavigationSiteDTO dto = new NavigationSiteDTO();
                    dto.setCategoryId(site.getCategoryId());
                    dto.setName(site.getName());
                    dto.setDescription(site.getDescription());
                    dto.setUrl(site.getUrl());
                    dto.setSortOrder(site.getSortOrder());
                    siteService.createNavigationSite(dto);
                }
            }

            log.info("书签导入完成：成功 {} 个，跳过 {} 个，错误 {} 个", successCount, skipCount, errorCount);
            return new ImportResult(successCount, skipCount, errorCount);

        } catch (Exception e) {
            log.error("导入书签失败", e);
            return new ImportResult("导入失败：" + e.getMessage());
        }
    }

    /**
     * 验证上传文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的文件");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        String fileName = file.getOriginalFilename();
        if (StrUtil.isBlank(fileName) || !fileName.toLowerCase().endsWith(".html")) {
            throw new IllegalArgumentException("只支持HTML格式的书签文件");
        }
    }

    /**
     * 验证URL格式
     */
    private boolean isValidUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return false;
        }

        return url.startsWith("http://") || url.startsWith("https://");
    }
}