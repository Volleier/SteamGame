/**
 * 管理后台 API
 */
import http from './http';

export interface SystemConfig {
  steamApiTimeoutSeconds: number;
  detailsTimeoutSeconds: number;
  detailsDelayMillis: number;
  maxBatchSize: number;
  storeLanguage: string;
  storeCountryCode: string;
  configPath: string;
  credentialRevalidateHours: number;
}

export interface SteamApiMethod {
  name: string;
  version: number;
  httpMethod: string;
  parameters: { name: string; type: string; optional: boolean; description: string }[];
}

export interface SteamApiInterface {
  name: string;
  methods: SteamApiMethod[];
}

export interface SteamApiSupportedList {
  source: string;
  withKey: boolean;
  interfaces: SteamApiInterface[];
}

function unwrap<T>(response: any): T {
  return response.data?.data ?? response.data ?? ({} as T);
}

/** GET /api/admin/system-config */
export async function getSystemConfig(): Promise<SystemConfig> {
  const response = await http.get('/admin/system-config');
  return unwrap<SystemConfig>(response);
}

/** GET /api/admin/steam-api/supported */
export async function getSteamApiSupported(withKey = false): Promise<SteamApiSupportedList> {
  const response = await http.get('/admin/steam-api/supported', { params: { withKey } });
  return unwrap<SteamApiSupportedList>(response);
}
