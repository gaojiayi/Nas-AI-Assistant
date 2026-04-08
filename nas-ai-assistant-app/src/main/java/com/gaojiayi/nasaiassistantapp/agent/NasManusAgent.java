package com.gaojiayi.nasaiassistantapp.agent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.gaojiayi.nasaiassistantapp.agent.model.AgentState;
import com.gaojiayi.nasaiassistantapp.agent.thinking.QuestionClassifier;
import com.gaojiayi.nasaiassistantapp.agent.thinking.ThinkingChain;
import com.gaojiayi.nasaiassistantapp.chatmemory.DatabaseBasedChatMemory;
import com.gaojiayi.nasaiassistantapp.common.Utils;
import com.gaojiayi.nasaiassistantapp.constant.PromptConstant;

/**
 * 实现think的超级智能体
 * NasManusAgent->thinkAgent->ToolCallAgent->ReActAgent->BaseAgent
 */
@Component
public class NasManusAgent extends ThinkingAgent {
    private static final Logger log = LoggerFactory.getLogger(NasManusAgent.class);

    private DatabaseBasedChatMemory databaseBasedChatMemory;
    private String chatId; // 用于在 think() 中访问当前对话ID
    private boolean historyLoaded = false; // 标记历史消息是否已加载

    // 错误计数器：用于跟踪相同错误的出现次数
    private final java.util.Map<String, Integer> errorCountMap = new java.util.concurrent.ConcurrentHashMap<>();
    private static final int MAX_SAME_ERROR_COUNT = 2; // 相同错误最多允许出现的次数

    @Autowired
    public NasManusAgent(ToolCallback[] manusTools, DashScopeChatModel dashScopeChatModel,
            DatabaseBasedChatMemory databaseBasedChatMemory) {
        super(manusTools);
        this.databaseBasedChatMemory = databaseBasedChatMemory;
        this.setName("NasManus");

        // 获取当前日期
        java.time.LocalDate now = java.time.LocalDate.now();
        String currentDate = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy年M月d日"));
        int currentYear = now.getYear();
        String SYSTEM_PROMPT = String.format(PromptConstant.SYSTEM_PROMPT_TEMPLATE, currentDate, currentYear);
        this.setSystemPrompt(SYSTEM_PROMPT);

        this.setNextStepPrompt(PromptConstant.NEXT_STEP_PROMPT);
        this.setMaxSteps(20);
        // 初始化客户端
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }

