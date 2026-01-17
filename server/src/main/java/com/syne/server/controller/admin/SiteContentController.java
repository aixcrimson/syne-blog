package com.syne.server.controller.admin;

import com.syne.server.common.Result;
import com.syne.server.model.entity.Notice;
import com.syne.server.model.entity.Project;
import com.syne.server.model.entity.Skill;
import com.syne.server.model.entity.Timeline;
import com.syne.server.service.NoticeService;
import com.syne.server.service.ProjectService;
import com.syne.server.service.SkillService;
import com.syne.server.service.TimelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者信息管理控制器
 * 包含公告、技能、项目、时间线的 CRUD 接口
 */
@Slf4j
@Tag(name = "作者信息管理", description = "公告、技能、项目、时间线管理接口")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class SiteContentController {

    private final NoticeService noticeService;
    private final SkillService skillService;
    private final ProjectService projectService;
    private final TimelineService timelineService;

    // ==================== 公告管理 ====================

    /**
     * 获取公告列表
     */
    @Operation(summary = "获取公告列表", description = "获取所有公告，按排序权重降序")
    @GetMapping("/notices")
    public Result<List<Notice>> getNoticeList() {
        log.info("获取公告列表");
        return Result.success(noticeService.getAllNotices());
    }

    /**
     * 获取公告详情
     */
    @Operation(summary = "获取公告详情", description = "根据ID获取公告详情")
    @GetMapping("/notices/{id}")
    public Result<Notice> getNoticeById(
            @Parameter(description = "公告ID", required = true)
            @PathVariable Long id
    ) {
        log.info("获取公告详情: id={}", id);
        return Result.success(noticeService.getNoticeById(id));
    }

    /**
     * 创建公告
     */
    @Operation(summary = "创建公告", description = "创建新公告")
    @PostMapping("/notices")
    public Result<Notice> createNotice(
            @Parameter(description = "公告数据", required = true)
            @Valid @RequestBody Notice notice
    ) {
        log.info("创建公告: {}", notice);
        return Result.success(noticeService.createNotice(notice));
    }

    /**
     * 更新公告
     */
    @Operation(summary = "更新公告", description = "更新公告信息")
    @PutMapping("/notices/{id}")
    public Result<Notice> updateNotice(
            @Parameter(description = "公告ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "公告数据", required = true)
            @Valid @RequestBody Notice notice
    ) {
        log.info("更新公告: id={}, data={}", id, notice);
        return Result.success(noticeService.updateNotice(id, notice));
    }

    /**
     * 删除公告
     */
    @Operation(summary = "删除公告", description = "删除公告（支持批量）")
    @DeleteMapping("/notices")
    public Result<String> deleteNotices(
            @Parameter(description = "公告ID，多个用逗号分隔", required = true)
            @RequestParam("ids") String ids
    ) {
        log.info("删除公告: ids={}", ids);
        return noticeService.deleteNotices(ids);
    }

    /**
     * 切换公告显示状态
     */
    @Operation(summary = "切换公告显示状态", description = "切换公告的显示/隐藏状态")
    @PutMapping("/notices/{id}/toggle-show")
    public Result<Notice> toggleNoticeShow(
            @Parameter(description = "公告ID", required = true)
            @PathVariable Long id
    ) {
        log.info("切换公告显示状态: id={}", id);
        return Result.success(noticeService.toggleShow(id));
    }

    // ==================== 技能管理 ====================

    /**
     * 获取技能列表
     */
    @Operation(summary = "获取技能列表", description = "获取所有技能，按排序权重降序")
    @GetMapping("/skills")
    public Result<List<Skill>> getSkillList() {
        log.info("获取技能列表");
        return Result.success(skillService.getAllSkills());
    }

    /**
     * 获取技能详情
     */
    @Operation(summary = "获取技能详情", description = "根据ID获取技能详情")
    @GetMapping("/skills/{id}")
    public Result<Skill> getSkillById(
            @Parameter(description = "技能ID", required = true)
            @PathVariable Long id
    ) {
        log.info("获取技能详情: id={}", id);
        return Result.success(skillService.getSkillById(id));
    }

    /**
     * 创建技能
     */
    @Operation(summary = "创建技能", description = "创建新技能")
    @PostMapping("/skills")
    public Result<Skill> createSkill(
            @Parameter(description = "技能数据", required = true)
            @Valid @RequestBody Skill skill
    ) {
        log.info("创建技能: {}", skill);
        return Result.success(skillService.createSkill(skill));
    }

    /**
     * 更新技能
     */
    @Operation(summary = "更新技能", description = "更新技能信息")
    @PutMapping("/skills/{id}")
    public Result<Skill> updateSkill(
            @Parameter(description = "技能ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "技能数据", required = true)
            @Valid @RequestBody Skill skill
    ) {
        log.info("更新技能: id={}, data={}", id, skill);
        return Result.success(skillService.updateSkill(id, skill));
    }

    /**
     * 删除技能
     */
    @Operation(summary = "删除技能", description = "删除技能（支持批量）")
    @DeleteMapping("/skills")
    public Result<String> deleteSkills(
            @Parameter(description = "技能ID，多个用逗号分隔", required = true)
            @RequestParam("ids") String ids
    ) {
        log.info("删除技能: ids={}", ids);
        return skillService.deleteSkills(ids);
    }

    // ==================== 项目管理 ====================

    /**
     * 获取项目列表
     */
    @Operation(summary = "获取项目列表", description = "获取所有项目，精选优先，按排序权重降序")
    @GetMapping("/projects")
    public Result<List<Project>> getProjectList() {
        log.info("获取项目列表");
        return Result.success(projectService.getAllProjects());
    }

    /**
     * 获取项目详情
     */
    @Operation(summary = "获取项目详情", description = "根据ID获取项目详情")
    @GetMapping("/projects/{id}")
    public Result<Project> getProjectById(
            @Parameter(description = "项目ID", required = true)
            @PathVariable Long id
    ) {
        log.info("获取项目详情: id={}", id);
        return Result.success(projectService.getProjectById(id));
    }

    /**
     * 创建项目
     */
    @Operation(summary = "创建项目", description = "创建新项目")
    @PostMapping("/projects")
    public Result<Project> createProject(
            @Parameter(description = "项目数据", required = true)
            @Valid @RequestBody Project project
    ) {
        log.info("创建项目: {}", project);
        return Result.success(projectService.createProject(project));
    }

    /**
     * 更新项目
     */
    @Operation(summary = "更新项目", description = "更新项目信息")
    @PutMapping("/projects/{id}")
    public Result<Project> updateProject(
            @Parameter(description = "项目ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "项目数据", required = true)
            @Valid @RequestBody Project project
    ) {
        log.info("更新项目: id={}, data={}", id, project);
        return Result.success(projectService.updateProject(id, project));
    }

    /**
     * 删除项目
     */
    @Operation(summary = "删除项目", description = "删除项目（支持批量）")
    @DeleteMapping("/projects")
    public Result<String> deleteProjects(
            @Parameter(description = "项目ID，多个用逗号分隔", required = true)
            @RequestParam("ids") String ids
    ) {
        log.info("删除项目: ids={}", ids);
        return projectService.deleteProjects(ids);
    }

    /**
     * 切换项目精选状态
     */
    @Operation(summary = "切换项目精选状态", description = "切换项目的精选/普通状态")
    @PutMapping("/projects/{id}/toggle-featured")
    public Result<Project> toggleProjectFeatured(
            @Parameter(description = "项目ID", required = true)
            @PathVariable Long id
    ) {
        log.info("切换项目精选状态: id={}", id);
        return Result.success(projectService.toggleFeatured(id));
    }

    // ==================== 时间线管理 ====================

    /**
     * 获取时间线列表
     */
    @Operation(summary = "获取时间线列表", description = "获取所有时间线，按年份降序")
    @GetMapping("/timelines")
    public Result<List<Timeline>> getTimelineList() {
        log.info("获取时间线列表");
        return Result.success(timelineService.getAllTimelines());
    }

    /**
     * 获取时间线详情
     */
    @Operation(summary = "获取时间线详情", description = "根据ID获取时间线详情")
    @GetMapping("/timelines/{id}")
    public Result<Timeline> getTimelineById(
            @Parameter(description = "时间线ID", required = true)
            @PathVariable Long id
    ) {
        log.info("获取时间线详情: id={}", id);
        return Result.success(timelineService.getTimelineById(id));
    }

    /**
     * 创建时间线
     */
    @Operation(summary = "创建时间线", description = "创建新时间线")
    @PostMapping("/timelines")
    public Result<Timeline> createTimeline(
            @Parameter(description = "时间线数据", required = true)
            @Valid @RequestBody Timeline timeline
    ) {
        log.info("创建时间线: {}", timeline);
        return Result.success(timelineService.createTimeline(timeline));
    }

    /**
     * 更新时间线
     */
    @Operation(summary = "更新时间线", description = "更新时间线信息")
    @PutMapping("/timelines/{id}")
    public Result<Timeline> updateTimeline(
            @Parameter(description = "时间线ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "时间线数据", required = true)
            @Valid @RequestBody Timeline timeline
    ) {
        log.info("更新时间线: id={}, data={}", id, timeline);
        return Result.success(timelineService.updateTimeline(id, timeline));
    }

    /**
     * 删除时间线
     */
    @Operation(summary = "删除时间线", description = "删除时间线（支持批量）")
    @DeleteMapping("/timelines")
    public Result<String> deleteTimelines(
            @Parameter(description = "时间线ID，多个用逗号分隔", required = true)
            @RequestParam("ids") String ids
    ) {
        log.info("删除时间线: ids={}", ids);
        return timelineService.deleteTimelines(ids);
    }
}
