<template>
  <div class="chat-window">
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>对话历史</h2>
        <button class="new-chat-btn" @click="createNewChat">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M8 1v14M1 8h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          新建对话
        </button>
      </div>
      <div class="conversation-list">
        <div 
          v-for="(conversation, index) in conversations" 
          :key="conversation.id"
          :class="['conversation-item', { active: currentConversationId === conversation.id }]"
          @click="switchConversation(conversation.id)"
        >
          <button 
            class="delete-conversation-btn" 
            @click.stop="confirmDeleteConversation(conversation.id)"
            title="删除对话"
          >
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M1 1L13 13M1 13L13 1" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
          <div class="conversation-preview">
            <p class="conversation-title">{{ conversation.title || '新对话' }}</p>
            <p class="conversation-last-message">{{ conversation.lastMessage || '暂无消息' }}</p>
          </div>
          <div class="conversation-time">{{ formatConversationTime(conversation.updatedAt) }}</div>
        </div>
      </div>
    </div>
    
    <div class="main-content">
      <div class="page-header">
        <h1>AI 助手</h1>
        <p>智能对话助手，随时为您提供帮助</p>
      </div>
      
      <div class="chat-container">
        <div class="chat-messages" ref="messagesContainer">
        <div class="message ai-message">
          <div class="message-avatar">
            <img src="/logo.jpeg" alt="AI助手" class="ai-avatar-image" />
          </div>
          <div class="message-content">
            <p>您好！我是您的NAS AI助手，有什么可以帮助您的吗？</p>
            <span class="message-time">刚刚</span>
          </div>
        </div>
        
        <div v-for="(message, index) in messages" :key="index" :class="['message', message.sender === 'user' ? 'user-message' : 'ai-message']">
          <div v-if="message.sender === 'ai'" class="message-avatar">
            <img src="/logo.jpeg" alt="AI助手" class="ai-avatar-image" />
          </div>
          <div class="message-content">
            <p>{{ message.text }}</p>
            <span class="message-time">{{ formatTime(message.time) }}</span>
          </div>
          <div v-if="message.sender === 'user'" class="message-avatar">
            <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
              <circle cx="16" cy="16" r="14" stroke="currentColor" stroke-width="2"/>
              <circle cx="16" cy="12" r="4" fill="currentColor"/>
              <path d="M8 24c0-4 4-6 8-6s8 2 8 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
        </div>
        
        <div v-if="isTyping" class="message ai-message">
          <div class="message-avatar">
            <img src="/logo.jpeg" alt="AI助手" class="ai-avatar-image" />
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="chat-input">
        <div class="input-container">
          <input 
            v-model="inputMessage" 
            @keyup.enter="sendMessage"
            type="text" 
            placeholder="输入消息..." 
            class="message-input"
            :disabled="isTyping"
          />
          <button class="send-button" @click="sendMessage" :disabled="!inputMessage.trim() || isTyping">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M18 2l-8 16M18 2l-4 16M18 2H6M18 2l-4 8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
        
        <!-- 功能按钮 -->
        <div class="feature-buttons">
          <button 
            class="feature-btn" 
            :class="{ active: enableRAG }" 
            @click="toggleRAG"
            :disabled="isTyping || enableThinking"
          >
            <span class="btn-icon">📚</span>
            <span>启用RAG</span>
          </button>
          <button 
            class="feature-btn" 
            :class="{ active: enableTools }" 
            @click="toggleTools"
            :disabled="isTyping || enableThinking"
          >
            <span class="btn-icon">🔧</span>
            <span>调用工具</span>
          </button>
          <button 
            class="feature-btn" 
            :class="{ active: enableThinking }" 
            @click="toggleThinking"
            :disabled="isTyping"
          >
            <span class="btn-icon">🤔</span>
            <span>推理与思考</span>
          </button>
        </div>
      </div>
    </div>
    
    <!-- 删除确认对话框 -->
    <div v-if="showDeleteConfirm" class="delete-confirm-overlay" @click="cancelDelete">
      <div class="delete-confirm-dialog" @click.stop>
        <h3>确认删除对话</h3>
        <p>确定要删除这个对话吗？此操作无法撤销。</p>
        <div class="delete-confirm-buttons">
          <button class="cancel-btn" @click="cancelDelete">取消</button>
          <button class="confirm-btn" @click="confirmDelete">删除</button>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { chatAPI } from '../services/api.js'

const inputMessage = ref('')
const messages = ref([])
const isTyping = ref(false)
const messagesContainer = ref(null)

// 功能按钮状态
const enableRAG = ref(false)
const enableTools = ref(false)
const enableThinking = ref(false)

// 对话历史管理
const conversations = ref([])
const currentConversationId = ref(null)

