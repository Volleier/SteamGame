<template>
  <!-- ...existing code... -->
  
  <!-- 注册模态框部分 -->
  <div class="register-modal" v-if="showRegisterModal">
    <div class="register-modal-content">
      <h2>注册新账户</h2>
      
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="registerSteamId">Steam ID</label>
          <input 
            type="text" 
            id="registerSteamId" 
            v-model="registerSteamId" 
            required 
            :disabled="isRegisterLoading"
          />
        </div>
        
        <div class="form-group">
          <label for="registerApiKey">API Key</label>
          <input 
            type="password" 
            id="registerApiKey" 
            v-model="registerApiKey" 
            required 
            :disabled="isRegisterLoading"
          />
        </div>
        
        <div class="form-group">
          <label>
            <input type="checkbox" v-model="registerRememberMe" :disabled="isRegisterLoading" />
            记住我
          </label>
        </div>
        
        <div class="form-actions">
          <button type="button" @click="closeRegisterModal" :disabled="isRegisterLoading">取消</button>
          <button type="submit" class="primary-button" :disabled="isRegisterLoading">
            <span class="loading-spinner" v-if="isRegisterLoading"></span>
            <span v-if="isRegisterLoading">验证中...</span>
            <span v-else>注册</span>
          </button>
        </div>
      </form>
    </div>
  </div>
  
  <!-- ...existing code... -->
</template>

<script src="../script/login.ts"></script>
<style lang="scss" src="../styles/login.scss" scoped></style>