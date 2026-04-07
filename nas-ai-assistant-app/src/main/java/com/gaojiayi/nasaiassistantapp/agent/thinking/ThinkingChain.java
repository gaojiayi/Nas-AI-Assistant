package com.gaojiayi.nasaiassistantapp.agent.thinking;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 思考链
 * 表示智能体的思考过程和决策结果
 */

public class ThinkingChain {

    /**
     * 问题类型
     */
    public enum QuestionType {
        SIMPLE,      // 简单问题（问候、闲聊）
        COMPLEX,     // 复杂问题（需要工具）
        UNKNOWN      // 未知类型
    }

    /**
     * 工具调用信息
     */
    @Data
    public static class ToolCall {
        private String toolName;
        private String arguments;

        public ToolCall(String toolName, String arguments) {
            this.toolName = toolName;
            this.arguments = arguments;
        }
    }

    // 问题类型
    private QuestionType questionType;

    // 思考内容
    private String thinking;

    // 是否需要工具
    private boolean needTools;

    // 工具调用列表
    private List<ToolCall> toolCalls = new ArrayList<>();

    // 直接回复内容（不需要工具时）
    private String directReply;

    // 原始响应
    private String rawResponse;

    /**
     * 是否有工具调用
     */
    public boolean hasToolCalls() {
        return needTools && !toolCalls.isEmpty();
    }

    /**
     * 添加工具调用
     */
    public void addToolCall(String toolName, String arguments) {
        this.toolCalls.add(new ToolCall(toolName, arguments));
        this.needTools = true;
    }

    /**
     * 获取最终回复内容
     */
    public String getFinalReply() {
        if (directReply != null && !directReply.isEmpty()) {
            return directReply;
        }
        return rawResponse;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getThinking() {
        return thinking;
    }

    public void setThinking(String thinking) {
        this.thinking = thinking;
    }

    public boolean isNeedTools() {
        return needTools;
    }

    public void setNeedTools(boolean needTools) {
        this.needTools = needTools;
    }

    public List<ToolCall> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<ToolCall> toolCalls) {
        this.toolCalls = toolCalls;
    }

    public String getDirectReply() {
        return directReply;
    }

    public void setDirectReply(String directReply) {
        this.directReply = directReply;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    
}
