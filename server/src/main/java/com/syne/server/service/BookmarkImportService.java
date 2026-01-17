package com.syne.server.service;

import com.syne.server.model.dto.BookmarkMappingDTO;
import com.syne.server.model.dto.BookmarkPreviewDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 书签导入服务接口
 *
 * @author syne
 * @since 2025-12-15
 */
public interface BookmarkImportService {

    /**
     * 解析书签文件并生成预览数据
     *
     * @param file 书签文件
     * @return 预览数据
     * @throws IOException 文件读取异常
     */
    BookmarkPreviewDTO parseBookmarkFile(MultipartFile file) throws IOException;

    /**
     * 导入书签数据
     *
     * @param mappingDTO 映射和书签数据
     * @return 导入结果统计
     */
    ImportResult importBookmarks(BookmarkMappingDTO mappingDTO);

    /**
     * 导入结果
     */
    class ImportResult {
        private int successCount;
        private int skipCount;
        private int errorCount;
        private String errorMessage;

        public ImportResult(int successCount, int skipCount, int errorCount) {
            this.successCount = successCount;
            this.skipCount = skipCount;
            this.errorCount = errorCount;
        }

        public ImportResult(String errorMessage) {
            this.errorMessage = errorMessage;
            this.successCount = 0;
            this.skipCount = 0;
            this.errorCount = 0;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getSkipCount() {
            return skipCount;
        }

        public int getErrorCount() {
            return errorCount;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public boolean hasErrors() {
            return errorMessage != null || errorCount > 0;
        }
    }
}