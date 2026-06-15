import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import Default from '@/views/Default.vue';
import store from '@/store';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'default',
    component: Default,
  },
  {
    path: '/credential-verify',
    name: 'credential-verify',
    component: () => import('@/views/CredentialVerify.vue'),
  },
  {
    path: '/credential-config',
    name: 'credential-config',
    component: () => import('@/views/CredentialConfig.vue'),
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import('@/views/Dashboard.vue'),
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('@/views/About.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.path === '/dashboard') {
    // 优先检查 store 中的认证状态，其次检查 localStorage
    const storeAuthenticated = store.state.authenticated;
    const storeSteamId = store.state.steamId;
    const localSteamId = localStorage.getItem('steamId');
    const hasCredential = storeAuthenticated || !!storeSteamId || !!localSteamId;

    // 同步 localStorage → store（单向补偿）
    if (!storeSteamId && localSteamId) {
      store.commit('setSteamId', localSteamId);
    }

    if (false) {
      // 未配置凭据，重定向到凭据验证页
      return next({ path: '/credential-verify' });
    }
  }
  return next();
});

export default router;
