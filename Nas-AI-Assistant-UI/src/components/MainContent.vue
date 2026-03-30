<template>
  <div class="main-content">
    <!-- 聊天窗口模式 -->
    <div v-if="showChat" class="chat-fullscreen">
      <div class="chat-header">
        <div class="chat-title">
          <div class="status-indicator online"></div>
          <span>AI 助手</span>
        </div>
        <button class="close-button" @click="$emit('close-chat')">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M6 6l8 8M6 14l8-8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>
      
      <div class="chat-messages">
        <div class="message ai-message">
          <div class="message-avatar">
            <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
              <circle cx="16" cy="16" r="14" stroke="currentColor" stroke-width="2"/>
              <path d="M12 16h8M16 12v8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
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

    <!-- 正常内容模式 -->
    <div v-else class="normal-content">
      <!-- 知识库管理页面 -->
      <KnowledgeBase v-if="currentPage === 'knowledge'" />
      
      <!-- 主页内容 -->
      <div v-else>
      <div class="banner">
        <div class="banner-content">
          <!-- 如果有合成图片，使用这个 -->
          <!-- <div class="banner-composite" style="display: none;">
            <img src="/images/banner-composite.png" alt="NAS玩家助手横幅" class="composite-image" />
          </div> -->
          
          <!-- 当前版本，隐藏备用 -->
          <div class="banner-title-with-images">
            <h1>骨灰级NAS玩家助手</h1>
            <div class="banner-images">
              <img src="/images/nas_devices.png" alt="NAS设备组" class="banner-image devices-image" />
              <img src="/images/nas_board.png" alt="NAS电路板" class="banner-image circuit-image" />
              <img src="/images/nas_host.png" alt="NAS主机" class="banner-image nas-image" />
            </div>
          </div>
          
          <div class="banner-illustration">
            <div class="floating-elements">
              <div class="element element-1"></div>
              <div class="element element-2"></div>
              <div class="element element-3"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="content-section">
        <div class="section-header">
          <h2>体验社区</h2>
          <a href="#" class="see-all">See all</a>
        </div>
        <div class="carousel-container">
          <div class="carousel-wrapper" ref="carouselWrapper">
            <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
              <div class="carousel-slide">
                <CommunityCard
                  title="家庭建站"
                  description="NAS能够搭建家庭跨境路由,全设备支持跨境直播，社交互动，和产品展示，为中小商家及个体户拓展新的销售渠道。"
                  image="tiktok"
                />
              </div>
              <div class="carousel-slide">
                <CommunityCard
                  title="存储焦虑"
                  description="不再为存储空间焦虑，不管有多少设备，或者更换设备，以及家人共享，NAS都会为你的数据保驾护航。"
                  image="backup"
                />
              </div>
            </div>
          </div>
          <button class="carousel-btn prev-btn" @click="prevSlide">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <button class="carousel-btn next-btn" @click="nextSlide">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <div class="carousel-indicators">
            <span 
              v-for="(_, index) in 2" 
              :key="index"
              :class="{ active: currentSlide === index }"
              @click="goToSlide(index)"
            ></span>
          </div>
        </div>
      </div>

      <div class="content-section">
        <div class="section-header">
          <h2>关键特性</h2>
          <a href="#" class="see-all">See all</a>
        </div>
        <div class="cards-grid">
          <FeatureCard
            title="三层 Agent 架构"
            description="基于 OpenManus 设计理念，实现思考层、规划层、执行层分离，让 AI 具备自主推理能力"
            icon="performance"
            color="#667eea"
          />
          <FeatureCard
            title="RAG 向量检索"
            description="集成向量数据库，支持知识库增强，提供精准的上下文信息和专业领域知识"
            icon="file-ai"
            color="#f093fb"
          />
          <FeatureCard
            title="MCP 工具集成"
            description="支持多种工具调用，包括网络搜索、文件处理、终端命令、邮件发送等丰富功能"
            icon="security"
            color="#4facfe"
          />
          <FeatureCard
            title="多模态交互"
            description="支持图片理解、文档解析、语音交互等多种输入输出形式，打造全方位体验"
            icon="backup-ai"
            color="#43e97b"
          />
        </div>
      </div>
    </div>
    
    <!-- 用户资料窗口 -->
    <div class="profile-window" :class="{ active: showProfile }">
      <div class="profile-header">
        <h2>用户资料</h2>
        <button class="close-button" @click="$emit('close-profile')">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M6 6l8 8M6 14l8-8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>
      <div class="profile-content">
        <div class="profile-avatar">
          <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
            <circle cx="32" cy="32" r="28" stroke="currentColor" stroke-width="2"/>
            <circle cx="32" cy="24" r="8" fill="currentColor"/>
            <path d="M16 48c0-8 6-12 16-12s16 4 16 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <h3>NAS 玩家</h3>
        <p>高级用户</p>
        <div class="profile-stats">
          <div class="stat">
            <span class="stat-value">128</span>
            <span class="stat-label">文件数</span>
          </div>
          <div class="stat">
            <span class="stat-value">2.4TB</span>
            <span class="stat-label">存储空间</span>
          </div>
          <div class="stat">
            <span class="stat-value">99.9%</span>
            <span class="stat-label">在线率</span>
          </div>
        </div>
      </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CommunityCard from './CommunityCard.vue'
import FeatureCard from './FeatureCard.vue'
import KnowledgeBase from './KnowledgeBase.vue'

const props = defineProps({
  showChat: Boolean,
  showProfile: Boolean,
  currentPage: String
})

const emit = defineEmits(['close-chat', 'close-profile'])

