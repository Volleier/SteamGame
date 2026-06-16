<template>
  <div class="flex flex-col gap-3">
    <div
      v-for="game in games"
      :key="game.appid"
      class="flex items-center gap-3 bg-white/5 border border-white/5 hover:border-[#00ffd5]/30 hover:bg-[#00ffd5]/5 p-3 rounded-lg transition-all duration-300 transform hover:translate-x-1"
    >
      <div class="w-10 h-[60px] rounded-md overflow-hidden bg-black/40 border border-white/10 flex-shrink-0 flex items-center justify-center">
        <img :src="`https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/${game.appid}/library_600x900.jpg`" @error="onImgError" class="w-full h-full object-cover" />
      </div>
      <div class="flex-1 min-w-0">
        <h3 class="text-sm font-bold text-white truncate">{{ game.name }}</h3>
        <p class="text-[10px] text-gray-400 font-mono mt-0.5">AppID: {{ game.appid }}</p>
      </div>
      <div class="text-right flex-shrink-0">
        <span class="text-sm font-black text-transparent bg-clip-text bg-gradient-to-br from-[#00ffd5] to-[#00d4ff]">{{ (game.playtime2Weeks / 60).toFixed(1) }}</span>
        <span class="text-[10px] text-gray-500 font-bold block">小时/2周</span>
      </div>
    </div>
    <div v-if="games.length === 0" class="text-center py-8 text-gray-500 text-sm">两周内暂无游玩记录</div>
  </div>
</template>

<script setup lang="ts">
import type { RecentGameItem } from '@/api/player';
import defaultAvatar from '@/assets/images/SteamGame_Icon.png';

defineProps<{ games: RecentGameItem[] }>();

function onImgError(e: Event) {
  const img = e.target as HTMLImageElement;
  if (img && img.src !== defaultAvatar) img.src = defaultAvatar;
}
</script>
