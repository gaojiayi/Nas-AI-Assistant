package com.gaojiayi.nasaiassistantapp.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.gaojiayi.nasaiassistantapp.agent.NasManusAgent;
import com.gaojiayi.nasaiassistantapp.service.NasChatService;
import jakarta.annotation.Resource;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/ai/nas")
public class NasAIChatController {

    private static final Logger log = LoggerFactory.getLogger(NasAIChatController.class);

    @Resource
    private NasChatService nasChatService;

    @Resource
    private NasManusAgent nasManusAgent;

    /**
     * NAS AI chat SSE interface
     * 
     * @param message     user message
     * @param chatId      conversation ID
     * @param enableTools enable tools
     * @param enableRAG   enable RAG
     * @param enableThink enable thinking
     * @return
     */
    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSse(@RequestParam String message,
            @RequestParam String chatId,
            @RequestParam Boolean enableTools,
            @RequestParam Boolean enableRAG,
            @RequestParam Boolean enableThink) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(30000L); // 3 分钟超时

        if (enableThink) {
            try {
                nasManusAgent.runStream(message, chatId, sseEmitter);
            } catch (Exception e) {
                // 检查是否是内容安全检测失败
                if (e.getMessage() != null && e.getMessage().contains("DataInspectionFailed")) {
                    log.warn("内容安全检测失败，用户输入可能包含敏感内容：{}", message);
                    try {
                        sseEmitter.send(SseEmitter.event()
                                .name("error")
                                .data("抱歉，您的输入可能包含不当内容，请修改后重试。"));
                        sseEmitter.complete();
                    } catch (Exception sendEx) {
                        log.error("发送错误消息失败", sendEx);
                    }
                }
                // 其他异常
                log.error("Manus 智能体异常：{}", e.getMessage(), e);
                try {
                    sseEmitter.send(SseEmitter.event()
                            .name("error")
                            .data("抱歉，发生了错误：" + e.getMessage()));
                    sseEmitter.complete();
                } catch (Exception sendEx) {
                    log.error("发送错误消息失败", sendEx);
                }
            }

        } else {
            nasChatService
                    .chatStream(message, chatId, enableTools != null && enableTools, enableRAG != null && enableRAG)
                    .subscribe(chunk -> {
                        try {
                            sseEmitter.send(SseEmitter.event().data(chunk));

                        } catch (IOException e) {
                            sseEmitter.completeWithError(e);
                        }
                    }, sseEmitter::completeWithError, sseEmitter::complete);
        }
        return sseEmitter;
    }
}
