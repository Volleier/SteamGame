/**
 * 游戏库同步状态 —— 从 Settings.vue 提取。
 */
import { ref, computed } from 'vue';
import { syncOwnedGames } from '@/api/games';
import { getLastSyncTime, setLastSyncTime } from '@/utils/storage';

export function useGameSync() {
  const isSyncing = ref(false);
  const syncStatus = ref<'success' | 'error' | ''>('');
  const syncCount = ref(0);
  const syncErrorMsg = ref('');
  const lastSyncTime = ref(getLastSyncTime());

  const statusClass = computed(() => {
    if (syncStatus.value === 'success') return 'bg-green-500/10 border-green-500/30 text-green-400';
    if (syncStatus.value === 'error') return 'bg-red-500/10 border-red-500/30 text-red-400';
    return '';
  });

  const syncStatusIcon = computed(() => syncStatus.value === 'success' ? '🚀' : '⚠️');
  const syncStatusTitle = computed(() => syncStatus.value === 'success' ? '同步成功' : '同步失败');
  const syncStatusMsg = computed(() => {
    if (syncStatus.value === 'success') return `成功拉取并导入 ${syncCount.value} 款游戏。`;
    if (syncStatus.value === 'error') return syncErrorMsg.value || '未知错误，请检查后端服务日志。';
    return '';
  });

  async function handleSync() {
    if (isSyncing.value) return;
    isSyncing.value = true;
    syncStatus.value = '';
    syncErrorMsg.value = '';
    try {
      const games = await syncOwnedGames();
      syncCount.value = games.length;
      syncStatus.value = 'success';
      const now = new Date().toLocaleString();
      lastSyncTime.value = now;
      setLastSyncTime(now);
    } catch (err: any) {
      console.error('Sync failed:', err);
      syncStatus.value = 'error';
      syncErrorMsg.value = err.response?.data?.msg || err.response?.data?.message || err.message || '网络连接或服务器发生错误';
    } finally {
      isSyncing.value = false;
    }
  }

  return {
    isSyncing, syncStatus, syncCount, syncErrorMsg, lastSyncTime,
    statusClass, syncStatusIcon, syncStatusTitle, syncStatusMsg,
    handleSync,
  };
}
