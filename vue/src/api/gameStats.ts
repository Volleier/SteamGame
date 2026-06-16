/**
 * 游戏在线人数 API
 */
import http from './http';

export interface CurrentPlayerCount {
  appid: number;
  playerCount: number;
  cached: boolean;
  stale: boolean;
  syncedAt: string | null;
}

export interface CurrentPlayerBatchResult {
  items: CurrentPlayerCount[];
}

function unwrap<T>(response: any): T {
  return response.data?.data ?? response.data ?? ({} as T);
}

/** GET /api/game-stats/current-players */
export async function getCurrentPlayers(appid: number, forceRefresh = false): Promise<CurrentPlayerCount> {
  const response = await http.get('/game-stats/current-players', { params: { appid, forceRefresh } });
  return unwrap<CurrentPlayerCount>(response);
}

/** POST /api/game-stats/current-players/batch */
export async function getCurrentPlayersBatch(appids: number[], forceRefresh = false): Promise<CurrentPlayerBatchResult> {
  const response = await http.post('/game-stats/current-players/batch', { appids, forceRefresh });
  return unwrap<CurrentPlayerBatchResult>(response);
}
