<template>
  <div class="community-card" @click="handlePlay">
    <div class="card-image" :class="imageClass">
      <div class="image-placeholder"></div>
      <div class="play-button">
        <svg width="60" height="60" viewBox="0 0 60 60" fill="none">
          <circle cx="30" cy="30" r="28" fill="rgba(255, 255, 255, 0.9)" stroke="rgba(255, 255, 255, 0.3)" stroke-width="2"/>
          <path d="M24 18l18 12-18 12V18z" fill="#333333"/>
        </svg>
      </div>
    </div>
    <div class="card-content">
      <h3>{{ title }}</h3>
      <p>{{ description }}</p>
      <div class="card-stats" v-if="onlineCount || memberCount">
        <div class="stat" v-if="onlineCount">
          <div class="online-indicator"></div>
          <span>{{ formatNumber(onlineCount) }} Online</span>
        </div>
        <div class="stat" v-if="memberCount">
          <span>{{ formatNumber(memberCount) }} Members</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: String,
  description: String,
  image: String,
  onlineCount: Number,
  memberCount: Number
})

const emit = defineEmits(['play'])

const imageClass = computed(() => `image-${props.image}`)

const formatNumber = (num) => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

const handlePlay = () => {
  emit('play', {
    title: props.title,
    description: props.description,
    image: props.image
  })
}
</script>

<style scoped>
.community-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  width: 100%;
  aspect-ratio: 16 / 12;
  display: flex;
  flex-direction: column;
}

.community-card:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-12px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
}

.card-image {
  aspect-ratio: 16 / 7.5;
  position: relative;
  overflow: hidden;
  width: 100%;
}

.play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  opacity: 0.9;
  transition: all 0.3s ease;
  pointer-events: none;
}

.community-card:hover .play-button {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1.1);
}

.image-placeholder {
  width: 100%;
  height: 100%;
}

.image-backup .image-placeholder {
  background: url('/images/backup.png') center/cover no-repeat;
}

.image-tiktok .image-placeholder {
  background: url('/images/tiktok.png') center/cover no-repeat;
}

.image-vr .image-placeholder {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.image-gaming .image-placeholder {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.image-art .image-placeholder {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.image-nft .image-placeholder {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.image-placeholder::before {
  content: '';
  position: absolute;
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
}

.image-vr .image-placeholder::before {
  content: '🥽';
  font-size: 40px;
  background: none;
}

.image-gaming .image-placeholder::before {
  content: '🎮';
  font-size: 40px;
  background: none;
}

.image-art .image-placeholder::before {
  content: '🎨';
  font-size: 40px;
  background: none;
}

.image-nft .image-placeholder::before {
  content: '🖼️';
  font-size: 40px;
  background: none;
}

.card-content {
  padding: 20px;
  flex: 1;
}

.card-content h3 {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 8px;
}

.card-content p {
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.5;
  margin-bottom: 16px;
}

.card-stats {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.stat {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.online-indicator {
  width: 8px;
  height: 8px;
  background: #4ecdc4;
  border-radius: 50%;
  animation: pulse 2s infinite;
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
</style>
