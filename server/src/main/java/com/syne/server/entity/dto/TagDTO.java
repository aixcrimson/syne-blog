package com.syne.server.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签数据传输对象
 */
@Data
@Schema(description = "标签数据传输对象")
public class TagDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "标签ID（更新接口需要）")
    private Long id;

    @Schema(description = "标签名称", required = true, example = "Vue")
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    private String name;

    @Schema(description = "标签别名", required = true, example = "vue")
    @NotBlank(message = "标签别名不能为空")
    @Size(max = 50, message = "标签别名长度不能超过50个字符")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "标签别名只能包含小写字母、数字和连字符")
    private String slug;

    @Schema(description = "标签颜色", example = "#42b883")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色值必须是有效的十六进制格式")
    private String color;
}