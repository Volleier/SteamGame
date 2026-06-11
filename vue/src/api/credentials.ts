/**
 * 凭据相关 API 调用
 */

import http from './http';

export interface CredentialStatus {
  steamId: string;
}

export interface CredentialConfigPayload {
  steamId: string;
  apiKey: string;
  rememberMe: boolean;
}

export interface CredentialVerifyResult {
  success: boolean;
  code?: string;
  message?: string;
  data?: unknown;
}

/** 获取当前凭据状态 */
export async function getCredentialStatus(): Promise<CredentialStatus> {
  const response = await http.get('/credentials/status');
  const data = response.data || {};
  return {
    steamId: data.steamId || data.steam_id || data.SteamId || '',
  };
}

/** 保存并验证凭据 */
export async function configureCredential(payload: CredentialConfigPayload): Promise<CredentialVerifyResult> {
  const response = await http.post('/credentials/configure', payload);
  return response.data || {};
}

/** 验证已配置的凭据 */
export async function verifyCredential(): Promise<CredentialVerifyResult> {
  const response = await http.post('/credentials/verify');
  return response.data || {};
}
