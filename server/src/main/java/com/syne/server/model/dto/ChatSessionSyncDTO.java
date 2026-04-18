package com.syne.server.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "Chat session sync DTO")
public class ChatSessionSyncDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Sessions to sync")
    private List<ChatSessionDTO> sessions;
}
