/** Dashboard 卡片 ID */
export type DashboardCardId = 1 | 2 | 3 | 4 | 5;

/** 卡片强调色 */
export type DashboardCardAccent = 'cyan' | 'magenta' | 'green';

/** Dashboard 卡片配置 */
export interface DashboardCardConfig {
  id: DashboardCardId;
  tag: string;
  heading: string;
  accent: DashboardCardAccent;
  initX: number;
  initY: number;
  x: number;
  y: number;
  w: number;
  visible: boolean;
  className: string;
  content: string;
}

/** Dashboard 数据状态 */
export interface DashboardDataState {
  gamesCount: number | null;
  profile: import('@/api/player').PlayerProfile | null;
  steamId: string;
  topGames: import('@/api/games').OwnedGame[];
  recentGames: import('@/api/player').RecentGameItem[];
  wishlist: import('@/api/player').WishlistItem[];
  friends: import('@/api/player').FriendItem[];
}
