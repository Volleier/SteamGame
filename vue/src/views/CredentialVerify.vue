<template>
  <CyberGlassCard compact>
    <transition name="card-fade" appear>
      <div class="credential-panel credential-panel-flex">
        <div class="credential-panel-left">
          <div class="credential-panel-icon">
            <img src="@/assets/images/SteamGame_Icon.png" alt="Icon" />
          </div>
        </div>
        <div class="credential-panel-right">
          <div class="credential-panel-glass"></div>

          <div class="credential-panel-content">
            <div class="credential-panel-header">
              <h1>凭据验证</h1>
              <p>使用本地配置的 Steam API 验证凭据（不展示明文 apiKey）</p>
            </div>

            <!-- 无配置状态：引导用户先去配置 -->
            <div v-if="!steamId" class="no-credential-guide">
              <div class="guide-icon">⚠️</div>
              <p class="guide-title">尚未配置凭据</p>
              <p class="guide-desc">您需要先配置 Steam ID 和 API Key 才能进行凭据验证。</p>
              <button type="button" class="credential-btn" @click="goToConfig">
                <div class="credential-slider"></div>
                <div class="credential-text-container">
                  <span>前往配置</span>
                </div>
              </button>
            </div>

            <!-- 已配置状态：显示只读 Steam ID 和验证按钮 -->
            <form v-else class="credential-form" @submit.prevent="handleVerify">
              <!-- 用户信息卡片 -->
              <div class="profile-verify-card">
                <div class="profile-verify-card-glass"></div>
                <div class="profile-verify-card-content">
                  <!-- Left: Avatar (rounded matrix/square) -->
                  <div class="avatar-container">
                    <img :src="profile?.avatarFull || profile?.avatarMedium || profile?.avatar || defaultAvatar" @error="handleAvatarError" alt="Avatar" class="profile-avatar" />
                  </div>
                  <!-- Right: Name & ID -->
                  <div class="info-container">
                    <div class="profile-name">{{ profile?.personaName || '加载中...' }}</div>
                    <div class="profile-id">ID: {{ steamId }}</div>
                  </div>
                  <span class="steam-id-status-badge">✅ 已加载</span>
                </div>
              </div>

              <div class="button-container flex flex-col items-center gap-4 w-full">
                <button type="submit" class="credential-btn" :disabled="isLoading">
                  <div class="credential-slider"></div>
                  <div class="credential-text-container">
                    <span v-if="isLoading" class="loading-spinner"></span>
                    <span>{{ isLoading ? '验证中...' : '验证凭据' }}</span>
                  </div>
                </button>

                <button type="button" class="credential-btn" @click="goToConfig">
                  <div class="credential-slider"></div>
                  <div class="credential-text-container">
                    <span>凭据配置</span>
                  </div>
                </button>
              </div>
              <div v-if="errorMessage" class="error-message">
                {{ errorMessage }}
              </div>
            </form>
          </div>
        </div>
      </div>
    </transition>
  </CyberGlassCard>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import CyberGlassCard from '@/components/CyberGlassCard.vue';
import { useCredentialVerify } from '@/composables/useCredentialVerify';
import { getPlayerProfile, type PlayerProfile } from '@/api/player';
import defaultAvatar from '@/assets/images/SteamGame_Icon.png';

const { steamId, isLoading, errorMessage, handleVerify, goToConfig } = useCredentialVerify();

const profile = ref<PlayerProfile | null>(null);

const handleAvatarError = (event: Event) => {
  const target = event.target as HTMLImageElement;
  if (target && target.src !== defaultAvatar) {
    target.src = defaultAvatar;
  }
};

const fetchProfile = async () => {
  if (steamId.value) {
    try {
      const data = await getPlayerProfile();
      profile.value = data;
    } catch (error) {
      console.error('Failed to fetch profile in verification screen:', error);
    }
  }
};

watch(steamId, () => {
  fetchProfile();
});

onMounted(() => {
  fetchProfile();
});
</script>

<style scoped lang="scss">
@use '@/assets/styles/components/default' as *;
@use '@/assets/styles/pages/credential-verify' as *;

.no-credential-guide {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 0.75rem 0.5rem;
  gap: 0.4rem;
}

.guide-icon {
  font-size: 1.6rem;
}

.guide-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.85);
  margin: 0;
}

.guide-desc {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.5);
  margin: 0 0 0.25rem 0;
  max-width: 260px;
  line-height: 1.4;
}

.profile-verify-card {
  position: relative;
  width: 100%;
  margin-bottom: 1.25rem;
  box-sizing: border-box;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.03);
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.2);

  .profile-verify-card-glass {
    position: absolute;
    inset: 0;
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    z-index: 0;
  }

  .profile-verify-card-content {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 0.85rem 1rem;
  }

  .avatar-container {
    flex-shrink: 0;
    width: 3.5rem;
    height: 3.5rem;
    border-radius: 10px;
    overflow: hidden;
    border: 1px solid rgba(255, 255, 255, 0.15);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);

    .profile-avatar {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .info-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-width: 0;
    text-align: left;

    .profile-name {
      font-size: 1.1rem;
      font-weight: 700;
      color: #ffffff;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
    }

    .profile-id {
      font-size: 0.8rem;
      color: rgba(255, 255, 255, 0.55);
      font-family: monospace;
      margin-top: 0.2rem;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .steam-id-status-badge {
    align-self: flex-start;
    font-size: 0.7rem;
    color: #4ade80;
    background: rgba(74, 222, 128, 0.1);
    border: 1px solid rgba(74, 222, 128, 0.2);
    padding: 0.15rem 0.4rem;
    border-radius: 4px;
    flex-shrink: 0;
    font-weight: 600;
  }
}
</style>
