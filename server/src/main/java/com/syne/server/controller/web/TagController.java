package com.syne.server.controller.web;

import com.syne.server.common.Result;
import com.syne.server.model.vo.TagListVO;
import com.syne.server.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端标签控制器
 */
@Slf4j
@Tag(name = "用户端标签管理", description = "用户端标签相关接口")
@RestController("webTagController")
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController{

    private final TagService tagService;

    /**
     * 查询所有标签列表
     *
     * @return 标签列表
     */
    @Operation(summary = "查询标签列表", description = "用户端查询标签列表")
    @GetMapping
    public Result<List<TagListVO>> getAllTagList(){
        log.info("查询标签列表");
        List<TagListVO> result = tagService.getAllTagList();

        return Result.success(result);
    }
}