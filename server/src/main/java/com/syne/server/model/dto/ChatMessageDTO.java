package com.syne.server.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "Chat message DTO")
public class ChatMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Role: user/assistant")
    @NotBlank(message = "role is required")
    private String role;

    @Schema(description = "Message content")
    @NotBlank(message = "content is required")
    private String content;

    @Schema(description = "Message timestamp (ms)")
    private Long timestamp;
}
