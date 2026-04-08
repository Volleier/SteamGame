import axios from 'axios';

export default {
  name: 'CredentialVerifyPage',
  data() {
    return {
      Steam_Id: '',
      Api_Key: '',
      isLoading: false,
      errorMessage: '',

      // 临时用于内嵌配置流程
      showConfigModal: false,
      configSteamId: '',
      configApiKey: '',
      isConfigLoading: false,
      configError: '',
      showConfigSuccess: false,
      configSuccessMessage: '',
    };
  },

  created() {
    this.loadCredentialStatus();
  },

  methods: {
    async loadCredentialStatus() {
      try {
        const response = await axios.get('/api/credentials/status');
        const data = response.data || {};
        this.Steam_Id = data.steamId || data.steam_id || data.SteamId || '';
        // 不在前端保留明文 apiKey
      } catch (error) {
        console.error('加载凭据状态失败:', error);
      }
    },

    async handleVerify() {
      this.isLoading = true;
      this.errorMessage = '';

      try {
        console.log('发送 POST /api/credentials/verify');
        const response = await axios.post('/api/credentials/verify');
        const resp = response.data || {};
        // 按统一 ApiResponse 结构处理
        if (resp.success) {
          this.$store && this.$store.commit && this.$store.commit('setAuthenticated', true);
          this.$router.push('/dashboard');
        } else {
          const code = resp.code;
          this.errorMessage = resp.message || mapCodeToMessage(code) || `验证失败（code=${code})`;
        }
      } catch (err: any) {
        console.error('凭据验证失败:', err);
        const status = err?.response?.status;
        const respData = err?.response?.data;
        if (!err?.response) {
          this.errorMessage = '网络错误或服务器无响应（请检查网络或 CORS）';
        } else if (respData && (respData.message || respData.error || respData.msg)) {
          this.errorMessage = respData.message || respData.error || respData.msg;
        } else {
          this.errorMessage = `验证失败（HTTP ${status}）`;
        }
      } finally {
        this.isLoading = false;
      }
    },

    openConfigModal() {
      this.showConfigModal = true;
    },

    async handleConfig() {
      try {
        this.configError = '';
        this.isConfigLoading = true;
        const payload = { steamId: this.configSteamId, apiKey: this.configApiKey };
        const response = await axios.post('/api/credentials/configure', payload);
        if (response.status === 200) {
          this.closeConfigModal();
          this.showConfigSuccess = true;
          this.configSuccessMessage = '凭据配置并验证成功，正在跳转...';
          this.$store && this.$store.commit && this.$store.commit('setAuthenticated', true);
          setTimeout(() => {
            this.$router.push('/dashboard');
          }, 1200);
        } else {
          this.configError = response.data?.message || `配置返回状态: ${response.status}`;
        }
      } catch (error: any) {
        console.error('配置失败:', error);
        this.configError = error?.response?.data?.message || '配置失败';
      } finally {
        this.isConfigLoading = false;
      }
    },

    closeConfigModal() {
      this.showConfigModal = false;
      this.configSteamId = '';
      this.configApiKey = '';
      this.isConfigLoading = false;
    },

    syncConfigData(steamId, apiKey) {
      if (!steamId || !apiKey) return;
      this.Steam_Id = steamId;
      localStorage.setItem('steamId', steamId);
    },
  },
  function mapCodeToMessage(code) {
    switch (String(code)) {
      case 'LOGIN_OK':
        return '凭据验证通过';
      case 'CONFIG_NOT_FOUND':
        return '未配置凭据，请先保存配置';
      case 'DECRYPT_FAILED':
        return '凭据解密失败，请检查加密密钥配置';
      case 'INVALID_KEY_OR_USER':
        return 'apiKey 或 SteamID 无效';
      case 'STEAM_API_UNAVAILABLE':
        return 'Steam 服务不可用，请稍后重试';
      case 'PROFILE_PRIVATE_OR_NO_GAMES':
        return '用户资料私密或无游戏';
      default:
        return '凭据验证失败';
    }
  }
};
