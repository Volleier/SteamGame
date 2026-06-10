<template>
  <CyberGlassCard compact>
    <transition name="card-fade" appear>
      <div class="reg-card reg-card-flex">
        <div class="reg-left">
          <div class="reg-icon">
            <img src="@/assets/images/SteamGame_Icon.png" alt="Icon" />
          </div>
        </div>

        <div class="reg-right">
          <div class="reg-right-glass"></div>

          <div class="reg-right-content">
            <div class="reg-header">
              <h1>凭据配置</h1>
            </div>

            <form class="reg-form" @submit.prevent="handleConfigure">
              <div class="reg-form-group">
                <label for="config-steam-id" class="reg-label">Steam ID</label>
                <input id="config-steam-id" type="text" v-model="configSteamId" class="reg-input" placeholder="请输入你的 Steam ID" required :disabled="isConfigLoading" />
              </div>

              <div class="reg-form-group">
                <label for="config-api-key" class="reg-label">API Key</label>
                <input id="config-api-key" type="text" v-model="configApiKey" class="reg-input" placeholder="请输入你的 Steam API Key" required :disabled="isConfigLoading" />
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
import CyberGlassCard from '@/components/CyberGlassCard.vue';
import { useCredentialConfig } from '@/script/credentialConfig';

const { configSteamId, configApiKey, rememberMe, isConfigLoading, configError, configured, handleConfigure, handleReturnToVerify, clearConfigError } = useCredentialConfig();
</script>

<style scoped lang="scss">
@use '@/assets/styles/default' as *;
@import url('@/assets/styles/register.scss');
</style>
