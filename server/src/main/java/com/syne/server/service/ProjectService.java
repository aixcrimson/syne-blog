package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.Result;
import com.syne.server.model.entity.Project;

import java.util.List;

/**
 * 项目管理服务接口
 */
public interface ProjectService extends IService<Project> {

    /**
     * 获取所有项目列表
     * @return 项目列表
     */
    List<Project> getAllProjects();

    /**
     * 根据ID获取项目
     * @param id 项目ID
     * @return 项目信息
     */
    Project getProjectById(Long id);

    /**
     * 创建项目
     * @param project 项目数据
     * @return 创建的项目
     */
    Project createProject(Project project);

    /**
     * 更新项目
     * @param id 项目ID
     * @param project 项目数据
     * @return 更新后的项目
     */
    Project updateProject(Long id, Project project);

    /**
     * 删除项目
     * @param ids 项目ID，多个用逗号分隔
     * @return 删除结果
     */
    Result<String> deleteProjects(String ids);

    /**
     * 切换项目精选状态
     * @param id 项目ID
     * @return 更新后的项目
     */
    Project toggleFeatured(Long id);
}
