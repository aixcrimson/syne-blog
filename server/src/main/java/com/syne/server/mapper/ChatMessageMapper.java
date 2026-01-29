package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.model.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
