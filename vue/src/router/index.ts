import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import Default from '@/views/Default.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'default',
    component: Default,
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue'),
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/Register.vue'),
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
