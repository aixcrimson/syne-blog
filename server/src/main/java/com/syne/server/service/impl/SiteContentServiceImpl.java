package com.syne.server.service.impl;

import com.syne.server.common.Result;
import com.syne.server.model.entity.Notice;
import com.syne.server.model.entity.Project;
import com.syne.server.model.entity.Skill;
import com.syne.server.model.entity.Timeline;
import com.syne.server.model.entity.User;
import com.syne.server.mapper.NoticeMapper;
import com.syne.server.mapper.ProjectMapper;
import com.syne.server.mapper.SkillMapper;
import com.syne.server.mapper.TimelineMapper;
import com.syne.server.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private final UserMapper userMapper;

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
        LambdaQueryWrapper<Timeline> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Timeline::getDeleted, 0)
            .orderByDesc(Timeline::getYear)
            .orderByDesc(Timeline::getCreateTime);
        return Result.success(timelineMapper.selectList(queryWrapper));
    }

    @Override
    public Result<Object> getAuthorInfo() {
        // 查找最早创建的管理员（作为站长/博主）
        List<User> admins = userMapper.selectList(
            new LambdaQueryWrapper<User>()
                .eq(User::getRole, 1) // 1-管理员
                .orderByAsc(User::getCreateTime)
                .last("LIMIT 1")
        );
        
        User user = admins.isEmpty() ? null : admins.get(0);

        if (user != null) {
            // 构建返回对象，去除敏感信息
            java.util.Map<String, Object> userInfo = new java.util.HashMap<>();
            userInfo.put("username", user.getUsername());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("bio", user.getBio());
            userInfo.put("email", user.getEmail());
            userInfo.put("github", user.getGithub());
            userInfo.put("bilibili", user.getBilibili());
            return Result.success(userInfo);
        }
        return Result.success(null);
    }
}
