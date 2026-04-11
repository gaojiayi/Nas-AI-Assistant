<template>
  <div class="community-page">
    <div class="page-header">
      <h1>{{ pageInfo.title }}</h1>
      <p>{{ pageInfo.subtitle }}</p>
    </div>
    
    <div class="content-section">
      <div class="section-header">
        <h2>{{ pageInfo.sectionTitle }}</h2>
        <a href="#" class="see-all">See all</a>
      </div>
      <div class="carousel-container">
        <div class="carousel-wrapper" ref="carouselWrapper">
          <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
            <div v-for="(card, index) in communityCards" :key="index" class="carousel-slide">
              <CommunityCard
                :title="card.title"
                :description="card.description"
                :image="card.image"
                type="community"
                @play="openPlayer"
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
            v-for="(_, index) in communityCards.length" 
            :key="index"
            :class="{ active: currentSlide === index }"
            @click="goToSlide(index)"
          ></span>
        </div>
      </div>
    </div>
    
    <!-- 视频播放器弹窗 -->
    <div v-if="showPlayer" class="video-player-overlay" @click="closePlayer">
      <div class="video-player-container" @click.stop>
        <div class="video-player-header">
          <h3>{{ currentPlayerData.title }}</h3>
          <button class="close-button" @click="closePlayer">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M6 6l12 12M6 18l12-12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
        <div class="video-player-content">
          <div v-if="currentPlayerData.embedUrl" class="video-embed-container">
            <iframe 
              :src="currentPlayerData.embedUrl" 
              frameborder="0" 
              allowfullscreen
              class="video-iframe"
            ></iframe>
          </div>
          <div v-else class="video-placeholder">
            <div class="video-info">
              <h4>{{ currentPlayerData.title }}</h4>
              <p>{{ currentPlayerData.description }}</p>
              <div class="video-controls">
                <button class="control-btn play-btn" @click="openVideo">
                  <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                    <circle cx="24" cy="24" r="22" fill="rgba(255, 255, 255, 0.9)" stroke="rgba(255, 255, 255, 0.3)" stroke-width="2"/>
                    <path d="M18 12l18 12-18 12V12z" fill="#333333"/>
                  </svg>
                </button>
                <div class="video-progress">
                  <div class="progress-bar">
                    <div class="progress-fill" style="width: 35%"></div>
                  </div>
                  <span class="time-text">2:45 / 7:30</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { communityAPI } from '../services/api.js'
import CommunityCard from '../components/CommunityCard.vue'

const currentSlide = ref(0)
const showPlayer = ref(false)
const currentPlayerData = ref(null)
const loading = ref(false)

const communityCards = ref([])
const pageInfo = ref({})

// 加载社区数据
const loadCommunityData = async () => {
  loading.value = true
  try {
    const [cardsData, pageData] = await Promise.all([
      communityAPI.getCards(),
      communityAPI.getPageInfo()
    ])
    communityCards.value = cardsData
    pageInfo.value = pageData
  } catch (error) {
    console.error('加载社区数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCommunityData()
})

const totalSlides = computed(() => communityCards.value.length)

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % totalSlides.value
}

const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + totalSlides.value) % totalSlides.value
}

const goToSlide = (index) => {
  currentSlide.value = index
}

const openPlayer = (data) => {
  currentPlayerData.value = data
  showPlayer.value = true
}

const closePlayer = () => {
  showPlayer.value = false
}

const openVideo = () => {
  if (currentPlayerData.value.title === '家庭VPN建站') {
    // 在当前播放器中嵌入B站视频
    currentPlayerData.value.videoUrl = 'https://www.bilibili.com/video/BV1G1wizLE4U?t=506.0'
    currentPlayerData.value.embedUrl = 'https://player.bilibili.com/player.html?bvid=BV1G1wizLE4U&t=506'
  } else {
    // 其他视频的默认处理
    console.log('播放其他视频:', currentPlayerData.value.title)
  }
}
</script>

<style scoped>
.community-page {
  padding: 24px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 20px;
  min-height: calc(100vh - 48px);
  color: #ffffff;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-header p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
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

.carousel-container {
  position: relative;
  overflow: hidden;
  border-radius: 16px;
  width: 60%;
  aspect-ratio: 16 / 9;
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

/* 视频播放器弹窗 */
.video-player-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(10px);
}

.video-player-container {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 20px;
  width: 90%;
  max-width: 1200px;
  height: 80vh;
  max-height: 800px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.video-player-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.video-player-header h3 {
  font-size: 24px;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
}

.close-button {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 8px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

.close-button:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
}

.video-player-content {
  flex: 1;
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border: 2px dashed rgba(255, 255, 255, 0.2);
}

.video-embed-container {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.video-iframe {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 12px;
}

.video-info {
  text-align: center;
  color: #ffffff;
}

.video-info h4 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
}

.video-info p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 32px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  line-height: 1.6;
}

.video-controls {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.control-btn.play-btn {
  background: none;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.control-btn.play-btn:hover {
  transform: scale(1.1);
}

.video-progress {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  max-width: 400px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.time-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  white-space: nowrap;
}

@media (max-width: 768px) {
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
  
  /* 播放器弹窗移动端样式 */
  .video-player-container {
    width: 95%;
    height: 90vh;
    margin: 0 16px;
  }
  
  .video-player-header {
    padding: 16px 20px;
  }
  
  .video-player-header h3 {
    font-size: 20px;
  }
  
  .video-player-content {
    padding: 16px;
  }
  
  .video-info h4 {
    font-size: 24px;
  }
  
  .video-info p {
    font-size: 14px;
  }
  
  .control-btn.play-btn svg {
    width: 40px;
    height: 40px;
  }
  
  .video-progress {
    max-width: 300px;
  }
}
</style>
