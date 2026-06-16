<template>
  <div class="about-container relative w-full h-full overflow-hidden bg-[#121212] font-sans flex-1">
    <div class="canvas-viewport absolute inset-0 overflow-hidden touch-none" ref="viewport"
      @pointerdown="onViewportPointerDown" @pointermove="onPointerMove" @pointerup="onPointerUp"
      @pointercancel="onPointerUp" @wheel="onWheel" @contextmenu.prevent>

      <div class="dot-grid-layer absolute inset-0 pointer-events-none" :style="gridStyle"></div>

      <div class="canvas-content absolute top-0 left-0 origin-top-left will-change-transform" :style="contentStyle" ref="canvasEl">
        <template v-for="card in cards" :key="card.id">
          <DashboardCard
            v-if="card.visible !== false"
            :card="card"
            :is-dragging="drag.cardId === card.id"
            :is-armed="drag.armed && drag.cardId === card.id"
            :is-focus-mode="isFocusMode"
            @cardDown="onCardDown"
            @cardClick="onCardClick"
          >
            <DashboardProfileCard v-if="card.id === 1" :profile="profile" :steamId="steamId" :gamesCount="gamesCount" />
            <DashboardTopGamesCard v-else-if="card.id === 2" :games="topGames" />
            <DashboardRecentGamesCard v-else-if="card.id === 3" :games="recentGames" />
            <DashboardWishlistCard v-else-if="card.id === 4" :items="wishlist" />
            <DashboardFriendsCard v-else-if="card.id === 5" :friends="friends" />
          </DashboardCard>
        </template>
      </div>
    </div>

    <!-- Controls -->
    <div class="canvas-controls absolute bottom-8 left-1/2 -translate-x-1/2 flex gap-4 bg-black/50 backdrop-blur-lg border border-white/10 p-2 rounded-full shadow-2xl z-50 items-center">
      <button @click="resetView" class="px-6 py-2 text-xs font-bold text-gray-300 hover:text-white uppercase tracking-widest transition-colors rounded-full hover:bg-white/10">RESET</button>
      <button @click="fitView" class="px-6 py-2 text-xs font-bold text-[#00d4ff] hover:text-white uppercase tracking-widest transition-colors rounded-full hover:bg-white/10">FIT</button>
    </div>

    <!-- Side LAYOUT Button -->
    <button @click="isDrawerOpen = !isDrawerOpen"
      class="fixed right-0 top-1/2 -translate-y-1/2 bg-black/80 backdrop-blur-md border border-white/10 border-r-0 px-3 py-12 text-2xl rounded-l-xl text-[#00ffd5] hover:text-white hover:bg-white/10 transition-all duration-300 shadow-[0_0_15px_rgba(0,255,213,0.2)] z-[102] flex items-center justify-center font-black"
      :style="{ transform: isDrawerOpen ? 'translateX(-640px)' : 'translateX(0)' }">
      {{ isDrawerOpen ? '>' : '<' }}
    </button>

    <!-- Fullscreen Button -->
    <button @click="toggleFullscreen"
      class="absolute bottom-8 right-8 w-12 h-12 flex items-center justify-center bg-black/50 backdrop-blur-lg border border-white/10 text-gray-300 hover:text-white hover:bg-white/10 transition-colors rounded-full shadow-2xl z-50">
      <svg v-if="!isFullscreen" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" /></svg>
      <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 14h4v4m0-4l-5 5m15-1v4h-4m4-4l-5 5M4 10h4V6m0 4l-5-5m15 1V6h-4m4 4l-5-5" /></svg>
    </button>

    <!-- Drawer -->
    <DashboardDrawer :cards="cards" :isOpen="isDrawerOpen" @drawerCardDown="onDrawerCardDown" @saveLayout="handleSaveLayout" />

    <!-- Save Toast -->
    <transition name="fade">
      <div v-if="showSaveToast" class="fixed bottom-24 right-8 px-6 py-3 bg-[#00ffd5] text-black font-black uppercase text-xs tracking-widest rounded-lg shadow-[0_0_20px_rgba(0,255,213,0.4)] z-[200] border border-white/20 transition-all">
        布局已成功保存
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue';
import { useDashboardData } from '@/features/dashboard/composables/useDashboardData';
import { useDashboardCanvas } from '@/features/dashboard/composables/useDashboardCanvas';
import { useDashboardLayout, createDefaultCards } from '@/features/dashboard/composables/useDashboardLayout';
import DashboardCard from '@/features/dashboard/components/DashboardCard.vue';
import DashboardProfileCard from '@/features/dashboard/components/DashboardProfileCard.vue';
import DashboardTopGamesCard from '@/features/dashboard/components/DashboardTopGamesCard.vue';
import DashboardRecentGamesCard from '@/features/dashboard/components/DashboardRecentGamesCard.vue';
import DashboardWishlistCard from '@/features/dashboard/components/DashboardWishlistCard.vue';
import DashboardFriendsCard from '@/features/dashboard/components/DashboardFriendsCard.vue';
import DashboardDrawer from '@/features/dashboard/components/DashboardDrawer.vue';

