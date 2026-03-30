import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import KnowledgeBase from '../views/KnowledgeBase.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/knowledge',
    name: 'KnowledgeBase',
    component: KnowledgeBase
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
