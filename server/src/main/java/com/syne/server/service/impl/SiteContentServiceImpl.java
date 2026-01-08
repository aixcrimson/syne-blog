package com.syne.server.service.impl;

import com.syne.server.common.Result;
import com.syne.server.entity.Notice;
import com.syne.server.entity.Project;
import com.syne.server.entity.Skill;
import com.syne.server.entity.Timeline;
import com.syne.server.mapper.NoticeMapper;
import com.syne.server.mapper.ProjectMapper;
import com.syne.server.mapper.SkillMapper;
import com.syne.server.mapper.TimelineMapper;
import com.syne.server.service.SiteContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteContentServiceImpl implements SiteContentService {

    private final NoticeMapper noticeMapper;
    private final SkillMapper skillMapper;
    private final ProjectMapper projectMapper;
    private final TimelineMapper timelineMapper;

    @Override
    public Result<List<Notice>> getNotices() {
        return Result.success(noticeMapper.selectShowNotices());
    }

    @Override
    public Result<List<Skill>> getSkills() {
        return Result.success(skillMapper.selectAllSkills());
    }

    @Override
    public Result<List<Project>> getFeaturedProjects() {
        return Result.success(projectMapper.selectFeaturedProjects());
    }

    @Override
    public Result<List<Project>> getAllProjects() {
        return Result.success(projectMapper.selectAllProjects());
    }

    @Override
    public Result<List<Timeline>> getTimelines() {
        return Result.success(timelineMapper.selectAllTimelines());
    }
}
