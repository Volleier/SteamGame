/**
 * 凭据相关 API 调用 — 前后端契约对齐
 */

import http from './http';

// ===== 契约类型（对齐 DevList §6） =====

export interface CredentialStatus {
  steamId: string;
  hasApiKey: boolean;
  persisted: boolean;
  validationStatus?: 'VALID' | 'INVALID' | 'UNKNOWN';
  updatedAt?: string;
  lastValidatedAt?: string;
}

export interface CredentialConfigPayload {
  steamId: string;
  apiKey: string;
  rememberMe: boolean;
}

export interface CredentialLoginResult {
  steamId: string;
  persisted: boolean;
  validKeyAndUser: boolean;
  ownsGames: boolean;
  profilePrivate: boolean;
  gameCount: number;
  validatedAt?: string;
}

export interface CredentialVerifyResult {
  success: boolean;
  code?: string;
  message?: string;
  data?: CredentialLoginResult | null;
}

// ===== API 函数 =====

/** 获取当前凭据状态 */
export async function getCredentialStatus(): Promise<CredentialStatus> {
  const response = await http.get('/credentials/status');
  const body = response.data || {};          // ApiResponse wrapper
  const data = body.data || {};              // actual payload
  return {
    steamId: data.steamId || data.steam_id || '',
    hasApiKey: !!data.hasApiKey,
    persisted: !!data.persisted,
    validationStatus: data.validationStatus,
    updatedAt: data.updatedAt,
    lastValidatedAt: data.lastValidatedAt,
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
