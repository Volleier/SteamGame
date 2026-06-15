<template>
  <header v-show="!isFullscreen" class="header-container">

    <!-- Left: Two Logos — 点击返回主界面 -->
    <router-link to="/" class="header-logo-group">
      <!-- SteamGame Logo -->
      <img src="@/assets/images/SteamGame_Icon.png" alt="SteamGame Logo" />

      <!-- 白色竖线分隔 -->
      <div class="logo-separator" aria-hidden="true"></div>

      <!-- Volleier Logo -->
      <img src="@/assets/images/Volleier_Logo.png" alt="Volleier Logo" />

    </router-link>

    <!-- Right: User profile card (only shown when logged in/authenticated) -->
    <div v-if="showProfileCard" class="user-profile-card">
      <img :src="profile.avatar" alt="Avatar" class="user-avatar" />
      <span class="user-name">{{ profile.personaName }}</span>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import { getPlayerProfile, type PlayerProfile } from '@/api/player';

const store = useStore();
const route = useRoute();
const isFullscreen = computed(() => store.state.isFullscreen);
const profile = ref<PlayerProfile | null>(null);

const steamId = computed(() => store.state.steamId);
const isAuthenticated = computed(() => store.state.authenticated || !!store.state.steamId);
const isDashboard = computed(() => route?.path?.startsWith('/dashboard'));
const showProfileCard = computed(() => isDashboard.value && isAuthenticated.value && !!profile.value);

const fetchProfile = async () => {
  if (isDashboard.value && isAuthenticated.value) {
    try {
      const data = await getPlayerProfile();
      profile.value = data;
    } catch (error) {
      console.error('Failed to fetch player profile:', error);
    }
  } else {
    profile.value = null;
  }
};

watch([steamId, isDashboard], () => {
  fetchProfile();
}, { immediate: true });

onMounted(() => {
  fetchProfile();
});
</script>

<style scoped lang="scss">
@use '@/assets/styles/components/header' as *;

.user-profile-card {
  @apply flex items-center gap-2 px-3 py-1 bg-white/5 border border-white/10 rounded-lg mr-4 max-h-[2.25rem] overflow-hidden;
  
  .user-avatar {
    @apply w-6 h-6 rounded-md object-cover border border-white/10;
  }
  
  .user-name {
    @apply text-sm font-semibold text-gray-200 tracking-wide truncate max-w-[100px];
  }
}
</style>