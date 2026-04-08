<template>
  <!-- ...existing code... -->

  <!-- 凭据配置模态框部分 -->
  <div class="credential-config-modal" v-if="showRegisterModal">
    <div class="credential-config-modal-content">
      <h2>凭据配置</h2>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="configSteamId">Steam ID</label>
          <input type="text" id="configSteamId" v-model="configSteamId" required :disabled="isConfigLoading" />
        </div>

        <div class="form-group">
          <label for="configApiKey">API Key</label>
          <input type="password" id="configApiKey" v-model="configApiKey" required :disabled="isConfigLoading" />
        </div>

        <div class="form-group">
          <label>
            <input type="checkbox" v-model="registerRememberMe" :disabled="isConfigLoading" />
            记住我
          </label>
        </div>

        <div class="form-actions">
          <button type="button" @click="closeRegisterModal" :disabled="isConfigLoading">取消</button>
          <button type="submit" class="primary-button" :disabled="isConfigLoading">
            <span class="loading-spinner" v-if="isConfigLoading"></span>
            <span v-if="isConfigLoading">验证中...</span>
            <span v-else>配置并验证</span>
          </button>
        </div>
      </form>
    </div>
  </div>

  <!-- ...existing code... -->
</template>

<script src="../script/credentialVerify.ts"></script>
<style lang="scss" src="../styles/login.scss" scoped></style>
