import { createStore } from 'vuex';

export interface RootState {
  authenticated: boolean;
  steamId: string;
  isFullscreen: boolean;
}

export default createStore<RootState>({
  state: {
    authenticated: false,
    steamId: localStorage.getItem('steamId') || '',
    isFullscreen: false,
  },

  mutations: {
    setAuthenticated(state, value: boolean) {
      state.authenticated = value;
    },
    setSteamId(state, value: string) {
      state.steamId = value;
      if (value) {
        localStorage.setItem('steamId', value);
      } else {
        localStorage.removeItem('steamId');
      }
    },
    setIsFullscreen(state, value: boolean) {
      state.isFullscreen = value;
    },
  },

  actions: {
    logout({ commit }) {
      commit('setAuthenticated', false);
      commit('setSteamId', '');
      localStorage.removeItem('steamId');
    },
  },
});
