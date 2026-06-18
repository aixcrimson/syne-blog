package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 月度互动数据VO
 * 用于仪表盘堆叠面积图展示每月浏览/点赞/评论趋势
 */
@Data
@Schema(description = "月度互动数据")
public class MonthlyInteractionVO {

    @Schema(description = "月份，格式: yyyy-MM")
    private String month;

    @Schema(description = "浏览总数")
    private Long views;

    @Schema(description = "点赞总数")
    private Long likes;

    @Schema(description = "评论总数")
    private Long comments;
}
