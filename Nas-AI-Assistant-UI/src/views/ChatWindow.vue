<template>
  <div class="chat-window">
    <div class="page-header">
      <h1>AI 助手</h1>
      <p>智能对话助手，随时为您提供帮助</p>
    </div>
    
    <div class="content-area">
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
              <p class="conversation-title">{{ getConversationTitle(conversation) }}</p>
              <p class="conversation-last-message">{{ conversation.lastMessage || '暂无消息' }}</p>
            </div>
            <div class="conversation-time">{{ formatConversationTime(conversation.updatedAt) }}</div>
          </div>
        </div>
      </div>
      
      <div class="main-content">
        <div class="chat-container">
        <div class="chat-messages" ref="messagesContainer">
        <div v-for="(message, index) in messages" :key="index" :class="['message', message.sender === 'user' ? 'user-message' : 'ai-message', { 'error-message': message.isError }]">
          <div v-if="message.sender === 'ai'" class="message-avatar">
            <img src="/logo.jpeg" alt="AI助手" class="ai-avatar-image" />
          </div>
          <div class="message-content">
            <div v-if="message.thinkingSteps && message.thinkingSteps.length > 0" class="thinking-process">
              <div class="thinking-header">
                <span class="thinking-icon">🤔</span>
                <span class="thinking-title">AI 思考过程...</span>
              </div>
              <div class="thinking-steps">
                <div v-for="(step, stepIndex) in message.thinkingSteps" :key="stepIndex" class="thinking-step">
                  {{ stepIndex + 1 }}. {{ step }}
                </div>
              </div>
            </div>
            
            <div class="message-text-container">
              <div v-if="message.sender === 'ai'" class="message-text markdown-body" v-html="renderMarkdown(message.text)"></div>
              <p v-if="message.sender === 'user'" class="message-text">{{ message.text }}</p>
            </div>
            
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
    </div>
    
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
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'

const md = new MarkdownIt({
  html: false,
  linkify: true,
  typographer: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs"><code>' +
          hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
          '</code></pre>'
      } catch (__) {}
    }
    return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + '</code></pre>'
  }
})

const renderMarkdown = (text) => {
  if (!text) return ''
  return md.render(text)
}

const throttle = (func, delay) => {
  let lastCall = 0
  return function(...args) {
    const now = Date.now()
    if (now - lastCall < delay) return
    lastCall = now
    return func.apply(this, args)
  }
}

const throttledHighlight = throttle(() => {
  if (typeof hljs !== 'undefined') {
    document.querySelectorAll('pre code').forEach((block) => {
      hljs.highlightElement(block)
    })
  }
}, 200)

const inputMessage = ref('')
const messages = ref([])
const isTyping = ref(false)
const messagesContainer = ref(null)

const enableRAG = ref(false)
const enableTools = ref(false)
const enableThinking = ref(false)

const conversations = ref([])
const currentConversationId = ref(null)

const showDeleteConfirm = ref(false)
const conversationToDelete = ref(null)

const initializeConversations = async () => {
  try {
    const apiConversations = await chatAPI.getConversations()
    
    conversations.value = apiConversations.map(conv => ({
      id: conv.conversationId,
      lastMessage: conv.lastMessage || '暂无消息',
      updatedAt: conv.lastMessageTime
    }))
    
    if (conversations.value.length > 0) {
      currentConversationId.value = conversations.value[0].id
      loadConversationMessages(currentConversationId.value)
    } else {
      createNewChat()
    }
  } catch (error) {
    console.error('加载对话历史失败:', error)
    createNewChat()
  }
}

const createNewChat = () => {
  const newConversation = {
    id: Date.now().toString(),
    lastMessage: '',
    updatedAt: new Date()
  }
  
  conversations.value.unshift(newConversation)
  currentConversationId.value = newConversation.id
  messages.value = []
}

const switchConversation = (conversationId) => {
  currentConversationId.value = conversationId
  loadConversationMessages(conversationId)
}

