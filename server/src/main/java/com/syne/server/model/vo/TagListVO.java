package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签列表视图对象
 */
@Data
@Schema(description = "标签列表视图对象")
public class TagListVO implements Serializable {

    public static final long serialVersionUID = 1L;

    @Schema(description = "标签ID")
    private Long id;

    @Schema(description = "标签名称")
    private String name;

    @Schema(description = "标签别名")
    private String slug;

    @Schema(description = "标签颜色")
    private String color;

    @Schema(description = "有效文章数")
    private Integer articleCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
