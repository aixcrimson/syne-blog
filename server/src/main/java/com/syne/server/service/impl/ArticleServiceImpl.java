package com.syne.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.entity.Article;
import com.syne.server.entity.vo.ArticleListVO;
import com.syne.server.mapper.ArticleMapper;
import com.syne.server.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final ArticleMapper articleMapper;

    public PageResult<ArticleListVO> getArticleList(PageQuery pageQuery, Integer status) {
        // 查询文章列表
        List<ArticleListVO> list = articleMapper.selectArticleList(
                pageQuery.getOffset(),
                pageQuery.getPageSize(),
                status
        );

        // 处理标签字段：将都好分隔得字符串转为 List
        list.forEach(article -> {
            if(article.getTags() != null && !article.getTags().isEmpty()) {
                String tagsStr = article.getTags().get(0);
                if(tagsStr != null && !tagsStr.isEmpty()) {
                    article.setTags(Arrays.asList(tagsStr.split(",")));
                }
            }
        });

        // 查询总数
        Long total = articleMapper.countArticles(status);

        // 构建分页结果
        return PageResult.build(
                pageQuery.getPage(),
                pageQuery.getPageSize(),
                total,
                list
        );
    }
}
