import { createStore } from 'vuex';

export interface RootState {
  authenticated: boolean;
  steamId: string;
}

export default createStore<RootState>({
  state: {
    authenticated: false,
    steamId: localStorage.getItem('steamId') || '',
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
  },

  actions: {
    logout({ commit }) {
      commit('setAuthenticated', false);
      commit('setSteamId', '');
      localStorage.removeItem('steamId');
    },
  },
});
