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
  methods: {
    // 添加临时登录方法
    handleTempLogin() {
      // 创建临时用户数据
      const tempUser = {
        id: 'temp-user-123',
        name: '演示用户',
        steamId: 'DEMO_STEAM_ID',
      };

      // 创建临时token
      const tempToken = 'demo-token-' + Date.now();

      // 存储认证信息
      localStorage.setItem('token', tempToken);
      localStorage.setItem('user', JSON.stringify(tempUser));

      // 如果使用了Vuex，更新状态
      if (this.$store && typeof this.$store.commit === 'function') {
        this.$store.commit('setUser', tempUser);
        this.$store.commit('setAuthenticated', true);
      }

      // 跳转到仪表盘
      this.$router.push('/dashboard');
    },

    async handleLogin() {
      this.isLoading = true;
      this.errorMessage = '';

      // 创建LoginDto数据结构
      const loginData = {
        time: new Date().toISOString(), // 当前时间的ISO字符串格式
        steamId: this.Steam_Id,
        apiKey: this.Api_Key,
        rememberMe: this.rememberMe,
      };

      // 添加调试信息 - 打印请求信息
      console.log(`发送数据：`, {
        steamId: this.Steam_Id,
        apiKey: this.Api_Key,
      });

      try {
        // 调用后端API进行验证 - 使用指定的URL路径
        const response = await axios.post('/api/login', loginData);

        // 添加调试信息 - 打印响应数据
        console.log('接收到响应：', response.data);

        // 处理登录成功
        const { token, user } = response.data;

        // 存储认证信息
        localStorage.setItem('token', token);
        if (this.rememberMe) {
          localStorage.setItem('user', JSON.stringify(user));
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
