<script>
import TheHeader from '@/components/TheHeader.vue'
import TheSidebar from '@/components/TheSidebar.vue'
import TheFooter from '@/components/TheFooter.vue'
import MainPage from '@/views/Dashboard/MainPage.vue'
import GameList from '@/views/Dashboard/GameList.vue'

export default {
  name: "Dashboard",
  components: {
    TheHeader,
    TheSidebar,
    TheFooter,
    MainPage,
    GameList,
  },
  data() {
    return {
      username: 'User',
      currentView: 'dashboard',
      sidebarOpen: false,
    }
  },
  methods: {
    changeView(viewName) {
      this.currentView = viewName;
      this.sidebarOpen = false; // 移动端切换视图后关闭侧边栏
    },
    toggleSidebar() {
      this.sidebarOpen = !this.sidebarOpen;
    },
  }
}
</script>

<template>
  <div class="dashboard-layout">
    <!-- 移动端汉堡按钮 -->
    <button class="mobile-toggle" @click="toggleSidebar" aria-label="切换导航菜单">
      <span class="hamburger-line" :class="{ open: sidebarOpen }"></span>
      <span class="hamburger-line" :class="{ open: sidebarOpen }"></span>
      <span class="hamburger-line" :class="{ open: sidebarOpen }"></span>
    </button>

    <!-- 移动端遮罩层 -->
    <div v-if="sidebarOpen" class="sidebar-overlay" @click="sidebarOpen = false"></div>

    <TheSidebar
      @change-view="changeView"
      :currentView="currentView"
      :class="{ 'sidebar-open': sidebarOpen }"
    />
    <div class="main-content">
      <TheHeader :username="username" />
      <main class="content">
        <transition name="fade" mode="out-in">
          <div v-if="currentView === 'dashboard'" key="dashboard" class="view-content">
            <MainPage />
          </div>

          <div v-else-if="currentView === 'games'" key="games" class="view-content">
            <GameList />
          </div>
        </transition>
      </main>
      <TheFooter />
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.dashboard-content {
  flex: 1;
  padding: 1.5rem;
}

.dashboard-layout {
  display: flex;
  height: 100vh;
  position: relative;
}

/* 移动端汉堡按钮 */
.mobile-toggle {
  display: none;
  position: fixed;
  top: 0.75rem;
  left: 0.75rem;
  z-index: 100;
  width: 40px;
  height: 36px;
  background: rgba(0, 0, 0, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 6px;
  cursor: pointer;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
  padding: 0;
  backdrop-filter: blur(8px);
}

.hamburger-line {
  display: block;
  width: 20px;
  height: 2px;
  background: #fff;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.hamburger-line.open:nth-child(1) {
  transform: translateY(7px) rotate(45deg);
}

.hamburger-line.open:nth-child(2) {
  opacity: 0;
}

.hamburger-line.open:nth-child(3) {
  transform: translateY(-7px) rotate(-45deg);
}

/* 移动端遮罩 */
.sidebar-overlay {
  display: none;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-left: 240px;
  padding-top: 70px;
  width: calc(100% - 240px);
  min-width: 0; /* 防止 flex 子元素溢出 */
}

.content {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
  min-width: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

/* ===== 移动端适配 (< 768px) ===== */
@media (max-width: 767px) {
  .mobile-toggle {
    display: flex;
  }

  .sidebar-overlay {
    display: block;
    position: fixed;
    inset: 0;
    z-index: 40;
    background: rgba(0, 0, 0, 0.5);
  }

  .main-content {
    margin-left: 0;
    width: 100%;
    padding-top: 60px;
  }

  .content {
    padding: 1rem;
  }

  /* 侧边栏需要配合 TheSidebar 的响应式样式：默认隐藏，点击后滑入 */
}
</style>
