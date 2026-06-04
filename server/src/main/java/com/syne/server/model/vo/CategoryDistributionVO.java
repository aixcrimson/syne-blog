package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分类文章分布VO
 * 用于仪表盘饼图展示各分类下的文章数量
 */
@Data
@Schema(description = "分类文章分布")
public class CategoryDistributionVO {

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "文章数量")
    private Long articleCount;
}
