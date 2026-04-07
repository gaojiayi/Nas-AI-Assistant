package com.gaojiayi.nasaiassistantapp.chatmemory;

import java.util.List;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

@Component
public class DefaultShortChatMemory implements ChatMemory {

    /**
     * ChatMemory 聊天对话记忆的存储
     */

    private final ChatMemory chatMemory;

    public DefaultShortChatMemory() {
        // 当前版本的 MessageWindowChatMemory 是 ChatMemory 的唯一默认实现类
        // 并且构造器已经私有化，只提供Builder模式来创建实例；这点与之前的版本不一样
        this.chatMemory = MessageWindowChatMemory.builder()
                // 对话存储的repository存储库层的实现方式，如果不配置，默认也是 Spring 提供的 InMemoryChatMemoryRepository
                .chatMemoryRepository(new InMemoryChatMemoryRepository()) // 有默认
                .maxMessages(20) // 最大消息数
                .build();

    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        this.chatMemory.add(conversationId, messages);
    }

    @Override
    public List<Message> get(String conversationId) {
        return this.chatMemory.get(conversationId);
    }

    @Override
    public void clear(String conversationId) {
       
        this.chatMemory.clear(conversationId);
    }
}
