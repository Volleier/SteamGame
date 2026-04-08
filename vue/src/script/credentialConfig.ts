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

      if (response.status >= 200 && response.status < 300) {
        if (response.data?.token) {
          localStorage.setItem('token', response.data.token);
        }

        store.commit && store.commit('setAuthenticated', true);
        response.data?.user && store.commit && store.commit('setUser', response.data.user);

        configured.value = true;
        setTimeout(() => {
          router.push('/credential-verify');
          setTimeout(() => {
            configured.value = false;
          }, 1000);
        }, 1500);
      } else {
        configError.value = response.data?.message || '验证失败，请检查您的 SteamID 和 ApiKey';
      }
    } catch (error: any) {
      console.error('凭据配置失败:', error);
      configError.value = error.response?.data?.message || '配置失败，请稍后再试';
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
