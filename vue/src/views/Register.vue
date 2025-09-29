<template>
    <div class="register-container">
        <div class="background-container">
            <div class="background-gradient"></div>
            <div class="background-img">
                <img src="@/assets/images/Background.png" alt="Background" class="background-image" />
            </div>
            <div class="background-overlay"></div>
        </div>
        <transition name="card-fade" appear>
            <div class="register-card login-card-flex">
                <div class="card-left">
                    <div class="icon">
                        <img src="@/assets/images/Icon.png" alt="Icon" />
                    </div>
                </div>
                <div class="card-divider"></div>
                <div class="card-right">
                    <div class="login-header">
                        <h1>注册</h1>
                    </div>

                    <form class="login-form" @submit.prevent="handleRegister">
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
                        </div>

                        <div class="button-container">
                            <button v-if="!registered" type="submit" class="login-button" :disabled="isRegisterLoading">
                                <span v-if="isRegisterLoading" class="loading-spinner"></span>
                                <span>{{ isRegisterLoading ? '验证中...' : '提交' }}</span>
                            </button>

                            <button v-else type="button" class="login-button" @click="handleReturnToLogin">
                                返回登录
                            </button>
                        </div>
                    </form>

                    <!-- 错误模态：当 registerError 有值时显示 -->
                    <div v-if="registerError" class="modal-overlay" @click.self="clearRegisterError">
                        <div class="modal-content" role="dialog" aria-modal="true">
                            <div class="modal-body">
                                <p>{{ registerError }}</p>
                            </div>
                            <div class="modal-buttons">
                                <button class="submit-button" @click="clearRegisterError">确定</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </transition>
    </div>
</template>

<script setup lang="ts">
import { useRegister } from '@/script/register';

const {
    registerSteamId,
    registerApiKey,
    registerRememberMe,
    isRegisterLoading,
    registerError,
    registered,
    handleRegister,
    handleReturnToLogin,
    clearRegisterError,
} = useRegister();

</script>

<style scoped lang="scss">
@import url('@/assets/styles/register.scss');
</style>