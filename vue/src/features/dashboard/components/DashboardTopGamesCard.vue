<template>
  <div class="flex flex-col gap-3">
    <div v-for="(game, index) in games" :key="game.app_id"
         class="flex items-center gap-3 bg-white/5 border border-white/5 hover:border-[#ff00ff]/30 hover:bg-[#ff00ff]/5 p-3 rounded-lg transition-all duration-300 transform hover:translate-x-1">
      <div class="w-6 h-6 flex items-center justify-center rounded-md text-xs font-black shrink-0" :class="getRankClass(index)">{{ index + 1 }}</div>
      <div class="w-10 h-[60px] rounded-md overflow-hidden bg-black/40 border border-white/10 flex-shrink-0 flex items-center justify-center">
        <img :src="`https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/${game.app_id}/library_600x900.jpg`" @error="onImgError" class="w-full h-full object-cover" />
      </div>
      <div class="flex-1 min-w-0">
        <h3 class="text-sm font-bold text-white truncate">{{ game.app_name }}</h3>
        <p class="text-[10px] text-gray-400 font-mono mt-0.5">AppID: {{ game.app_id }}</p>
      </div>
      <div class="text-right flex-shrink-0">
        <span class="text-sm font-black text-transparent bg-clip-text bg-gradient-to-br from-[#00d4ff] to-[#00f7ff]">{{ game.app_time }}</span>
        <span class="text-[10px] text-gray-500 font-bold block">小时</span>
      </div>
    </div>
    <div v-if="games.length === 0" class="text-center py-8 text-gray-500 text-sm">暂无游玩记录</div>
  </div>
</template>

<script setup lang="ts">
import type { OwnedGame } from '@/api/games';
import defaultAvatar from '@/assets/images/SteamGame_Icon.png';
import { getRankClass } from '@/features/dashboard/composables/useDashboardData';

defineProps<{ games: OwnedGame[] }>();

function onImgError(e: Event) {
  const img = e.target as HTMLImageElement;
  if (img && img.src !== defaultAvatar) img.src = defaultAvatar;
}
</script>
