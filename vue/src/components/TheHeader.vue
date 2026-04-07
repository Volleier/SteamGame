<template>
  <header class="header-container">
    
    <!-- Left: Two Logos -->
    <div class="header-logo-group">
      <!-- Placeholder SteamGame Logo -->
      <img src="https://placehold.co/180x40/eef8fc/007399?text=SteamGame+Logo" alt="SteamGame Logo" />
      
      <!-- Placeholder Volleier Logo -->
      <img src="https://placehold.co/140x40/eef8fc/007399?text=Volleier+Logo" alt="Volleier Logo" />
    </div>

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
@import '@/assets/styles/header.scss';
</style>