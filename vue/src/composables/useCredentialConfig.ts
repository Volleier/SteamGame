/**
 * 凭据配置逻辑 — Composition API
 */

import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { configureCredential } from '@/api/credentials';
import { getCredentialMessage } from '@/services/credentialMessages';

// P0-3: 输入格式正则
const STEAM_ID_PATTERN = /^\d{17}$/;
const API_KEY_PATTERN = /^[A-Fa-f0-9]{32}$/;

export function useCredentialConfig() {
  const configSteamId = ref('');
  const configApiKey = ref('');
  const rememberMe = ref(false);
  const isConfigLoading = ref(false);
  const configError = ref('');
  const configured = ref(false);

  const router = useRouter();
  const store = useStore();

  /** P0-3: 前端输入预校验，返回 null 表示通过，否则返回错误信息 */
  function validateInput(): string | null {
    const steamId = configSteamId.value.trim();
    const apiKey = configApiKey.value.trim();

    if (!steamId) return '请输入 Steam ID';
    if (!STEAM_ID_PATTERN.test(steamId)) return 'Steam ID 应为 17 位数字';
    if (!apiKey) return '请输入 Steam API Key';
    if (!API_KEY_PATTERN.test(apiKey)) return 'API Key 通常为 32 位十六进制字符串';
    return null;
  }

  const handleConfigure = async (): Promise<void> => {
    // 前端预校验
    const validationError = validateInput();
    if (validationError) {
      configError.value = validationError;
      return;
    }

    try {
      configError.value = '';
      isConfigLoading.value = true;

      const payload = {
        steamId: configSteamId.value.trim(),
        apiKey: configApiKey.value.trim(),
        rememberMe: rememberMe.value,
      };

      if (import.meta.env.DEV) {
        console.log('准备发送凭据配置请求:', { steamId: payload.steamId, rememberMe: payload.rememberMe });
      }

      const resp = await configureCredential(payload);

      if (import.meta.env.DEV) {
        console.log('配置保存并验证响应:', resp);
      }

      if (resp.success) {
        configured.value = true;
        store.commit('setAuthenticated', true);
        store.commit('setSteamId', payload.steamId);

        // P0-4: 成功后直接进入 Dashboard（而非回凭据验证页）
        if (rememberMe.value || resp.data?.persisted) {
          localStorage.setItem('steamId', payload.steamId);
        }

        setTimeout(() => {
          router.push('/dashboard');
        }, 600);
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

  const handleReturnToDashboard = (): void => {
    router.push('/dashboard');
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
    handleReturnToDashboard,
    clearConfigError,
  };
}
