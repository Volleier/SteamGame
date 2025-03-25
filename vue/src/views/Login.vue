<script setup>
import { username, password, handleLogin } from '../logic/loginLogic.js';
import { useRouter } from 'vue-router';

const router = useRouter();

const handleSubmit = () => {
    handleLogin(router); // 传入 router
};
</script>

<template>
    <div class="login-container">
        <h2>LOGIN</h2>
        <form @submit.prevent="handleSubmit"> <!-- 调用 handleSubmit -->
            <div>
                <label for="username">Steam User ID:</label>
                <input type="text" id="username" v-model="username" required />
            </div>
            <div>
                <label for="password">Steam Api Key:</label>
                <input type="password" id="password" v-model="password" required />
            </div>
            <button type="submit">ENTER</button>
        </form>
    </div>
</template>