package com.gaojiayi.nasaiassistantapp.agent;

import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.gaojiayi.nasaiassistantapp.agent.model.AgentState;

import cn.hutool.core.collection.CollUtil;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存了工具调用信息的响应
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;

    public ChatOptions getChatOptions() {
        return chatOptions;
    }

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .withInternalToolExecutionEnabled(false)
                .build();
    }

    public void setToolCallChatResponse(ChatResponse toolCallChatResponse) {
        this.toolCallChatResponse = toolCallChatResponse;
    }

    /**
     * 在父类中已经使用了 think 方法，这里不需要重写
     */
    public boolean think() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'think'");
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有工具调用";
        }

        // 调用工具
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 只添加工具响应消息到消息列表（不要覆盖整个列表，因为助手消息已经在 think() 中添加了）
        // conversationHistory 包含：原有消息 + 助手消息 (tool_calls) + 工具响应消息 (tool_responses)
        // 我们只需要添加最后一个工具响应消息
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil
                .getLast(toolExecutionResult.conversationHistory());
        getMessageList().add(toolResponseMessage);
        // 判断是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate"));
        if (terminateToolCalled) {
            // 任务结束，更改状态
            setState(AgentState.FINISHED);
        }
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 完成了它的任务！结果: " + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info(results);

        // 记录工具执行结果用于循环检测
        recordResponse(results);

        return results;
    }

    public ToolCallback[] getAvailableTools() {
        return availableTools;
    }

    public ChatResponse getToolCallChatResponse() {
        return toolCallChatResponse;
    }

    public ToolCallingManager getToolCallingManager() {
        return toolCallingManager;
    }

}
