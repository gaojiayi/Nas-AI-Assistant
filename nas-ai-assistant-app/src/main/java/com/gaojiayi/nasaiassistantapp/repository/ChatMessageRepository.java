package com.gaojiayi.nasaiassistantapp.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gaojiayi.nasaiassistantapp.entity.ChatMessage;
import com.gaojiayi.nasaiassistantapp.entity.vo.ConversationVO;
import com.gaojiayi.nasaiassistantapp.mapper.ChatMessageMapper;

@Repository
public class ChatMessageRepository extends ServiceImpl<ChatMessageMapper, ChatMessage> {

    public List<ChatMessage> listByConversationId(String conversationId) {
        // 1. 建议：如果 conversationId 为空，直接返回空列表，避免全表扫描或报错
        if (conversationId == null || conversationId.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId).orderByAsc(ChatMessage::getCreateTime);
        return this.list(queryWrapper);
    }

    public List<ChatMessage> listLatestByConversationId(String conversationId, int limit) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId)
                .orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT " + limit);

        List<ChatMessage> chatMessages = this.list(queryWrapper);

        if (!chatMessages.isEmpty()) {
            Collections.reverse(chatMessages);
        }

        return chatMessages;
    }

    public void deleteByConversationId(String conversationId) {
        // 直接执行SQL更新 is_delete = 1，与 listConversations 中的逻辑保持一致
        baseMapper.updateDeleteByConversationId(conversationId);
    }

    /**
     * 获取会话列表，按最后消息时间倒序
     * 
     */
    public List<ConversationVO> latestConversations() {
        List<ConversationVO> conversations = baseMapper.listConversations();

        if (conversations == null) {
            return Collections.emptyList();
        }

        return conversations;
    }

    public List<ConversationVO> listConversations() {
        List<ConversationVO> conversations = baseMapper.listConversations();  
        return conversations != null ? conversations : Collections.emptyList();
    }

}