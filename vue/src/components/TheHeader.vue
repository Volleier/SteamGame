<template>
  <header class="header-container">

    <!-- Left: Two Logos — 点击返回主界面 -->
    <router-link to="/" class="header-logo-group">
      <!-- SteamGame Logo -->
      <img src="@/assets/images/SteamGame_Icon.png" alt="SteamGame Logo" />

      <!-- 白色竖线分隔 -->
      <div class="logo-separator" aria-hidden="true"></div>

      <!-- Volleier Logo -->
      <img src="@/assets/images/Volleier_Logo.png" alt="Volleier Logo" />

    </router-link>

    <!-- Right: ABOUT Button seamlessly integrated -->
    <router-link to="/about" class="about-btn">

      <div class="about-slider"></div>

      <div class="about-text-wrapper">
        <span>About</span>
      </div>

    </router-link>

  </header>
</template>

<script setup lang="ts">
// Logic remains lightweight
</script>

<style scoped lang="scss">
@use '@/assets/styles/header' as *;
</style>