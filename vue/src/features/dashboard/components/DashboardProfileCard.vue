<template>
  <div>
    <div class="flex items-center gap-4 border-b border-white/10 pb-4 mb-4">
      <div class="w-16 h-16 rounded-xl overflow-hidden border border-white/15 flex-shrink-0">
        <img :src="avatarSrc" @error="onAvatarError" alt="Avatar" class="w-full h-full object-cover" />
      </div>
      <div class="flex flex-col justify-center gap-0.5 min-w-0">
        <div class="text-2xl font-black text-white truncate leading-none">{{ profile?.personaName || '加载中...' }}</div>
        <div class="text-xs text-gray-500 font-mono tracking-wider leading-none">ID: {{ steamId || '--' }}</div>
        <div class="text-base font-bold leading-none" :class="statusClass">{{ statusText }}</div>
      </div>
    </div>
    <div class="text-center py-2">
      <p class="text-gray-400 text-xs tracking-widest uppercase mb-1">已收录游戏</p>
      <p class="text-5xl font-black text-transparent bg-clip-text bg-gradient-to-br from-[#00d4ff] to-[#00ffd5] leading-tight">
        {{ gamesCount !== null ? gamesCount : '--' }}
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { PlayerProfile } from '@/api/player';
import defaultAvatar from '@/assets/images/SteamGame_Icon.png';
import { getStatusText, getStatusClass } from '@/features/dashboard/composables/useDashboardData';

const props = defineProps<{
  profile: PlayerProfile | null;
  steamId: string;
  gamesCount: number | null;
}>();

const avatarSrc = computed(() => props.profile?.avatarFull || props.profile?.avatarMedium || props.profile?.avatar || defaultAvatar);
const statusText = computed(() => getStatusText(props.profile?.personaState));
const statusClass = computed(() => getStatusClass(props.profile?.personaState));

function onAvatarError(e: Event) {
  const img = e.target as HTMLImageElement;
  if (img && img.src !== defaultAvatar) img.src = defaultAvatar;
}
</script>
