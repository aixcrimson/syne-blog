package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 */
@Data
@TableName("users")
@Schema(description = "用户实体类")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户名")
    @TableField("username")
    private String username;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "密码哈希值")
    @TableField("password_hash")
    private String passwordHash;

    @Schema(description = "头像")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "个人简介")
    @TableField("bio")
    private String bio;

    @Schema(description = "github地址")
    @TableField("github")
    private String github;

    @Schema(description = "bilibili地址")
    @TableField("bilibili")
    private String bilibili;

    @Schema(description = "用户角色：1-管理员，2-普通用户")
    @TableField("role")
    private Integer role;

    @Schema(description = "帐号状态：1-正常，0-禁用")
    @TableField("status")
    private Integer status;

}