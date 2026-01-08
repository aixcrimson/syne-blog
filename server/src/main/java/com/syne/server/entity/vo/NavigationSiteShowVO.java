package com.syne.server.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户端导航站点展示VO
 */
@Data
@Schema(description = "用户端导航站点展示VO")
public class NavigationSiteShowVO {

    @Schema(description = "站点分类ID")
    private Long categoryId;

    @Schema(description = "站点分类名称")
    private String categoryName;

    @Schema(description = "站点列表")
    private List<NavigationSiteVO> sites;
}