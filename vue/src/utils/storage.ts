/**
 * localStorage / sessionStorage 统一封装。
 */

const LAST_SYNC_KEY = 'lastSyncTime';
const STEAM_ID_KEY = 'steamId';

export function getLastSyncTime(): string {
  return localStorage.getItem(LAST_SYNC_KEY) || '';
}

export function setLastSyncTime(value: string): void {
  localStorage.setItem(LAST_SYNC_KEY, value);
}

export function getStoredSteamId(): string {
  return localStorage.getItem(STEAM_ID_KEY) || '';
}

export function setStoredSteamId(value: string): void {
  if (value) localStorage.setItem(STEAM_ID_KEY, value);
  else localStorage.removeItem(STEAM_ID_KEY);
}
