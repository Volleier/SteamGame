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

function stringifyForDialog(value: unknown): string {
  if (value == null) return '';
  if (typeof value === 'string') return value;
  try {
    return JSON.stringify(value, null, 2);
  } catch {
    return String(value);
  }
}

function truncate(value: string, maxLength = 1600): string {
  return value.length > maxLength ? `${value.slice(0, maxLength)}\n...（内容已截断）` : value;
}

function buildApiFailureMessage(resp: { code?: string; message?: string; data?: unknown }): string {
  const lines = ['配置凭据失败'];
  if (resp.code) lines.push(`错误码：${resp.code}`);
  lines.push(`错误信息：${resp.message || getCredentialMessage(resp.code, '验证失败，请检查您的凭据')}`);

  const detail = stringifyForDialog(resp.data);
  if (detail && detail !== 'null') {
    lines.push('', '后端返回详情：', truncate(detail));
  }

  return lines.join('\n');
}

function buildRequestErrorMessage(error: unknown): string {
  const e = error as any;
  const response = e?.response;
  const request = e?.config || {};
  const data = response?.data;

  const lines = ['配置凭据请求失败'];
  if (response) {
    lines.push(`HTTP 状态：${response.status}${response.statusText ? ` ${response.statusText}` : ''}`);

    if (typeof data === 'string') {
      lines.push('响应内容：', truncate(data.trim() || '(空响应)'));
    } else if (data && typeof data === 'object') {
      if (data.code) lines.push(`错误码：${data.code}`);
      if (data.message || data.error || data.msg) lines.push(`错误信息：${data.message || data.error || data.msg}`);
      if (data.path) lines.push(`后端路径：${data.path}`);
      if (data.timestamp) lines.push(`时间戳：${data.timestamp}`);

      const detail = stringifyForDialog(data.data ?? data.errors ?? data);
      if (detail && detail !== '{}') {
        lines.push('', '完整响应：', truncate(detail));
      }
    } else {
      lines.push('响应内容：(空响应)');
    }
  } else if (e?.request) {
    lines.push('未收到后端响应。请确认后端服务已启动，且 Vite 代理指向 http://localhost:8080。');
  } else {
    lines.push('请求未能发出。');
  }

  if (e?.message) lines.push(`Axios 信息：${e.message}`);
  if (request?.method || request?.url) lines.push(`请求：${String(request.method || 'GET').toUpperCase()} ${request.baseURL || ''}${request.url || ''}`);

  return lines.join('\n');
}

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
          router.push('/dashboard?initialSync=true');
        }, 600);
      } else {
        configError.value = buildApiFailureMessage(resp);
      }
    } catch (error) {
      console.error('凭据配置失败:', error);
      configError.value = buildRequestErrorMessage(error);
    } finally {
      isConfigLoading.value = false;
    }
  };

  const handleReturnToDashboard = (): void => {
    router.push('/dashboard?initialSync=true');
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
