/**
 * 玩家相关 API — profile, summary, recent games, friends, wishlist
 */
import http from './http';

export interface PlayerProfile {
  steamId: string;
  personaName: string;
  profileUrl: string;
  avatar: string;
  avatarMedium: string;
  avatarFull: string;
  personaState: number;
  communityVisibilityState: number;
  lastLogoff: number;
  timeCreated: number;
  countryCode: string;
}

export interface PlayerSummary {
  profile: PlayerProfile | null;
  ownedGameCount: number;
  recentGameCount: number;
  totalPlaytimeForever: number | null;
  lastSyncedAt: string | null;
}

export interface RecentGameItem {
  appid: number;
  name: string;
  playtime2Weeks: number;
  playtimeForever: number;
  iconUrl: string;
}

export interface RecentGameResult {
  totalCount: number;
  games: RecentGameItem[];
}

export interface FriendItem {
  steamId: string;
  relationship: string;
  friendSince: number;
}

export interface FriendResult {
  items: FriendItem[];
}

export interface WishlistItem {
  appid: number;
  name: string;
  nameEn?: string;
  price?: string;
  priority: number;
  addedAt: number;
}

export interface WishlistResult {
  items: WishlistItem[];
}

function unwrap<T>(response: any): T {
  return response.data?.data ?? response.data ?? ({} as T);
}

/** GET /api/player/profile */
export async function getPlayerProfile(userId = 'default'): Promise<PlayerProfile | null> {
  const response = await http.get('/player/profile', { params: { userId } });
  return unwrap<PlayerProfile>(response);
}

/** GET /api/player/summary */
export async function getPlayerSummary(userId = 'default'): Promise<PlayerSummary> {
  const response = await http.get('/player/summary', { params: { userId } });
  return unwrap<PlayerSummary>(response);
}

/** GET /api/player/recent-games */
export async function getRecentGames(userId = 'default', count = 10): Promise<RecentGameResult> {
  const response = await http.get('/player/recent-games', { params: { userId, count } });
  return unwrap<RecentGameResult>(response);
}

/** GET /api/player/friends */
export async function getFriends(userId = 'default'): Promise<FriendResult> {
  const response = await http.get('/player/friends', { params: { userId } });
  return unwrap<FriendResult>(response);
}

/** GET /api/player/wishlist */
export async function getWishlist(userId = 'default'): Promise<WishlistResult> {
  const response = await http.get('/player/wishlist', { params: { userId } });
  return unwrap<WishlistResult>(response);
}
