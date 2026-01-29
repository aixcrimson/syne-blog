package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "Chat message VO")
public class ChatMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Role: user/assistant")
    private String role;

    @Schema(description = "Message content")
    private String content;

    @Schema(description = "Message timestamp (ms)")
    private Long timestamp;
}
