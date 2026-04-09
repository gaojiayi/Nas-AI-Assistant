package com.gaojiayi.nasaiassistantapp.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class ChatClientService {

    private final ChatClient chatClient;

    @Resource
    private ToolCallback[] nasAITools;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private RagService ragService;

    public ChatClientService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Flux<String> chatStream(String message, String chatId, boolean enableTools, boolean useRag) {
        // 如果使用 RAG，先进行查询重写
        String queryMessage = useRag ? ragService.rewriteQuery(message) : message;

        var promptSpec = chatClient
                .prompt()
                .system("你是一个专业的AI助手，能够回答各种问题并提供帮助。")
                .user(queryMessage)
                // 为聊天会话设置对话记忆功能。
                // 每次 AI 对话完成后
                // Advisor 调用 DatabaseBasedChatMemory.add() 方法
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId));

        // 条件性添加工具回调
        if (enableTools && nasAITools != null && nasAITools.length > 0) {
            promptSpec = promptSpec.toolCallbacks(nasAITools);
            log.debug("已启用工具调用，共{}个工具", nasAITools.length);
        }

        // 条件性添加 RAG advisor
        if (useRag && pgVectorVectorStore != null) {
            // 使用向量存储进行检索增强
            promptSpec = promptSpec.advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build());
            log.debug("已启用 RAG 检索增强");
        }
        // TODO: 添加思考

        return promptSpec.stream().content();
    }

}
