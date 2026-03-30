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
      
      <div class="user-avatar">
        <div class="avatar-content">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
            <circle cx="16" cy="16" r="14" stroke="currentColor" stroke-width="2"/>
            <circle cx="16" cy="12" r="4" fill="currentColor"/>
            <path d="M8 24c0-4 3-6 8-6s8 2 8 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
      </div>
    </div>
    
    <!-- 右侧菜单区域 -->
    <div class="sidebar-right">
      <div class="nav-menu">
        <div 
          v-for="item in menuItems" 
          :key="item.name"
          class="nav-item"
          :class="{ active: (item.route && route.path === item.route) || (item.name === '主页' && route.path === '/community') }"
          @click="navigate(item)"
        >
          <div class="nav-icon">
            <component :is="item.icon" />
          </div>
          <span v-if="!isSidebarCollapsed">{{ item.name }}</span>
        </div>
        
        <!-- 申明信息 -->
        <div class="declaration-section">
          <div class="declaration-item">
            <div class="declaration-content">
              <div class="app-name">Nas AI Assistant</div>
              <div class="version">v1.0.0</div>
              <div class="made-by">Made by GaoJiaYi</div>
              <div class="copyright">© 2026</div>
              <div class="github-link" @click="openGitHub">
                <div class="github-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                    <path d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <span>GitHub</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { h, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

// 使用router和route
const router = useRouter()
const route = useRoute()

// 侧边栏收缩状态
const isSidebarCollapsed = ref(false)

// 切换侧边栏状态
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

const menuItems = [
  {
    name: '体验社区',
    icon: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z', stroke: 'currentColor', 'stroke-width': 2, fill: 'none' }),
      h('path', { d: 'M9 22V12h6v10', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round' })
    ]),
    route: '/community'
  },
  {
    name: 'AI助手',
    icon: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z', stroke: 'currentColor', 'stroke-width': 2, fill: 'none' }),
      h('path', { d: 'M8 10h8M8 14h6', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round' })
    ]),
    route: '/chat'
  },
  {
    name: '知识库管理',
    icon: h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none' }, [
      h('rect', { x: '2', y: '8', width: '5', height: '7', stroke: 'currentColor', 'stroke-width': 2 }),
      h('rect', { x: '9.5', y: '8', width: '5', height: '7', stroke: 'currentColor', 'stroke-width': 2 }),
      h('rect', { x: '17', y: '8', width: '5', height: '7', stroke: 'currentColor', 'stroke-width': 2 }),
      h('path', { d: 'M3 8v1M11.5 8v1M19 8v1', stroke: 'currentColor', 'stroke-width': 1 })
    ]),
    route: '/knowledge'
  }
]

const navigate = (item) => {
  if (item.action) {
    item.action()
  } else if (item.route) {
    router.push(item.route)
  }
}

const openGitHub = () => {
  window.open('https://github.com/gaojiayi/Nas-AI-Assistant', '_blank')
}
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
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.avatar-content {
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
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
  gap: 12px;
  padding: 14px 20px;
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
  width: 36px;
  height: 36px;
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

/* 申明样式 */
.declaration-section {
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.declaration-item {
  padding: 0 20px;
}

.declaration-content {
  text-align: center;
  color: rgba(255, 255, 255, 0.6);
}

.app-name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  color: rgba(255, 255, 255, 0.8);
}

.version {
  font-size: 10px;
  margin-bottom: 4px;
  color: rgba(255, 255, 255, 0.4);
  font-weight: 500;
}

.made-by {
  font-size: 11px;
  margin-bottom: 4px;
  color: rgba(255, 255, 255, 0.5);
}

.copyright {
  font-size: 10px;
  margin-bottom: 8px;
  color: rgba(255, 255, 255, 0.4);
}

.github-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
  transition: all 0.3s ease;
}

.github-link:hover {
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.8);
}

.github-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 收缩状态的样式调整 */
.sidebar.collapsed .nav-item {
  justify-content: center;
  padding: 10px;
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
