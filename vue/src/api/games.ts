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
  const data: OwnedGameDTO[] = response.data || [];
  return data.map(toOwnedGame);
}
