/**
 * 凭据相关错误码 → 中文提示映射（合并自 verify/config 双端）
 */

const MESSAGES: Record<string, string> = {
  // 通用
  LOGIN_OK: '凭据验证通过',
  STEAM_API_UNAVAILABLE: 'Steam 服务不可用，请稍后重试',

  // 验证页
  CONFIG_NOT_FOUND: '未配置凭据，请先保存配置',
  DECRYPT_FAILED: '凭据解密失败，请检查加密密钥配置',
  INVALID_KEY_OR_USER: 'apiKey 或 SteamID 无效',
  PROFILE_PRIVATE_OR_NO_GAMES: '用户资料私密或无游戏',

  // 配置页
  REGISTER_OK: '凭据已保存并验证成功',
  INVALID_STEAM_ID: '无效的 SteamID',
  INVALID_API_KEY_FORMAT: 'ApiKey 格式不正确',
  CONFIG_INVALID: '配置内容不完整或格式错误',
  INTERNAL_ERROR: '服务器内部错误，请联系管理员',
};

export function getCredentialMessage(code: string | number | undefined | null, fallback = '凭据验证失败'): string {
  if (code == null) return fallback;
  return MESSAGES[String(code)] || fallback;
}
