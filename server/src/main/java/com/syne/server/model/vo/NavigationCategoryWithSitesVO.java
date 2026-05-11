package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端导航分类（含站点列表）VO
 */
@Data
@Schema(description = "管理端导航分类（含站点列表）VO")
public class NavigationCategoryWithSitesVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;


    @Schema(description = "排序权重")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "站点列表")
    private List<NavigationSiteVO> sites;
}
