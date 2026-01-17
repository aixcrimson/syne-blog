package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.model.entity.Article;
import com.syne.server.model.vo.StatsVO;

/**
 * 统计服务接口
 */
public interface StatsService extends IService<Article> {

    /**
     * 获取统计数据（文章数量，分类数量，文章浏览数量）
     */
    StatsVO getStats();
}