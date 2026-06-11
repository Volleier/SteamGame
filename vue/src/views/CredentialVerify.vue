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
            <form class="credential-form" @submit.prevent="handleVerify">
              <div class="form-group">
                <input id="Steam_Id" type="text" v-model="Steam_Id" disabled readonly class="form-input readonly-input" placeholder="暂无 Steam ID 数据" />
              </div>
              <div class="button-container flex flex-col items-center gap-4 w-full">
                <button type="submit" class="credential-btn" :disabled="isLoading">
                  <div class="credential-slider"></div>
                  <div class="credential-text-container">
                    <span v-if="isLoading" class="loading-spinner"></span>
                    <span>{{ isLoading ? '验证中...' : '验证凭据' }}</span>
                  </div>
                </button>

                <button type="button" class="credential-btn" @click="handleConfigure">
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

<script>
import CyberGlassCard from '@/components/CyberGlassCard.vue';
import credentialVerifyScript from '@/script/credentialVerify.ts';
export default {
  components: {
    CyberGlassCard,
  },
  ...credentialVerifyScript,
  methods: {
    ...credentialVerifyScript.methods,
    handleConfigure() {
      this.$router.push({ name: 'credential-config' });
    },
  },
};
</script>

<style scoped lang="scss">
@use '@/assets/styles/default' as *;
@use '@/assets/styles/login' as *;
</style>
