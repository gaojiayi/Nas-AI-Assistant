<template>
  <div class="main-content">
    <!-- Banner区域 -->
    <div class="banner">
      <div class="banner-content">
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

    <!-- 动态内容区域 -->
    <div class="dynamic-content">
      <router-view />
    </div>
    
    <!-- 关键特性区域 -->
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
</template>

<script setup>
import FeatureCard from './FeatureCard.vue'
</script>

<style scoped>
.main-content {
  flex: 1;
  background: #0a0a0a;
  overflow: hidden;
  position: relative;
}

/* 内容模式 */
.main-content {
  height: 100vh;
  overflow-y: auto;
  padding: 12px;
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 24px 32px;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
}

.banner-title-with-images {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 12px;
  width: 100%;
  gap: 24px;
}

.banner-content h1 {
  font-size: 32px;
  font-weight: 900;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 50%, #a855f7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 1px;
  text-transform: uppercase;
  font-family: 'Arial Black', 'Helvetica Neue', Arial, sans-serif;
}

.banner-images {
  display: flex;
  align-items: center;
  gap: 12px;
  z-index: 10;
  margin-left: 12px;
}

.banner-image {
  object-fit: contain;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
  max-height: 80px;
}

.devices-image {
  width: 80px;
  opacity: 0.9;
}

.circuit-image {
  width: 90px;
  opacity: 0.9;
}

.nas-image {
  width: 100px;
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

.dynamic-content {
  margin-bottom: 24px;
}

.content-section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 24px;
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

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

@media (max-width: 768px) {
  .cards-grid {
    grid-template-columns: 1fr;
  }
}
</style>