// 删除确认相关
const showDeleteConfirm = ref(false)
const conversationToDelete = ref(null)

// 初始化对话历史
const initializeConversations = () => {
  // 从localStorage加载对话历史
  const saved = localStorage.getItem('chat_conversations')
  if (saved) {
    conversations.value = JSON.parse(saved)
  } else {
    // 创建默认对话
    createNewChat()
  }
  
  // 设置当前对话ID
  if (conversations.value.length > 0) {
    currentConversationId.value = conversations.value[0].id
    // 加载当前对话的消息
    loadConversationMessages(currentConversationId.value)
  }
}

// 创建新对话
const createNewChat = () => {
  const newConversation = {
    id: Date.now().toString(),
    title: '新对话',
    lastMessage: '',
    messages: [],
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
  
  conversations.value.unshift(newConversation)
  currentConversationId.value = newConversation.id
  messages.value = []
  
  saveConversations()
}

// 切换对话
const switchConversation = (conversationId) => {
  currentConversationId.value = conversationId
  loadConversationMessages(conversationId)
}

// 加载对话消息
const loadConversationMessages = (conversationId) => {
  const conversation = conversations.value.find(c => c.id === conversationId)
  if (conversation) {
    messages.value = conversation.messages || []
    nextTick(() => scrollToBottom())
  }
}

// 保存对话历史到localStorage
const saveConversations = () => {
  localStorage.setItem('chat_conversations', JSON.stringify(conversations.value))
}

// 格式化对话时间
const formatConversationTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  
  const days = Math.floor(hours / 24)
  return `${days}天前`
}

// 确认删除对话
const confirmDeleteConversation = (conversationId) => {
  conversationToDelete.value = conversationId
  showDeleteConfirm.value = true
}

// 取消删除
const cancelDelete = () => {
  showDeleteConfirm.value = false
  conversationToDelete.value = null
}

// 确认删除
const confirmDelete = () => {
  if (conversationToDelete.value) {
    const isCurrentConversation = currentConversationId.value === conversationToDelete.value
    
    // 删除对话
    conversations.value = conversations.value.filter(c => c.id !== conversationToDelete.value)
    
    // 如果删除的是当前对话，创建新对话或切换到其他对话
    if (isCurrentConversation) {
      if (conversations.value.length > 0) {
        currentConversationId.value = conversations.value[0].id
        loadConversationMessages(currentConversationId.value)
      } else {
        createNewChat()
      }
    }
    
    saveConversations()
    cancelDelete()
  }
}

// 切换RAG状态
const toggleRAG = () => {
  enableRAG.value = !enableRAG.value
}

// 切换工具状态
const toggleTools = () => {
  enableTools.value = !enableTools.value
}

// 切换推理与思考状态
const toggleThinking = () => {
  enableThinking.value = !enableThinking.value
  // 如果启用推理与思考，禁用其他两个选项
  if (enableThinking.value) {
    enableRAG.value = false
    enableTools.value = false
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isTyping.value) return
  
  // 如果没有对话历史，创建新对话
  if (conversations.value.length === 0 || !currentConversationId.value) {
    createNewChat()
  }
  
  // 添加用户消息
  const userMessage = {
    text: inputMessage.value.trim(),
    sender: 'user',
    time: new Date()
  }
  messages.value.push(userMessage)
  
  const userInput = inputMessage.value.trim()
  inputMessage.value = ''
  
  // 更新当前对话的消息和最后消息
  updateConversation(userMessage.text)
  
  // 滚动到底部
  await nextTick()
  scrollToBottom()
  
  // 显示AI正在输入
  isTyping.value = true
  await nextTick()
  scrollToBottom()
  
  // 调用AI回复
  try {
    const aiResponse = await generateAIResponse(userInput)
    messages.value.push({
      text: aiResponse,
      sender: 'ai',
      time: new Date()
    })
    
    // 更新对话的最后消息为AI回复
    updateConversation(aiResponse)
  } catch (error) {
    console.error('发送消息失败:', error)
    messages.value.push({
      text: '抱歉，我无法处理您的请求。请稍后再试。',
      sender: 'ai',
      time: new Date()
    })
  }
  
  isTyping.value = false
  nextTick(() => {
    scrollToBottom()
  })
}

// 更新对话信息
const updateConversation = (lastMessage) => {
  const conversation = conversations.value.find(c => c.id === currentConversationId.value)
  if (conversation) {
    conversation.messages = [...messages.value]
    conversation.lastMessage = lastMessage
    conversation.updatedAt = new Date().toISOString()
    
    // 如果是第一条消息，更新对话标题
    if (conversation.messages.length === 1 && conversation.title === '新对话') {
      conversation.title = lastMessage.substring(0, 20) + (lastMessage.length > 20 ? '...' : '')
    }
    
    // 将当前对话移到顶部
    conversations.value = conversations.value.filter(c => c.id !== currentConversationId.value)
    conversations.value.unshift(conversation)
    
    saveConversations()
  }
}

