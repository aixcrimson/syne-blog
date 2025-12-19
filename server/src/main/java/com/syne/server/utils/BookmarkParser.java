package com.syne.server.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chrome书签解析器
 * 用于解析Chrome导出的HTML书签文件
 *
 * @author syne
 * @since 2025-12-15
 */
@Slf4j
public class BookmarkParser {

    /**
     * Chrome书签文件的DOCTYPE声明
     */
    private static final String CHROME_BOOKMARK_DOCTYPE = "NETSCAPE-Bookmark-file-1";

    /**
     * 解析Chrome书签文件
     *
     * @param inputStream 书签文件输入流
     * @return 解析结果，包含文件夹和书签信息
     * @throws IOException 文件解析异常
     */
    public static BookmarkParseResult parse(InputStream inputStream) throws IOException {
        // 解析HTML文档
        Document doc = Jsoup.parse(inputStream, StandardCharsets.UTF_8.name(), "");

        log.info("解析HTML文档完成，标题: {}, DOCTYPE: {}", doc.title(), doc.documentType());

        // 验证是否为Chrome书签文件
        if (!isChromeBookmarkFile(doc)) {
            log.error("书签文件验证失败，标题: {}, DOCTYPE: {}, H3元素数: {}, A元素数: {}",
                    doc.title(), doc.documentType(),
                    doc.select("dt > h3").size(),
                    doc.select("dt > a[href]").size());
            throw new IllegalArgumentException("不是有效的Chrome书签文件");
        }

        BookmarkParseResult result = new BookmarkParseResult();

        // 查找所有书签文件夹（DT>H3）
        Elements folderElements = doc.select("dt > h3");

        // 查找所有书签链接（DT>A）
        Elements linkElements = doc.select("dt > a[href]");

        // 构建文件夹路径映射
        Map<Element, String> folderPaths = buildFolderPaths(doc);

        // 解析书签
        for (Element link : linkElements) {
            String url = link.attr("href");
            String name = link.text();
            String folderPath = getBookmarkFolderPath(link, folderPaths);

            if (StrUtil.isNotBlank(url) && StrUtil.isNotBlank(name)) {
                BookmarkItem bookmark = new BookmarkItem();
                bookmark.setName(name.trim());
                bookmark.setUrl(url.trim());
                bookmark.setFolder(folderPath);

                result.addBookmark(bookmark);
                result.incrementFolderCount(folderPath);
            }
        }

        log.info("解析书签文件完成，共解析出 {} 个书签，{} 个文件夹",
                result.getTotalBookmarks(), result.getFolderCount());

        return result;
    }

    /**
     * 验证是否为Chrome书签文件
     */
    private static boolean isChromeBookmarkFile(Document doc) {
        // 检查文档标题
        String title = doc.title();
        log.debug("Document title: {}", title);

        if ("Bookmarks".equals(title)) {
            // 检查是否包含Chrome书签的典型结构
            boolean hasH3 = !doc.select("dt > h3").isEmpty();
            boolean hasA = !doc.select("dt > a[href]").isEmpty();
            log.debug("Has H3 elements: {}, Has A elements: {}", hasH3, hasA);

            // 如果有标题且有书签结构，认为是有效的
            return hasH3 || hasA;
        }

        // 也检查DOCTYPE声明（如果存在）
        if (doc.documentType() != null) {
            String docType = doc.documentType().toString();
            log.debug("DOCTYPE: {}", docType);
            if (docType.contains(CHROME_BOOKMARK_DOCTYPE)) {
                return true;
            }
        }

        // 检查是否有META标签指定了Content-Type
        Elements metaTags = doc.select("meta[http-equiv=Content-Type]");
        if (!metaTags.isEmpty()) {
            log.debug("Found meta Content-Type tag");
            return true;
        }

        return false;
    }

    /**
     * 构建文件夹路径映射
     */
    private static Map<Element, String> buildFolderPaths(Document doc) {
        Map<Element, String> folderPaths = new HashMap<>();

        // 查找body下的所有DL元素，包括隐藏的DL
        Elements allTopDlElements = doc.select("body > dl");

        // 为每个顶级DL递归构建路径
        for (Element dl : allTopDlElements) {
            buildFolderPathsRecursive(dl, "", folderPaths);
        }

        return folderPaths;
    }

    /**
     * 递归构建文件夹路径
     */
    private static void buildFolderPathsRecursive(Element dl, String parentPath,
                                                 Map<Element, String> folderPaths) {
        if (dl == null) return;

        // 获取当前DL下的所有DT
        Elements dtElements = dl.select("> dt");

        for (Element dt : dtElements) {
            // 检查是否是文件夹（H3标签）
            Element h3 = dt.selectFirst("> h3");
            if (h3 != null) {
                String folderName = h3.text();
                String currentPath = parentPath.isEmpty() ? folderName : parentPath + "/" + folderName;

                // 将文件夹路径存入映射
                folderPaths.put(dt, currentPath);

                // 递归处理子文件夹 - 获取dt内的dl元素（H3和DL是同一个DT的子元素）
                Element childDl = dt.selectFirst("> dl");
                if (childDl != null) {
                    buildFolderPathsRecursive(childDl, currentPath, folderPaths);
                }
            }
        }
    }

