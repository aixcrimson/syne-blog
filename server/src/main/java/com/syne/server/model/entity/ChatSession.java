package com.syne.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("chat_sessions")
@Schema(description = "Chat session entity")
public class ChatSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("user_id")
    private Long userId;

    @TableField("title")
    private String title;

    @TableField("created_at")
    private Long createdAt;

    @TableField("updated_at")
    private Long updatedAt;
}
