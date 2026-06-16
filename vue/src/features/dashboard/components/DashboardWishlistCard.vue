<template>
  <div class="flex flex-col gap-3">
    <div
      v-for="item in items"
      :key="item.appid"
      class="flex items-center gap-3 bg-white/5 border border-white/5 hover:border-[#00d4ff]/30 hover:bg-[#00d4ff]/5 p-3 rounded-lg transition-all duration-300 transform hover:translate-x-1"
    >
      <div class="w-10 h-[60px] rounded-md overflow-hidden bg-black/40 border border-white/10 flex-shrink-0 flex items-center justify-center">
        <img :src="`https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/${item.appid}/library_600x900.jpg`" @error="onImgError" class="w-full h-full object-cover" />
      </div>
      <div class="flex-1 min-w-0">
        <h3 class="text-sm font-bold text-white truncate">{{ item.name || '未知游戏' }}</h3>
        <p class="text-[13px] font-black text-[#00ffd5] mt-1 tracking-wider">{{ item.price || '￥ --' }}</p>
      </div>
    </div>
    <div v-if="items.length === 0" class="text-center py-8 text-gray-500 text-sm">愿望单暂无游戏</div>
  </div>
</template>

<script setup lang="ts">
import type { WishlistItem } from '@/api/player';
import defaultAvatar from '@/assets/images/SteamGame_Icon.png';

defineProps<{ items: WishlistItem[] }>();

function onImgError(e: Event) {
  const img = e.target as HTMLImageElement;
  if (img && img.src !== defaultAvatar) img.src = defaultAvatar;
}
</script>
