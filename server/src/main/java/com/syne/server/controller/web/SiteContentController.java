package com.syne.server.controller.web;

import com.syne.server.common.Result;
import com.syne.server.entity.Notice;
import com.syne.server.entity.Project;
import com.syne.server.entity.Skill;
import com.syne.server.entity.Timeline;
import com.syne.server.service.SiteContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Web - 站点内容接口", description = "获取公告、技能、项目、时间线等内容")
@RestController("webSiteContentController")
@RequestMapping("/site")
@RequiredArgsConstructor
public class SiteContentController {

    private final SiteContentService siteContentService;

    @Operation(summary = "获取公告列表")
    @GetMapping("/notices")
    public Result<List<Notice>> getNotices() {
        return siteContentService.getNotices();
    }

    @Operation(summary = "获取作者信息")
    @GetMapping("/author")
    public Result<Object> getAuthorInfo() {
        return siteContentService.getAuthorInfo();
    }

    @Operation(summary = "获取技能列表")
    @GetMapping("/skills")
    public Result<List<Skill>> getSkills() {
        return siteContentService.getSkills();
    }

    @Operation(summary = "获取精选项目列表")
    @GetMapping("/projects")
    public Result<List<Project>> getFeaturedProjects() {
        return siteContentService.getFeaturedProjects();
    }
    
    @Operation(summary = "获取所有项目列表")
    @GetMapping("/projects/all")
    public Result<List<Project>> getAllProjects() {
        return siteContentService.getAllProjects();
    }

    @Operation(summary = "获取时间线列表")
    @GetMapping("/timelines")
    public Result<List<Timeline>> getTimelines() {
        return siteContentService.getTimelines();
    }
}