const inputMessage = ref('')
const messages = ref([])
const currentSlide = ref(0)
const totalSlides = 2

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

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % totalSlides
}

const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + totalSlides) % totalSlides
}

const goToSlide = (index) => {
  currentSlide.value = index
}
</script>

<style scoped>
.main-content {
  flex: 1;
  background: #0a0a0a;
  overflow: hidden;
  position: relative;
}

/* 全屏聊天窗口 */
.chat-fullscreen {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: rgba(30, 30, 46, 0.95);
  backdrop-filter: blur(10px);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  color: #ffffff;
  font-size: 18px;
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
  width: 36px;
  height: 36px;
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
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message {
  display: flex;
  gap: 16px;
  max-width: 70%;
}

.ai-message {
  align-self: flex-start;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-avatar {
  width: 48px;
  height: 48px;
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

.message-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
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

.image-gaming .image-placeholder {
  color: rgba(255, 255, 255, 0.4);
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

/* 正常内容模式 */
.normal-content {
  height: 100vh;
  overflow-y: auto;
  padding: 24px;
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 30px 40px;
  margin-bottom: 48px;
  position: relative;
  overflow: hidden;
}

.banner-composite {
  width: 100%;
  height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.composite-image {
  max-width: 100%;
  max-height: 200px;
  object-fit: contain;
}

.banner-title-with-images {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 20px;
  width: 100%;
  gap: 40px;
}

.banner-content h1 {
  font-size: 42px;
  font-weight: 900;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 50%, #a855f7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 2px;
  text-transform: uppercase;
  font-family: 'Arial Black', 'Helvetica Neue', Arial, sans-serif;
}

.banner-images {
  display: flex;
  align-items: center;
  gap: 20px;
  z-index: 10;
  margin-left: 20px;
}

.banner-image {
  object-fit: contain;
  filter: drop-shadow(0 8px 16px rgba(0, 0, 0, 0.3));
  max-height: 120px;
}

.devices-image {
  width: 120px;
  opacity: 0.9;
}

.circuit-image {
  width: 140px;
  opacity: 0.9;
}

.nas-image {
  width: 160px;
  opacity: 0.95;
}

.banner-illustration {
  position: absolute;
  right: 40px;
  top: 50%;
  transform: translateY(-50%);
  width: 200px;
  height: 200px;
}

.floating-elements {
  position: relative;
  width: 100%;
  height: 100%;
}

.element {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.element-1 {
  width: 80px;
  height: 80px;
  top: 20px;
  right: 20px;
  background: linear-gradient(45deg, #ff6b6b, #4ecdc4);
}

.element-2 {
  width: 60px;
  height: 60px;
  bottom: 30px;
  left: 40px;
  background: linear-gradient(45deg, #4ecdc4, #45b7d1);
}

.element-3 {
  width: 40px;
  height: 40px;
  top: 50%;
  left: 20px;
  background: linear-gradient(45deg, #f9ca24, #f0932b);
}

.content-section {
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #ffffff;
}

.see-all {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.see-all:hover {
  color: #764ba2;
}

.carousel-container {
  position: relative;
  overflow: hidden;
  border-radius: 16px;
  width: 60%;
  aspect-ratio: 16 / 11;
  margin: 0 auto;
}

.carousel-wrapper {
  position: relative;
  width: 100%;
}

.carousel-track {
  display: flex;
  transition: transform 0.5s ease-in-out;
}

.carousel-slide {
  width: 100%;
  flex-shrink: 0;
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(80, 80, 80, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  z-index: 10;
}

.carousel-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
}

.prev-btn {
  left: 16px;
}

.next-btn {
  right: 16px;
}

.carousel-indicators {
  position: absolute;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.carousel-indicators span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.carousel-indicators span.active {
  background: rgba(255, 255, 255, 0.8);
  transform: scale(1.2);
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

/* 用户资料窗口 */
.profile-window {
  position: fixed;
  right: 20px;
  top: 50%;
  transform: translate(120%, -50%);
  width: 400px;
  background: rgba(30, 30, 46, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  opacity: 0;
  transition: all 0.3s ease;
  z-index: 1000;
}

.profile-window.active {
  transform: translate(0, -50%);
  opacity: 1;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.profile-header h2 {
  color: #ffffff;
  font-size: 20px;
  font-weight: 600;
}

.profile-content {
  padding: 32px 20px;
  text-align: center;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #ff6b6b 0%, #4ecdc4 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  margin: 0 auto 20px;
}

.profile-content h3 {
  color: #ffffff;
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.profile-content p {
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 24px;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  gap: 16px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

@media (max-width: 768px) {
  .banner {
    padding: 40px 24px;
  }
  
  .banner-title-with-images {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .banner-content h1 {
    font-size: 28px;
  }
  
  .carousel-container {
    width: 60%;
    aspect-ratio: 16 / 11;
  }
  
  .carousel-btn {
    width: 40px;
    height: 40px;
    background: rgba(80, 80, 80, 0.6);
  }
  
  .carousel-slide .community-card {
    height: 300px; /* 移动端卡片高度 */
  }
  
  .carousel-slide .community-card .card-image,
.carousel-slide .community-card .card-image.image-vr,
.carousel-slide .community-card .card-image.image-gaming {
    height: 200px !important;
  }
  
  .banner-illustration {
    display: none;
  }
  
  .cards-grid {
    grid-template-columns: 1fr;
  }
  
  .profile-window {
    width: calc(100vw - 40px);
    right: 20px;
    left: 20px;
  }
}
</style>
