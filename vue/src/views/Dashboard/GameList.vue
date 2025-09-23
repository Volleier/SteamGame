<template>
  <div class="container mx-auto px-4 py-8">
    <!-- 顶部栏 -->
    <!-- 增加上下内边距（py-6），保持左右内边距 px-4 -->
    <div class="bg-white shadow rounded-lg mb-6">
      <div class="flex flex-col sm:flex-row items-center justify-between py-6 px-4 space-y-3 sm:space-y-0">
        <div class="flex items-center space-x-3">
          <h1 class="text-xl font-semibold text-gray-800">游戏列表</h1>
        </div>

        <div class="flex-1 max-w-md mx-0 sm:mx-4 w-full">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索游戏名称或ID..."
            class="w-full border border-gray-200 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <!-- 操作区域：始终垂直排列，排序横向按钮在上，导出按钮固定在下方 -->
        <div class="flex flex-col items-end space-y-2">
          <div class="flex items-center space-x-2">
            <span class="text-sm text-gray-600 mr-2">排序：</span>
            <button @click="setSort('app_id')" :class="['px-3 py-1 rounded-md text-sm border', sortKey === 'app_id' ? 'bg-indigo-600 text-white' : 'bg-white text-gray-700']">
              ID <span v-if="sortKey === 'app_id'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
            </button>
            <button @click="setSort('app_name')" :class="['px-3 py-1 rounded-md text-sm border', sortKey === 'app_name' ? 'bg-indigo-600 text-white' : 'bg-white text-gray-700']">
              名称 <span v-if="sortKey === 'app_name'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
            </button>
            <button @click="setSort('app_time')" :class="['px-3 py-1 rounded-md text-sm border', sortKey === 'app_time' ? 'bg-indigo-600 text-white' : 'bg-white text-gray-700']">
              时长 <span v-if="sortKey === 'app_time'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
            </button>
          </div>
          <div>
            <button @click="exportCSV" class="px-3 py-2 bg-green-600 text-white rounded-md text-sm hover:bg-green-500">导出为 CSV</button>
          </div>
        </div>
      </div>
    </div>

    <div class="overflow-x-auto rounded-lg shadow">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">游戏ID</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">游戏名称</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">游戏时长</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="game in filteredAndSortedGames" :key="game.app_id" class="hover:bg-gray-100">
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
              {{ game.app_id }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ game.app_name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ (Number(game.app_time) || 0).toFixed(2) }} 小时</td>
          </tr>
          <!-- 没有数据时显示 -->
          <tr v-if="filteredAndSortedGames.length === 0">
            <td colspan="3" class="px-6 py-4 text-center text-sm text-gray-500">暂无游戏数据</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GameList',
  data() {
    // 页面顶部包含三个区域：左侧标题，中间搜索框，右侧操作按钮（导出 CSV 与排序）。
    // 搜索与排序均在前端执行
    return {
      games: [],
      searchQuery: '',
      sortKey: '',
      sortOrder: 'asc', // or 'desc'
    };
  },
  mounted() {
    // 从后端获取已保存的 owned games
    fetch('/api/ownedgames/list')
      .then((res) => res.json())
      .then((data) => {
        // 后端返回字段: appid, name, playtimeForever
        // 页面的搜索输入支持按游戏ID或名称进行模糊匹配（大小写不敏感）。
        this.games = data.map((g) => ({
          app_id: g.appid,
          app_name: g.name,
          // 转换分钟为小时并保留两位小数
          // 使用 Number(toFixed(2)) 将字符串转换回数字以便排序和显示一致
          app_time: Number(((g.playtimeForever || 0) / 60).toFixed(2)),
        }));
      })
      .catch((err) => {
        console.error('Failed to load games', err);
      });
  },
  computed: {
    filteredAndSortedGames() {
      const q = (this.searchQuery || '').trim().toLowerCase();
      let list = this.games;
      if (q) {
        list = list.filter((g) => {
          return String(g.app_id).toLowerCase().includes(q) || (g.app_name || '').toLowerCase().includes(q);
        });
      }

      if (this.sortKey) {
        const key = this.sortKey;
        const order = this.sortOrder === 'asc' ? 1 : -1;
        list = list.slice().sort((a, b) => {
          const va = a[key];
          const vb = b[key];
          if (va == null && vb == null) return 0;
          if (va == null) return -1 * order;
          if (vb == null) return 1 * order;
          // 数字比较优先
          if (!isNaN(Number(va)) && !isNaN(Number(vb))) {
            return (Number(va) - Number(vb)) * order;
          }
          return String(va).localeCompare(String(vb)) * order;
        });
      }

      return list;
    },
  },
  methods: {
    setSort(key) {
      if (this.sortKey === key) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
      } else {
        this.sortKey = key;
        this.sortOrder = 'asc';
      }
    },
    exportCSV() {
      const rows = this.filteredAndSortedGames;
      if (!rows || rows.length === 0) {
        // 可以考虑提示用户无数据
        return;
      }
      const header = ['游戏ID', '游戏名称', '游戏时长(小时)'];
      const csv = [header.join(',')]
        .concat(rows.map((r) => `${r.app_id},"${(r.app_name || '').replace(/"/g, '""')}",${typeof r.app_time === 'number' ? r.app_time.toFixed(2) : r.app_time}`))
        .join('\n');

      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'games_export.csv';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    },
  },
};
</script>
