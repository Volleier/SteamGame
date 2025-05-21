<template>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <h1>登录</h1>
                <p>使用你的Steam API来获取你的游戏信息</p>
            </div>

            <div class="temp-login">
                <button @click="handleTempLogin" class="temp-login-button">
                    临时登录
                </button>
            </div>

            <form class="login-form" @submit.prevent="handleLogin">
                <div class="form-group">
                    <label for="Steam_Id" class="form-label">Steam_Id</label>
                    <input id="Steam_Id" type="text" v-model="Steam_Id" required class="form-input"
                        placeholder="请输入你的 Steam ID" />
                </div>

                <div class="form-group">
                    <label for="Api_Key" class="form-label">Api_Key</label>
                    <input id="Api_Key" type="text" v-model="Api_Key" required class="form-input"
                        placeholder="请输入你的 Steam API" />
                </div>

                <div class="checkbox-container">
                    <div class="remember-me">
                        <input id="remember-me" type="checkbox" v-model="rememberMe">
                        <label for="remember-me">记住我</label>
                    </div>
                </div>

                <div>
                    <button type="submit" class="login-button">
                        登录
                    </button>
                </div>
                <div v-if="errorMessage" class="error-message">
                    {{ errorMessage }}
                </div>
            </form>
        </div>
    </div>
</template>

<script>
import loginScript from '@/script/login.ts';
export default loginScript;
</script>

<style scoped lang="scss">
@import url('@/assets/styles/login.scss');

/* 为临时登录按钮添加样式 */
.temp-login-button {
    background-color: #4CAF50;
    color: white;
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: 500;
    transition: background-color 0.15s ease;

    &:hover {
        background-color: #45a049;
    }
}
</style>