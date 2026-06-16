/**
 * 游戏新闻 API
 */
import http from './http';

export interface GameNewsItem {
  gid: string;
  title: string;
  url: string;
  isExternalUrl: boolean;
  author: string;
  contents: string;
  feedLabel: string;
  date: number;
}

export interface GameNewsResult {
  appid: number;
  items: GameNewsItem[];
}

function unwrap<T>(response: any): T {
  return response.data?.data ?? response.data ?? ({} as T);
}

/** GET /api/game-news */
export async function getGameNews(appid: number, count = 10, maxlength?: number): Promise<GameNewsResult> {
  const params: Record<string, any> = { appid, count };
  if (maxlength != null) params.maxlength = maxlength;
  const response = await http.get('/game-news', { params });
  return unwrap<GameNewsResult>(response);
}
