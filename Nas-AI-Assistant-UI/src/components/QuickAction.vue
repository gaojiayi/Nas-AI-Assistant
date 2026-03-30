<template>
  <div class="quick-action">
    <div class="action-icon">
      <component :is="iconComponent" />
    </div>
    <div class="action-content">
      <h3>{{ title }}</h3>
      <p>{{ description }}</p>
    </div>
    <div class="action-arrow">
      <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
        <path d="M6 15l4-4m0 0l-4-4m4 4H2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { h } from 'vue'

const props = defineProps({
  title: String,
  description: String,
  icon: String
})

const iconComponent = computed(() => {
  const icons = {
    upload: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4', stroke: 'currentColor', 'stroke-width': 2 }),
      h('polyline', { points: '17,8 12,3 7,8', stroke: 'currentColor', 'stroke-width': 2 }),
      h('line', { x1: 12, y1: 3, x2: 12, y2: 15, stroke: 'currentColor', 'stroke-width': 2 })
    ]),
    backup: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4', stroke: 'currentColor', 'stroke-width': 2 }),
      h('polyline', { points: '7,10 12,15 17,10', stroke: 'currentColor', 'stroke-width': 2 }),
      h('line', { x1: 12, y1: 15, x2: 12, y2: 3, stroke: 'currentColor', 'stroke-width': 2 })
    ]),
    diagnostic: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M9 11H3m6 0v6m0-6l-3 3m3-3l3 3m6 0h6m-6 0v6m0-6l3 3m-3-3l-3 3', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
      h('circle', { cx: 12, cy: 11, r: 3, stroke: 'currentColor', 'stroke-width': 2 })
    ]),
    users: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2', stroke: 'currentColor', 'stroke-width': 2 }),
      h('circle', { cx: 9, cy: 7, r: 4, stroke: 'currentColor', 'stroke-width': 2 }),
      h('path', { d: 'M23 21v-2a4 4 0 0 0-3-3.87', stroke: 'currentColor', 'stroke-width': 2 }),
      h('path', { d: 'M16 3.13a4 4 0 0 1 0 7.75', stroke: 'currentColor', 'stroke-width': 2 })
    ])
  }
  return icons[props.icon] || icons.upload
})
</script>

<style scoped>
.quick-action {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.quick-action:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.action-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: #ffffff;
  flex-shrink: 0;
}

.action-content {
  flex: 1;
}

.action-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 4px;
}

.action-content p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  margin: 0;
}

.action-arrow {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.4);
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.quick-action:hover .action-arrow {
  background: rgba(102, 126, 234, 0.2);
  color: #667eea;
  transform: translateX(4px);
}

@media (max-width: 640px) {
  .quick-action {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .action-arrow {
    transform: rotate(-90deg);
  }
  
  .quick-action:hover .action-arrow {
    transform: rotate(-90deg) translateX(4px);
  }
}
</style>
