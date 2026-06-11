/**
 * 统一 HTTP 客户端
 * 封装 axios 实例，统一 baseURL、超时、错误处理。
 */

import axios, { type AxiosInstance, type AxiosResponse } from 'axios';

const http: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 响应拦截：统一解包 data
http.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    if (import.meta.env.DEV) {
      console.error('[API Error]', error?.response?.status, error?.message);
    }
    return Promise.reject(error);
  },
);

export default http;
