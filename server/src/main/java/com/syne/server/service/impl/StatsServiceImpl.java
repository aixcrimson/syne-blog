package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.model.entity.Article;
import com.syne.server.model.vo.StatsVO;
import com.syne.server.mapper.ArticleMapper;
import com.syne.server.mapper.CategoryMapper;
import com.syne.server.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl extends ServiceImpl<ArticleMapper, Article> implements StatsService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public StatsVO getStats(){
        StatsVO vo = new StatsVO();

        // 1.获取文章总数
        LambdaQueryWrapper<Article> articleQuery = new LambdaQueryWrapper<>();
        articleQuery.eq(Article::getStatus, 1); // 文章状态为已发布
        vo.setTotalArticles(articleMapper.selectCount(articleQuery));

        // 2. 获取分类总数
        vo.setTotalCategories(categoryMapper.selectCount(null));

        // 3.获取文章浏览总数
        List<Article> articles = articleMapper.selectList(null);
        vo.setTotalViews(articles.stream().mapToLong(a -> a.getViews() != null ? a.getViews() : 0).sum());

        return vo;
    }
}