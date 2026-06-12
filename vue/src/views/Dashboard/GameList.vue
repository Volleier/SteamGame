<template>
  <div class="container mx-auto px-4 py-8 text-white font-sans">
    <!-- 顶部栏 -->
    <div class="bg-black/40 backdrop-blur-xl border border-white/10 shadow-2xl rounded-xl mb-6">
      <div class="flex flex-col sm:flex-row items-center justify-between py-6 px-4 space-y-3 sm:space-y-0">
        <div class="flex items-center space-x-3">
          <h1 class="text-xl font-bold text-white tracking-widest uppercase" style="text-shadow: 0 0 10px rgba(0, 212, 255, 0.3);">游戏列表</h1>
        </div>

        <div class="flex-1 max-w-md mx-0 sm:mx-4 w-full">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索游戏名称或ID..."
            class="w-full bg-black/50 border border-white/20 rounded-md px-3 py-2 text-sm text-white placeholder-gray-400 focus:outline-none focus:border-[#00d4ff] focus:ring-1 focus:ring-[#00d4ff] transition-colors"
          />
        </div>

        <!-- 操作区域 -->
        <div class="flex flex-col items-end space-y-2">
          <div class="flex items-center space-x-2">
            <span class="text-sm text-gray-400 mr-2 uppercase tracking-wider">排序：</span>
            <button @click="setSort('app_id')" :class="['px-3 py-1 rounded-md text-sm border transition-colors', sortKey === 'app_id' ? 'bg-[#00d4ff]/20 border-[#00d4ff] text-[#00d4ff]' : 'bg-transparent border-white/20 text-gray-300 hover:border-white/50']">
              ID <span v-if="sortKey === 'app_id'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
            </button>
            <button @click="setSort('app_name')" :class="['px-3 py-1 rounded-md text-sm border transition-colors', sortKey === 'app_name' ? 'bg-[#00d4ff]/20 border-[#00d4ff] text-[#00d4ff]' : 'bg-transparent border-white/20 text-gray-300 hover:border-white/50']">
              名称 <span v-if="sortKey === 'app_name'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
            </button>
            <button @click="setSort('app_time')" :class="['px-3 py-1 rounded-md text-sm border transition-colors', sortKey === 'app_time' ? 'bg-[#00d4ff]/20 border-[#00d4ff] text-[#00d4ff]' : 'bg-transparent border-white/20 text-gray-300 hover:border-white/50']">
              时长 <span v-if="sortKey === 'app_time'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
            </button>
          </div>
          <div>
            <button @click="exportCSV" class="px-3 py-2 bg-[#00d4ff]/10 border border-[#00d4ff] text-[#00d4ff] rounded-md text-sm hover:bg-[#00d4ff]/30 transition-colors uppercase tracking-wider font-bold">导出为 CSV</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="flex flex-col items-center justify-center py-20 bg-black/40 backdrop-blur-xl border border-white/10 rounded-xl shadow-2xl">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-[#00d4ff]/30 border-t-[#00d4ff] mb-4"></div>
      <p class="text-gray-400 text-sm tracking-widest uppercase">正在加载游戏列表...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="errorMessage" class="flex flex-col items-center justify-center py-20 bg-black/40 backdrop-blur-xl border border-red-500/30 rounded-xl shadow-2xl">
      <div class="text-4xl mb-4">😵</div>
      <p class="text-red-400 text-sm mb-4">{{ errorMessage }}</p>
      <button @click="loadGames" class="px-4 py-2 bg-red-500/20 border border-red-500 text-red-400 rounded-md text-sm hover:bg-red-500/40 transition-colors uppercase">
        重新加载
      </button>
    </div>

    <!-- 空状态 -->
    <div v-else-if="games.length === 0" class="flex flex-col items-center justify-center py-20 bg-black/40 backdrop-blur-xl border border-white/10 rounded-xl shadow-2xl">
      <div class="text-4xl mb-4">🎮</div>
      <p class="text-gray-300 text-sm mb-2 uppercase tracking-widest">暂无游戏数据</p>
      <p class="text-gray-500 text-xs">您的 Steam 账户中可能没有公开的游戏，或尚未同步。</p>
    </div>

    <!-- 表格 -->
    <div v-else class="overflow-x-auto rounded-xl shadow-2xl bg-black/40 backdrop-blur-xl border border-white/10">
      <table class="min-w-full divide-y divide-white/10">
        <thead class="bg-black/50">
          <tr>
            <th scope="col" class="px-6 py-4 text-left text-xs font-bold text-[#00d4ff] uppercase tracking-widest">游戏ID</th>
            <th scope="col" class="px-6 py-4 text-left text-xs font-bold text-[#00d4ff] uppercase tracking-widest">游戏名称</th>
            <th scope="col" class="px-6 py-4 text-left text-xs font-bold text-[#00d4ff] uppercase tracking-widest">游戏时长</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-white/5">
          <tr v-for="game in filteredAndSortedGames" :key="game.app_id" class="hover:bg-white/5 transition-colors">
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-200">
              {{ game.app_id }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-white">
              {{ game.app_name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">
              <span class="text-[#00d4ff] font-medium">{{ (Number(game.app_time) || 0).toFixed(2) }}</span> 小时
            </td>
          </tr>
          <!-- 搜索无匹配 -->
          <tr v-if="filteredAndSortedGames.length === 0 && games.length > 0">
            <td colspan="3" class="px-6 py-8 text-center text-sm text-gray-500 uppercase tracking-widest">
              没有匹配 "{{ searchQuery }}" 的游戏
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGameList } from '@/composables/useGameList';

const {
  isLoading,
  errorMessage,
  searchQuery,
  sortKey,
  sortOrder,
  filteredAndSortedGames,
  games,
  loadGames,
  setSort,
  exportCSV,
} = useGameList();
</script>
