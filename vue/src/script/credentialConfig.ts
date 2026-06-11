import { ref } from 'vue';
import axios, { AxiosResponse } from 'axios';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';

export function useCredentialConfig() {
  const configSteamId = ref<string>('');
  const configApiKey = ref<string>('');
  const rememberMe = ref<boolean>(false);
  const isConfigLoading = ref<boolean>(false);
  const configError = ref<string>('');
  const configured = ref<boolean>(false);

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

      console.log('准备发送凭据配置请求:', payload);
      const response: AxiosResponse<any> = await axios.post('/api/credentials/configure', payload);
      console.log('配置保存并验证响应:', response.data);

      // 统一响应：{ success, code, message, data }
      const resp = response.data || {};
      const code = resp.code;
      if (resp.success) {
        configured.value = true;
        store.commit && store.commit('setAuthenticated', true);
        // 不在本地存明文 apiKey；可保存 steamId 用于展示
        if (payload.steamId) {
          localStorage.setItem('steamId', payload.steamId);
        }
        setTimeout(() => {
          router.push('/credential-verify');
          setTimeout(() => {
            configured.value = false;
          }, 1000);
        }, 900);
      } else {
        // 根据 code 映射更友好的中文提示
        const msg = resp.message || mapCodeToMessage(code) || '验证失败，请检查您的凭据';
        configError.value = msg;
      }
    } catch (error) {
      console.error('凭据配置失败:', error);
      configError.value = (error as any).response?.data?.message || '配置失败，请稍后再试';
    } finally {
      isConfigLoading.value = false;
    }
  };

  function mapCodeToMessage(code: any): string {
    switch (String(code)) {
      case 'REGISTER_OK':
        return '凭据已保存并验证成功';
      case 'INVALID_STEAM_ID':
        return '无效的 SteamID';
      case 'INVALID_API_KEY_FORMAT':
        return 'ApiKey 格式不正确';
      case 'CONFIG_INVALID':
        return '配置内容不完整或格式错误';
      case 'INTERNAL_ERROR':
        return '服务器内部错误，请联系管理员';
      case 'STEAM_API_UNAVAILABLE':
        return 'Steam 服务不可用，请稍后重试';
      default:
        return '';
    }
  }

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