    public void runStream(String userPrompt, String chatId, SseEmitter sseEmitter) {
        // SseEmitter sseEmitter = new SseEmitter(300000L);
        this.chatId = chatId; // 保存 chatId 供 think() 使用
        this.historyLoaded = false; // 重置历史消息加载标志
        getMessageList().clear(); // 清空消息列表

        // 【新增】重置错误计数器（每次新对话都重置）
        errorCountMap.clear();

        // 【关键修复】重置状态和辅助组件，确保每次新对话都是从头开始
        setState(AgentState.RUNNING);
        getDeadLoopDetector().reset();
        getExecutionMonitor().start();

        // 定义输出函数
        Consumer<String> sendSse = (data) -> {
            try {
                sseEmitter.send(SseEmitter.event().data(data).build());
            } catch (IOException e) {
                log.error("SSE send error: " + e.getMessage());
            }
        };

        // 记录当前消息列表的起始位置（必须在 think() 加载历史消息之后！）
        // 这样保存时 subList(initialMessageCount, size) 只会包含：用户消息 + 助手回复
        // 历史消息不会被重复保存
        final int[] initialMessageCount = new int[1];
        initialMessageCount[0] = 0;

        // 标志位，记录是否已发送最终回复
        final boolean[] responseSent = new boolean[1];
        responseSent[0] = false;

        // ForkJoinPool.commonPool() 公共线程池去执行
        CompletableFuture.runAsync(() -> {
            try {
                // 添加用户消息
                getMessageList().add(new UserMessage(userPrompt));
                // 不需要发送用户消息到前端，前端已经显示了

                // 【关键修复】在添加用户消息后立即记录起始位置
                // 这样无论 think() 是否加载历史消息，都能正确包含用户消息
                initialMessageCount[0] = getMessageList().size() - 1;

                // 发送思考开始事件
                try {
                    sseEmitter.send(SseEmitter.event()
                            .name("thinking_start")
                            .data("{\"status\": \"started\"}"));
                } catch (IOException e) {
                    log.error("发送思考开始事件失败", e);
                }

                long thinkingStartTime = System.currentTimeMillis();

                // 执行多步推理和行动循环
                for (int i = 0; i < getMaxSteps(); i++) {
                    // Think - 使用流式思考（带实时步骤输出）
                    boolean shouldAct = false;
                    // 获取最后一条用户消息
                    String lastUserInput = null;

                    for (int j = getMessageList().size() - 1; j >= 0; j--) {
                        org.springframework.ai.chat.messages.Message msg = getMessageList().get(j);
                        if (msg instanceof UserMessage) {
                            lastUserInput = ((UserMessage) msg).getText();
                            break;
                        }
                    }
                    if (lastUserInput != null) {
                        // 判断问题类型
                        ThinkingChain.QuestionType type = QuestionClassifier.classify(lastUserInput);
                        log.info("问题分类: {} - {}", type,
                                lastUserInput.length() > 50 ? lastUserInput.substring(0, 50) + "..." : lastUserInput);
                        if (type == ThinkingChain.QuestionType.COMPLEX) {
                            // 复杂问题：使用流式思考
                            shouldAct = deepThinkStream(lastUserInput, (step) -> {
                                try {
                                    // 发送思考步骤到前端
                                    String stepJson = String.format(
                                            "{\"step\": \"%s\", \"timestamp\": %d}",
                                            Utils.escapeJson(step),
                                            System.currentTimeMillis());
                                    sseEmitter.send(SseEmitter.event()
                                            .name("thinking_step")
                                            .data(stepJson));
                                } catch (IOException e) {
                                    log.error("发送思考步骤失败", e);
                                }
                            });
                        } else {
                            // 简单问题：直接思考，不输出步骤
                            shouldAct = think();
                        }

                    } else {
                        // 没有用户输入，使用默认思考
                        shouldAct = think();
                    }

                    // 计算思考时间并发送思考结束事件
                    long thinkingEndTime = System.currentTimeMillis();
                    double thinkingTime = (thinkingEndTime - thinkingStartTime) / 1000.0;
                    try {
                        sseEmitter.send(SseEmitter.event()
                                .name("thinking_end")
                                .data(String.format("{\"status\": \"completed\", \"time\": %.1f}", thinkingTime)));
                    } catch (IOException e) {
                        log.error("发送思考结束事件失败", e);
                    }

                    if (!shouldAct) {
                        // 没有工具调用，获取最终响应
                        String finalResponse = getLastAssistantMessageText();
                        sendThinkingAndMessage(sseEmitter, sendSse, finalResponse);
                        responseSent[0] = true; // 标记已发送
                        break;
                    }

                    // Act - 执行工具
                    log.info("开始执行工具调用");
                    final String[] actResult = new String[1];
                    final boolean[] actCompleted = new boolean[1];
                    final Exception[] actException = new Exception[1]; // 记录异常
                    actCompleted[0] = false;

                    // 在单独的线程中执行工具，设置超时时间
                    Thread actThread = new Thread(() -> {
                        try {
                            actResult[0] = act();
                            actCompleted[0] = true;
                            log.info("工具执行完成");
                        } catch (Exception e) {
                            log.error("工具执行异常: {}", e.getMessage(), e);
                            actException[0] = e;
                            actResult[0] = "工具执行失败: " + e.getMessage();
                            actCompleted[0] = true;
                        }
                    });

                    actThread.start();

                    // 等待工具执行完成，最多等待30秒
                    try {
                        actThread.join(30000); // 30秒超时
                    } catch (InterruptedException e) {
                        log.error("等待工具执行被中断", e);
                    }

                    if (!actCompleted[0]) {
                        // 超时了，中断线程
                        actThread.interrupt();

                        // 获取最后调用的工具名称
                        String lastToolName = "未知工具";
                        try {
                            if (getToolCallChatResponse() != null && getToolCallChatResponse().hasToolCalls()) {
                                var toolCalls = getToolCallChatResponse().getResult().getOutput().getToolCalls();
                                if (!toolCalls.isEmpty()) {
                                    lastToolName = toolCalls.get(toolCalls.size() - 1).name();
                                }
                            }
                        } catch (Exception e) {
                            log.error("获取工具名称失败", e);
                        }

                        log.warn("工具执行超时（30秒），工具: {}", lastToolName);

                        // 超时后，直接回复用户，不再等待工具结果
                        String timeoutResponse = String.format(
                                "抱歉，工具 %s 执行时间过长（超过30秒）。让我直接回答你的问题。",
                                lastToolName);

                        // 添加一个助手消息，包含超时提示
                        AssistantMessage timeoutMessage = new AssistantMessage(timeoutResponse);
                        getMessageList().add(timeoutMessage);

                        // 发送超时提示给用户
                        sendThinkingAndMessage(sseEmitter, sendSse, timeoutResponse);
                        responseSent[0] = true;
                        break;
                    }

                    // 检查是否结束
                    if (getState() == AgentState.FINISHED) {
                        // 获取最终响应并发送
                        String finalResponse = getLastAssistantMessageText();
                        if (finalResponse != null && !finalResponse.isEmpty()) {
                            sendThinkingAndMessage(sseEmitter, sendSse, finalResponse);
                            responseSent[0] = true; // 标记已发送
                        }
                        break;
                    }

                    // 重置思考开始时间（为下一轮思考计时）
                    thinkingStartTime = System.currentTimeMillis();

                }

                // 【关键修复】循环结束后，如果还没有发送回复，发送最后的回复
                // 这样无论循环如何结束（达到最大步数、工具执行完成等），都会发送回复
                if (!responseSent[0]) {
                    String finalResponse = getLastAssistantMessageText();
                    if (finalResponse != null && !finalResponse.isEmpty()) {
                        sendThinkingAndMessage(sseEmitter, sendSse, finalResponse);
                    }
                }

                // 无论对话如何结束，都保存到数据库（只保存当前轮次新增的消息）
                if (chatId != null && !chatId.isEmpty()) {
                    try {
                        List<Message> currentMessages = getMessageList();
                        // 只获取当前轮次新增的消息（从 initialMessageCount[0] 开始）
                        List<Message> newMessages = currentMessages.subList(initialMessageCount[0],
                                currentMessages.size());
                        List<Message> filteredMessages = Utils.filterMessages(newMessages);
                        databaseBasedChatMemory.add(chatId, filteredMessages);
                    } catch (Exception saveException) {
                        log.error("保存对话失败：{}", saveException.getMessage());
                    }
                }

                sseEmitter.complete();
            } catch (Exception e) {
                log.error("Stream error: " + e.getMessage(), e);
                sendSse.accept("错误：" + e.getMessage());
                // 异常发生时也尝试保存对话（只保存当前轮次新增的消息）
                if (chatId != null && !chatId.isEmpty()) {
                    try {
                        List<Message> currentMessages = getMessageList();
                        List<Message> newMessages = currentMessages.subList(initialMessageCount[0],
                                currentMessages.size());
                        List<Message> filteredMessages = Utils.filterMessages(newMessages);
                        databaseBasedChatMemory.add(chatId, filteredMessages);
                    } catch (Exception saveException) {
                        log.error("保存对话失败：{}", saveException.getMessage());
                    }
                }
                sseEmitter.completeWithError(e);
            }

        });

        return sseEmitter;
    }

