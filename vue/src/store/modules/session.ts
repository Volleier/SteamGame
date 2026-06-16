/**
 * Session store module — 管理认证状态和 Steam ID。
 */
import { getStoredSteamId, setStoredSteamId } from '@/utils/storage';

export interface SessionState {
  authenticated: boolean;
  steamId: string;
}

export const sessionModule = {
  state: (): SessionState => ({
    authenticated: false,
    steamId: getStoredSteamId(),
  }),
  mutations: {
    setAuthenticated(state: SessionState, value: boolean) {
      state.authenticated = value;
    },
    setSteamId(state: SessionState, value: string) {
      state.steamId = value;
      setStoredSteamId(value);
    },
  },
  actions: {
    logout({ commit }: any) {
      commit('setAuthenticated', false);
      commit('setSteamId', '');
    },
  },
};
