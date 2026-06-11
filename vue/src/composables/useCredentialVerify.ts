/**
 * 凭据验证逻辑 — Composition API
 */

import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { getCredentialStatus, verifyCredential, configureCredential } from '@/api/credentials';
import { getCredentialMessage } from '@/services/credentialMessages';

export function useCredentialVerify() {
  const steamId = ref('');
  const isLoading = ref(false);
  const errorMessage = ref('');

  const router = useRouter();
  const store = useStore();

  async function loadCredentialStatus() {
    try {
      const status = await getCredentialStatus();
      steamId.value = status.steamId;
      if (status.steamId) {
        store.commit('setSteamId', status.steamId);
      }
    } catch (error) {
      console.error('加载凭据状态失败:', error);
    }
  }

  async function handleVerify() {
    isLoading.value = true;
    errorMessage.value = '';

    try {
      if (import.meta.env.DEV) {
        console.log('发送 POST /api/credentials/verify');
      }

      const resp = await verifyCredential();

      if (resp.success) {
        store.commit('setAuthenticated', true);
        router.push('/dashboard');
      } else {
        const code = resp.code;
        errorMessage.value = resp.message || getCredentialMessage(code, `验证失败（code=${code})`);
      }
    } catch (err) {
      const e = err as any;
      console.error('凭据验证失败:', e);
      const status = e?.response?.status;
      const respData = e?.response?.data;
      if (!e?.response) {
        errorMessage.value = '网络错误或服务器无响应（请检查网络或 CORS）';
      } else if (respData && (respData.message || respData.error || respData.msg)) {
        errorMessage.value = respData.message || respData.error || respData.msg;
      } else {
        errorMessage.value = `验证失败（HTTP ${status}）`;
      }
    } finally {
      isLoading.value = false;
    }
  }

  function goToConfig() {
    router.push('/credential-config');
  }

  onMounted(() => {
    loadCredentialStatus();
  });

  return {
    steamId,
    isLoading,
    errorMessage,
    loadCredentialStatus,
    handleVerify,
    goToConfig,
  };
}
