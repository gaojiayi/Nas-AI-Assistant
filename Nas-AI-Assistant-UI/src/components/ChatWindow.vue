<template>
  <div class="chat-window" :class="{ active: show }">
    <div class="chat-header">
      <div class="chat-title">
        <div class="status-indicator online"></div>
        <span>AI 助手</span>
      </div>
      <button class="close-button" @click="$emit('close')">
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
          <path d="M6 6l8 8M6 14l8-8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
    </div>
    
    <div class="chat-messages">
      <!-- 默认欢迎消息 -->
      <div class="message ai-message" v-if="messages.length === 0">
        <div class="message-avatar">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <path d="M9 12h6M12 9v6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="message-content">
          <p>您好！我是您的NAS AI助手，有什么可以帮助您的吗？</p>
          <span class="message-time">刚刚</span>
        </div>
      </div>
      
      <!-- 动态消息列表 -->
      <div v-for="(message, index) in messages" :key="index" 
           :class="['message', message.sender === 'user' ? 'user-message' : 'ai-message']">
        <div class="message-avatar" v-if="message.sender === 'ai'">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <path d="M9 12h6M12 9v6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="message-content">
          <p>{{ message.text }}<span v-if="message.isStreaming" class="streaming-cursor">|</span></p>
          <span class="message-time">{{ formatTime(message.time) }}</span>
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
        />
        <button class="send-button" @click="sendMessage" :disabled="!inputMessage.trim()">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M18 2l-8 16M18 2l-4 16M18 2H6M18 2l-4 8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue'
import { chatAPI } from '../services/api'

const props = defineProps({
  show: Boolean
})

const emit = defineEmits(['close'])

const inputMessage = ref('')
const messages = ref([])
const isLoading = ref(false)

const formatTime = (time) => {
  const now = new Date()
  const messageTime = new Date(time)
  const diff = now - messageTime
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return messageTime.toLocaleDateString()
}

const sendMessage = async () => {
  if (inputMessage.value.trim() && !isLoading.value) {
    // 添加用户消息
    const userMessage = {
      text: inputMessage.value,
      sender: 'user',
      time: new Date()
    }
    messages.value.push(userMessage)
    
    // 添加AI消息占位符
    const aiMessage = {
      text: '',
      sender: 'ai',
      time: new Date(),
      isStreaming: true
    }
    messages.value.push(aiMessage)
    
    const messageText = inputMessage.value
    inputMessage.value = ''
    isLoading.value = true
    
    try {
      console.log('Sending message:', messageText)
      // 调用SSE API
      const response = await chatAPI.sendMessage(messageText)
      console.log('SSE response received:', response)
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      
      let buffer = ''
      while (true) {
        const { done, value } = await reader.read()
        if (done) {
          // 处理缓冲区中剩余的数据
          if (buffer.trim()) {
            const lines = buffer.split('\n')
            for (const line of lines) {
              if (line.startsWith('data:')) {
                const data = line.slice(5).trim() // 移除 'data:' 前缀并去除空格
                console.log('Parsed data:', data)
                if (data && data !== '') {
                  aiMessage.text += data
                  // Force Vue reactivity update
                  messages.value[messages.value.length - 1] = { ...aiMessage }
                  console.log('Final buffer data:', data)
                }
              }
            }
          }
          // Force Vue reactivity update
          messages.value[messages.value.length - 1] = { ...aiMessage }
          break
        }
        
        const chunk = decoder.decode(value, { stream: true })
        console.log('Raw chunk:', chunk)
        buffer += chunk
        
        // 处理完整的行
        const lines = buffer.split('\n')
        buffer = lines.pop() || '' // 保留最后一个不完整的行
        
        for (const line of lines) {
          if (line.startsWith('data:')) {
            const data = line.slice(5).trim() // 移除 'data:' 前缀并去除空格
            console.log('Parsed data:', data)
            if (data && data !== '') {
              aiMessage.text += data
              // Force Vue reactivity update
              messages.value[messages.value.length - 1] = { ...aiMessage }
              console.log('Current AI message:', aiMessage.text)
            }
          }
        }
      }
      
      // 确保流结束后UI更新
      setTimeout(() => {
        aiMessage.isStreaming = false
        messages.value[messages.value.length - 1] = { ...aiMessage }
        console.log('Final AI message:', aiMessage.text)
      }, 100)
    } catch (error) {
      console.error('Error sending message:', error)
      aiMessage.text = '抱歉，发送消息时出现错误，请稍后重试。'
      aiMessage.isStreaming = false
    } finally {
      isLoading.value = false
    }
  }
}
</script>

<style scoped>
.chat-window {
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 380px;
  height: 500px;
  background: rgba(30, 30, 46, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  transform: translateY(120%);
  opacity: 0;
  transition: all 0.3s ease;
  z-index: 1000;
}

.chat-window.active {
  transform: translateY(0);
  opacity: 1;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #ffffff;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.status-indicator.online {
  background: #4ecdc4;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(78, 205, 196, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(78, 205, 196, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(78, 205, 196, 0);
  }
}

.close-button {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
  border: none;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-button:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
}

.ai-message {
  align-self: flex-start;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
}

.message-content {
  background: rgba(255, 255, 255, 0.05);
  padding: 12px 16px;
  border-radius: 12px;
  flex: 1;
}

.user-message .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.message-content p {
  color: #ffffff;
  margin: 0 0 4px 0;
  line-height: 1.4;
}

.message-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.chat-input {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.input-container {
  display: flex;
  gap: 8px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 8px;
}

.message-input {
  flex: 1;
  background: transparent;
  border: none;
  color: #ffffff;
  outline: none;
  font-size: 14px;
}

.message-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.send-button {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
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

.streaming-cursor {
  animation: blink 1s infinite;
  color: #667eea;
  font-weight: bold;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

@media (max-width: 480px) {
  .chat-window {
    width: calc(100vw - 40px);
    right: 20px;
    left: 20px;
  }
}
</style>
