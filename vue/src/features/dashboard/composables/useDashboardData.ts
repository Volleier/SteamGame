/**
 * Dashboard 数据加载组合式函数 —— 集中管理所有卡片数据请求。
 */
import { ref, type Ref } from 'vue';
import { getGamesCount, getOwnedGames, type OwnedGame } from '@/api/games';
import { getPlayerProfile, getRecentGames, getFriends, getWishlist, type PlayerProfile, type RecentGameItem, type FriendItem, type WishlistItem } from '@/api/player';

export function useDashboardData() {
  const gamesCount: Ref<number | null> = ref(null);
  const profile: Ref<PlayerProfile | null> = ref(null);
  const steamId: Ref<string> = ref('');
  const topGames: Ref<OwnedGame[]> = ref([]);
  const recentGames: Ref<RecentGameItem[]> = ref([]);
  const wishlist: Ref<WishlistItem[]> = ref([]);
  const friends: Ref<FriendItem[]> = ref([]);

  async function fetchGamesCount() {
    try {
      gamesCount.value = await getGamesCount();
    } catch (e) {
      console.error('Failed to fetch games count:', e);
    }
  }

  async function fetchProfile() {
    try {
      const data = await getPlayerProfile();
      profile.value = data;
      if (data) steamId.value = data.steamId;
    } catch (e) {
      console.error('Failed to fetch profile:', e);
    }
  }

  async function fetchTopGames() {
    try {
      const list = await getOwnedGames();
      topGames.value = [...list].sort((a, b) => b.app_time - a.app_time).slice(0, 5);
    } catch (e) {
      console.error('Failed to fetch top games:', e);
    }
  }

  async function fetchRecentGames() {
    try {
      const result = await getRecentGames('default', 5);
      recentGames.value = result?.games || [];
    } catch (e) {
      console.warn('Failed to fetch recent games:', e);
    }
  }

  async function fetchWishlist() {
    try {
      const result = await getWishlist();
      wishlist.value = (result?.items || []).slice(0, 5);
    } catch (e) {
      console.warn('Failed to fetch wishlist:', e);
    }
  }

  async function fetchFriends() {
    try {
      const result = await getFriends();
      friends.value = (result?.items || []).slice(0, 5);
    } catch (e) {
      console.warn('Failed to fetch friends:', e);
    }
  }

  async function loadDashboardData() {
    await Promise.all([
      fetchGamesCount(),
      fetchProfile(),
      fetchTopGames(),
      fetchRecentGames(),
      fetchWishlist(),
      fetchFriends(),
    ]);
  }

  return {
    gamesCount, profile, steamId, topGames, recentGames, wishlist, friends,
    loadDashboardData, fetchGamesCount, fetchProfile, fetchTopGames,
    fetchRecentGames, fetchWishlist, fetchFriends,
  };
}

/** 获取用户在线状态的中文文案 */
export function getStatusText(state: number | undefined): string {
    if (state === undefined) return '加载中...';
    switch (state) {
      case 0: return '离线 (OFFLINE)';
      case 1: return '在线 (ONLINE)';
      case 2: return '忙碌 (BUSY)';
      case 3: return '离开 (AWAY)';
      case 4: return '打盹 (SNOOZE)';
      case 5: return '想交易 (LOOKING TO TRADE)';
      case 6: return '想玩游戏 (LOOKING TO PLAY)';
      default: return '未知 (UNKNOWN)';
    }
  }

/** 获取在线状态对应的 CSS class */
export function getStatusClass(state: number | undefined): string {
    if (state === undefined) return 'text-gray-500';
    switch (state) {
      case 0: return 'text-gray-400';
      case 1: case 5: case 6: return 'text-[#00ffd5]';
      case 2: return 'text-red-400';
      case 3: case 4: return 'text-yellow-400';
      default: return 'text-gray-400';
    }
  }

/** 获取排名徽章 class */
export function getRankClass(index: number): string {
    switch (index) {
      case 0: return 'bg-gradient-to-br from-[#ffd700] to-[#ffa500] text-black shadow-[0_0_8px_rgba(255,215,0,0.5)]';
      case 1: return 'bg-gradient-to-br from-[#e0e0e0] to-[#9e9e9e] text-black shadow-[0_0_6px_rgba(224,224,224,0.4)]';
      case 2: return 'bg-gradient-to-br from-[#cd7f32] to-[#8b5a2b] text-white shadow-[0_0_6px_rgba(205,127,50,0.3)]';
      default: return 'bg-white/10 text-gray-300';
    }
  }
