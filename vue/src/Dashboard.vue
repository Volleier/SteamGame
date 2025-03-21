<template>
    <div class="dashboard">
        <header class="header">
            <h1>导航板</h1>
        </header>
        <aside class="sidebar">
            <nav>
                <ul>
                    <li><router-link to="/">主页</router-link></li>
                    <li><router-link to="/games">游戏列表</router-link></li>
                    <li><router-link to="/settings">设置</router-link></li>
                </ul>
            </nav>
        </aside>
        <main class="main-content">
            <router-view></router-view>
        </main>
    </div>
</template>

<script>
export default {
    name: 'Dashboard',
};
</script>

<style scoped>
.dashboard {
    display: flex;
    height: 100vh;
}

.header {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 60px;
    background-color: #333;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.sidebar {
    width: 200px;
    background-color: #f4f4f4;
    padding-top: 60px;
    /* To offset the fixed header */
    position: fixed;
    top: 0;
    left: 0;
    height: 100%;
    overflow-y: auto;
}

.sidebar nav ul {
    list-style-type: none;
    padding: 0;
}

.sidebar nav ul li {
    margin: 0;
    padding: 0;
}

.sidebar nav ul li a {
    display: block;
    padding: 10px 20px;
    color: #333;
    text-decoration: none;
}

.sidebar nav ul li a:hover {
    background-color: #ddd;
}

.main-content {
    margin-left: 200px;
    /* To offset the sidebar */
    padding-top: 60px;
    /* To offset the fixed header */
    padding: 20px;
    width: calc(100% - 200px);
    height: calc(100vh - 60px);
    overflow-y: auto;
}
</style>