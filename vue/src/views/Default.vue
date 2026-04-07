<template>
  <!-- Background Layer: Dark Gray Solid currently with overflow hidden -->
  <div class="relative min-h-screen w-full flex items-center justify-center bg-[#151b23] text-white overflow-hidden font-sans">
    
    <!-- Parallax Rolling Posters Background -->
    <!-- Scale to 150% so that the corners cover the screen entirely when rotated -->
    <div class="absolute inset-0 z-0 w-[150%] h-[150%] top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 rotate-[-30deg] pointer-events-none flex justify-center gap-4 sm:gap-6 opacity-[0.25] select-none">
      
      <!-- We render 7 columns to ensure the screen width is fully saturated across all aspects -->
      <div 
        v-for="colIndex in 7" :key="'col-' + colIndex"
        class="flex flex-col gap-4 sm:gap-6 w-32 sm:w-48 lg:w-56"
        :class="colIndex % 2 === 0 ? 'scroll-down' : 'scroll-up'"
      >
        <!-- The images array is rendered twice per column to construct a seamless infinite 50% translation loop -->
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

    <!-- Foreground Content -->
    <!-- Glassmorphism Card Container sits strictly above (z-10) with blur to distinct it from background -->
    <div class="relative z-10 flex flex-col items-center px-12 py-12 sm:px-20 sm:py-16 rounded-2xl bg-white/[0.02] backdrop-blur-md border border-white/5 shadow-2xl">
      
      <!-- LOGO Area -->
      <!-- Use hidden images to enforce correct aspect ratio and sizing -->
      <div class="flex items-center justify-center gap-2 mb-5">
        
        <!-- Left Logo: Steam (with CSS Mask & Animated Blue Gradient) -->
        <div class="relative inline-block h-28 sm:h-40">
          <img :src="leftLogo" class="invisible h-full w-auto" alt="Spacer" />
          <div 
            class="absolute inset-0 bg-gradient-to-r from-[#00d4ff] via-[#2196F3] to-[#00d4ff] bg-[length:200%_auto] animate-gradient-flow"
            :style="{
              maskImage: `url(${leftLogo})`,
              WebkitMaskImage: `url(${leftLogo})`,
              maskSize: '100% 100%',
              WebkitMaskSize: '100% 100%',
              maskRepeat: 'no-repeat',
              WebkitMaskRepeat: 'no-repeat'
            }"
          ></div>
        </div>

        <!-- Right Logo: Game (pure white without glowing effects) -->
        <div class="relative inline-block h-28 sm:h-40">
          <img 
            :src="rightLogo" 
            class="h-full w-auto object-contain" 
            alt="Game" 
          />
        </div>
        
      </div>

      <!-- Horizontal Divider: Thicker, centered fading line WITHOUT glowing shadow -->
      <div class="w-full max-w-[85%] h-[2px] bg-gradient-to-r from-transparent via-white to-transparent mb-12"></div>

      <!-- Techwear Skewed Login Button matching new reference -->
      <router-link to="/login" class="group relative inline-flex items-center justify-center w-56 h-14 bg-white text-black font-extrabold text-2xl tracking-[0.1em] -skew-x-[10deg] shadow-[6px_6px_0_rgba(0,0,0,1)] border-b-[4px] border-[#ff00ff] overflow-hidden hover:shadow-[8px_8px_0_rgba(0,0,0,1)] hover:-translate-y-0.5 transition-all duration-300">
        
        <!-- Cyan Slider Block: Expands to cover the entire button and turns paler white/blue on hover -->
        <div class="absolute top-0 left-0 w-[40%] h-full bg-[#00d4ff] transition-all duration-500 ease-[cubic-bezier(0.25,1,0.5,1)] group-hover:w-full group-hover:bg-[#e0faff]"></div>
        
        <!-- Text Content: Un-skewed so the letters stand vertically straight -->
        <span class="relative z-10 skew-x-[10deg] uppercase">Login</span>
        
      </router-link>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getTrendingGamePosters, type GamePosterData } from '@/api/game';
import leftLogo from '@/assets/images/SteamGame_Left_Logo.png';
import rightLogo from '@/assets/images/SteamGame_Right_Logo.png';

const games = ref<GamePosterData[]>([]);

onMounted(async () => {
  try {
    games.value = await getTrendingGamePosters();
  } catch (error) {
    console.error("Failed to load posters for background:", error);
  }
});
</script>

<style scoped>
/* Gradient Flow Animation for the Steam Logo */
@keyframes gradient-flow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}
.animate-gradient-flow {
  animation: gradient-flow 3s ease infinite;
}

/* Bi-directional Infinite Scrolling Physics */
.scroll-up {
  animation: slide-up 40s linear infinite;
  will-change: transform;
}

.scroll-down {
  /* To move downward naturally in HTML flow, we start at -50% and translate to 0 */
  animation: slide-down 40s linear infinite;
  will-change: transform;
}

@keyframes slide-up {
  0% { transform: translateY(0); }
  100% { transform: translateY(-50%); }
}

@keyframes slide-down {
  0% { transform: translateY(-50%); }
  100% { transform: translateY(0); }
}
</style>