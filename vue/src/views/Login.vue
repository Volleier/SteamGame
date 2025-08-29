<template>
    <div class="login-container">
        <div class="login-card login-card-flex">
            <div class="card-left">
                <div class="steamGame-icon">
                    <img src="@/assets/images/SteamGame-Icon.png" alt="Steam Game Icon" />
                </div>
            </div>
            <div class="card-divider"></div>
            <div class="card-right">
                <div class="login-header">
                    <h1>登录</h1>
                    <p>使用你的Steam API来获取你的游戏信息</p>
                </div>
                <form class="login-form" @submit.prevent="handleLogin">
                    <div class="form-group">
                        <label for="Steam_Id" class="form-label">Steam_Id</label>
                        <input id="Steam_Id" type="text" v-model="Steam_Id" disabled readonly
                            class="form-input readonly-input" placeholder="请输入你的 Steam ID" />
                    </div>
                    <div class="button-container">
                        <button type="submit" class="login-button" :disabled="isLoading">
                            <span v-if="isLoading" class="loading-spinner"></span>
                            <span>{{ isLoading ? '登录中...' : '确认登录' }}</span>
                        </button>
                        <button type="button" class="register-button" @click="handleRegister">
                            注册
                        </button>
                    </div>
                    <div v-if="errorMessage" class="error-message">
                        {{ errorMessage }}
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script>
import loginScript from '@/script/login.ts';
export default {
    ...loginScript,
    methods: {
        ...loginScript.methods,
        handleRegister() {
            this.$router.push({ name: 'register' });
        },
    },
};
</script>

<style scoped lang="scss">
@import url('@/assets/styles/login.scss');

.loading-spinner {
    display: inline-block;
    width: 16px;
    height: 16px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: #fff;
    animation: spin 1s infinite linear;
    margin-right: 8px;
    vertical-align: middle;
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}
</style>