<template>
    <CyberGlassCard compact>
        <transition name="card-fade" appear>
            <div class="reg-card reg-card-flex">

                <!-- 左侧图片区 -->
                <div class="reg-left">
                    <div class="reg-icon">
                        <img src="@/assets/images/SteamGame_Icon.png" alt="Icon" />
                    </div>
                </div>

                <!-- 右侧内容区 -->
                <div class="reg-right">
                    <!-- 高模糊度黑色毛玻璃背景层 -->
                    <div class="reg-right-glass"></div>

                    <!-- 内容层，浮在玻璃层之上 -->
                    <div class="reg-right-content">
                        <div class="reg-header">
                            <h1>注册</h1>
                        </div>

                        <form class="reg-form" @submit.prevent="handleRegister">
                            <div class="reg-form-group">
                                <label for="register-steam-id" class="reg-label">Steam ID</label>
                                <input
                                    id="register-steam-id"
                                    type="text"
                                    v-model="registerSteamId"
                                    class="reg-input"
                                    placeholder="请输入你的 Steam ID"
                                    required
                                    :disabled="isRegisterLoading"
                                />
                            </div>

                            <div class="reg-form-group">
                                <label for="register-api-key" class="reg-label">API Key</label>
                                <input
                                    id="register-api-key"
                                    type="text"
                                    v-model="registerApiKey"
                                    class="reg-input"
                                    placeholder="请输入你的 Steam API Key"
                                    required
                                    :disabled="isRegisterLoading"
                                />
                            </div>

                            <!-- 单一操作按钮 -->
                            <div class="reg-btn-wrap">
                                <button v-if="!registered" type="submit" class="login-btn" :disabled="isRegisterLoading">
                                    <div class="login-slider"></div>
                                    <div class="login-text-container">
                                        <span v-if="isRegisterLoading" class="reg-spinner"></span>
                                        <span>{{ isRegisterLoading ? '验证中...' : '提交注册' }}</span>
                                    </div>
                                </button>

                                <button v-else type="button" class="login-btn" @click="handleReturnToLogin">
                                    <div class="login-slider"></div>
                                    <div class="login-text-container">
                                        <span>返回登录</span>
                                    </div>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

            </div>
        </transition>

        <!-- 错误模态 -->
        <div v-if="registerError" class="reg-modal-overlay" @click.self="clearRegisterError">
            <div class="reg-modal-content" role="dialog" aria-modal="true">
                <div class="reg-modal-body">
                    <p>{{ registerError }}</p>
                </div>
                <div class="reg-modal-buttons">
                    <button class="reg-modal-confirm" @click="clearRegisterError">确定</button>
                </div>
            </div>
        </div>
    </CyberGlassCard>
</template>

<script setup lang="ts">
import CyberGlassCard from '@/components/CyberGlassCard.vue';
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
@import '@/assets/styles/default.scss';
@import url('@/assets/styles/register.scss');
</style>