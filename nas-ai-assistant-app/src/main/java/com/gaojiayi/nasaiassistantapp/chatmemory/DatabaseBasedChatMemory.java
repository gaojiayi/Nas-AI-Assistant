package com.gaojiayi.nasaiassistantapp.chatmemory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
@RequiredArgsConstructor// 构造函数注入
public class DatabaseBasedChatMemory implements ChatMemory {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBasedChatMemory.class);


    @Override
    public void add(String conversationId, List<Message> messages) {
        log.info("Adding messages to conversation: {}", conversationId);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public List<Message> get(String conversationId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public void clear(String conversationId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }
    
  
}
