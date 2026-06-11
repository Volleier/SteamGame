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
              <h1>凭据配置</h1>
            </div>

            <form class="reg-form" @submit.prevent="handleConfigure">
              <div class="reg-form-group">
                <label for="config-steam-id" class="reg-label">Steam ID</label>
                <input id="config-steam-id" type="text" v-model="configSteamId" class="reg-input" placeholder="请输入你的 Steam ID" required :disabled="isConfigLoading" />
              </div>

              <div class="reg-form-group">
                <label for="config-api-key" class="reg-label">API Key</label>
                <div class="reg-password-wrap">
                  <input id="config-api-key" :type="showApiKey ? 'text' : 'password'" v-model="configApiKey" class="reg-input" placeholder="请输入你的 Steam API Key" required :disabled="isConfigLoading" autocomplete="off" />
                  <button type="button" class="reg-password-toggle" @click="showApiKey = !showApiKey" :aria-label="showApiKey ? '隐藏 API Key' : '显示 API Key'" tabindex="-1">
                    <span v-if="showApiKey">🙈</span>
                    <span v-else>👁</span>
                  </button>
                </div>
              </div>

              <div class="reg-form-group reg-checkbox-group">
                <label class="reg-checkbox-label">
                  <input type="checkbox" v-model="rememberMe" class="reg-checkbox" />
                  <span>记住我（下次自动填充已保存的 Steam ID）</span>
                </label>
              </div>

              <div class="reg-btn-wrap">
                <button v-if="!configured" type="submit" class="credential-btn" :disabled="isConfigLoading">
                  <div class="credential-slider"></div>
                  <div class="credential-text-container">
                    <span v-if="isConfigLoading" class="reg-spinner"></span>
                    <span>{{ isConfigLoading ? '验证中...' : '保存并验证配置' }}</span>
                  </div>
                </button>

                <button v-else type="button" class="credential-btn" @click="handleReturnToVerify">
                  <div class="credential-slider"></div>
                  <div class="credential-text-container">
                    <span>返回凭据验证</span>
                  </div>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </transition>

    <div v-if="configError" class="reg-modal-overlay" @click.self="clearConfigError">
      <div class="reg-modal-content" role="dialog" aria-modal="true">
        <div class="reg-modal-body">
          <p>{{ configError }}</p>
        </div>
        <div class="reg-modal-buttons">
          <button class="reg-modal-confirm" @click="clearConfigError">确定</button>
        </div>
      </div>
    </div>
  </CyberGlassCard>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import CyberGlassCard from '@/components/CyberGlassCard.vue';
import { useCredentialConfig } from '@/composables/useCredentialConfig';

const { configSteamId, configApiKey, rememberMe, isConfigLoading, configError, configured, handleConfigure, handleReturnToVerify, clearConfigError } = useCredentialConfig();

const showApiKey = ref(false);
</script>

<style scoped lang="scss">
@use '@/assets/styles/components/default' as *;
@use '@/assets/styles/pages/credential-config' as *;

.reg-password-wrap {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.reg-password-toggle {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
  padding: 0.25rem;
  line-height: 1;
  opacity: 0.6;
  transition: opacity 0.2s;

  &:hover {
    opacity: 1;
  }
}

.reg-checkbox-group {
  margin-top: 0.25rem;
}

.reg-checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  user-select: none;

  .reg-checkbox {
    width: 1rem;
    height: 1rem;
    accent-color: #00d4ff;
    cursor: pointer;
  }
}
</style>
