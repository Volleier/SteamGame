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
import axios from 'axios';

// 扩展原有的登录脚本
export default {
    ...loginScript,
    data() {
        return {
            ...loginScript.data(),
            // 新增注册相关数据
            showRegisterModal: false,
            registerSteamId: '',
            registerApiKey: '',
            registerRememberMe: false
        }
    },
    methods: {
        ...loginScript.methods,
        // 新增注册相关方法
        openRegisterModal() {
            this.showRegisterModal = true;
        },
        closeRegisterModal() {
            this.showRegisterModal = false;
            // 重置表单数据
            this.registerSteamId = '';
            this.registerApiKey = '';
            this.registerRememberMe = false;
        },
        async handleRegister() {
            try {
                // 准备发送到后端的数据
                const registerData = {
                    steamId: this.registerSteamId,
                    apiKey: this.registerApiKey,
                    rememberMe: this.registerRememberMe
                };

                console.log('准备发送注册信息:', registerData);

                // 发送请求到后端API
                const response = await axios.post('/api/register', registerData);

                console.log('注册成功:', response.data);

                // 检查响应格式并处理数据
                if (typeof response.data === 'object' && response.data.steamId && response.data.apiKey) {
                    // 后端返回了完整的用户数据对象
                    const { steamId, apiKey } = response.data;
                    this.syncLoginData(steamId, apiKey);
                } else {
                    // 后端只返回了成功消息，使用表单中的数据
                    this.syncLoginData(this.registerSteamId, this.registerApiKey);
                }

                // 关闭弹窗
                this.closeRegisterModal();

                // 提示注册成功
                this.$emit('register-success', '注册成功！');
            } catch (error) {
                console.error('注册失败:', error);
                // 处理错误情况
                this.$emit('register-error', error.response?.data?.message || '注册失败，请稍后再试');
            }
        },

        // 新增钩子函数：同步数据到登录表单
        syncLoginData(steamId, apiKey) {
            if (!steamId || !apiKey) {
                console.warn('同步登录数据失败：缺少steamId或apiKey');
                return;
            }

            console.log('正在同步登录数据:', { steamId, apiKeyLength: apiKey?.length });

            // 更新登录表单数据
            this.Steam_Id = steamId;
            this.Api_Key = apiKey;

            // 如果选择了记住我，保存到本地存储
            if (this.registerRememberMe) {
                localStorage.setItem('steamId', steamId);
                localStorage.setItem('apiKey', apiKey);
                localStorage.setItem('rememberMe', 'true');

                console.log('已将登录凭证保存到本地存储');
            }

            // 取消登录表单的禁用状态
            this.$nextTick(() => {
                const steamIdInput = document.getElementById('Steam_Id');
                const apiKeyInput = document.getElementById('Api_Key');

                if (steamIdInput && apiKeyInput) {
                    // 更新以反映真实状态
                    steamIdInput.disabled = false;
                    apiKeyInput.disabled = false;

                    console.log('已启用登录表单输入框');
                }
            });

            // 可以选择自动登录
            // this.handleLogin();
        }
    }
};
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

/* 按钮容器样式 */
.button-container {
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 100%;
}

/* 注册按钮样式 */
.register-button {
    background-color: #2196F3;
    color: white;
    padding: 0.75rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: 500;
    transition: background-color 0.15s ease;

    &:hover {
        background-color: #0b7dda;
    }
}

/* 模态窗口样式 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 10;
}

.modal-content {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 90%;
    max-width: 400px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);

    h2 {
        margin-top: 0;
        margin-bottom: 20px;
        text-align: center;
    }
}

.modal-buttons {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;

    button {
        padding: 0.5rem 1rem;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-weight: 500;
    }

    .cancel-button {
        background-color: #f44336;
        color: white;

        &:hover {
            background-color: #d32f2f;
        }
    }

    .submit-button {
        background-color: #4CAF50;
        color: white;

        &:hover {
            background-color: #45a049;
        }
    }
}

/* 模态窗口过渡动画 */
.modal-slide-enter-active,
.modal-slide-leave-active {
    transition: all 0.3s ease;
}

.modal-slide-enter-from {
    opacity: 0;

    .modal-content {
        transform: translateY(-50vh);
    }
}

.modal-slide-leave-to {
    opacity: 0;

    .modal-content {
        transform: translateY(-50vh);
    }
}

/* 调整模态内容的过渡效果 */
.modal-content {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 90%;
    max-width: 400px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    transition: transform 0.3s ease;

    h2 {
        margin-top: 0;
        margin-bottom: 20px;
        text-align: center;
    }
}
</style>