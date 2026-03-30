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
      <div class="message ai-message">
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
      
      <div class="message user-message" v-if="messages.length > 0">
        <div class="message-content">
          <p>{{ messages[messages.length - 1].text }}</p>
          <span class="message-time">刚刚</span>
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

const props = defineProps({
  show: Boolean
})

const emit = defineEmits(['close'])

const inputMessage = ref('')
const messages = ref([])

const sendMessage = () => {
  if (inputMessage.value.trim()) {
    messages.value.push({
      text: inputMessage.value,
      sender: 'user',
      time: new Date()
    })
    
    // 模拟AI回复
    setTimeout(() => {
      messages.value.push({
        text: '我正在处理您的请求，请稍候...',
        sender: 'ai',
        time: new Date()
      })
    }, 1000)
    
    inputMessage.value = ''
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

@media (max-width: 480px) {
  .chat-window {
    width: calc(100vw - 40px);
    right: 20px;
    left: 20px;
  }
}
</style>
