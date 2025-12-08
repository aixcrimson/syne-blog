package com.syne.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("users")
@Schema(description = "用户实体类")
public class User implements Serializable {

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

    @Schema(description = "帐号状态：1-正常，2-禁用")
    @TableField("status")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除：0-未删除，1-已删除")
    @TableField("deleted")
    private Integer deleted;
}