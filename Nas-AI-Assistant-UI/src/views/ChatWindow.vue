<template>
  <div class="chat-window">
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
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'

const inputMessage = ref('')
const messages = ref([])
const isTyping = ref(false)
const messagesContainer = ref(null)

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isTyping.value) return
  
  // 添加用户消息
  const userMessage = {
    text: inputMessage.value.trim(),
    sender: 'user',
    time: new Date()
  }
  messages.value.push(userMessage)
  
  const userInput = inputMessage.value.trim()
  inputMessage.value = ''
  
  // 滚动到底部
  await nextTick()
  scrollToBottom()
  
  // 显示AI正在输入
  isTyping.value = true
  await nextTick()
  scrollToBottom()
  
  // 模拟AI回复
  setTimeout(() => {
    const aiResponse = generateAIResponse(userInput)
    messages.value.push({
      text: aiResponse,
      sender: 'ai',
      time: new Date()
    })
    
    isTyping.value = false
    nextTick(() => {
      scrollToBottom()
    })
  }, 1500)
}

const generateAIResponse = (userInput) => {
  const responses = [
    '我理解您的需求。作为NAS AI助手，我可以帮助您管理存储、配置系统、优化性能等。',
    '这是一个很好的问题！让我为您提供一些关于NAS管理的建议。',
    '根据您的描述，我建议您可以尝试以下解决方案来优化您的NAS系统。',
    '感谢您的咨询！我会尽力为您提供最准确和有用的信息。',
    '我正在分析您的需求。作为专业的NAS助手，我在这方面有很多经验可以分享。'
  ]
  
  // 简单的关键词匹配回复
  if (userInput.includes('存储') || userInput.includes('空间')) {
    return '关于存储管理，我建议您定期检查磁盘空间使用情况，可以考虑启用数据压缩和去重功能来优化存储效率。'
  } else if (userInput.includes('备份') || userInput.includes('备份')) {
    return '备份是NAS的重要功能。我建议您设置3-2-1备份策略：3份数据，2种不同介质，1份离线备份。'
  } else if (userInput.includes('性能') || userInput.includes('速度')) {
    return '性能优化方面，建议您检查网络配置、使用SSD缓存、合理配置RAID级别来提升NAS性能。'
  } else if (userInput.includes('安全') || userInput.includes('权限')) {
    return '安全性很重要！建议您启用双因素认证、定期更新系统、配置适当的用户权限和防火墙规则。'
  }
  
  return responses[Math.floor(Math.random() * responses.length)]
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
  scrollToBottom()
})
</script>

<style scoped>
.chat-window {
  padding: 40px;
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
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

@media (max-width: 768px) {
  .chat-window {
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
}
</style>
