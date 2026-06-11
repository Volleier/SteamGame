<template>
  <div class="container mx-auto px-4 py-8">
    <!-- 顶部栏 -->
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

        <!-- 操作区域 -->
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

    <!-- 加载状态 -->
    <div v-if="isLoading" class="flex flex-col items-center justify-center py-20">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-indigo-200 border-t-indigo-600 mb-4"></div>
      <p class="text-gray-500 text-sm">正在加载游戏列表...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="errorMessage" class="flex flex-col items-center justify-center py-20 bg-white rounded-lg shadow">
      <div class="text-4xl mb-4">😵</div>
      <p class="text-red-500 text-sm mb-4">{{ errorMessage }}</p>
      <button @click="loadGames" class="px-4 py-2 bg-indigo-600 text-white rounded-md text-sm hover:bg-indigo-500 transition-colors">
        重新加载
      </button>
    </div>

    <!-- 空状态（已加载但接口返回为空） -->
    <div v-else-if="games.length === 0" class="flex flex-col items-center justify-center py-20 bg-white rounded-lg shadow">
      <div class="text-4xl mb-4">🎮</div>
      <p class="text-gray-500 text-sm mb-2">暂无游戏数据</p>
      <p class="text-gray-400 text-xs">您的 Steam 账户中可能没有公开的游戏，或尚未同步。</p>
    </div>

    <!-- 表格 -->
    <div v-else class="overflow-x-auto rounded-lg shadow">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">游戏ID</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">游戏名称</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">游戏时长</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="game in filteredAndSortedGames" :key="game.app_id" class="hover:bg-gray-100 transition-colors">
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
              {{ game.app_id }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ game.app_name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ (Number(game.app_time) || 0).toFixed(2) }} 小时</td>
          </tr>
          <!-- 搜索无匹配 -->
          <tr v-if="filteredAndSortedGames.length === 0 && games.length > 0">
            <td colspan="3" class="px-6 py-8 text-center text-sm text-gray-400">
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