const loadConversationMessages = async (conversationId) => {
  try {
    const apiMessages = await chatAPI.getConversationHistory(conversationId)
    
    messages.value = apiMessages.map(msg => ({
      sender: msg.role === 'assistant' ? 'ai' : msg.role,
      text: msg.content,
      time: new Date(msg.createTime),
      thinkingSteps: msg.thinkingSteps || [],
      isComplete: true
    }))
    
    nextTick(() => {
      scrollToBottom()
      if (typeof hljs !== 'undefined') {
        document.querySelectorAll('pre code').forEach((block) => {
          hljs.highlightElement(block)
        })
      }
    })
  } catch (error) {
    console.error('加载对话消息失败:', error)
    messages.value = []
  }
}

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

const getConversationTitle = (conversation) => {
  if (!conversation.lastMessage) return '新对话'
  const title = conversation.lastMessage.substring(0, 20)
  return conversation.lastMessage.length > 20 ? title + '...' : title
}

const confirmDeleteConversation = (conversationId) => {
  conversationToDelete.value = conversationId
  showDeleteConfirm.value = true
}

const cancelDelete = () => {
  showDeleteConfirm.value = false
  conversationToDelete.value = null
}

const confirmDelete = async () => {
  if (conversationToDelete.value) {
    try {
      const result = await chatAPI.deleteConversation(conversationToDelete.value)
      
      if (result.code === 0) {
        const isCurrentConversation = currentConversationId.value === conversationToDelete.value
        
        conversations.value = conversations.value.filter(c => c.id !== conversationToDelete.value)
        
        if (isCurrentConversation) {
          if (conversations.value.length > 0) {
            currentConversationId.value = conversations.value[0].id
            loadConversationMessages(currentConversationId.value)
          } else {
            createNewChat()
          }
        }
        
        cancelDelete()
      } else {
        alert('删除失败: ' + result.message)
      }
    } catch (error) {
      console.error('删除对话失败:', error)
      alert('删除失败，请稍后重试')
    }
  }
}

const toggleRAG = () => {
  enableRAG.value = !enableRAG.value
}

const toggleTools = () => {
  const oldValue = enableTools.value
  enableTools.value = !enableTools.value
  console.log('toggleTools called:', oldValue, '->', enableTools.value)
}

const toggleThinking = () => {
  enableThinking.value = !enableThinking.value
  if (enableThinking.value) {
    enableRAG.value = false
    enableTools.value = false
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isTyping.value) return
  
  if (conversations.value.length === 0 || !currentConversationId.value) {
    createNewChat()
  }
  
  const userMessage = {
    text: inputMessage.value.trim(),
    sender: 'user',
    time: new Date(),
    isComplete: true
  }
  messages.value.push(userMessage)
  
  const userInput = inputMessage.value.trim()
  inputMessage.value = ''
  
  refreshConversations()
  
  await nextTick()
  scrollToBottom()
  
  isTyping.value = true
  console.log('sendMessage - enableTools:', enableTools.value, 'enableRAG:', enableRAG.value, 'enableThinking:', enableThinking.value)
  await nextTick()
  scrollToBottom()
  
  try {
    await generateAIResponse(userInput)
  } catch (error) {
    console.error('发送消息失败:', error)
  }
  
  isTyping.value = false
  nextTick(() => {
    scrollToBottom()
  })
}

const refreshConversations = async () => {
  try {
    const apiConversations = await chatAPI.getConversations()
    
    conversations.value = apiConversations.map(conv => ({
      id: conv.conversationId,
      lastMessage: conv.lastMessage || '暂无消息',
      updatedAt: conv.lastMessageTime
    }))
  } catch (error) {
    console.error('刷新对话列表失败:', error)
  }
}

const thinkingSteps = ref([])

