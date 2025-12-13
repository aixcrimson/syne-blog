package com.syne.server.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户列表视图对象
 */
@Data
@Schema(description = "用户列表视图对象")
public class UserListVO implements Serializable {

    public static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "GitHub链接")
    private String github;

    @Schema(description = "B站链接")
    private String bilibili;

    @Schema(description = "用户角色")
    private Integer role;

    @Schema(description = "账号状态")
    private Integer status;

    @Schema(description = "文章数量")
    private Integer articleCount;

    @Schema(description = "评论数量")
    private Integer commentCount;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}