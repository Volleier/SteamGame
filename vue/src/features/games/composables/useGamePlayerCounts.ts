/**
 * 批量游戏在线人数请求 —— 从 GameList.vue 提取。
 */
import { ref, watch, type Ref } from 'vue';
import { getCurrentPlayersBatch } from '@/api/gameStats';
import type { OwnedGame } from '@/api/games';

export function useGamePlayerCounts(games: Ref<OwnedGame[]>) {
  const playerCounts = ref<Record<number, number | string>>({});

  async function loadAllPlayerCounts() {
    const allIds = games.value
      .map(g => g.app_id)
      .filter((id: number) => playerCounts.value[id] === undefined);
    const chunkSize = 50;
    for (let i = 0; i < allIds.length; i += chunkSize) {
      const chunk = allIds.slice(i, i + chunkSize);
      try {
        const res = await getCurrentPlayersBatch(chunk);
        res.items.forEach((item: any) => {
          playerCounts.value[item.appid] = item.playerCount;
        });
        chunk.forEach((id: number) => {
          if (playerCounts.value[id] === undefined) playerCounts.value[id] = '-';
        });
      } catch {
        chunk.forEach((id: number) => {
          if (playerCounts.value[id] === undefined) playerCounts.value[id] = '-';
        });
      }
    }
  }

  watch(() => games.value, () => {
    if (games.value.length > 0) loadAllPlayerCounts();
  }, { immediate: true });

  return { playerCounts };
}