    /**
     * 重写 act 方法
     * 注意：数据库保存逻辑已在 runStream 方法中统一处理
     */
    @Override
    public String act() {
        // 调用父类的 act 方法
        return super.act();
    }

    /**
     * 重写 think 方法，手动集成数据库记忆
     * 使用 ToolCallAgent 的手动工具管理方式
     */
    @Override
    public boolean think() {
        try {

            // 1. 从数据库加载历史消息（只在第一次加载）
            if (!historyLoaded && chatId != null && !chatId.isEmpty()) {
                List<Message> historyMessages = databaseBasedChatMemory.get(chatId);
                if (historyMessages != null && !historyMessages.isEmpty()) {
                    // 【关键修复】历史消息应该插入到当前用户消息之前，而不是追加到末尾
                    // 这样才能保证消息顺序正确：历史消息 -> 当前用户消息 -> AI回复
                    int currentSize = getMessageList().size();
                    if (currentSize > 0) {
                        // 获取最后一条消息（当前用户消息）
                        Message currentUserMessage = getMessageList().remove(currentSize - 1);
                        // 添加历史消息
                        getMessageList().addAll(historyMessages);
                        // 重新添加当前用户消息
                        getMessageList().add(currentUserMessage);
                    } else {
                        // 如果消息列表为空，直接添加历史消息
                        getMessageList().addAll(historyMessages);
                    }
                }
                historyLoaded = true;
            }
            // 2. 添加 nextStepPrompt（如果有）
            if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
                UserMessage userMessage = new UserMessage(getNextStepPrompt());
                getMessageList().add(userMessage);
            }

            // 3. 使用父类的 think 方法（会禁用 Spring AI 内置工具执行）
            return super.think();

        } catch (Exception e) {
            // 获取错误信息
            String errorMsg = e.getMessage();
            String errorType = Utils.getErrorType(errorMsg);

            // 增加错误计数
            int errorCount = errorCountMap.getOrDefault(errorType, 0) + 1;
            errorCountMap.put(errorType, errorCount);
            log.warn("捕获到错误 [{}]，当前计数: {}/{}", errorType, errorCount, MAX_SAME_ERROR_COUNT);

            // 检查是否达到错误阈值
            if (errorCount >= MAX_SAME_ERROR_COUNT) {
                log.error("错误 [{}] 已出现 {} 次，发送友好提示给用户", errorType, errorCount);

                // 根据错误类型生成友好提示
                String friendlyMessage = Utils.generateFriendlyErrorMessage(errorType, errorMsg);

                // 添加友好的错误消息
                AssistantMessage errorMessage = new AssistantMessage(friendlyMessage);
                getMessageList().add(errorMessage);

                // 清空该错误的计数（避免下次对话继续累积）
                errorCountMap.remove(errorType);

                // 标记为完成，不再重试
                setState(AgentState.FINISHED);
                return false;
            }

            // 未达到阈值，继续抛出异常让 Spring AI 重试
            throw e;
        }
    }

    /**
     * 获取最后一条助手消息的文本
     */
    private String getLastAssistantMessageText() {
        List<Message> messages = getMessageList();
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message msg = messages.get(i);
            if (msg instanceof AssistantMessage) {
                return ((AssistantMessage) msg).getText();
            }
        }
        return null;
    }

    /**
     * 解析并发送思考和回复内容（流式输出）
     * 支持 DeepSeek 风格的【思考】和【回复】格式
     * 如果包含这两个标记，分别流式发送 thinking 和 message 事件
     * 否则流式发送 message 事件
     */
    private void sendThinkingAndMessage(SseEmitter sseEmitter, Consumer<String> sendSse, String content) {
        if (content == null || content.isEmpty()) {
            return;
        }

        // 检查是否包含【思考】标记
        if (content.contains("【思考】")) {
            // 检查是否包含【回复】标记
            if (content.contains("【回复】")) {
                // 完整格式：【思考】...【回复】...
                String thinkingContent = Utils.extractContent(content, "【思考】", "【回复】");
                String replyContent = Utils.extractReplyContent(content);

                // 流式发送思考内容（使用 thinking 事件名）
                if (thinkingContent != null && !thinkingContent.isEmpty()) {
                    sendStreamContent(sseEmitter, "thinking", thinkingContent);
                }

                // 流式发送回复内容（使用 message 事件名）
                if (replyContent != null && !replyContent.isEmpty()) {
                    sendStreamContent(sseEmitter, "message", replyContent);
                }
            } else {
                // 只有【思考】，没有【回复】（通常是工具调用阶段）
                // 这种情况下，不发送任何内容给用户
                // 因为工具执行后会有新的回复
            }
        } else {
            // 没有思考标记，直接流式发送回复内容（使用 message 事件名）
            sendStreamContent(sseEmitter, "message", content);
        }
    }

    /**
     * 流式发送内容（逐字发送，模拟打字效果）
     */
    private void sendStreamContent(SseEmitter sseEmitter, String eventName, String content) {
        try {
            // 按行分割内容
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    // 发送每一行
                    sseEmitter.send(SseEmitter.event().name(eventName).data(line).build());
                    // 短暂延迟，模拟流式输出（可选）
                    try {
                        Thread.sleep(50); // 50ms 延迟
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (IOException e) {
            log.error("流式发送内容失败: {}", e.getMessage());
        }
    }

}
