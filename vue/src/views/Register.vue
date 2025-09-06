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

                        <div v-if="registerError" class="error-message">
                            {{ registerError }}
                        </div>

                        <div class="button-container">
                            <button type="submit" class="login-button" :disabled="isRegisterLoading">
                                <span v-if="isRegisterLoading" class="loading-spinner"></span>
                                <span>{{ isRegisterLoading ? '验证中...' : '提交' }}</span>
                            </button>
                        </div>
                    </form>

                    <transition name="toast">
                        <div v-if="showRegisterSuccess" class="toast-message success">
                            {{ registerSuccessMessage }}
                        </div>
                    </transition>
                </div>
            </div>
        </transition>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            registerSteamId: '',
            registerApiKey: '',
            registerRememberMe: false,
            isRegisterLoading: false,
            registerError: '',
            showRegisterSuccess: false,
            registerSuccessMessage: '注册成功！',
        };
    },
    methods: {
        async handleRegister() {
            try {
                this.registerError = ''; // 清除之前的错误信息
                this.isRegisterLoading = true; // 设置加载状态

                const registerData = {
                    steamId: this.registerSteamId,
                    apiKey: this.registerApiKey,
                    rememberMe: this.registerRememberMe,
                };

                console.log('准备发送注册信息:', registerData);
                const response = await axios.post('/api/register', registerData);
                console.log('注册成功:', response.data);

                // 检查后端返回的验证结果
                if (response.data.status === 1 || response.data === 1) {
                    console.log('验证成功，准备跳转到dashboard');

                    // 关闭模态框并显示成功消息
                    this.showRegisterSuccess = true;
                    this.registerSuccessMessage = '注册验证成功！正在跳转...';

                    // 设置认证状态并重定向到dashboard
                    if (response.data.token) {
                        localStorage.setItem('token', response.data.token);
                    }

                    this.$store.commit('setAuthenticated', true);
                    if (response.data.user) {
                        this.$store.commit('setUser', response.data.user);
                    }

                    // 短暂延迟后跳转，让用户看到成功消息
                    setTimeout(() => {
                        this.$router.push('/dashboard');
                        setTimeout(() => {
                            this.showRegisterSuccess = false;
                        }, 1000);
                    }, 1500);
                } else {
                    // 验证失败
                    this.registerError = response.data.message || '验证失败，请检查您的SteamID和ApiKey';
                }
            } catch (error) {
                console.error('注册失败:', error);
                this.registerError = error.response?.data?.message || '注册失败，请稍后再试';
            } finally {
                this.isRegisterLoading = false; // 无论成功还是失败，最终都要关闭加载状态
            }
        },
    },
};
</script>

<style scoped lang="scss">
@import url('@/assets/styles/register.scss');
</style>