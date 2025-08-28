<template>
    <div class="register-container">
        <div class="register-card">
            <h1>注册</h1>
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
                            :disabled="isRegisterLoading" />
                        <label for="register-remember-me">记住我</label>
                    </div>
                </div>

                <div v-if="registerError" class="error-message">
                    {{ registerError }}
                </div>

                <div class="button-container">
                    <button type="submit" class="submit-button" :disabled="isRegisterLoading">
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

<style scoped>
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f5f5;
}

.register-card {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 400px;
}

.form-group {
    margin-bottom: 16px;
}

.form-label {
    display: block;
    margin-bottom: 8px;
    font-weight: bold;
}

.form-input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

.checkbox-container {
    margin-bottom: 16px;
}

.remember-me {
    display: flex;
    align-items: center;
}

.error-message {
    color: red;
    margin-bottom: 16px;
}

.button-container {
    display: flex;
    justify-content: flex-end;
}

.submit-button {
    background-color: #007bff;
    color: #fff;
    border: none;
    padding: 10px 20px;
    border-radius: 4px;
    cursor: pointer;
}

.submit-button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.toast-message {
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 12px 20px;
    border-radius: 4px;
    z-index: 2000;
    box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16);
}

.toast-message.success {
    background-color: #4caf50;
    color: white;
}
</style>