const props = defineProps<{ isFullscreen: boolean }>();
const emit = defineEmits(['toggle-fullscreen']);

const isDrawerOpen = ref(false);

const { gamesCount, profile, steamId, topGames, recentGames, wishlist, friends, loadDashboardData } = useDashboardData();
const { showSaveToast, loadLayout, saveLayout } = useDashboardLayout();

const cards = ref(createDefaultCards());

const canvas = useDashboardCanvas(cards);
const { viewport, gridStyle, contentStyle, isFocusMode, drag, onViewportPointerDown, onCardDown, onPointerMove, onPointerUp, onWheel, resetView, fitView, GRID_SIZE, snap } = canvas;

const canvasEl = canvas.canvas;

function getCardRect(cardId: number) {
  const card = cards.value.find(c => c.id === cardId);
  const el = document.querySelector(`[data-id="${cardId}"]`) as HTMLElement;
  if (!card || !el) return { x: 0, y: 0, w: 0, h: 0, cx: 0, cy: 0 };
  return { x: card.x, y: card.y, w: el.offsetWidth, h: el.offsetHeight, cx: card.x + el.offsetWidth / 2, cy: card.y + el.offsetHeight / 2 };
}

function onDrawerCardDown(ev: PointerEvent, card: any) {
  if (ev.button !== 0) return;
  card.visible = true;
  const worldX = snap((ev.clientX - canvas.state.panX) / canvas.state.scale - (card.w * GRID_SIZE) / 2);
  const worldY = snap((ev.clientY - canvas.state.panY) / canvas.state.scale - 200);
  card.x = worldX; card.y = worldY;
  onCardDown(ev, card);
  canvas.drag.armed = true;
  canvas.drag.dragging = true;
  canvas.drag.wasDragging = true;
  nextTick(() => {
    const el = document.querySelector(`[data-id="${card.id}"]`) as HTMLElement;
    if (el) el.setPointerCapture(ev.pointerId);
  });
}

function onCardClick(ev: MouseEvent, card: any) {
  if (canvas.drag.wasDragging) { ev.preventDefault(); ev.stopPropagation(); return; }
  isFocusMode.value = true;
  const rect = getCardRect(card.id);
  const targetScale = canvas.clampScale((window.innerWidth * 0.45) / rect.w);
  canvas.state.scale = Math.min(targetScale, 1.8);
  const vW = viewport.value?.offsetWidth || window.innerWidth;
  const vH = viewport.value?.offsetHeight || window.innerHeight;
  canvas.state.panX = vW / 2 - rect.cx * canvas.state.scale;
  canvas.state.panY = vH / 2 - rect.cy * canvas.state.scale;
}

function toggleFullscreen() { emit('toggle-fullscreen'); }

async function handleSaveLayout() {
  try { await saveLayout(cards.value); } catch (e) { console.error('Save layout failed:', e); }
}

watch(
  () => props.isFullscreen,
  (value) => {
    if (!value) {
      isDrawerOpen.value = false;
      canvas.resetDragState();
    }
  },
);

onMounted(async () => {
  canvas.startEdgeScroll();
  await loadLayout(cards.value);
  loadDashboardData();
});

onUnmounted(() => {
  canvas.stopEdgeScroll();
  if (canvas.drag.holdTimer) clearTimeout(canvas.drag.holdTimer);
});
</script>

<style scoped>
.dot-grid-layer {
  background-color: #121212;
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.15) 1.1px, transparent 1.1px);
  background-size: calc(var(--grid-size) * var(--zoom)) calc(var(--grid-size) * var(--zoom));
  background-position: calc(var(--pan-x) * 1px) calc(var(--pan-y) * 1px);
}

.about-card {
  min-height: 200px;
  transition: box-shadow 0.2s ease, border-color 0.2s ease, transform 0.4s cubic-bezier(0.16, 1, 0.3, 1), z-index 0s;
  cursor: grab;
}
.about-card.is-dragging {
  transition: none !important;
  cursor: grabbing;
}
.about-card.is-armed {
  box-shadow: 0 0 0 1px rgba(61, 220, 192, 0.42), 0 20px 44px rgba(0, 0, 0, 0.56);
}
.about-card.is-focus-mode {
  animation: breathe 3s ease-in-out infinite;
}

@keyframes breathe {
  0% { box-shadow: 0 0 10px rgba(0, 212, 255, 0.1); }
  50% { box-shadow: 0 0 25px rgba(0, 212, 255, 0.3); }
  100% { box-shadow: 0 0 10px rgba(0, 212, 255, 0.1); }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
