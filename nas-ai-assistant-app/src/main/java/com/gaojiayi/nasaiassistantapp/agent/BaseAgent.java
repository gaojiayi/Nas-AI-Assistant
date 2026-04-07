package com.gaojiayi.nasaiassistantapp.agent;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;

import com.gaojiayi.nasaiassistantapp.agent.detector.DeadLoopDetector;
import com.gaojiayi.nasaiassistantapp.agent.model.AgentState;
import com.gaojiayi.nasaiassistantapp.agent.monitor.ExecutionMonitor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public  abstract class BaseAgent {

    // ====== 核心属性 ======
    private String name;
    private String systemPrompt;
    private String nextStepPrompt;

    // ====== 状态管理 ======
    private AgentState state = AgentState.IDLE;
    private int maxSteps = 10;
    private int currentStep = 0;

    // ====== LLM 和消息 ======
    private ChatClient chatClient;
    private List<Message> messageList = new ArrayList<>();

    // ====== 辅助组件（重构后使用组合模式）======
    private DeadLoopDetector deadLoopDetector = new DeadLoopDetector();
    private ExecutionMonitor executionMonitor = new ExecutionMonitor();
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSystemPrompt() {
        return systemPrompt;
    }
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    public String getNextStepPrompt() {
        return nextStepPrompt;
    }
    public void setNextStepPrompt(String nextStepPrompt) {
        this.nextStepPrompt = nextStepPrompt;
    }
    public AgentState getState() {
        return state;
    }
    public void setState(AgentState state) {
        this.state = state;
    }
    public int getMaxSteps() {
        return maxSteps;
    }
    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }
    public int getCurrentStep() {
        return currentStep;
    }
    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }
    public ChatClient getChatClient() {
        return chatClient;
    }
    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    public List<Message> getMessageList() {
        return messageList;
    }
    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
    // ====== 辅助方法（委托给检测器和监控器）======

    /**
     * 记录响应（用于循环检测）
     */
    protected void recordResponse(String response) {
        deadLoopDetector.recordResponse(response);
    }

    /**
     * 记录工具调用（用于循环检测）
     */
    protected void recordToolCall(String toolName, String arguments) {
        deadLoopDetector.recordToolCall(toolName, arguments);
    }

    /**
     * 记录已尝试的工具
     */
    protected void recordAttemptedTool(String toolName) {
        // 委托给 deadLoopDetector
        deadLoopDetector.recordToolCall(toolName, null);
    }

    /**
     * 记录失败的尝试
     */
    protected void recordFailedAttempt() {
        deadLoopDetector.recordFailure();
    }

    /**
     * 获取执行已用时间
     */
    protected long getExecutionElapsedTime() {
        return executionMonitor.getElapsedTime();
    }

    /**
     * 检查执行是否超时
     */
    protected boolean isExecutionTimeout() {
        return executionMonitor.isTimeout();
    }

    /**
     * 设置最大执行时间
     */
    public void setMaxExecutionTimeMs(long maxExecutionTimeMs) {
        executionMonitor.setMaxExecutionTimeMs(maxExecutionTimeMs);
    }

    /**
     * 设置相似度阈值
     */
    public void setSimilarityThreshold(double similarityThreshold) {
        deadLoopDetector.setSimilarityThreshold(similarityThreshold);
    }

    /**
     * 获取死循环检测器（供子类使用）
     */
    protected DeadLoopDetector getDeadLoopDetector() {
        return deadLoopDetector;
    }

    /**
     * 获取执行监控器（供子类使用）
     */
    protected ExecutionMonitor getExecutionMonitor() {
        return executionMonitor;
    }


     /**
     * 执行单个步骤（子类必须实现）
     *
     * @return 步骤执行结果
     */
    public abstract String step();

    /**
     * 清理资源（子类可以重写）
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }
    
    
}
