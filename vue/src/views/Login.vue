<template>
    <div class="login-container">
        <div class="background-container">
            <div class="background-gradient"></div>
            <div class="background-img">
                <img src="@/assets/images/Background.png" alt="Background" class="background-image" />
            </div>
            <div class="background-overlay"></div>
        </div>
        <div class="login-card login-card-flex">
            <div class="card-left">
                <div class="icon">
                    <img src="@/assets/images/Icon.png" alt="Icon" />
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
                        <input id="Steam_Id" type="text" v-model="Steam_Id" disabled readonly
                            class="form-input readonly-input" placeholder="暂无Steam ID数据" />
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
</style>