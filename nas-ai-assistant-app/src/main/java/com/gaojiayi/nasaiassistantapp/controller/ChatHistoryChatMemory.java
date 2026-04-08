package com.gaojiayi.nasaiassistantapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaojiayi.nasaiassistantapp.entity.ChatMessage;
import com.gaojiayi.nasaiassistantapp.entity.vo.ConversationVO;
import com.gaojiayi.nasaiassistantapp.entity.vo.MessageVO;
import com.gaojiayi.nasaiassistantapp.repository.ChatMessageRepository;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/ai/history")
public class ChatHistoryChatMemory {

    @Resource
    private ChatMessageRepository chatMessageRepository;

    @GetMapping
    public List<ConversationVO> listConversations(
            @RequestParam(required = false, defaultValue = "") String appType) {
        return chatMessageRepository.listConversations();
    }

    /**
     * 获取指定会话的消息历史
     *
     * @param chatId 会话ID
     * @return 消息列表
     */
    @GetMapping("/{chatId}")
    public List<MessageVO> getConversationHistory(@PathVariable("chatId") String chatId) {
        List<ChatMessage> messages = chatMessageRepository.listByConversationId(chatId);
        return messages.stream().map(msg -> {
            // 确保 role 字段正确：user / assistant / system / tool
            String role = "user"; // 默认值
            if (msg.getMessageType() != null) {
                role = msg.getMessageType().name().toLowerCase();
            }

            MessageVO.MessageVOBuilder builder = MessageVO.builder()
                    .role(role)
                    .content(msg.getContent())
                    .createTime(msg.getCreateTime());

            // 从 metadata 中提取思考步骤和思考时间
            if (msg.getMetadata() != null) {
                Object thinkingSteps = msg.getMetadata().get("thinkingSteps");
                if (thinkingSteps instanceof List) {
                    builder.thinkingSteps((List<String>) thinkingSteps);
                }

                Object thinkingTime = msg.getMetadata().get("thinkingTime");
                if (thinkingTime instanceof Number) {
                    builder.thinkingTime(((Number) thinkingTime).intValue());
                }
            }

            return builder.build();
        }).collect(Collectors.toList());
    }

        /**
     * 删除指定会话
     *
     * @param chatId 会话ID
     * @return 删除结果
     */
    @DeleteMapping("/{chatId}")
    public Map<String, Object> deleteConversation(@PathVariable("chatId") String chatId) {
        Map<String, Object> result = new HashMap<>();
        try {
            chatMessageRepository.deleteByConversationId(chatId);
            result.put("code", 0);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
}
