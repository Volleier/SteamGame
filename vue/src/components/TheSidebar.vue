<template>
  <div class="sidebar">
    <nav class="sidebar-nav">
      <div class="nav-item" :class="{ active: currentView === 'dashboard' }" @click="$emit('change-view', 'dashboard')">
        <i class="icon dashboard-icon"></i>
        <span class="text">主界面</span>
      </div>

      <div class="nav-item" :class="{ active: currentView === 'games' }" @click="$emit('change-view', 'games')">
        <i class="icon games-icon"></i>
        <span class="text">游戏列表</span>
      </div>

      <div class="nav-item" :class="{ active: currentView === 'settings' }" @click="$emit('change-view', 'settings')">
        <i class="icon settings-icon"></i>
        <span class="text">系统设置</span>
      </div>
    </nav>

    <div class="sidebar-footer">
      <button type="button" class="logout-button" @click="logout">
        <i class="icon logout-icon"></i>
        <span class="text">返回凭据验证</span>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TheSidebar',
  props: {
    currentView: {
      type: String,
      required: true,
    },
  },
  methods: {
    logout() {
      // 处理退出凭据验证逻辑
      this.$store.dispatch('logout');
      this.$router.push('/credential-verify');
    },
  },
};
</script>

<style scoped>
.sidebar {
  display: flex;
  flex-direction: column;
  width: 240px;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  color: #ffffff;
  box-shadow: 4px 0 30px rgba(0, 0, 0, 0.5);
  position: fixed;
  left: 0;
  top: 0;
  z-index: 10;
  transition:
    transform 0.42s cubic-bezier(0.22, 1, 0.36, 1),
    opacity 0.28s ease,
    visibility 0s linear 0s;
  will-change: transform, opacity;
}

.sidebar.sidebar-hidden {
  transform: translateX(-100%);
  opacity: 0;
  visibility: hidden;
  pointer-events: none;
  transition:
    transform 0.42s cubic-bezier(0.22, 1, 0.36, 1),
    opacity 0.28s ease,
    visibility 0s linear 0.42s;
}

.sidebar-header {
  padding: 1.5rem;
  border-bottom: 1px solid #2c2c2c;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
}

.sidebar-nav {
  flex: 1;
  padding: 1rem 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 0.8rem 1.5rem;
  color: #b8b8b8;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
  border-left: 4px solid transparent;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.nav-item.active {
  background-color: rgba(0, 212, 255, 0.1);
  color: #00d4ff;
  border-left: 4px solid #00d4ff;
}

.icon {
  width: 20px;
  height: 20px;
  margin-right: 10px;
  background-position: center;
  background-repeat: no-repeat;
  background-size: contain;
}

.dashboard-icon {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/></svg>');
}

.games-icon {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M21 6H3c-1.1 0-2 .9-2 2v8c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm-10 7H8v3H6v-3H3v-2h3V8h2v3h3v2zm4.5 2c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm4-3c-.83 0-1.5-.67-1.5-1.5S18.67 9 19.5 9s1.5.67 1.5 1.5-.67 1.5-1.5 1.5z"/></svg>');
}

.settings-icon {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"/></svg>');
}

.details-icon {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm-1 7V3.5L18.5 9H13z"/></svg>');
}

.sidebar-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #2c2c2c;
}

.logout-button {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0.8rem;
  background-color: transparent;
  border: none;
  border-radius: 4px;
  color: #b8b8b8;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.logout-icon {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/></svg>');
}

.text {
  font-size: 1rem;
  font-weight: 500;
  color: inherit;
  transition: color 0.3s ease, text-shadow 0.3s ease;
}

.nav-item.active .text {
  color: #00d4ff;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

/* ===== 移动端适配 ===== */
@media (max-width: 767px) {
  .sidebar {
    transform: translateX(-100%);
    transition:
      transform 0.3s ease,
      opacity 0.2s ease,
      visibility 0s linear 0.3s;
    z-index: 50;
  }

  .sidebar.sidebar-open {
    transform: translateX(0);
    box-shadow: 4px 0 20px rgba(0, 0, 0, 0.4);
  }
}
</style>
