package com.syne.server.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 分类数据传输对象
 */
@Data
@Schema(description = "分类数据传输对象")
public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类ID（更新接口需要）")
    private Long id;

    @Schema(description = "分类名称", required = true, example = "Vue 3")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 20, message = "分类名称长度不能超过20个字符")
    private String name;

    @Schema(description = "分类别名", required = true, example = "Vue 3 框架")
    private String slug;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "分类描述", example = "Vue 3 是一个用于构建用户界面的渐进式框架")
    private String description;

    @Schema(description = "分类排序权重，数值越小越靠前", example = "1")
    @NotBlank(message = "排序权重不能为空")
    private Integer sortOrder;
}