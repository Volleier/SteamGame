/**
 * 路由认证守卫 —— 凭据检查、session 同步。
 */
import { getStoredSteamId } from '@/utils/storage';

export function hasCredential(): boolean {
  return !!getStoredSteamId();
}

export function syncSessionFromStorage(): string {
  return getStoredSteamId();
}

export function requireCredentialGuard(): boolean {
  // 当前 MVP 阶段允许所有用户进入 dashboard
  // 后续可启用真实检查：return hasCredential();
  return true;
}
