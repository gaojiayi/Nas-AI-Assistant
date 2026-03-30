<template>
  <div class="status-card" :class="statusClass">
    <div class="card-header">
      <div class="card-icon">
        <component :is="iconComponent" />
      </div>
      <div class="status-dot" :class="status"></div>
    </div>
    <div class="card-content">
      <h3>{{ title }}</h3>
      <p class="value">{{ value }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { h } from 'vue'

const props = defineProps({
  title: String,
  value: String,
  status: String,
  icon: String
})

const statusClass = computed(() => `status-${props.status}`)

const iconComponent = computed(() => {
  const icons = {
    cpu: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('rect', { x: 4, y: 4, width: 16, height: 16, rx: 2, stroke: 'currentColor', 'stroke-width': 2 }),
      h('rect', { x: 9, y: 9, width: 6, height: 6, stroke: 'currentColor', 'stroke-width': 2 }),
      h('path', { d: 'M9 1v3M15 1v3M9 20v3M15 20v3M20 9h3M20 15h3M1 9h3M1 15h3', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round' })
    ]),
    memory: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('rect', { x: 6, y: 6, width: 12, height: 12, rx: 2, stroke: 'currentColor', 'stroke-width': 2 }),
      h('path', { d: 'M9 6V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2M9 18v2a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2v-2', stroke: 'currentColor', 'stroke-width': 2 })
    ]),
    storage: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M3 7v10a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2z', stroke: 'currentColor', 'stroke-width': 2 }),
      h('path', { d: 'M8 21v-4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v4M3 3h18', stroke: 'currentColor', 'stroke-width': 2 })
    ]),
    network: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M5 12h14M12 5l7 7-7 7', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
      h('circle', { cx: 5, cy: 12, r: 3, stroke: 'currentColor', 'stroke-width': 2 }),
      h('circle', { cx: 19, cy: 12, r: 3, stroke: 'currentColor', 'stroke-width': 2 })
    ])
  }
  return icons[props.icon] || icons.cpu
})
</script>

<style scoped>
.status-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.status-card:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.2);
}

.status-card.status-normal {
  border-color: rgba(78, 205, 196, 0.3);
}

.status-card.status-warning {
  border-color: rgba(245, 87, 108, 0.3);
}

.status-card.status-critical {
  border-color: rgba(255, 107, 107, 0.3);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.status-dot.normal {
  background: #4ecdc4;
}

.status-dot.warning {
  background: #f5576c;
}

.status-dot.critical {
  background: #ff6b6b;
}

.card-content h3 {
  font-size: 16px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 8px;
}

.card-content .value {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
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
