/**
 * 游戏成就 API
 */
import http from './http';

export interface AchievementGlobalPercent {
  name: string;
  percent: number;
}

export interface AchievementGlobalResult {
  appid: number;
  achievements: AchievementGlobalPercent[];
}

function unwrap<T>(response: any): T {
  return response.data?.data ?? response.data ?? ({} as T);
}

/** GET /api/game-achievements/global-percentages */
export async function getGlobalAchievementPercentages(appid: number): Promise<AchievementGlobalResult> {
  const response = await http.get('/game-achievements/global-percentages', { params: { appid } });
  return unwrap<AchievementGlobalResult>(response);
}
