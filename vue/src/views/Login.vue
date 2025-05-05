<template>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <h1>登录</h1>
                <p>使用你的Steam API来获取你的游戏信息</p>
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
        async handleLogin() {
            this.isLoading = true;
            this.errorMessage = '';

            try {
                // 调用后端API进行验证 - 注意这里使用了正确的本地后端地址和端口
                const response = await axios.post(`${process.env.VUE_APP_API_URL}/api/auth/login`, {
                    steamId: this.Steam_Id,
                    apiKey: this.Api_Key
                });

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
</style>