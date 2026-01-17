package com.syne.server.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新数据传输对象
 */
@Data
@Schema(description = "用户更新数据传输对象")
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Schema(description = "邮箱地址", example = "test@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Schema(description = "用户角色：1-管理员，2-普通用户", example = "2")
    private Integer role;

    @Schema(description = "账号状态：0-禁用，1-正常", example = "1")
    private Integer status;

    @Schema(description = "头像URL", example = "/avatars/default.jpg")
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String avatar;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "GitHub链接", example = "https://github.com/testuser")
    @Size(max = 255, message = "GitHub链接长度不能超过255个字符")
    private String github;

    @Schema(description = "B站链接", example = "https://space.bilibili.com/123456")
    @Size(max = 255, message = "B站链接长度不能超过255个字符")
    private String bilibili;
}
