<template>
  <div class="sidebar" :class="{ collapsed: isSidebarCollapsed }">
    <!-- 左侧固定宽度区域 -->
    <div class="sidebar-left">
      <!-- 折叠/展开图标 -->
      <div class="toggle-container">
        <!-- 只在展开状态显示收缩图标 -->
        <svg 
          v-if="!isSidebarCollapsed" 
          class="toggle-icon" 
          width="24" height="24" 
          viewBox="0 0 20 20" 
          fill="none" 
          @click="toggleSidebar"
        >
          <path d="M15 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        
        <!-- 只在收缩状态显示展开图标 -->
        <svg 
          v-if="isSidebarCollapsed" 
          class="toggle-icon" 
          width="24" height="24" 
          viewBox="0 0 20 20" 
          fill="none" 
          @click="toggleSidebar"
        >
          <path d="M3 6h14M3 10h14M3 14h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
      
      <div class="user-avatar" @click="emit('open-profile')">
        <div class="avatar-content">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
            <circle cx="16" cy="16" r="14" stroke="currentColor" stroke-width="2"/>
            <circle cx="16" cy="12" r="4" fill="currentColor"/>
            <path d="M8 24c0-4 3-6 8-6s8 2 8 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
      </div>
      
      <div class="chat-button" @click="emit('open-chat')">
        <div class="chat-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
      </div>
      
      <div class="add-button">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path d="M12 8v8M8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
    </div>
    
    <!-- 右侧菜单区域 -->
    <div class="sidebar-right">
      <div class="nav-menu">
        <div 
          v-for="item in menuItems" 
          :key="item.name"
          class="nav-item"
          :class="{ active: item.name === activePage }"
          @click="item.action"
        >
          <div class="nav-icon">
            <component :is="item.icon" />
          </div>
          <span v-if="!isSidebarCollapsed">{{ item.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { h, ref } from 'vue'

// 定义emit事件
const emit = defineEmits(['open-chat', 'open-profile', 'page-change'])

// 侧边栏收缩状态
const isSidebarCollapsed = ref(false)

// 当前激活的页面
const activePage = ref('主页')

// 切换侧边栏状态
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
  emit('toggle-sidebar', isSidebarCollapsed.value)
}

const menuItems = [
  {
    name: '主页',
    icon: h('svg', { width: 20, height: 20, viewBox: '0 0 20 20', fill: 'none' }, [
      h('path', { d: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z', stroke: 'currentColor', 'stroke-width': 2, fill: 'none' }),
      h('path', { d: 'M9 22V12h6v10', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round' })
    ]),
    action: () => {
      activePage.value = '主页'
      emit('page-change', 'home')
    }
  },
  {
    name: 'GitHub',
    icon: h('svg', { width: 20, height: 20, viewBox: '0 0 20 20', fill: 'none' }, [
      h('path', { d: 'M10 2C5.58 2 2 5.58 2 10c0 3.86 2.5 7.13 6 8.27v-2.13c-1.73.38-2.13-.85-2.13-.85-.29-.73-.71-1.23-.71-1.23-.58-.4.04-.39.04-.39.64.04.98.66.98.66.58 1 1.52 1.48 2.32 1.48.21-.42.42-.72.61-.88-1.44-.16-2.96-.72-2.96-3.2 0-.71.25-1.29.66-1.74-.07-.16-.29-.82.06-1.71 0 0 .54-.17 1.76.66.51-.14 1.06-.21 1.6-.21.54 0 1.09.07 1.6.21 1.22-.83 1.76-.66 1.76-.66.35.89.13 1.55.06 1.71.41.45.66 1.03.66 1.74 0 2.49-1.52 3.04-2.97 3.19.23.2.44.59.44 1.19v1.76c3.5-1.14 6-4.41 6-8.27 0-4.42-3.58-8-8-8z', stroke: 'currentColor', 'stroke-width': 2 })
    ]),
    action: () => window.open('https://github.com/gaojiayi/Nas-AI-Assistant#', '_blank')
  },
  {
    name: '知识库管理',
    icon: h('svg', { width: 20, height: 20, viewBox: '0 0 20 20', fill: 'none' }, [
      h('path', { d: 'M3 7v10a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7m-8 0V3a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v4m-8 0h8', stroke: 'currentColor', 'stroke-width': 2 })
    ]),
    action: () => {
      activePage.value = '知识库管理'
      emit('page-change', 'knowledge')
    }
  }
]
</script>

<style scoped>
.sidebar {
  width: 280px;
  background: linear-gradient(135deg, #2d1b69 0%, #1a0e3d 100%);
  display: flex;
  flex-direction: row;
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  height: 100vh;
  transition: width 0.3s ease;
}

.sidebar.collapsed {
  width: 80px;
}

/* 左侧固定宽度区域 */
.sidebar-left {
  width: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  gap: 10px;
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
}

.toggle-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2px;
}

.toggle-icon {
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toggle-icon:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.15);
  transform: scale(1.1);
}

.user-avatar {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #ff6b6b 0%, #4ecdc4 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.user-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 16px rgba(255, 107, 107, 0.3);
}

.avatar-content {
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-button {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.chat-button:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.05);
}

.chat-icon {
  color: rgba(255, 255, 255, 0.8);
}

.add-button {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-button:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  transform: scale(1.05);
}

/* 右侧菜单区域 */
.sidebar-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 54px 20px 20px 20px;
  overflow: hidden;
}

.nav-menu {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  font-size: 14px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(45deg, #ff6b6b, #4ecdc4);
  border-radius: 2px;
}

.nav-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  color: #ffffff;
  flex-shrink: 0;
}

.nav-item.active .nav-icon {
  background: linear-gradient(135deg, #ff6b6b 0%, #4ecdc4 100%);
}

/* 收缩状态的样式调整 */
.sidebar.collapsed .nav-item {
  justify-content: center;
  padding: 12px;
}

.sidebar.collapsed .nav-icon {
  margin: 0;
}

.sidebar.collapsed .sidebar-right {
  padding: 54px 10px 20px 10px;
}

.sidebar.collapsed .nav-menu {
  margin-top: 0;
}
</style>
