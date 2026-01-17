package com.syne.server.controller.web;

import com.syne.server.common.Result;
import com.syne.server.model.vo.StatsVO;
import com.syne.server.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端统计控制器
 */
@Slf4j
@Tag(name = "用户端统计接口", description = "用户端统计数据相关接口")
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController{

    private final StatsService statsService;

    /**
     * 获取统计信息
     *
     * @return 统计数据
     */
    @Operation(summary = "获取统计数据", description = "用户端获取统计数据（文章总数，分类总数，总浏览量）")
    @GetMapping
    public Result<StatsVO> getStats(){
        StatsVO vo = statsService.getStats();
        return Result.success(vo);
    }
}