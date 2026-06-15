/**
 * 游戏列表状态与逻辑 — 搜索、排序、加载、导出
 */

import { ref, computed, onMounted } from 'vue';
import { getOwnedGames, type OwnedGame } from '@/api/games';
import { downloadCsv } from '@/utils/csv';

export function useGameList() {
  const games = ref<OwnedGame[]>([]);
  const isLoading = ref(false);
  const errorMessage = ref('');
  const searchQuery = ref('');
  const sortKey = ref<'app_id' | 'app_name' | 'app_time' | ''>('app_time');
  const sortOrder = ref<'asc' | 'desc'>('desc');

  const filteredAndSortedGames = computed<OwnedGame[]>(() => {
    const q = searchQuery.value.trim().toLowerCase();
    let list = games.value;

    if (q) {
      list = list.filter(g =>
        String(g.app_id).toLowerCase().includes(q)
        || (g.app_name || '').toLowerCase().includes(q),
      );
    }

    if (sortKey.value) {
      const key = sortKey.value;
      const order = sortOrder.value === 'asc' ? 1 : -1;
      list = list.slice().sort((a, b) => {
        const va = a[key];
        const vb = b[key];
        if (va == null && vb == null) return 0;
        if (va == null) return -1 * order;
        if (vb == null) return 1 * order;
        if (!isNaN(Number(va)) && !isNaN(Number(vb))) {
          return (Number(va) - Number(vb)) * order;
        }
        return String(va).localeCompare(String(vb)) * order;
      });
    }

    return list;
  });

  function loadGames() {
    isLoading.value = true;
    errorMessage.value = '';

    getOwnedGames()
      .then((data) => {
        games.value = data;
      })
      .catch((err) => {
        if (import.meta.env.DEV) {
          console.error('Failed to load games', err);
        }
        errorMessage.value = err.message || '加载游戏列表失败，请检查网络连接';
      })
      .finally(() => {
        isLoading.value = false;
      });
  }

  function setSort(key: 'app_id' | 'app_name' | 'app_time') {
    if (sortKey.value === key) {
      sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
    } else {
      sortKey.value = key;
      sortOrder.value = 'asc';
    }
  }

  function exportCSV() {
    const rows = filteredAndSortedGames.value;
    if (!rows || rows.length === 0) return;
    const header = ['游戏ID', '游戏名称', '游戏时长(小时)'];
    const data = rows.map(r => [
      String(r.app_id),
      r.app_name,
      r.app_time.toFixed(2),
    ]);
    downloadCsv('games_export.csv', header, data);
  }

  onMounted(() => {
    loadGames();
  });

  return {
    games,
    isLoading,
    errorMessage,
    searchQuery,
    sortKey,
    sortOrder,
    filteredAndSortedGames,
    loadGames,
    setSort,
    exportCSV,
  };
}
