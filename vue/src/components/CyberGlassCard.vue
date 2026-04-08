<template>
  <!-- Background Layer: Even Darker Solid currently with overflow hidden -->
  <div class="relative min-h-screen w-full flex items-center justify-center bg-[#0a0d14] text-white overflow-hidden font-sans">
    <!-- Parallax Rolling Posters Background (Outer Global Layer) -->
    <div
      class="absolute z-0 w-[150vw] h-[150vh] top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 rotate-[-30deg] pointer-events-none flex justify-center gap-4 sm:gap-6 opacity-[0.25] select-none"
    >
      <div v-for="colIndex in 7" :key="'col-' + colIndex" class="flex flex-col gap-4 sm:gap-6 w-32 sm:w-48 lg:w-56" :class="colIndex % 2 === 0 ? 'scroll-down' : 'scroll-up'">
        <template v-for="loop in 2" :key="'loop-' + loop + '-' + colIndex">
          <div v-for="game in games" :key="game.id + '-' + loop + '-' + colIndex" class="w-full flex-shrink-0">
            <img
              :src="game.imageUrl"
              class="w-full object-cover rounded-xl border border-white/10 shadow-[0_8px_30px_rgba(0,0,0,0.8)] brightness-75"
              loading="lazy"
              alt="Game Poster"
            />
          </div>
        </template>
      </div>
    </div>

    <!-- Glassmorphism Card Container sits strictly above (z-10) with blur to distinct it from background -->
    <div
      :class="[
        'relative z-10 flex flex-col items-center rounded-2xl bg-black/[0.4] border border-white/5 shadow-2xl overflow-hidden backdrop-blur-xl',
        compact ? 'px-4 py-4 sm:px-6 sm:py-6' : 'px-12 py-12 sm:px-20 sm:py-16',
      ]"
    >
      <!-- Internal Duplicated Grid (Pixelation Effect Wrapper) -->
      <!-- Synchronized 1:1 with the outer global layer through absolute centering logic -->
      <div
        class="absolute z-0 w-[150vw] h-[150vh] top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 rotate-[-30deg] pointer-events-none flex justify-center gap-4 sm:gap-6 opacity-[0.35] select-none"
      >
        <div
          v-for="colIndex in 7"
          :key="'inner-col-' + colIndex"
          class="flex flex-col gap-4 sm:gap-6 w-32 sm:w-48 lg:w-56"
          :class="colIndex % 2 === 0 ? 'scroll-down' : 'scroll-up'"
        >
          <template v-for="loop in 2" :key="'inner-loop-' + loop + '-' + colIndex">
            <div v-for="game in games" :key="'inner-' + game.id + '-' + loop + '-' + colIndex" class="w-full flex-shrink-0">
              <img
                :src="game.imageUrl"
                class="w-full object-cover rounded-xl border border-white/10 shadow-[0_8px_30px_rgba(0,0,0,0.8)] brightness-50 moving-pixels"
                loading="lazy"
                alt="Game Poster Pixelated"
              />
            </div>
          </template>
        </div>
      </div>

      <!-- Foregound UI Interface Layer that stays unaffected by inner layers -->
      <div class="relative z-10 flex flex-col items-center w-full">
        <!-- Allows users to drop forms, logos, and buttons elegantly into the center container -->
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getTrendingGamePosters, type GamePosterData } from '@/api/game';

// 支持紧凑模式（用于 凭据配置/凭据验证 页面缩小内边距）
const props = defineProps<{
  compact?: boolean;
}>();

const games = ref<GamePosterData[]>([]);

onMounted(async () => {
  try {
    games.value = await getTrendingGamePosters();
  } catch (error) {
    console.error('Failed to load posters for background:', error);
  }
});
</script>

<style scoped lang="scss">
@import url('@/assets/styles/default.scss');
</style>
