import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import Default from '@/views/Default.vue';
// 简单路由守卫：未配置凭据时禁止进入 /dashboard

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
    const hasSteamId = !!localStorage.getItem('steamId');
    if (!hasSteamId) {
      // 未配置凭据，重定向到凭据验证页引导用户先配置/验证
      return next({ path: '/credential-verify' });
    }
  }
  return next();
});

export default router;
