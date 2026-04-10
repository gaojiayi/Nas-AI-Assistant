import { createRouter, createWebHistory } from 'vue-router'
import Community from '../views/Community.vue'
import KnowledgeBase from '../views/KnowledgeBase.vue'
import ChatWindow from '../views/ChatWindow.vue'

const routes = [
  {
    path: '/community',
    name: 'Community',
    component: Community,
    meta: { title: 'NAS AI Assistant - 体验社区' }
  },
  {
    path: '/knowledge',
    name: 'KnowledgeBase',
    component: KnowledgeBase,
    meta: { title: 'NAS AI Assistant - 知识库' }
  },
  {
    path: '/chat',
    name: 'ChatWindow',
    component: ChatWindow,
    meta: { title: 'NAS AI Assistant - 聊天' }
  },
  {
    path: '/',
    redirect: '/community'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 动态标题
router.beforeEach((to, _, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})
export default router
