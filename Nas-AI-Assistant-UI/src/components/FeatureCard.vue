<template>
  <div class="feature-card">
    <div class="card-icon" :style="{ background: `linear-gradient(135deg, ${color} 0%, ${adjustColor(color, -20)} 100%)` }">
      <component :is="iconComponent" />
    </div>
    <div class="card-content">
      <h3>{{ title }}</h3>
      <p>{{ description }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { h } from 'vue'

const props = defineProps({
  title: String,
  description: String,
  icon: String,
  color: String
})

const iconComponent = computed(() => {
  const icons = {
    'file-ai': h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z', stroke: 'currentColor', 'stroke-width': 2 }),
      h('polyline', { points: '14,2 14,8 20,8', stroke: 'currentColor', 'stroke-width': 2 }),
      h('circle', { cx: 12, cy: 15, r: 2, stroke: 'currentColor', 'stroke-width': 2 }),
      h('path', { d: 'M12 12v1M12 17v1', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round' })
    ]),
    'backup-ai': h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4', stroke: 'currentColor', 'stroke-width': 2 }),
      h('polyline', { points: '7,10 12,15 17,10', stroke: 'currentColor', 'stroke-width': 2 }),
      h('line', { x1: 12, y1: 15, x2: 12, y2: 3, stroke: 'currentColor', 'stroke-width': 2 })
    ]),
    'performance': h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M13 2L3 14h9l-1 8 10-12h-9l1-8z', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
    ]),
    'security': h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z', stroke: 'currentColor', 'stroke-width': 2 }),
      h('path', { d: 'M9 12l2 2 4-4', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
    ])
  }
  return icons[props.icon] || icons['file-ai']
})

const adjustColor = (color, amount) => {
  const num = parseInt(color.replace('#', ''), 16)
  const r = Math.max(0, Math.min(255, (num >> 16) + amount))
  const g = Math.max(0, Math.min(255, ((num >> 8) & 0x00FF) + amount))
  const b = Math.max(0, Math.min(255, (num & 0x0000FF) + amount))
  return `#${((r << 16) | (g << 8) | b).toString(16).padStart(6, '0')}`
}
</script>

<style scoped>
.feature-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #8b5cf6 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.feature-card:hover {
  background: transparent;
  transform: translateY(-8px);
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.4);
}


.card-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  color: #ffffff;
  margin-bottom: 16px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.card-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 6px;
}

.card-content p {
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.4;
  margin-bottom: 16px;
  font-size: 13px;
}

.card-action {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  padding: 10px 16px;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.card-action:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(2px);
}

.card-action svg {
  transition: transform 0.3s ease;
}

.card-action:hover svg {
  transform: translateX(2px);
}
</style>
