import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import Default from '@/views/Default.vue';
import store from '@/store';
import { hasCredential, syncSessionFromStorage } from './guards/auth';

const routes: Array<RouteRecordRaw> = [
  { path: '/', name: 'default', component: Default },
  { path: '/credential-verify', name: 'credential-verify', component: () => import('@/views/CredentialVerify.vue') },
  { path: '/credential-config', name: 'credential-config', component: () => import('@/views/CredentialConfig.vue') },
  { path: '/dashboard', name: 'dashboard', component: () => import('@/views/Dashboard.vue') },
  { path: '/about', name: 'about', component: () => import('@/views/About.vue') },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, _from, next) => {
  if (to.path === '/dashboard') {
    const storeSteamId = store.state.steamId;
    const localSteamId = syncSessionFromStorage();

    // Sync localStorage → store (one-way compensation)
    if (!storeSteamId && localSteamId) {
      store.commit('setSteamId', localSteamId);
    }
  }
  return next();
});

export default router;