const generateAIResponse = async (userInput) => {
  try {
    thinkingSteps.value = []
    
    console.log('generateAIResponse - enableTools:', enableTools.value, 'enableRAG:', enableRAG.value, 'enableThinking:', enableThinking.value)
    const stream = await chatAPI.sendMessage(userInput, {
      chatId: currentConversationId.value || 'default',
      enableRAG: enableRAG.value,
      enableTools: enableTools.value,
      enableThinking: enableThinking.value
    })
    
    const reader = stream.getReader()
    const decoder = new TextDecoder()
    let buffer = ""
    let fullResponse = ''
    let aiMessageIndex = -1
    
    const aiMessage = {
      text: '',
      sender: 'ai',
      time: new Date(),
      thinkingSteps: [],
      isComplete: false
    }
    messages.value.push(aiMessage)
    aiMessageIndex = messages.value.length - 1
    
    while (true) {
      const { done, value } = await reader.read()
      
      if (done) {
        if (buffer.length > 0) {
          processSseLine(buffer, aiMessageIndex, fullResponse)
        }
        if (aiMessageIndex >= 0) {
          messages.value[aiMessageIndex].isComplete = true
          await nextTick()
          if (typeof hljs !== 'undefined') {
            document.querySelectorAll('pre code').forEach((block) => {
              hljs.highlightElement(block)
            })
          }
        }
        if (currentConversationId.value) {
          await loadConversationMessages(currentConversationId.value)
        }
        break
      }
      
      const text = decoder.decode(value, { stream: true })
      buffer += text
      
      const lines = buffer.split('\n')
      buffer = lines.pop() || ""
      
      for (const line of lines) {
        fullResponse = processSseLine(line, aiMessageIndex, fullResponse)
      }
    }
    
    return fullResponse
  } catch (error) {
    console.error('AI回复失败:', error)
    
    const errorMessage = {
      text: '抱歉，我无法处理您的请求。请稍后再试。',
      sender: 'ai',
      time: new Date(),
      isError: true,
      isComplete: true
    }
    messages.value.push(errorMessage)
    
    await nextTick()
    smartScroll()
    
    return '抱歉，我无法处理您的请求。请稍后再试。'
  }
}

const processSseLine = (line, aiMessageIndex, fullResponse) => {
  if (!line.trim()) return fullResponse

  if (line.startsWith('event:')) {
    return fullResponse
  }

  if (line.startsWith('data:')) {
    const data = line.slice(5).trim()

    if (data === '[DONE]' || !data) {
      return fullResponse
    }

    if (data.startsWith('thinking:')) {
      const step = data.substring(9).trim()
      thinkingSteps.value.push(step)
      if (aiMessageIndex >= 0) {
        messages.value[aiMessageIndex].thinkingSteps = [...thinkingSteps.value]
      }
    } else if (data.startsWith('error:')) {
      const errorMsg = data.substring(6).trim()
      throw new Error(errorMsg)
    } else {
      fullResponse += data
      if (aiMessageIndex >= 0) {
        messages.value[aiMessageIndex].text = fullResponse
        smartScroll()
        throttledHighlight()
      }
    }
  }

  return fullResponse
}

const smartScroll = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      const container = messagesContainer.value
      const isNearBottom = container.scrollHeight - container.scrollTop - container.clientHeight < 100
      
      if (isNearBottom) {
        scrollToBottom()
      }
    }
  })
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

onMounted(async () => {
  enableRAG.value = false
  enableTools.value = false
  enableThinking.value = false
  console.log('onMounted - enableTools:', enableTools.value, 'enableRAG:', enableRAG.value, 'enableThinking:', enableThinking.value)
  await initializeConversations()
  scrollToBottom()
})
</script>

