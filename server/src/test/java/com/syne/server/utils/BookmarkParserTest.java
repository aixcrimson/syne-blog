package com.syne.server.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 书签解析器测试
 */
@SpringBootTest
class BookmarkParserTest {

    @Test
    void testParseBookmarkFile() throws Exception {
        // 读取测试文件
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("bookmarks_2025_12_17.html");
        if (inputStream == null) {
            // 如果测试资源文件不存在，跳过测试
            System.out.println("测试文件不存在，跳过测试");
            return;
        }

        // 解析书签文件
        BookmarkParser.BookmarkParseResult result = BookmarkParser.parse(inputStream);

        // 验证解析结果
        assertTrue(result.getTotalBookmarks() > 0, "应该解析出书签");
        assertTrue(result.getFolderCount() > 0, "应该解析出文件夹");

        // 打印解析结果用于调试
        System.out.println("解析结果：");
        System.out.println("总书签数：" + result.getTotalBookmarks());
        System.out.println("文件夹数：" + result.getFolderCount());
        System.out.println("文件夹统计：");
        result.getFolderCounts().forEach((folder, count) -> {
            System.out.println("  " + folder + " : " + count + " 个书签");
        });

        // 验证特定文件夹
        assertTrue(result.getFolderCounts().containsKey("书签栏/learning"), "应该包含 learning 文件夹");
        assertTrue(result.getFolderCounts().containsKey("书签栏/tools"), "应该包含 tools 文件夹");
        assertTrue(result.getFolderCounts().containsKey("书签栏/tools/airport"), "应该包含 airport 子文件夹");

        // 验证书签的文件夹归属
        boolean foundLearningBookmark = result.getBookmarks().stream()
                .anyMatch(b -> "书签栏/learning".equals(b.getFolder()));
        assertTrue(foundLearningBookmark, "应该找到属于 learning 文件夹的书签");

        boolean foundAirportBookmark = result.getBookmarks().stream()
                .anyMatch(b -> "书签栏/tools/airport".equals(b.getFolder()));
        assertTrue(foundAirportBookmark, "应该找到属于 airport 子文件夹的书签");
    }
}