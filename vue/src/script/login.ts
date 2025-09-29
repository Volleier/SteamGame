import axios from 'axios';

export default {
  name: 'LoginPage',
  data() {
    return {
      // 登录表单数据
      Steam_Id: '',
      Api_Key: '',
      isLoading: false,
      errorMessage: '',

      // 注册表单数据
      showRegisterModal: false,
      registerSteamId: '',
      registerApiKey: '',
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
      };

      console.log(`准备发送登录数据:`, loginData);

      try {
        let response;

        // 按新设计：始终使用本地配置来登录（仅发送空 POST），不再通过请求体提交凭据
        console.log('发送空 POST /api/login（后端将使用本地 YAML 配置验证）');
        response = await axios.post('/api/login');

        console.log('登录请求返回，状态码:', response.status, '数据:', response.data);

        if (response.status === 200) {
          const { token, user } = response.data || {};

          // 存储认证数据
          if (token) {
            localStorage.setItem('token', token);
          }
          if (user) {
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
          // 如果 $store 可用则使用提交，否则回退到 localStorage 保存状态，避免未注册 store 导致的错误
          if (user) {
            if (this.$store && typeof this.$store.commit === 'function') {
              this.$store.commit('setUser', user);
            } else {
              localStorage.setItem('user', JSON.stringify(user));
            }
          }

          if (this.$store && typeof this.$store.commit === 'function') {
            this.$store.commit('setAuthenticated', true);
          } else {
            localStorage.setItem('authenticated', 'true');
          }

          this.$router.push('/dashboard');
        } else {
          // 非预期的 2xx 之外的成功状态（如果有）也处理
          this.errorMessage = response.data?.message || `登录返回状态: ${response.status}`;
        }
      } catch (err: any) {
        console.error('登录失败:', err);
        const status = err?.response?.status;
        const respData = err?.response?.data;
        console.error('错误详情:', { message: err?.message, response: respData, status });

        // 无响应（网络、超时、CORS）
        if (!err?.response) {
          this.errorMessage = '网络错误或服务器无响应（请检查网络或 CORS）';
        } else if (respData && (respData.message || respData.error || respData.msg)) {
          // 优先显示后端 message 字段或常见替代字段
          this.errorMessage = respData.message || respData.error || respData.msg;
        } else if (status === 400) {
          this.errorMessage = '错误的 SteamID 或 ApiKey 格式';
        } else if (status === 404) {
          this.errorMessage = '未找到用户或 API Key 无效';
        } else if (status === 502) {
          this.errorMessage = 'Steam 服务不可用，请稍后重试';
        } else if (status === 500) {
          this.errorMessage = '服务器内部错误，请联系管理员';
        } else {
          // 回退：显示状态码、状态文本及响应摘要，便于排查
          const statusText = err.response?.statusText || '';
          const snippet = typeof respData === 'string' ? respData : JSON.stringify(respData || {}).slice(0, 200);
          this.errorMessage = `未知错误 (status ${status}${statusText ? ' ' + statusText : ''})${snippet ? ': ' + snippet : ''}`;
        }
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
        };

        console.log('准备发送注册信息:', registerData);
        const response = await axios.post('/api/register', registerData);
        console.log('注册请求返回，状态码:', response.status, '数据:', response.data);

        if (response.status === 200) {
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

          // 设置认证状态并重定向
          if (this.$store && typeof this.$store.commit === 'function') {
            this.$store.commit('setAuthenticated', true);
            if (response.data.user) {
              this.$store.commit('setUser', response.data.user);
            }
          } else {
            localStorage.setItem('authenticated', 'true');
            if (response.data.user) {
              localStorage.setItem('user', JSON.stringify(response.data.user));
            }
          }

          // 短暂延迟后跳转，让用户看到成功消息
          setTimeout(() => {
            this.$router.push('/dashboard');
            setTimeout(() => {
              this.showRegisterSuccess = false;
            }, 1000);
          }, 1500);
        } else {
          // 非 200 的成功响应（不常见）
          this.registerError = response.data?.message || `注册返回状态: ${response.status}`;
        }
      } catch (error: any) {
        console.error('注册失败:', error);
        const status = error?.response?.status;
        const respData = error?.response?.data;
        console.error('注册错误详情:', { message: error?.message, response: respData, status });

        if (!error?.response) {
          this.registerError = '网络错误或服务器无响应（请检查网络或 CORS）';
        } else if (respData && (respData.message || respData.error || respData.msg)) {
          this.registerError = respData.message || respData.error || respData.msg;
        } else if (status === 400) {
          this.registerError = '请求错误：请检查提交的 SteamID 与 ApiKey';
        } else if (status === 404) {
          this.registerError = '未找到用户或 API Key 无效';
        } else if (status === 502) {
          this.registerError = '上游服务（Steam API）不可用，请稍后再试';
        } else if (status === 500) {
          this.registerError = '服务器内部错误';
        } else {
          const statusText = error.response?.statusText || '';
          const snippet = typeof respData === 'string' ? respData : JSON.stringify(respData || {}).slice(0, 200);
          this.registerError = `注册失败 (status ${status}${statusText ? ' ' + statusText : ''})${snippet ? ': ' + snippet : ''}`;
        }
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

      localStorage.setItem('steamId', steamId);
      localStorage.setItem('apiKey', apiKey);
      console.log('已将登录凭证保存到本地存储');

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
