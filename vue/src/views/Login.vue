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
            </form>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: 'LoginPage',
    data() {
        return {
            Steam_Id: '',
            Api_Key: '',
            rememberMe: false,
            isLoading: false,
            errorMessage: ''
        }
    },
    methods: {
        // 添加临时登录方法
        handleTempLogin() {
            // 创建临时用户数据
            const tempUser = {
                id: 'temp-user-123',
                name: '演示用户',
                steamId: 'DEMO_STEAM_ID'
            };

            // 创建临时token
            const tempToken = 'demo-token-' + Date.now();

            // 存储认证信息
            localStorage.setItem('token', tempToken);
            localStorage.setItem('user', JSON.stringify(tempUser));

            // 如果使用了Vuex，更新状态
            if (this.$store && typeof this.$store.commit === 'function') {
                this.$store.commit('setUser', tempUser);
                this.$store.commit('setAuthenticated', true);
            }

            // 跳转到仪表盘
            this.$router.push('/dashboard');
        },

        async handleLogin() {
            this.isLoading = true;
            this.errorMessage = '';

            // 添加调试信息 - 打印请求信息
            // 替换 process.env 为 import.meta.env
            const apiUrl = import.meta.env.VUE_APP_API_URL || 'http://localhost:8080';
            console.log(`发送至：${apiUrl}，发送数据：`, {
                steamId: this.Steam_Id,
                apiKey: this.Api_Key
            });

            try {
                // 调用后端API进行验证
                const response = await axios.post(`${apiUrl}/test`, {
                    steamId: this.Steam_Id,
                    apiKey: this.Api_Key
                });

                // 添加调试信息 - 打印响应数据
                console.log('接收到响应：', response.data);

                // 处理登录成功
                const { token, user } = response.data;

                // 存储认证信息
                localStorage.setItem('token', token);
                if (this.rememberMe) {
                    localStorage.setItem('user', JSON.stringify(user));
                }

                // 更新全局状态
                this.$store.commit('setUser', user);
                this.$store.commit('setAuthenticated', true);

                // 跳转到仪表盘
                this.$router.push('/dashboard');

            } catch (error) {
                // 处理登录失败
                console.error('登录失败:', error);
                console.error('错误详情:', {
                    message: error.message,
                    response: error.response?.data,
                    status: error.response?.status
                });
                this.errorMessage = error.response?.data?.message || '登录失败，请检查您的凭据';
            } finally {
                this.isLoading = false;
            }
        }
    }
}
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