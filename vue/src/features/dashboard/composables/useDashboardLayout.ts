/**
 * Dashboard 布局管理 —— 加载 / 保存布局，卡片默认配置。
 */
import { ref } from 'vue';
import { getDashboardLayout, saveDashboardLayout, type DashboardLayoutItemDTO } from '@/api/dashboardLayout';
import type { DashboardCardConfig } from '@/features/dashboard/types';

export const DEFAULT_CARDS: Omit<DashboardCardConfig, 'x' | 'y' | 'visible'>[] = [
  { id: 1, tag: '01_INVENTORY', heading: '库容 / INVENTORY', accent: 'cyan', initX: 120, initY: 120, w: 14, className: 'card-inventory', content: '' },
  { id: 2, tag: '02_PLAYTIME', heading: '游玩排行 / PLAYTIME TOP 5', accent: 'magenta', initX: 480, initY: 120, w: 16, className: 'card-playtime', content: '' },
  { id: 3, tag: '03_RECENT', heading: '最近游玩 / RECENT PLAYED', accent: 'green', initX: 888, initY: 120, w: 16, className: 'card-recent', content: '' },
  { id: 4, tag: '04_WISHLIST', heading: '愿望单 / WISHLIST', accent: 'cyan', initX: 120, initY: 520, w: 14, className: 'card-wishlist', content: '' },
  { id: 5, tag: '05_FRIENDS', heading: '好友 / FRIENDS', accent: 'green', initX: 480, initY: 720, w: 16, className: 'card-friends', content: '' },
];

export function createDefaultCards(): DashboardCardConfig[] {
  return DEFAULT_CARDS.map(c => ({ ...c, x: c.initX, y: c.initY, visible: true }));
}

export function useDashboardLayout() {
  const showSaveToast = ref(false);

  async function loadLayout(cards: DashboardCardConfig[]): Promise<boolean> {
    try {
      const layout = await getDashboardLayout();
      if (layout && layout.length > 0) {
        layout.forEach((item: DashboardLayoutItemDTO) => {
          const card = cards.find(c => c.id === item.id);
          if (card) {
            card.x = item.x; card.y = item.y;
            if (item.w) card.w = item.w;
            card.visible = item.visible !== false;
          }
        });
        return true;
      }
    } catch (e) {
      console.error('Failed to fetch dashboard layout:', e);
    }
    return false;
  }

  async function saveLayout(cards: DashboardCardConfig[]): Promise<void> {
    const payload: DashboardLayoutItemDTO[] = cards.map(c => ({
      id: c.id, x: c.x, y: c.y, w: c.w, visible: c.visible !== false,
    }));
    await saveDashboardLayout(payload);
    showSaveToast.value = true;
    setTimeout(() => { showSaveToast.value = false; }, 3000);
  }

  return { showSaveToast, loadLayout, saveLayout };
}
