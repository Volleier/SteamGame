<template>
    <div class="sidebar">

        <nav class="sidebar-nav">
            <div class="nav-item" :class="{ 'active': currentView === 'dashboard' }"
                @click="$emit('change-view', 'dashboard')">
                <i class="icon dashboard-icon"></i>
                <span class="text">主界面</span>
            </div>

            <div class="nav-item" :class="{ 'active': currentView === 'games' }" @click="$emit('change-view', 'games')">
                <i class="icon games-icon"></i>
                <span class="text">游戏列表</span>
            </div>

            <div class="nav-item" :class="{ 'active': currentView === 'game-details' }"
                @click="$emit('change-view', 'game-details')">
                <i class="icon details-icon"></i>
                <span class="text">游戏详情</span>
            </div>
        </nav>

        <div class="sidebar-footer">
            <button class="logout-button" @click="logout">
                <i class="icon logout-icon"></i>
                <span class="text">退出登录</span>
            </button>
        </div>
    </div>
</template>

<script>
export default {
    name: 'TheSidebar',
    props: {
        currentView: {
            type: String,
            required: true
        }
    },
    methods: {
        logout() {
            // 处理退出登录逻辑
            this.$store.dispatch('logout');
            this.$router.push('/login');
        }
    }
}
</script>

<style scoped>
.sidebar {
    display: flex;
    flex-direction: column;
    width: 240px;
    height: 100%;
    background-color: #00ffd5;
    color: #ffffff;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
    position: fixed;
    left: 0;
    top: 0;
    z-index: 10;
}

.sidebar-header {
    padding: 1.5rem;
    border-bottom: 1px solid #2c2c2c;
}

.sidebar-header h3 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: 600;
}

.sidebar-nav {
    flex: 1;
    padding: 1rem 0;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.nav-item {
    display: flex;
    align-items: center;
    padding: 0.8rem 1.5rem;
    color: #b8b8b8;
    text-decoration: none;
    transition: all 0.3s ease;
}

.nav-item:hover {
    background-color: #ffffff;
    color: #ffffff;
}

.nav-item.active {
    background-color: #00b7ff;
    color: #ffffff;
}

.icon {
    width: 20px;
    height: 20px;
    margin-right: 10px;
    background-position: center;
    background-repeat: no-repeat;
    background-size: contain;
}

.dashboard-icon {
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/></svg>');
}

.games-icon {
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M21 6H3c-1.1 0-2 .9-2 2v8c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm-10 7H8v3H6v-3H3v-2h3V8h2v3h3v2zm4.5 2c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm4-3c-.83 0-1.5-.67-1.5-1.5S18.67 9 19.5 9s1.5.67 1.5 1.5-.67 1.5-1.5 1.5z"/></svg>');
}

.details-icon {
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm-1 7V3.5L18.5 9H13z"/></svg>');
}

.sidebar-footer {
    padding: 1rem 1.5rem;
    border-top: 1px solid #2c2c2c;
}

.logout-button {
    display: flex;
    align-items: center;
    width: 100%;
    padding: 0.8rem;
    background-color: transparent;
    border: none;
    border-radius: 4px;
    color: #b8b8b8;
    cursor: pointer;
    transition: all 0.3s ease;
}

.logout-button:hover {
    background-color: rgba(255, 255, 255, 0.1);
    color: #ffffff;
}

.logout-icon {
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/></svg>');
}

.text {
    font-size: 1rem;
    font-weight: 500;
    color: #000000;
}
</style>