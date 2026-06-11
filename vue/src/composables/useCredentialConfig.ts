/**
 * 凭据配置逻辑 — Composition API
 */

import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { configureCredential } from '@/api/credentials';
import { getCredentialMessage } from '@/services/credentialMessages';

export function useCredentialConfig() {
  const configSteamId = ref('');
  const configApiKey = ref('');
  const rememberMe = ref(false);
  const isConfigLoading = ref(false);
  const configError = ref('');
  const configured = ref(false);

  const router = useRouter();
  const store = useStore();

  const handleConfigure = async (): Promise<void> => {
    try {
      configError.value = '';
      isConfigLoading.value = true;

      const payload = {
        steamId: configSteamId.value,
        apiKey: configApiKey.value,
        rememberMe: rememberMe.value,
      };

      if (import.meta.env.DEV) {
        console.log('准备发送凭据配置请求:', payload);
      }

      const resp = await configureCredential(payload);

      if (import.meta.env.DEV) {
        console.log('配置保存并验证响应:', resp);
      }

      if (resp.success) {
        configured.value = true;
        store.commit('setAuthenticated', true);
        if (payload.steamId) {
          localStorage.setItem('steamId', payload.steamId);
          store.commit('setSteamId', payload.steamId);
        }
        setTimeout(() => {
          router.push('/credential-verify');
          setTimeout(() => {
            configured.value = false;
          }, 1000);
        }, 900);
      } else {
        const msg = resp.message || getCredentialMessage(resp.code, '验证失败，请检查您的凭据');
        configError.value = msg;
      }
    } catch (error) {
      console.error('凭据配置失败:', error);
      configError.value = (error as any).response?.data?.message || '配置失败，请稍后再试';
    } finally {
      isConfigLoading.value = false;
    }
  };

  const handleReturnToVerify = (): void => {
    router.push('/credential-verify');
  };

  const clearConfigError = (): void => {
    configError.value = '';
  };

  return {
    configSteamId,
    configApiKey,
    rememberMe,
    isConfigLoading,
    configError,
    configured,
    handleConfigure,
    handleReturnToVerify,
    clearConfigError,
  };
}
