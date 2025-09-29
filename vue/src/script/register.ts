import { ref } from 'vue';
import axios, { AxiosResponse } from 'axios';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';

export function useRegister() {
  const registerSteamId = ref<string>('');
  const registerApiKey = ref<string>('');
  const registerRememberMe = ref<boolean>(false);
  const isRegisterLoading = ref<boolean>(false);
  const registerError = ref<string>('');
  const registered = ref<boolean>(false);

  const router = useRouter();
  const store = useStore();

  const handleRegister = async (): Promise<void> => {
    try {
      registerError.value = '';
      isRegisterLoading.value = true;

      const registerData = {
        steamId: registerSteamId.value,
        apiKey: registerApiKey.value,
        rememberMe: registerRememberMe.value,
      };

      console.log('准备发送注册信息:', registerData);
      const response: AxiosResponse<any> = await axios.post('/api/register', registerData);
      console.log('注册成功:', response.data);

      // Treat any 2xx HTTP status as success (backend returns 201 on success)
      if (response.status >= 200 && response.status < 300) {
        console.log('验证成功，准备跳转到登录');

        if (response.data?.token) {
          localStorage.setItem('token', response.data.token);
        }

        store.commit('setAuthenticated', true);
        if (response.data?.user) {
          store.commit('setUser', response.data.user);
        }

        // 标记为已注册，替换按钮显示；5s 后跳转到登录页
        registered.value = true;
        setTimeout(() => {
          router.push('/login');
          setTimeout(() => {
            registered.value = false;
          }, 1000);
        }, 5000);
      } else {
        registerError.value = response.data?.message || '验证失败，请检查您的SteamID和ApiKey';
      }
    } catch (error: any) {
      console.error('注册失败:', error);
      registerError.value = error.response?.data?.message || '注册失败，请稍后再试';
    } finally {
      isRegisterLoading.value = false;
    }
  };

  const handleReturnToLogin = (): void => {
    router.push('/login');
  };

  const clearRegisterError = (): void => {
    registerError.value = '';
  };

  return {
    registerSteamId,
    registerApiKey,
    registerRememberMe,
    isRegisterLoading,
    registerError,
    registered,
    handleRegister,
    handleReturnToLogin,
    clearRegisterError,
  };
}
