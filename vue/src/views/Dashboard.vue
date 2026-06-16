<script>
import TheHeader from '@/components/TheHeader.vue'
import TheSidebar from '@/components/TheSidebar.vue'
import TheFooter from '@/components/TheFooter.vue'
import MainPage from '@/views/Dashboard/MainPage.vue'
import GameList from '@/views/Dashboard/GameList.vue'
import Settings from '@/views/Dashboard/Settings.vue'
import SyncModal from '@/components/SyncModal.vue'

export default {
  name: "Dashboard",
  components: {
    TheHeader,
    TheSidebar,
    TheFooter,
    MainPage,
    GameList,
    Settings,
    SyncModal,
  },
  data() {
    return {
      username: 'User',
      currentView: 'dashboard',
      sidebarOpen: false,
      isAppFullscreen: false,
      showInitialSyncModal: false,
    }
  },
  created() {
    this.$store.commit('setIsFullscreen', false);
    if (this.$route.query.initialSync === 'true') {
      this.showInitialSyncModal = true;
      // 移除 URL 中的 query 参数，避免刷新再次弹出
      const query = { ...this.$route.query };
      delete query.initialSync;
      this.$router.replace({ query }).catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          console.error(err);
        }
      });
    }
  },
  methods: {
    changeView(viewName) {
      if (viewName !== 'dashboard' && this.isAppFullscreen) {
        this.isAppFullscreen = false;
        this.$store.commit('setIsFullscreen', false);
      }
      this.currentView = viewName;
      this.sidebarOpen = false; // 移动端切换视图后关闭侧边栏
    },
    toggleSidebar() {
      this.sidebarOpen = !this.sidebarOpen;
    },
    toggleAppFullscreen() {
      this.isAppFullscreen = !this.isAppFullscreen;
      this.$store.commit('setIsFullscreen', this.isAppFullscreen);
    },
    handleSyncComplete() {
      this.showInitialSyncModal = false;
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
      :aria-hidden="isAppFullscreen"
      :class="{ 'sidebar-open': sidebarOpen, 'sidebar-hidden': isAppFullscreen }"
    />
    <div
      class="main-content"
      :class="{
        'fullscreen-mode': isAppFullscreen,
        'subpage-mode': currentView !== 'dashboard'
      }"
    >
      <main class="content">
        <div class="view-content flex-1 h-full flex flex-col relative">
          <MainPage
            v-show="currentView === 'dashboard'"
            class="dashboard-view-panel"
            @toggle-fullscreen="toggleAppFullscreen"
            :is-fullscreen="isAppFullscreen"
          />
          <GameList
            v-show="currentView === 'games'"
            class="dashboard-view-panel dashboard-scroll-panel"
          />
          <Settings
            v-show="currentView === 'settings'"
            class="dashboard-view-panel dashboard-scroll-panel"
          />
        </div>
      </main>
      <TheFooter v-if="currentView !== 'dashboard'" />
    </div>

    <!-- 初始同步弹窗 -->
    <transition name="fade">
      <SyncModal v-if="showInitialSyncModal" @sync-complete="handleSyncComplete" />
    </transition>
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
  background-color: #0a0d14; /* Dark void background */
  color: #ffffff; /* White text for dark mode */
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
  width: calc(100% - 240px);
  min-width: 0; /* 防止 flex 子元素溢出 */
  transition:
    margin-left 0.42s cubic-bezier(0.22, 1, 0.36, 1),
    width 0.42s cubic-bezier(0.22, 1, 0.36, 1);
  will-change: margin-left, width;
}

.main-content.fullscreen-mode {
  margin-left: 0;
  width: 100%;
}

.content {
  flex: 1;
  overflow-y: auto;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.main-content.subpage-mode .content {
  padding-top: 3rem;
}

.view-content {
  min-height: 0;
}

.dashboard-view-panel {
  flex: 1 1 auto;
  min-height: 0;
  width: 100%;
}

.dashboard-scroll-panel {
  overflow-y: auto;
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

  .main-content.subpage-mode .content {
    padding-top: 0;
  }

  .content {
    padding: 1rem;
  }

  /* 侧边栏需要配合 TheSidebar 的响应式样式：默认隐藏，点击后滑入 */
}
</style>
