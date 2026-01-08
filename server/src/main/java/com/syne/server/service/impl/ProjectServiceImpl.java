package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.Result;
import com.syne.server.entity.Project;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.ProjectMapper;
import com.syne.server.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    private final ProjectMapper projectMapper;

    @Override
    public List<Project> getAllProjects() {
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getDeleted, 0)
                .orderByDesc(Project::getIsFeatured)
                .orderByDesc(Project::getSortOrder)
                .orderByDesc(Project::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public Project getProjectById(Long id) {
        Project project = super.getById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new BusinessException("项目不存在");
        }
        return project;
    }

    @Override
    public Project createProject(Project project) {
        // 设置默认值
        if (project.getIsFeatured() == null) {
            project.setIsFeatured(0);
        }
        if (project.getSortOrder() == null) {
            project.setSortOrder(0);
        }
        
        this.save(project);
        log.info("创建项目成功: id={}, title={}", project.getId(), project.getTitle());
        return project;
    }

    @Override
    public Project updateProject(Long id, Project project) {
        Project existing = this.getProjectById(id);
        
        existing.setTitle(project.getTitle());
        existing.setDescription(project.getDescription());
        existing.setCoverImage(project.getCoverImage());
        existing.setGithubUrl(project.getGithubUrl());
        existing.setPreviewUrl(project.getPreviewUrl());
        existing.setTechStack(project.getTechStack());
        existing.setIsFeatured(project.getIsFeatured());
        existing.setSortOrder(project.getSortOrder());
        
        this.updateById(existing);
        log.info("更新项目成功: id={}", id);
        return this.getProjectById(id);
    }

    @Override
    @Transactional
    public Result<String> deleteProjects(String ids) {
        if (!StringUtils.hasText(ids)) {
            throw new BusinessException("项目ID不能为空");
        }

        String[] idArray = ids.split(",");
        List<String> failedIds = new ArrayList<>();
        int successCount = 0;

        for (String idStr : idArray) {
            try {
                Long id = Long.parseLong(idStr.trim());
                this.deleteProject(id);
                successCount++;
            } catch (NumberFormatException e) {
                failedIds.add(idStr + "(格式错误)");
            } catch (BusinessException e) {
                failedIds.add(idStr + "(" + e.getMessage() + ")");
            }
        }

        String message;
        if (failedIds.isEmpty()) {
            message = successCount == 1 ? "删除项目成功" :
                    String.format("成功删除 %d 个项目", successCount);
        } else {
            message = String.format("成功删除 %d 个项目，失败 %s",
                    successCount, String.join(", ", failedIds));
        }

        return Result.success(message);
    }

    /**
     * 逻辑删除单个项目
     */
    private void deleteProject(Long id) {
        Project project = this.getProjectById(id);
        
        LambdaUpdateWrapper<Project> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Project::getId, id)
                .set(Project::getDeleted, 1)
                .set(Project::getUpdateTime, LocalDateTime.now());
        
        this.update(updateWrapper);
        log.info("删除项目成功: id={}", id);
    }

    @Override
    public Project toggleFeatured(Long id) {
        Project project = this.getProjectById(id);
        
        // 切换精选状态
        project.setIsFeatured(project.getIsFeatured() == 1 ? 0 : 1);
        this.updateById(project);
        
        log.info("切换项目精选状态: id={}, isFeatured={}", id, project.getIsFeatured());
        return this.getProjectById(id);
    }
}
