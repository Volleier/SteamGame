import axios from 'axios';

export default {
  name: 'LoginPage',
  data() {
    return {
      // 登录表单数据
      Steam_Id: '',
      Api_Key: '',
      rememberMe: false,
      isLoading: false,
      errorMessage: '',

      // 注册表单数据
      showRegisterModal: false,
      registerSteamId: '',
      registerApiKey: '',
      registerRememberMe: false,
      isRegisterLoading: false, // 新增：注册时的加载状态

      // 注册错误和成功信息
      registerError: '', // 注册错误信息
      showRegisterSuccess: false, // 显示注册成功消息
      registerSuccessMessage: '', // 注册成功消息内容
    };
  },

  created() {
    // 组件创建时加载用户凭据
    this.loadUserCredentials();
  },

  methods: {
    // === 认证方法 ===

    /**
     * 从后端加载保存的用户凭据
     */
    async loadUserCredentials() {
      try {
        const response = await axios.get('/api/login');
        console.log('后端返回的完整数据:', response.data);

        const data = response.data;

        // 处理不同字段名格式
        this.Steam_Id = data.steamId || data.steam_id || data.SteamId || '';
        this.Api_Key = data.apiKey || data.api_key || data.ApiKey || '';
        this.rememberMe = data.rememberMe || data.remember_me || false;

        if (this.Steam_Id || this.Api_Key) {
          console.log('加载用户凭据成功，已填充表单:', {
            steamId: this.Steam_Id,
            apiKeyLength: this.Api_Key ? this.Api_Key.length : 0,
          });
        } else {
          console.warn('加载用户凭据成功，但数据为空');
        }
      } catch (error) {
        console.error('加载用户凭据失败:', error);
      }
    },

    /**
     * 处理用户登录提交
     */
    async handleLogin() {
      this.isLoading = true;
      this.errorMessage = '';

      const loginData = {
        time: new Date().toISOString(),
        steamId: this.Steam_Id,
        apiKey: this.Api_Key,
        rememberMe: this.rememberMe,
      };

      console.log(`准备发送登录数据:`, loginData);

      try {
        const response = await axios.post('/api/login', loginData);
        console.log('登录成功，接收到响应:', response.data);

        const { token, user } = response.data;

        // 存储认证数据
        localStorage.setItem('token', token);
        if (this.rememberMe) {
          localStorage.setItem(
            'user',
            JSON.stringify({
              ...user,
              steamId: this.Steam_Id,
              apiKey: this.Api_Key,
            })
          );
        }

        // 更新全局状态并重定向
        this.$store.commit('setUser', user);
        this.$store.commit('setAuthenticated', true);
        this.$router.push('/dashboard');
      } catch (error) {
        console.error('登录失败:', error);
        console.error('错误详情:', {
          message: error.message,
          response: error.response?.data,
          status: error.response?.status,
        });
        this.errorMessage = error.response?.data?.message || '登录失败，请检查您的凭据';
      } finally {
        this.isLoading = false;
      }
    },

    // === 注册方法 ===

    /**
     * 打开注册模态框
     */
    openRegisterModal() {
      this.showRegisterModal = true;
    },

    /**
     * 处理用户注册提交
     */
    async handleRegister() {
      try {
        this.registerError = ''; // 清除之前的错误信息
        this.isRegisterLoading = true; // 设置加载状态

        const registerData = {
          steamId: this.registerSteamId,
          apiKey: this.registerApiKey,
          rememberMe: this.registerRememberMe,
        };

        console.log('准备发送注册信息:', registerData);
        const response = await axios.post('/api/register', registerData);
        console.log('注册成功:', response.data);

        // 检查后端返回的验证结果
        if (response.data.status === 1 || response.data === 1) {
          console.log('验证成功，准备跳转到dashboard');

          // 处理响应数据
          if (typeof response.data === 'object' && response.data.steamId && response.data.apiKey) {
            const { steamId, apiKey } = response.data;
            this.syncLoginData(steamId, apiKey);
          } else {
            this.syncLoginData(this.registerSteamId, this.registerApiKey);
          }

          // 关闭模态框并显示成功消息
          this.closeRegisterModal();
          this.showRegisterSuccess = true;
          this.registerSuccessMessage = '注册验证成功！正在跳转...';

          // 设置认证状态并重定向到dashboard
          if (response.data.token) {
            localStorage.setItem('token', response.data.token);
          }

          this.$store.commit('setAuthenticated', true);
          if (response.data.user) {
            this.$store.commit('setUser', response.data.user);
          }

          // 短暂延迟后跳转，让用户看到成功消息
          setTimeout(() => {
            this.$router.push('/dashboard');
            setTimeout(() => {
              this.showRegisterSuccess = false;
            }, 1000);
          }, 1500);
        } else {
          // 验证失败
          this.registerError = response.data.message || '验证失败，请检查您的SteamID和ApiKey';
        }
      } catch (error) {
        console.error('注册失败:', error);
        this.registerError = error.response?.data?.message || '注册失败，请稍后再试';
      } finally {
        this.isRegisterLoading = false; // 无论成功还是失败，最终都要关闭加载状态
      }
    },

    /**
     * 关闭注册模态框并重置表单
     */
    closeRegisterModal() {
      this.showRegisterModal = false;
      this.registerSteamId = '';
      this.registerApiKey = '';
      this.registerRememberMe = false;
      this.isRegisterLoading = false; // 确保关闭模态框时重置加载状态
    },

    /**
     * 同步注册数据到登录表单
     */
    syncLoginData(steamId, apiKey) {
      if (!steamId || !apiKey) {
        console.warn('同步登录数据失败：缺少steamId或apiKey');
        return;
      }

      console.log('正在同步登录数据:', { steamId, apiKeyLength: apiKey?.length });

      // 更新登录表单数据
      this.Steam_Id = steamId;
      this.Api_Key = apiKey;

      // 如果勾选了记住我，则保存到本地存储
      if (this.registerRememberMe) {
        localStorage.setItem('steamId', steamId);
        localStorage.setItem('apiKey', apiKey);
        localStorage.setItem('rememberMe', 'true');
        console.log('已将登录凭证保存到本地存储');
      }

      // 启用登录表单输入框
      this.$nextTick(() => {
        const steamIdInput = document.getElementById('Steam_Id') as HTMLInputElement;
        const apiKeyInput = document.getElementById('Api_Key') as HTMLInputElement;

        if (steamIdInput && apiKeyInput) {
          steamIdInput.disabled = false;
          apiKeyInput.disabled = false;
          console.log('已启用登录表单输入框');
        }
      });
    },
  },
};
