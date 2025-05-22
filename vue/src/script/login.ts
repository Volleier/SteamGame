import axios from 'axios';

export default {
  name: 'LoginPage',
  data() {
    return {
      Steam_Id: '',
      Api_Key: '',
      rememberMe: false,
      isLoading: false,
      errorMessage: '',
    };
  },
  created() {
    // 组件创建时加载用户数据
    this.loadUserCredentials();
  },
  methods: {
    // 从后端加载用户凭据
    async loadUserCredentials() {
      try {
        const response = await axios.get('/api/login');

        // 打印完整的响应数据，便于调试
        console.log('后端返回的完整数据:', response.data);

        // 更灵活地处理后端返回的数据
        const data = response.data;

        // 尝试不同的字段名称
        // 有些API返回camelCase，有些返回snake_case格式
        this.Steam_Id = data.steamId || data.steam_id || data.SteamId || '';
        this.Api_Key = data.apiKey || data.api_key || data.ApiKey || '';
        this.rememberMe = data.rememberMe || data.remember_me || false;

        // 检查是否确实获取到了数据
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

    async handleLogin() {
      // 其余登录逻辑保持不变
      this.isLoading = true;
      this.errorMessage = '';

      // 创建LoginDto数据结构
      const loginData = {
        time: new Date().toISOString(),
        steamId: this.Steam_Id,
        apiKey: this.Api_Key,
        rememberMe: this.rememberMe,
      };

      // 增加更详细的日志
      console.log(`准备发送登录数据:`, loginData);

      try {
        // 调用后端API进行验证
        const response = await axios.post('/api/login', loginData);
        console.log('登录成功，接收到响应:', response.data);

        // 处理登录成功
        const { token, user } = response.data;

        // 存储认证信息
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

        // 更新全局状态
        this.$store.commit('setUser', user);
        this.$store.commit('setAuthenticated', true);

        // 跳转到仪表盘
        this.$router.push('/dashboard');
      } catch (error) {
        // 处理登录失败
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
  },
};
