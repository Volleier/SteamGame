<template>
    <CyberGlassCard compact>
        <transition name="card-fade" appear>
            <div class="login-card login-card-flex">
                <div class="card-left">
                    <div class="icon">
                        <img src="@/assets/images/SteamGame_Icon.png" alt="Icon" />
                    </div>
                </div>
                <div class="card-right">
                    <!-- 高模糊度黑色毛玻璃背景层 -->
                    <div class="card-right-glass"></div>

                    <!-- 内容层，需在玻璃层之上 -->
                    <div class="card-right-content">
                        <div class="login-header">
                            <h1>登录</h1>
                            <p>使用你的Steam API来获取你的游戏信息</p>
                        </div>
                        <form class="login-form" @submit.prevent="handleLogin">
                            <div class="form-group">
                                <input id="Steam_Id" type="text" v-model="Steam_Id" disabled readonly
                                    class="form-input readonly-input" placeholder="暂无Steam ID数据" />
                            </div>
                            <div class="button-container flex flex-col items-center gap-4 w-full">
                                <!-- Confirm Login Button -->
                                <button type="submit" class="login-btn" :disabled="isLoading">
                                    <div class="login-slider"></div>
                                    <div class="login-text-container">
                                        <span v-if="isLoading" class="loading-spinner"></span>
                                        <span>{{ isLoading ? '登录中...' : '确认登录' }}</span>
                                    </div>
                                </button>

                                <!-- Register Button -->
                                <button type="button" class="login-btn" @click="handleRegister">
                                    <div class="login-slider"></div>
                                    <div class="login-text-container">
                                        <span>注册</span>
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
import loginScript from '@/script/login.ts';
export default {
    components: {
        CyberGlassCard
    },
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
@import '@/assets/styles/default.scss';
@import url('@/assets/styles/login.scss');
</style>