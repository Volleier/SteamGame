<template>
    <header class="header">
        <div class="logo-container">
            <img src="@/assets/images/steam-logger-logo.png" alt="STEAM LOGGER" class="logo" />
        </div>
        <div class="user-container">
            <span class="username">{{ username }}</span>
            <div class="user-avatar">
                <span class="avatar-text">{{ usernameInitial }}</span>
            </div>
        </div>
    </header>
</template>

<script>
export default {
    name: 'TheHeader',
    props: {
        username: {
            type: String,
            default: 'User'
        }
    },
    computed: {
        usernameInitial() {
            return this.username.charAt(0).toUpperCase();
        }
    }
}
</script>

<style scoped>
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 2rem;
    background: linear-gradient(135deg, #00ffd5, #00d5ff);
    color: white;
    z-index: 20;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    width: 100%;

}

.logo-container {
    display: flex;
    align-items: center;
}

.logo {
    height: 40px;
    max-width: 100%;
}

.user-container {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.username {
    font-size: 1rem;
    font-weight: 500;
}

.user-avatar {
    width: 36px;
    height: 36px;
    background-color: #4a5568;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.avatar-text {
    font-size: 1.2rem;
    font-weight: bold;
    color: white;
}
</style>