package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 用户信息视图对象
 */
@Data
@Schema(description = "用户信息视图对象")
public class UserInfoVO implements Serializable {

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
}