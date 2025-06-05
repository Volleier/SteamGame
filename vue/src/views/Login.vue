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
                    <input id="Steam_Id" type="text" v-model="Steam_Id" disabled readonly
                        class="form-input readonly-input" placeholder="请输入你的 Steam ID" />
                </div>

                <div class="form-group">
                    <label for="Api_Key" class="form-label">Api_Key</label>
                    <input id="Api_Key" type="text" v-model="Api_Key" disabled required
                        class="form-input readonly-input" placeholder="请输入你的 Steam API" />
                </div>

                <div class="button-container">
                    <button type="button" @click="openRegisterModal" class="register-button">
                        注册
                    </button>
                    <button type="submit" class="login-button" :disabled="isLoading">
                        <span v-if="isLoading" class="loading-spinner"></span>
                        <span>{{ isLoading ? '登录中...' : '登录' }}</span>
                    </button>
                </div>
                <div v-if="errorMessage" class="error-message">
                    {{ errorMessage }}
                </div>
            </form>
        </div>
    </div>

    <!-- 注册弹窗 -->
    <transition name="modal-slide">
        <div v-if="showRegisterModal" class="modal-overlay" @click.self="closeRegisterModal">
            <div class="modal-content">
                <h2>注册</h2>
                <form @submit.prevent="handleRegister">
                    <div class="form-group">
                        <label for="register-steam-id" class="form-label">Steam ID</label>
                        <input id="register-steam-id" type="text" v-model="registerSteamId" class="form-input"
                            placeholder="请输入你的 Steam ID" required :disabled="isRegisterLoading" />
                    </div>

                    <div class="form-group">
                        <label for="register-api-key" class="form-label">API Key</label>
                        <input id="register-api-key" type="text" v-model="registerApiKey" class="form-input"
                            placeholder="请输入你的 Steam API Key" required :disabled="isRegisterLoading" />
                    </div>

                    <div class="checkbox-container">
                        <div class="remember-me">
                            <input id="register-remember-me" type="checkbox" v-model="registerRememberMe"
                                :disabled="isRegisterLoading">
                            <label for="register-remember-me">记住我</label>
                        </div>
                    </div>

                    <!-- 添加注册错误信息显示 -->
                    <div v-if="registerError" class="error-message">
                        {{ registerError }}
                    </div>

                    <div class="modal-buttons">
                        <button type="button" @click="closeRegisterModal" class="cancel-button"
                            :disabled="isRegisterLoading">取消</button>
                        <button type="submit" class="submit-button" :disabled="isRegisterLoading">
                            <span v-if="isRegisterLoading" class="loading-spinner"></span>
                            <span>{{ isRegisterLoading ? '验证中...' : '提交' }}</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </transition>

    <!-- 注册成功提示 -->
    <transition name="toast">
        <div v-if="showRegisterSuccess" class="toast-message success">
            {{ registerSuccessMessage }}
        </div>
    </transition>
</template>

<script>
import loginScript from '@/script/login.ts';
export default loginScript;
</script>

<style scoped lang="scss">
@import url('@/assets/styles/login.scss');

/* 加载指示器样式 - 添加到组件内部确保可用 */
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

/* 提交按钮在加载状态时的样式 */
.submit-button {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 120px;
    position: relative;

    &:disabled {
        opacity: 0.7;
        cursor: wait;
    }
}

/* 成功消息提示样式 */
.toast-message {
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 12px 20px;
    border-radius: 4px;
    z-index: 2000;
    box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16);

    &.success {
        background-color: #4caf50;
        color: white;
    }
}

.toast-enter-active,
.toast-leave-active {
    transition: all 0.3s ease;
}

.toast-enter,
.toast-leave-to {
    opacity: 0;
    transform: translateY(-20px);
}
</style>