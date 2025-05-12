<script>
import TheHeader from '@/components/TheHeader.vue'
import TheSidebar from '@/components/TheSidebar.vue'
import MainPage from '@/views/MainPage.vue'
import GameList from '@/views/GameList.vue'

export default {
  name: "Dashboard",
  components: {
    TheHeader,
    TheSidebar,
    MainPage,
    GameList,
  },
  data() {
    return {
      username: 'User',
      currentView: 'dashboard'
    }
  },
  methods: {
    changeView(viewName) {
      this.currentView = viewName;
    }
  }
}
</script>

<template>
  <div class="dashboard-layout">
    <TheSidebar @change-view="changeView" :currentView="currentView" />
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

          <div v-else-if="currentView === 'game-details'" key="game-details" class="view-content">
            <h2>游戏详情</h2>
            <!-- 内容 -->
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
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-left: 240px;
  padding-top: 70px;
  width: calc(100% - 240px);
}

.content {
  flex: 1;
  padding: 1.5rem;
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
</style>