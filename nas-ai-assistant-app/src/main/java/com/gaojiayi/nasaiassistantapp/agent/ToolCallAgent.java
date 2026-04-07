package com.gaojiayi.nasaiassistantapp.agent;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ToolCallAgent extends ReActAgent{

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

    public boolean think() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'think'");
    }

    @Override
    public String act() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'act'");
    }



    public static org.slf4j.Logger getLog() {
        return log;
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



    public ChatOptions getChatOptions() {
        return chatOptions;
    }


}
