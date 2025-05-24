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

    <!-- 注册弹窗 -->
    <transition name="modal-slide">
        <div v-if="showRegisterModal" class="modal-overlay" @click.self="closeRegisterModal">
            <div class="modal-content">
                <h2>注册</h2>
                <form @submit.prevent="handleRegister">
                    <div class="form-group">
                        <label for="register-steam-id" class="form-label">Steam ID</label>
                        <input id="register-steam-id" type="text" v-model="registerSteamId" class="form-input"
                            placeholder="请输入你的 Steam ID" required />
                    </div>

                    <div class="form-group">
                        <label for="register-api-key" class="form-label">API Key</label>
                        <input id="register-api-key" type="text" v-model="registerApiKey" class="form-input"
                            placeholder="请输入你的 Steam API Key" required />
                    </div>

                    <div class="checkbox-container">
                        <div class="remember-me">
                            <input id="register-remember-me" type="checkbox" v-model="registerRememberMe">
                            <label for="register-remember-me">记住我</label>
                        </div>
                    </div>

                    <div class="modal-buttons">
                        <button type="button" @click="closeRegisterModal" class="cancel-button">取消</button>
                        <button type="submit" class="submit-button">提交</button>
                    </div>
                </form>
            </div>
        </div>
    </transition>
</template>

<script>
import loginScript from '@/script/login.ts';
export default loginScript;

</script>

<style scoped lang="scss">
@import url('@/assets/styles/login.scss');
</style>