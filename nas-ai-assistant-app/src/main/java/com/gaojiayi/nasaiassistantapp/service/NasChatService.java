package com.gaojiayi.nasaiassistantapp.service;

import org.springframework.stereotype.Service;

import com.gaojiayi.nasaiassistantapp.common.NasQuestionClassifierService;
import com.gaojiayi.nasaiassistantapp.common.Utils;
import com.gaojiayi.nasaiassistantapp.exception.BusinessException;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class NasChatService {
    @Resource
    private ChatClientService chatClientService;

    @Resource
    private RagService ragService;

    public Flux<String> chatStream(String message, String chatId, boolean enableTools, boolean useRag) {
        Utils.validateInput(message, chatId);

        try {
            // 1. 问题分类
            NasQuestionClassifierService.QuestionType type = NasQuestionClassifierService.classify(message);
            log.info("问题分类结果: {}, 问题: {}", type, message);

            // 2. 敏感问题拒绝
            if (type == NasQuestionClassifierService.QuestionType.SENSITIVE) {
                return Flux.just("抱歉，我无法回答这个问题。");
            }

            boolean hasRelevantDocs = ragService.hasRelevantDocs(message);
            // 3. RAG fallback 策略
            log.info(hasRelevantDocs ? "使用RAG检索" : "RAG检索无结果，fallback到通用LLM");

            return chatClientService.chatStream(message, chatId, enableTools, hasRelevantDocs)
                    .onErrorResume(e -> {
                        log.error("流式对话失败: message={}, chatId={}", message, chatId, e);
                        return Flux.just("抱歉，AI服务暂时不可用，请稍后重试。");
                    });

        } catch (Exception e) {
            log.error("智能对话异常: message={}, chatId={}", message, chatId, e);
            throw new BusinessException(500, "智能对话失败，请稍后重试", e);
        }

    }

}
