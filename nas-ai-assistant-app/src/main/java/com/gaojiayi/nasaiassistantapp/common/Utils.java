package com.gaojiayi.nasaiassistantapp.common;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.MessageType;

public class Utils {

    /**
     * 过滤消息列表，只保留 USER 和 ASSISTANT 类型（用于保存到数据库）
     * 过滤掉：
     * 1. 系统消息、工具响应消息
     * 2. 内部提示（如 nextStepPrompt）
     * 3. 只有思考没有回复的消息（工具调用阶段的中间消息）
     * 4. 处理 AssistantMessage 中的思考内容，将思考步骤保存到 metadata，只保存回复部分到 content
     */
    public static List<Message> filterMessages(List<Message> messages) {
        return messages.stream()
                .map(msg -> {
                    // 过滤掉 TOOL 响应消息（不保存到数据库）
                    if (msg.getMessageType() == MessageType.TOOL) {
                        return null;
                    }

                    if (msg instanceof UserMessage userMsg) {
                        // 过滤掉 nextStepPrompt（内部决策提示）
                        String text = userMsg.getText();
                        if (text != null && (text.contains("基于工具") ||
                                text.contains("继续思考") ||
                                text.contains("基于工具执行结果") ||
                                text.contains("基于工具返回的结果做决策"))) {
                            return null; // 过滤掉内部提示
                        }
                    }
                    if (msg instanceof AssistantMessage assistantMsg) {
                        // 处理 AssistantMessage，提取回复部分和思考部分
                        String text = assistantMsg.getText();
                        if (text != null && text.contains("【思考】")) {
                            // 【关键修复】检查是否包含【回复】标记
                            // 如果只有【思考】没有【回复】，说明是工具调用阶段的中间消息，应该过滤掉
                            if (!text.contains("【回复】")) {
                                return null; // 过滤掉只有思考的消息
                            }

                            // 有【思考】和【回复】标记，提取思考内容保存到 metadata
                            String thinkingContent = extractContent(text, "【思考】", "【回复】");
                            String replyContent = extractReplyContent(text);

                            // 将思考内容按行分割，保存到 metadata
                            java.util.Map<String, Object> metadata = new java.util.HashMap<>(
                                    assistantMsg.getMetadata());
                            if (thinkingContent != null && !thinkingContent.isEmpty()) {
                                List<String> thinkingSteps = java.util.Arrays.stream(thinkingContent.split("\n"))
                                        .map(String::trim)
                                        .filter(line -> !line.isEmpty())
                                        .collect(Collectors.toList());
                                metadata.put("thinkingSteps", thinkingSteps);
                            }

                            // 【关键修复】创建新的 AssistantMessage，只包含回复内容，但保留 metadata
                            // 使用 replyContent 而不是原始 text
                            return new AssistantMessage(replyContent, metadata);
                        }
                    }
                    return msg;
                })
                .filter(msg -> msg != null)
                .collect(Collectors.toList());
    }

    /**
     * 从内容中提取指定标记之间的内容
     */
    public static String extractContent(String content, String startTag, String endTag) {
        int startIndex = content.indexOf(startTag);
        if (startIndex == -1) {
            return null;
        }
        startIndex += startTag.length();
        int endIndex = content.indexOf(endTag, startIndex);
        if (endIndex == -1) {
            return content.substring(startIndex).trim();
        }
        return content.substring(startIndex, endIndex).trim();
    }

    /**
     * 提取【回复】标记后的内容（用于数据库存储，只保存回复部分）
     */
    public static String extractReplyContent(String content) {
        int replyIndex = content.indexOf("【回复】");
        if (replyIndex == -1) {
            return content.trim();
        }
        return content.substring(replyIndex + 4).trim();
    }

    /**
     * 转义 JSON 特殊字符
     */
    public static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}