<style scoped>
.chat-window {
  padding: 24px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 20px;
  min-height: calc(100vh - 48px);
  color: #ffffff;
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

.content-area {
  display: flex;
  gap: 24px;
  flex: 1;
  min-height: 0;
}

.sidebar {
  width: 320px;
  height: 600px;
  background: rgba(100, 100, 100, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  padding: 24px 0;
  overflow: hidden;
  flex-shrink: 0;
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
  overflow-x: hidden;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  scroll-behavior: smooth;
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
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.chat-container {
  height: 600px;
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
  min-height: 0;
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
}

.thinking-process {
  background: rgba(102, 126, 234, 0.1);
  border: 1px solid rgba(102, 126, 234, 0.3);
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
}

.thinking-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.8);
}

.thinking-icon {
  font-size: 16px;
  animation: pulse 2s infinite ease-in-out;
}

.thinking-title {
  color: rgba(255, 255, 255, 0.9);
}

.thinking-steps {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.thinking-step {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-left: 3px solid rgba(102, 126, 234, 0.6);
  border-radius: 0 8px 8px 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.4;
}

.message-text-container {
  position: relative;
}

.message-text {
  color: #ffffff;
  margin: 0;
  line-height: 1.5;
  font-size: 15px;
  word-wrap: break-word;
}

.message-text.markdown-body {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.7;
}

.error-message {
  background: rgba(239, 68, 68, 0.1) !important;
  border-color: rgba(239, 68, 68, 0.3) !important;
}

.error-message .message-content {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
}

.error-message .message-text {
  color: #fca5a5;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
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
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  transition: background 0.2s ease;
}

.chat-messages::-webkit-scrollbar-thumb:hover,
.conversation-list::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

.chat-messages,
.conversation-list {
  scroll-behavior: smooth;
}

@media (max-width: 768px) {
  .chat-window {
    padding: 16px;
  }
  
  .page-header {
    margin-bottom: 16px;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .page-header p {
    font-size: 14px;
  }
  
  .content-area {
    flex-direction: column;
    gap: 16px;
  }
  
  .sidebar {
    width: 100%;
    height: 300px;
    padding: 16px 0;
  }
  
  .main-content {
    flex: 1;
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

<style>
.markdown-body {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.7;
  word-wrap: break-word;
}

.markdown-body p {
  margin: 0 0 12px 0;
}

.markdown-body p:last-child {
  margin-bottom: 0;
}

.markdown-body h1, .markdown-body h2, .markdown-body h3,
.markdown-body h4, .markdown-body h5, .markdown-body h6 {
  margin: 16px 0 8px 0;
  font-weight: 600;
  color: #ffffff;
}

.markdown-body h1 { font-size: 1.4em; }
.markdown-body h2 { font-size: 1.25em; }
.markdown-body h3 { font-size: 1.1em; }

.markdown-body ul, .markdown-body ol {
  margin: 8px 0;
  padding-left: 24px;
}

.markdown-body li {
  margin: 4px 0;
}

.markdown-body blockquote {
  margin: 8px 0;
  padding: 8px 16px;
  border-left: 4px solid rgba(102, 126, 234, 0.6);
  background: rgba(102, 126, 234, 0.1);
  color: rgba(255, 255, 255, 0.8);
}

.markdown-body code {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.9em;
  background: rgba(255, 255, 255, 0.1);
  color: #e2b86b;
  font-family: 'Menlo', 'Monaco', 'Courier New', monospace;
}

.markdown-body pre {
  margin: 12px 0;
  border-radius: 8px;
  overflow-x: auto;
  position: relative;
}

.markdown-body pre code {
  display: block;
  padding: 16px;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.4);
  color: rgba(255, 255, 255, 0.85);
  font-size: 0.85em;
  line-height: 1.6;
}

.markdown-body pre.hljs {
  background: rgba(0, 0, 0, 0.4);
  border-radius: 8px;
  margin: 12px 0;
}

.markdown-body pre.hljs code {
  background: transparent;
}

.markdown-body table {
  border-collapse: collapse;
  margin: 12px 0;
  width: 100%;
}

.markdown-body th, .markdown-body td {
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 8px 12px;
  text-align: left;
}

.markdown-body th {
  background: rgba(255, 255, 255, 0.1);
  font-weight: 600;
}

.markdown-body a {
  color: #667eea;
  text-decoration: none;
}

.markdown-body a:hover {
  text-decoration: underline;
}

.markdown-body hr {
  border: none;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  margin: 16px 0;
}

.markdown-body strong {
  color: #ffffff;
  font-weight: 600;
}

.markdown-body em {
  color: rgba(255, 255, 255, 0.85);
}
</style>
