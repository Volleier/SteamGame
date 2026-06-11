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
              <div class="form-group">
                <label class="steam-id-label">已配置的 Steam ID</label>
                <input id="Steam_Id" type="text" v-model="steamId" disabled readonly class="form-input readonly-input" placeholder="暂无 Steam ID 数据" />
                <span class="steam-id-status">✅ 已加载</span>
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
import CyberGlassCard from '@/components/CyberGlassCard.vue';
import { useCredentialVerify } from '@/composables/useCredentialVerify';

const { steamId, isLoading, errorMessage, handleVerify, goToConfig } = useCredentialVerify();
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

.steam-id-label {
  display: block;
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 0.3rem;
}

.steam-id-status {
  display: inline-block;
  margin-top: 0.3rem;
  font-size: 0.75rem;
  color: #4ade80;
}
</style>
