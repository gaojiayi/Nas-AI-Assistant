<template>
  <div class="home-page">
    <div class="page-header">
      <h1>骨灰级NAS玩家助手</h1>
      <p>专业的NAS管理和AI助手解决方案</p>
    </div>
    
    <div class="banner">
      <div class="banner-content">
        <div class="banner-title-with-images">
          <h1>骨灰级NAS玩家助手</h1>
          <div class="banner-images">
            <img src="/images/nas_devices.png" alt="设备" class="banner-image devices-image">
            <img src="/images/nas_board.png" alt="电路" class="banner-image circuit-image">
            <img src="/images/nas_host.png" alt="NAS" class="banner-image nas-image">
          </div>
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
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M12 5l-7 7 7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <button class="carousel-btn next-btn" @click="nextSlide">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M8 5l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
    </div>
    
    <!-- 关键特性 -->
    <div class="features-section">
      <h2>关键特性</h2>
      <div class="features-grid">
        <FeatureCard
          title="智能文件管理"
          description="自动分类和整理您的文件，让NAS管理更加高效便捷"
          icon="file-ai"
          color="#667eea"
        />
        <FeatureCard
          title="自动备份系统"
          description="多重备份策略，确保您的数据安全永不丢失"
          icon="backup-ai"
          color="#764ba2"
        />
        <FeatureCard
          title="高性能存储"
          description="优化的存储算法，提供更快的访问速度和更好的性能"
          icon="performance"
          color="#f093fb"
        />
        <FeatureCard
          title="安全防护"
          description="企业级安全防护，保护您的数据免受威胁"
          icon="security"
          color="#4facfe"
        />
      </div>
    </div>
    
    <!-- 统计数据 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat">
          <span class="stat-value">10K+</span>
          <span class="stat-label">活跃用户</span>
        </div>
        <div class="stat">
          <span class="stat-value">99.9%</span>
          <span class="stat-label">在线率</span>
        </div>
        <div class="stat">
          <span class="stat-value">2.4TB</span>
          <span class="stat-label">存储空间</span>
        </div>
        <div class="stat">
          <span class="stat-value">24/7</span>
          <span class="stat-label">技术支持</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CommunityCard from '../components/CommunityCard.vue'
import FeatureCard from '../components/FeatureCard.vue'

const currentSlide = ref(0)
const totalSlides = 2

const prevSlide = () => {
  currentSlide.value = currentSlide.value === 0 ? totalSlides - 1 : currentSlide.value - 1
}

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % totalSlides
}
</script>

<style scoped>
.home-page {
  padding: 24px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 20px;
  min-height: calc(100vh - 48px);
  color: #ffffff;
}

.page-header {
  margin-bottom: 32px;
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

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 30px 40px;
  margin-bottom: 48px;
  position: relative;
  overflow: hidden;
}

.banner-content {
  position: relative;
  z-index: 2;
}

.banner-title-with-images {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 20px;
  width: 100%;
  gap: 40px;
}

.banner-title-with-images h1 {
  font-size: 42px;
  font-weight: 900;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 50%, #a855f7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 2px;
  text-transform: uppercase;
  font-family: 'Arial Black', 'Helvetica Neue', Arial, sans-serif;
  margin: 0;
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

.carousel-container {
  position: relative;
  width: 60%;
  margin: 0 auto;
  aspect-ratio: 16/11;
  border-radius: 16px;
  overflow: hidden;
}

.carousel-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
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
  background: rgba(80, 80, 80, 0.8);
  color: #ffffff;
}

.prev-btn {
  left: -24px;
}

.next-btn {
  right: -24px;
}

.features-section {
  margin-bottom: 48px;
}

.features-section h2 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 32px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.stats-section {
  margin-top: 48px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.stat {
  text-align: center;
  padding: 24px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  transition: all 0.3s ease;
}

.stat:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-4px);
}

.stat-value {
  display: block;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

@media (max-width: 768px) {
  .carousel-container {
    width: 90%;
    aspect-ratio: 16/11;
  }
  
  .banner-title-with-images {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .banner-images {
    margin-left: 0;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
