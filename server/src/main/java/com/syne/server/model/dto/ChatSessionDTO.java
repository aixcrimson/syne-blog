package com.syne.server.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "Chat session DTO")
public class ChatSessionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Session id (optional for create)")
    private String id;

    @Schema(description = "Session title")
    private String title;

    @Schema(description = "Messages")
    private List<ChatMessageDTO> messages;

    @Schema(description = "Created at (ms)")
    private Long createdAt;

    @Schema(description = "Updated at (ms)")
    private Long updatedAt;
}
