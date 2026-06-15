<template>
  <div class="sync-modal-overlay">
    <div class="sync-modal-card">
      <div class="sync-modal-content">
        <div class="sync-icon-container">
          <svg class="sync-icon" :class="{ 'is-syncing': isSyncing }" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
        </div>
        
        <h2 class="sync-title">初始化同步游戏库</h2>
        <p class="sync-desc">正在从 Steam 获取您的游戏数据，请稍候...</p>
        
        <div class="progress-container">
          <div class="progress-bar-bg">
            <div class="progress-bar-fill" :style="{ width: progress + '%' }"></div>
            <div class="progress-glow" :style="{ left: progress + '%' }"></div>
          </div>
          <div class="progress-text">{{ Math.floor(progress) }}%</div>
        </div>
        
        <div class="status-text">{{ statusMessage }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { syncOwnedGames, getSyncStatus } from '@/api/games';

const emit = defineEmits(['sync-complete']);

const progress = ref(0);
const isSyncing = ref(true);
const statusMessage = ref('连接至 Steam 服务器...');

onMounted(async () => {
  try {
    statusMessage.value = '正在拉取游戏基本信息...';
    progress.value = 5;

    // Phase 1: list sync
    const listInterval = setInterval(() => {
      if (progress.value < 45) {
        progress.value += Math.floor(Math.random() * 3 + 1);
      }
    }, 200);

    await syncOwnedGames();
    clearInterval(listInterval);
    progress.value = 50;
    statusMessage.value = '获取游戏库成功，正在读取游戏详细信息...';

    // Phase 2: poll detail sync progress
    const pollInterval = setInterval(async () => {
      try {
        const status = await getSyncStatus();
        const { total, missing, synced, isSyncingDetails } = status;

        if (total > 0) {
          const detailPct = (synced / total) * 50;
          progress.value = Math.min(99, Math.floor(50 + detailPct));
          statusMessage.value = `读取详细游戏数据中... (${synced}/${total})`;
        } else {
          progress.value = 50;
          statusMessage.value = '处理中...';
        }

        // If background detail sync finished or no missing details left
        if (!isSyncingDetails || missing === 0) {
          clearInterval(pollInterval);
          progress.value = 100;
          statusMessage.value = '同步完成！';
          isSyncing.value = false;
          setTimeout(() => {
            emit('sync-complete');
          }, 800);
        }
      } catch (pollErr) {
        console.warn('获取同步进度失败:', pollErr);
      }
    }, 1000);

  } catch (err) {
    console.error('初始同步失败:', err);
    statusMessage.value = '同步失败，即将进入主界面';
    isSyncing.value = false;
    progress.value = 100;
    setTimeout(() => {
      emit('sync-complete');
    }, 1500);
  }
});
</script>

<style scoped>
.sync-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sync-modal-card {
  width: 90%;
  max-width: 480px;
  padding: 2.5rem;
  background: rgba(10, 13, 20, 0.65);
  backdrop-filter: blur(16px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5), 0 0 40px rgba(0, 240, 255, 0.1);
  border: 1px solid rgba(0, 240, 255, 0.2);
}

.sync-modal-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.sync-icon-container {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(0, 240, 255, 0.05);
  border: 1px solid rgba(0, 240, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
  box-shadow: 0 0 20px rgba(0, 240, 255, 0.1) inset;
}

.sync-icon {
  width: 40px;
  height: 40px;
  color: #00f0ff;
}

.is-syncing {
  animation: spin 2s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(-360deg); }
}

.sync-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #fff;
  margin: 0 0 0.5rem 0;
  letter-spacing: 1px;
}

.sync-desc {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 2rem 0;
}

.progress-container {
  width: 100%;
  margin-bottom: 1rem;
}

.progress-bar-bg {
  width: 100%;
  height: 8px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(0, 240, 255, 0.1);
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #00f0ff, #0088ff);
  border-radius: 4px;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 0 10px rgba(0, 240, 255, 0.5);
}

.progress-glow {
  position: absolute;
  top: 0;
  height: 100%;
  width: 20px;
  background: #fff;
  filter: blur(5px);
  opacity: 0.8;
  transform: translateX(-10px);
  transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.progress-text {
  margin-top: 0.5rem;
  font-size: 1.2rem;
  font-weight: 700;
  color: #00f0ff;
  text-shadow: 0 0 10px rgba(0, 240, 255, 0.5);
}

.status-text {
  font-size: 0.8rem;
  color: rgba(0, 240, 255, 0.8);
  height: 1rem;
}
</style>
