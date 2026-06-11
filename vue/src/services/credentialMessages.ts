/**
 * 凭据相关错误码 → 中文提示映射（完整）
 */

const MESSAGES: Record<string, string> = {
  // 成功
  LOGIN_OK: '凭据验证通过',
  REGISTER_OK: '凭据已保存并验证成功',

  // 配置相关
  CONFIG_NOT_FOUND: '未配置凭据，请先保存配置',
  CONFIG_INVALID: '配置内容不完整或格式错误',

  // 输入格式
  INVALID_STEAM_ID: 'Steam ID 应为 17 位数字',
  INVALID_API_KEY_FORMAT: 'API Key 通常为 32 位十六进制字符串',

  // 验证结果
  INVALID_KEY_OR_USER: 'apiKey 或 SteamID 无效',
  PROFILE_PRIVATE_OR_NO_GAMES: '用户资料私密或无游戏',

  // 服务器
  STEAM_API_TIMEOUT: 'Steam API 请求超时，请稍后重试',
  STEAM_API_UNAVAILABLE: 'Steam 服务不可用，请稍后重试',
  DECRYPT_FAILED: '凭据解密失败，请检查加密密钥配置',
  INTERNAL_ERROR: '服务器内部错误，请联系管理员',
};

export function getCredentialMessage(code: string | number | undefined | null, fallback = '凭据验证失败'): string {
  if (code == null) return fallback;
  return MESSAGES[String(code)] || fallback;
}
