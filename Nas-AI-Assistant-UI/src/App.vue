<template>
  <div class="app-container">
    <Sidebar @open-chat="openChat" @open-profile="openProfile" @page-change="handlePageChange" />
    <MainContent 
      :show-chat="showChat" 
      :show-profile="showProfile" 
      :current-page="currentPage"
      @close-chat="closeChat" 
      @close-profile="closeProfile" 
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './components/Sidebar.vue'
import MainContent from './components/MainContent.vue'

const showChat = ref(false)
const showProfile = ref(false)
const currentPage = ref('home')

const openChat = () => {
  showChat.value = !showChat.value
  showProfile.value = false
}

const openProfile = () => {
  showProfile.value = !showProfile.value
  showChat.value = false
}

const closeChat = () => {
  showChat.value = false
}

const closeProfile = () => {
  showProfile.value = false
}

const handlePageChange = (page) => {
  currentPage.value = page
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: #0a0a0a;
  color: #ffffff;
}

.app-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}
</style>
