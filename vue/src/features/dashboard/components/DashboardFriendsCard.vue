<template>
  <div class="flex flex-col gap-3">
    <div v-for="friend in friends" :key="friend.steamId"
         class="flex items-center gap-3 bg-white/5 border border-white/5 hover:border-[#00ffd5]/30 hover:bg-[#00ffd5]/5 p-3 rounded-lg transition-all duration-300 transform hover:translate-x-1">
      <div class="w-8 h-8 rounded-full overflow-hidden bg-black/40 border border-white/10 flex-shrink-0 flex items-center justify-center">
        <img :src="defaultAvatar" class="w-full h-full object-cover" />
      </div>
      <div class="flex-1 min-w-0">
        <h3 class="text-sm font-bold text-white truncate">{{ friend.steamId }}</h3>
        <p class="text-[10px] text-gray-400 font-mono mt-0.5">关系: {{ friend.relationship }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { FriendItem } from '@/api/player';
import defaultAvatar from '@/assets/images/SteamGame_Icon.png';

defineProps<{ friends: FriendItem[] }>();
</script>
