/**
 * UI store module — 管理全局 UI 状态。
 */
export interface UiState {
  isFullscreen: boolean;
}

export const uiModule = {
  state: (): UiState => ({
    isFullscreen: false,
  }),
  mutations: {
    setIsFullscreen(state: UiState, value: boolean) {
      state.isFullscreen = value;
    },
  },
};
