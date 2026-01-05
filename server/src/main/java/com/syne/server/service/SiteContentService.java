package com.syne.server.service;

import com.syne.server.common.Result;
import com.syne.server.entity.Notice;
import com.syne.server.entity.Project;
import com.syne.server.entity.Skill;
import com.syne.server.entity.Timeline;

import java.util.List;

public interface SiteContentService {

    /**
     * 获取公告列表
     * @return 列表
     */
    Result<List<Notice>> getNotices();

    /**
     * 获取技能列表
     * @return 列表
     */
    Result<List<Skill>> getSkills();

    /**
     * 获取精选项目列表
     * @return 列表
     */
    Result<List<Project>> getFeaturedProjects();

    /**
     * 获取所有项目列表
     * @return 列表
     */
    Result<List<Project>> getAllProjects();

    /**
     * 获取时间线列表
     * @return 列表
     */
    Result<List<Timeline>> getTimelines();
}
