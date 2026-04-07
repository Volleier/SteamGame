<template>
  <CyberGlassCard>
    
    <!-- LOGO Area -->
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
          }"></div>
      </div>

      <!-- Right Logo: Game (pure white without glowing effects) -->
      <div class="relative inline-block h-28 sm:h-40">
        <img :src="rightLogo" class="h-full w-auto object-contain" alt="Game" />
      </div>
    </div>

    <!-- Horizontal Divider: Thicker, centered fading line -->
    <div class="w-full max-w-[85%] h-[2px] bg-gradient-to-r from-transparent via-white to-transparent mb-12"></div>

    <!-- Techwear Skewed Login Button matching new reference -->
    <router-link to="/login"
      class="group relative inline-flex items-center w-56 h-14 bg-white text-black font-extrabold text-2xl tracking-[0.1em] -skew-x-[10deg] border-b-[4px] border-[#ff00ff] overflow-hidden transition-all duration-300">

      <!-- Cyan Slider Block: Expands to cover the entire button and turns paler white/blue on hover -->
      <div
        class="absolute top-0 left-0 w-[25%] h-full bg-[#00d4ff] transition-all duration-500 ease-[cubic-bezier(0.25,1,0.5,1)] group-hover:w-full group-hover:bg-[#e0faff]">
      </div>

      <!-- Text Content: Confined to the right 75% so it strictly avoids overlapping the slider initially -->
      <div class="absolute right-0 w-[75%] h-full flex items-center justify-center z-10 pointer-events-none">
        <span class="skew-x-[10deg] uppercase">Login</span>
      </div>

    </router-link>

  </CyberGlassCard>
</template>

<script setup lang="ts">
import CyberGlassCard from '@/components/CyberGlassCard.vue';
import leftLogo from '@/assets/images/SteamGame_Left_Logo.png';
import rightLogo from '@/assets/images/SteamGame_Right_Logo.png';
</script>