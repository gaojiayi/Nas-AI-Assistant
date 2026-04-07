package com.gaojiayi.nasaiassistantapp.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/ai/nas")
public class NasAIChatController {

    /**
     * NAS AI chat SSE interface
     * @param message user message
     * @param chatId conversation ID
     * @param enableTools enable tools
     * @param enableRAG enable RAG
     * @param enableThink enable thinking
     * @return
     */
    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSse(@RequestParam String message, @RequestParam String chatId, @RequestParam Boolean enableTools, @RequestParam Boolean enableRAG, @RequestParam Boolean enableThink) {
        // Create a long timeout SseEmitter
        // SseEmitter sseEmitter = new SseEmitter(180000L); // 3 minutes timeout
        
        // Return
        // return sseEmitter;

        if (enableThink) {
            // TODO: Implement thinking logic
        }
        return null;
    }
}
