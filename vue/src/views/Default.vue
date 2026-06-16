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
            WebkitMaskRepeat: 'no-repeat',
          }"
        ></div>
      </div>

      <!-- Right Logo: Game (pure white without glowing effects) -->
      <div class="relative inline-block h-28 sm:h-40">
        <img :src="rightLogo" class="h-full w-auto object-contain" alt="Game" />
      </div>
    </div>

    <!-- Horizontal Divider: Thicker, centered fading line -->
    <div class="w-full max-w-[85%] h-[2px] bg-gradient-to-r from-transparent via-white to-transparent mb-12"></div>

    <!-- Action Buttons Container -->
    <div class="flex flex-col sm:flex-row gap-6 items-center justify-center w-full">
      <!-- Techwear Skewed About Button -->
      <router-link to="/about" class="credential-btn max-w-[14rem]">
        <div class="credential-slider"></div>
        <div class="credential-text-container">
          <span>关于此项目</span>
        </div>
      </router-link>

      <!-- Techwear Skewed Credential Verify Button -->
      <router-link to="/credential-verify" class="credential-btn max-w-[14rem]">
        <div class="credential-slider"></div>
        <div class="credential-text-container">
          <span>凭据验证</span>
        </div>
      </router-link>
    </div>
  </CyberGlassCard>
</template>

<script setup lang="ts">
import CyberGlassCard from '@/components/CyberGlassCard.vue';
import leftLogo from '@/assets/images/SteamGame_Left_Logo.png';
import rightLogo from '@/assets/images/SteamGame_Right_Logo.png';
</script>

<style scoped lang="scss">
@use '@/assets/styles/components/default' as *;
</style>
