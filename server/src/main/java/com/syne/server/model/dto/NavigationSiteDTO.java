package com.syne.server.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 导航站点DTO类
 */
@Data
@Schema(description = "导航站点DTO")
public class NavigationSiteDTO {

    @Schema(description = "站点ID")
    private Long id;

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "站点名称")
    @NotBlank(message = "站点名称不能为空")
    @Size(max = 100, message = "站点名称长度不能超过100个字符")
    private String name;

    @Schema(description = "站点描述")
    @Size(max = 500, message = "站点描述长度不能超过500个字符")
    private String description;

    @Schema(description = "站点URL")
    @NotBlank(message = "站点URL不能为空")
    @Size(max = 500, message = "站点URL长度不能超过500个字符")
    private String url;


    @Schema(description = "排序权重")
    private Integer sortOrder;
}