<template>
  <div class="relative w-full min-h-screen">
    <!-- Header floats globally at the top -->
    <TheHeader />
    <!-- Dynamic Route Content Space -->
    <router-view></router-view>
  </div>
</template>

<script setup lang="ts">
import { defineAsyncComponent } from 'vue';
import TheHeader from '@/components/TheHeader.vue';
</script>

<style lang="scss">
@use '@/assets/styles/base/global' as *;
</style>