const generateAIResponse = async (userInput) => {
  try {
    const response = await chatAPI.sendMessage(userInput, {
      chatId: currentConversationId.value || 'default',
      enableRAG: enableRAG.value,
      enableTools: enableTools.value,
      enableThinking: enableThinking.value
    })
    
    // 处理SSE流式响应
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let fullResponse = ''
    
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      
      const chunk = decoder.decode(value)
      const lines = chunk.split('\n')
      
      for (const line of lines) {
        if (line.startsWith('data: ')) {
          const data = line.substring(6)
          if (data === '[DONE]') {
            return fullResponse
          }
          fullResponse += data
          // 可以在这里实时更新UI显示
        }
      }
    }
    
    return fullResponse
  } catch (error) {
    console.error('AI回复失败:', error)
    return '抱歉，我无法处理您的请求。请稍后再试。'
  }
}

const formatTime = (date) => {
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  
  const days = Math.floor(hours / 24)
  return `${days}天前`
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

onMounted(() => {
  initializeConversations()
  scrollToBottom()
})
</script>

<style scoped>
.chat-window {
  padding: 0;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 20px;
  min-height: calc(100vh - 48px);
  color: #ffffff;
  display: flex;
  flex-direction: row;
}

.sidebar {
  width: 320px;
  background: rgba(100, 100, 100, 0.3);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  flex-direction: column;
  padding: 24px 0;
}

.sidebar-header {
  padding: 0 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 16px;
}

.sidebar-header h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: #ffffff;
}

.new-chat-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.new-chat-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.conversation-item {
  padding: 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
}

.conversation-item:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

.conversation-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
  border-color: rgba(102, 126, 234, 0.5);
}

.conversation-preview {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.conversation-title {
  font-size: 14px;
  font-weight: 500;
  color: #ffffff;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-last-message {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
  align-self: flex-end;
}

.delete-conversation-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 6px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.3s ease;
  opacity: 0;
}

.conversation-item:hover .delete-conversation-btn {
  opacity: 1;
}

.delete-conversation-btn:hover {
  background: rgba(239, 68, 68, 0.3);
  color: #ef4444;
}

.main-content {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-header p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-height: calc(100vh - 300px);
}

.message {
  display: flex;
  gap: 16px;
  max-width: 70%;
  align-items: flex-start;
}

.ai-message {
  align-self: flex-start;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
  overflow: hidden;
}

.ai-avatar-image {
  width: 32px;
  height: 32px;
  object-fit: cover;
  border-radius: 50%;
}

.user-message .message-avatar {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.message-content {
  background: rgba(255, 255, 255, 0.05);
  padding: 16px 20px;
  border-radius: 16px;
  flex: 1;
}

.user-message .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.message-content p {
  color: #ffffff;
  margin: 0 0 8px 0;
  line-height: 1.5;
  font-size: 15px;
  white-space: pre-wrap;
}

.message-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: 0s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.6;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

.chat-input {
  padding: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
}

.feature-buttons {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  justify-content: flex-start;
}

.feature-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.feature-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
  color: #ffffff;
}

.feature-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.feature-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 16px;
}

.input-container {
  display: flex;
  gap: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 12px;
}

.message-input {
  flex: 1;
  background: transparent;
  border: none;
  color: #ffffff;
  outline: none;
  font-size: 15px;
  padding: 8px;
}

.message-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.message-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.send-button {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: #ffffff;
  cursor: pointer;
  transition: all 0.3s ease;
}

.send-button:hover:not(:disabled) {
  transform: scale(1.05);
}

.send-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar,
.conversation-list::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track,
.conversation-list::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb,
.conversation-list::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover,
.conversation-list::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

@media (max-width: 768px) {
  .chat-window {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
    max-height: 200px;
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding: 16px 0;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .message {
    max-width: 85%;
  }
  
  .chat-messages {
    padding: 16px;
  }
  
  .chat-input {
    padding: 16px;
  }
  
  .feature-buttons {
    flex-wrap: wrap;
  }
}

.delete-confirm-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.delete-confirm-dialog {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

.delete-confirm-dialog h3 {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 12px 0;
}

.delete-confirm-dialog p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 24px 0;
  line-height: 1.5;
}

.delete-confirm-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.cancel-btn,
.confirm-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.8);
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.confirm-btn {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: #ffffff;
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
}
</style>
