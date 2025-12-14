package com.syne.server.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 导航分类DTO类
 */
@Data
@Schema(description = "导航分类DTO")
public class NavigationCategoryDTO {

    @Schema(description = "导航分类ID（更新接口需要）")
    private Long id;

    @Schema(description = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String name;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "排序权重")
    private Integer sortOrder;
}