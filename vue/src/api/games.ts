/**
 * 用户游戏列表 API
 */

import http from './http';

export interface OwnedGameDTO {
  appid: number;
  name: string;
  playtimeForever: number;
}

export interface OwnedGame {
  app_id: number;
  app_name: string;
  app_time: number; // 小时
}

export function toOwnedGame(dto: OwnedGameDTO): OwnedGame {
  return {
    app_id: dto.appid,
    app_name: dto.name,
    app_time: Number(((dto.playtimeForever || 0) / 60).toFixed(2)),
  };
}

/** 获取用户拥有的游戏列表 */
export async function getOwnedGames(): Promise<OwnedGame[]> {
  const response = await http.get('/ownedgames/list');
  // 兼容标准包装格式 { code, msg, data: [...] } 和直接数组格式
  let list: OwnedGameDTO[] = [];
  if (Array.isArray(response.data)) {
    list = response.data;
  } else if (response.data && Array.isArray(response.data.data)) {
    list = response.data.data;
  }
  return list.map(toOwnedGame);
}

/** 获取用户库存 (游戏总数) */
export async function getGamesCount(): Promise<number> {
  try {
    const response = await http.get('/ownedgames/count');
    // 兼容标准包装格式 { data: { count } } 和直接 { count } 格式
    return response.data?.data?.count ?? response.data?.count ?? 0;
  } catch (error) {
    console.warn('获取游戏总数失败:', error);
    return 0;
  }
}

/** 触发游戏库同步 (POST /api/ownedgames/sync) */
export async function syncOwnedGames(): Promise<OwnedGame[]> {
  const response = await http.post('/ownedgames/sync');
  // 兼容标准包装格式 { data: [...] } 和直接数组格式
  const list = response.data?.data ?? response.data ?? [];
  return (Array.isArray(list) ? list : []).map(toOwnedGame);
}
