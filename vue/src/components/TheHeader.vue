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

    <!-- Right slot empty or placeholder for spacing -->
  </header>
</template>

<script setup lang="ts">
// Logic remains lightweight
</script>

<style scoped lang="scss">
@use '@/assets/styles/components/header' as *;
</style>