<template>
  <div class="container mx-auto px-4 pt-3 pb-8 text-white font-sans">
    <!-- 顶部控制栏 -->
    <div class="bg-black/40 backdrop-blur-xl border border-white/10 shadow-2xl rounded-xl mb-6 p-4">
      <div class="flex flex-col md:flex-row items-center justify-between gap-4">
        <!-- 搜索框 -->
        <div class="relative w-full md:max-w-md">
          <span class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </span>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索游戏名称或 ID..."
            class="w-full bg-black/65 border border-white/15 rounded-lg pl-10 pr-4 py-2 text-sm text-white placeholder-gray-500 focus:outline-none focus:border-[#00d4ff] focus:ring-1 focus:ring-[#00d4ff] transition-all"
          />
        </div>

        <!-- 排序与导出 -->
        <div class="flex flex-wrap items-center justify-end gap-3 w-full md:w-auto">
          <div class="flex items-center bg-black/40 border border-white/10 rounded-lg p-1">
            <button
              v-for="option in sortOptions"
              :key="option.key"
              @click="setSort(option.key)"
              :class="[
                'px-3.5 py-1.5 rounded-md text-xs font-bold uppercase tracking-wider transition-all duration-200 flex items-center gap-1.5',
                sortKey === option.key
                  ? 'bg-[#00d4ff]/20 text-[#00d4ff] border border-[#00d4ff]/30'
                  : 'text-gray-400 hover:text-white border border-transparent'
              ]"
            >
              {{ option.label }}
              <span v-if="sortKey === option.key" class="text-[10px]">
                {{ sortOrder === 'asc' ? '▲' : '▼' }}
              </span>
            </button>
          </div>

          <button
            @click="exportCSV"
            class="px-4 py-2 bg-[#00d4ff]/10 hover:bg-[#00d4ff]/25 border border-[#00d4ff]/30 text-[#00d4ff] rounded-lg text-xs font-bold uppercase tracking-wider transition-all flex items-center gap-2"
          >
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            导出 CSV
          </button>
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

    <!-- 游戏卡片列表 -->
    <div v-else class="flex flex-col gap-4">
      <div v-for="game in filteredAndSortedGames" :key="game.app_id"
           class="game-card flex flex-col md:flex-row bg-black/40 backdrop-blur-md border border-white/10 rounded-xl overflow-hidden hover:border-[#00d4ff]/40 hover:bg-[#00d4ff]/5 transition-all duration-300 group shadow-lg">
        
        <!-- 左侧: 游戏垂直海报 -->
        <a :href="`https://store.steampowered.com/app/${game.app_id}`" target="_blank"
           class="relative w-full md:w-[100px] lg:w-[120px] aspect-[2/3] md:h-[150px] shrink-0 bg-black/80 overflow-hidden border-r border-white/5 block cursor-pointer">
          <img
            :src="`https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/${game.app_id}/library_600x900.jpg`"
            @error="handleImageError"
            class="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-500"
            alt="Game Poster"
          />
        </a>

        <!-- 中部及右侧内容包装 -->
        <div class="flex-1 p-5 flex flex-col lg:flex-row gap-5 justify-between">
          <!-- 中左侧: 标题 -->
          <div class="flex-1 flex flex-col justify-center min-w-0">
            <a :href="`https://store.steampowered.com/app/${game.app_id}`" target="_blank" class="no-underline hover:no-underline cursor-pointer block">
              <h2 class="text-xl font-bold text-white group-hover:text-[#00d4ff] transition-colors truncate no-underline">
                {{ game.app_name }}
              </h2>
            </a>
          </div>

          <!-- 中右侧: 开发者与发行信息 -->
          <div class="w-full lg:w-[220px] shrink-0 flex flex-col justify-center text-[11px] text-gray-400 space-y-1.5 border-t lg:border-t-0 lg:border-l border-white/10 pt-4 lg:pt-0 lg:pl-4">
            <div><span class="text-gray-500">游戏 ID:</span> <span class="text-gray-300 font-mono ml-1">{{ game.app_id }}</span></div>
            <div><span class="text-gray-500">开发者:</span> <span class="text-gray-300 font-medium ml-1 truncate block">{{ game.developer || 'Unknown' }}</span></div>
            <div><span class="text-gray-500">发行商:</span> <span class="text-gray-300 font-medium ml-1 truncate block">{{ game.publisher || 'Unknown' }}</span></div>
            <div><span class="text-gray-500">发行日期:</span> <span class="text-gray-300 font-medium ml-1">{{ game.release_date || 'Unknown' }}</span></div>
          </div>

          <!-- 新增: 在线游玩人数 -->
          <div class="w-full lg:w-[120px] shrink-0 flex flex-col justify-center items-start lg:items-center border-t lg:border-t-0 lg:border-l border-white/10 pt-4 lg:pt-0 lg:pl-4">
            <div class="text-[10px] text-gray-500 uppercase tracking-widest text-center">当前在线</div>
            <div class="text-xl font-black text-[#00ffd5] mt-1">
              {{ playerCounts[game.app_id] !== undefined ? playerCounts[game.app_id] : '...' }}
            </div>
          </div>

          <!-- 右侧: 游戏时长与特性标签 -->
          <div class="w-full lg:w-[220px] shrink-0 flex flex-col justify-between items-start lg:items-end border-t lg:border-t-0 lg:border-l border-white/10 pt-4 lg:pt-0 lg:pl-4">
            <div class="text-left lg:text-right w-full">
              <div class="text-xs text-gray-500 uppercase tracking-widest">总游玩时间</div>
              <div class="text-2xl font-black text-white mt-0.5">
                <span class="text-[#00d4ff]">{{ (Number(game.app_time) || 0).toFixed(2) }}</span> <span class="text-xs text-gray-400 font-normal">小时</span>
              </div>
            </div>

            <!-- 特性标签 -->
            <div v-if="game.tags && game.tags.length > 0" class="flex flex-wrap lg:justify-end gap-1.5 w-full mt-3 overflow-y-auto max-h-[64px] custom-scrollbar pr-1">
              <span v-for="tag in game.tags" :key="tag"
                    class="px-1.5 py-0.5 rounded bg-white/5 border border-white/10 text-[9px] text-gray-300 tracking-wider whitespace-nowrap">
                {{ tag }}
              </span>
            </div>
          </div>
        </div>

      </div>

      <!-- 搜索无匹配 -->
      <div v-if="filteredAndSortedGames.length === 0 && games.length > 0"
           class="flex flex-col items-center justify-center py-20 bg-black/40 backdrop-blur-xl border border-white/10 rounded-xl shadow-2xl">
        <div class="text-4xl mb-4">🔍</div>
        <p class="text-gray-400 text-sm uppercase tracking-widest">没有匹配 "{{ searchQuery }}" 的游戏</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGameList } from '@/composables/useGameList';
import { useGamePlayerCounts } from '@/features/games/composables/useGamePlayerCounts';

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

const sortOptions = [
  { key: 'app_time', label: '时间' },
  { key: 'app_name', label: '名称' },
  { key: 'app_id', label: 'ID' }
] as const;

const { playerCounts } = useGamePlayerCounts(games);

function handleImageError(event: Event) {
  const img = event.target as HTMLImageElement;
  if (!img) return;
  const url = img.src;
  if (url.includes('library_600x900.jpg')) {
    img.src = url.replace('library_600x900.jpg', 'header.jpg');
  } else if (url.includes('header.jpg')) {
    img.src = url.replace('header.jpg', 'capsule_616x353.jpg');
  } else {
    img.src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="600" height="900" viewBox="0 0 600 900"><rect width="100%" height="100%" fill="%231a1d24"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-family="sans-serif" font-size="64" fill="%234a5264">🎮</text></svg>';
  }
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.2);
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 212, 255, 0.5);
}

.game-card {
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.4);
}
</style>
