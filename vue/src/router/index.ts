import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import Default from '@/views/Default.vue';

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
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