    /**
     * 获取书签所属的文件夹路径
     */
    private static String getBookmarkFolderPath(Element link, Map<Element, String> folderPaths) {
        // 找到包含该书签的DT元素
        Element bookmarkDt = link.parent();
        if (bookmarkDt == null) return "根目录";

        // 获取书签所在的DL元素
        Element parentDl = bookmarkDt.parent();
        if (parentDl == null || !"dl".equalsIgnoreCase(parentDl.tagName())) {
            return "根目录";
        }

        // 查找包含这个DL的DT（也就是文件夹）
        Element parentDt = findParentDT(parentDl);
        if (parentDt != null && folderPaths.containsKey(parentDt)) {
            return folderPaths.get(parentDt);
        }

        // 如果找不到，向父级继续查找
        return findParentFolderPathRecursive(parentDl, folderPaths);
    }

    /**
     * 递归向上查找文件夹路径
     */
    private static String findParentFolderPathRecursive(Element dl, Map<Element, String> folderPaths) {
        // 获取包含这个DL的更大范围的DL
        Element parent = dl.parent();
        if (parent == null) return "根目录";

        // 如果父元素是DT，找到它的父DL
        if ("dt".equalsIgnoreCase(parent.tagName())) {
            Element grandParent = parent.parent();
            if (grandParent != null && "dl".equalsIgnoreCase(grandParent.tagName())) {
                // 查找包含这个DT（parent）的DL的父文件夹
                Element greatParentDt = findParentDT(grandParent);
                if (greatParentDt != null && folderPaths.containsKey(greatParentDt)) {
                    return folderPaths.get(greatParentDt);
                }
                // 继续递归
                return findParentFolderPathRecursive(grandParent, folderPaths);
            }
        } else if ("dl".equalsIgnoreCase(parent.tagName())) {
            // 如果父元素是DL，直接递归查找它的父文件夹
            return findParentFolderPathRecursive(parent, folderPaths);
        }

        return "根目录";
    }

    /**
     * 查找父文件夹
     * 通过向上遍历DOM树，找到包含该书签的最近文件夹
     */
    private static Element findParentFolder(Element dt) {
        // 找到该DT所在的DL父元素
        Element parentDl = findParentDL(dt);
        if (parentDl == null) return null;

        // 在这个DL之前的同级DT中查找文件夹
        Element prevSibling = parentDl.previousElementSibling();
        if (prevSibling != null && "dt".equalsIgnoreCase(prevSibling.tagName())) {
            Element h3 = prevSibling.selectFirst("> h3");
            if (h3 != null) {
                return prevSibling;
            }
        }

        // 如果当前DL没有父文件夹，则递归向上查找
        // 需要找到包含这个DL的DT，然后继续向上查找
        Element parentDt = findParentDT(parentDl);
        if (parentDt != null) {
            return findParentFolder(parentDt);
        }

        return null;
    }

    /**
     * 查找包含指定DL的DT元素
     */
    private static Element findParentDT(Element dl) {
        Element current = dl;
        while (current != null) {
            Element parent = current.parent();
            if (parent != null && "dt".equalsIgnoreCase(parent.tagName())) {
                return parent;
            }
            current = parent;
            if (current != null && "body".equalsIgnoreCase(current.tagName())) {
                return null;
            }
        }
        return null;
    }

    /**
     * 查找元素最近的DL父元素
     */
    private static Element findParentDL(Element element) {
        Element current = element;
        while (current != null) {
            if ("dl".equalsIgnoreCase(current.tagName())) {
                return current;
            }
            current = current.parent();
            // 如果已经到达body，停止查找
            if (current != null && "body".equalsIgnoreCase(current.tagName())) {
                return null;
            }
        }
        return null;
    }

    /**
     * 书签解析结果
     */
    public static class BookmarkParseResult {
        private final List<BookmarkItem> bookmarks = new ArrayList<>();
        private final Map<String, Integer> folderCounts = new HashMap<>();

        public void addBookmark(BookmarkItem bookmark) {
            bookmarks.add(bookmark);
        }

        public void incrementFolderCount(String folder) {
            folderCounts.merge(folder, 1, Integer::sum);
        }

        public List<BookmarkItem> getBookmarks() {
            return bookmarks;
        }

        public Map<String, Integer> getFolderCounts() {
            return folderCounts;
        }

        public int getTotalBookmarks() {
            return bookmarks.size();
        }

        public int getFolderCount() {
            return folderCounts.size();
        }
    }

    /**
     * 书签项
     */
    public static class BookmarkItem {
        private String name;
        private String url;
        private String folder;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }
    }
}