import { createRouter, createWebHistory } from 'vue-router'
import Community from '../views/Community.vue'
import KnowledgeBase from '../views/KnowledgeBase.vue'
import ChatWindow from '../views/ChatWindow.vue'

const routes = [
  {
    path: '/community',
    name: 'Community',
    component: Community
  },
  {
    path: '/knowledge',
    name: 'KnowledgeBase',
    component: KnowledgeBase
  },
  {
    path: '/chat',
    name: 'ChatWindow',
    component: ChatWindow
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

export default router
