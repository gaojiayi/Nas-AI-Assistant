package com.gaojiayi.nasaiassistantapp.common;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import com.gaojiayi.nasaiassistantapp.exception.BusinessException;

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

    /**
     * 获取错误类型（用于分类统计）
     */
    public static String getErrorType(String errorMsg) {
        if (errorMsg == null) {
            return "UNKNOWN_ERROR";
        }

        // 内容审核错误
        if (errorMsg.contains("DataInspectionFailed") ||
                errorMsg.contains("inappropriate content")) {
            return "CONTENT_INSPECTION_FAILED";
        }

        // 限流错误
        if (errorMsg.contains("Throttling") ||
                errorMsg.contains("rate limit") ||
                errorMsg.contains("too many requests")) {
            return "RATE_LIMIT_EXCEEDED";
        }

        // 超时错误
        if (errorMsg.contains("timeout") ||
                errorMsg.contains("timed out")) {
            return "TIMEOUT_ERROR";
        }

        // 认证错误
        if (errorMsg.contains("Unauthorized") ||
                errorMsg.contains("Invalid API key")) {
            return "AUTH_ERROR";
        }

        // 其他错误
        return "UNKNOWN_ERROR";
    }

    /**
     * 根据错误类型生成友好的错误提示
     */
    public static String generateFriendlyErrorMessage(String errorType, String originalError) {
        switch (errorType) {
            case "CONTENT_INSPECTION_FAILED":
                return """
                        抱歉，您的问题可能包含敏感内容，无法处理。

                        可能的原因：
                        • 包含个人隐私信息（如手机号、身份证号、邮箱等）
                        • 包含敏感词汇或不适当的内容

                        建议：
                        • 请换一种方式提问
                        • 避免包含个人隐私信息
                        • 使用更通用的描述方式

                        如果问题持续出现，可以尝试开始新对话。
                        """;

            case "RATE_LIMIT_EXCEEDED":
                return """
                        抱歉，请求过于频繁，已达到速率限制。

                        建议：
                        • 请稍等片刻后再试
                        • 避免短时间内发送大量请求

                        通常等待 1-2 分钟后即可恢复正常。
                        """;

            case "TIMEOUT_ERROR":
                return """
                        抱歉，请求超时了。

                        可能的原因：
                        • 网络连接不稳定
                        • 服务器响应较慢

                        建议：
                        • 请重试一次
                        • 如果问题持续，请稍后再试
                        """;

            case "AUTH_ERROR":
                return """
                        抱歉，身份验证失败。

                        这是一个系统配置问题，请联系管理员检查：
                        • API Key 是否正确
                        • API Key 是否已过期
                        • 账户余额是否充足
                        """;

            default:
                return String.format("""
                        抱歉，遇到了一个技术问题，暂时无法处理您的请求。

                        错误信息：%s

                        建议：
                        • 请重试一次
                        • 如果问题持续，请联系技术支持
                        """, originalError != null ? originalError.substring(0, Math.min(100, originalError.length()))
                        : "未知错误");
        }
    }


        /**
     * 校验输入参数
     *
     * @param message 用户消息
     * @param chatId 对话ID
     */
    public static void validateInput(String message, String chatId) {
        if (message == null || message.trim().isEmpty()) {
            throw new BusinessException(400, "消息内容不能为空");
        }
        if (chatId == null || chatId.trim().isEmpty()) {
            throw new BusinessException(400, "对话ID不能为空");
        }
        if (message.length() > 10000) {
            throw new BusinessException(400, "消息内容过长，请控制在10000字符以内");
        }
    }

}
