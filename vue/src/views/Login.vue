<template>
    <div class="flex items-center justify-center min-h-screen">
        <div class="w-full max-w-md card">
            <div class="text-center">
                <h1>登录</h1>
                <p class="mt-2">使用你的Steam API来获取你的游戏信息</p>
            </div>

            <form class="space-y-6" @submit.prevent="handleLogin">
                <div>
                    <label for="Steam_Id" class="form-label">Steam_Id</label>
                    <input id="Steam_Id" type="text" v-model="Steam_Id" required class="input mt-1"
                        placeholder="请输入你的 Steam ID" />
                </div>

                <div>
                    <label for="Api_Key" class="form-label">Api_Key</label>
                    <input id="Api_Key" type="text" v-model="Api_Key" required class="input mt-1"
                        placeholder="请输入你的 Steam API" />
                </div>

                <div class="flex items-center justify-between">
                    <div class="flex items-center">
                        <input id="remember-me" type="checkbox" v-model="rememberMe" class="checkbox">
                        <label for="remember-me" class="block ml-2 text-sm text-gray-700">记住我</label>
                    </div>
                </div>

                <div>
                    <button type="submit" class="btn-full">
                        登录
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
export default {
    name: 'LoginPage',
    data() {
        return {
            Steam_Id: '',
            Api_Key: '',
            rememberMe: false
        }
    },
    methods: {
        handleLogin() {
            // 实现登录逻辑
            console.log('登录提交', {
                Steam_Id: this.Steam_Id,
                Api_Key: this.Api_Key,
                rememberMe: this.rememberMe
            });

            // 登录成功后导航到 Dashboard
            this.$router.push('/dashboard');
        }
    }
}
</script>