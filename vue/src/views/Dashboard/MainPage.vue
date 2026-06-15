<template>
  <div class="about-container relative w-full h-full overflow-hidden bg-[#121212] font-sans flex-1">
    <div
      class="canvas-viewport absolute inset-0 overflow-hidden touch-none"
      ref="viewport"
      @pointerdown="onViewportPointerDown"
      @pointermove="onPointerMove"
      @pointerup="onPointerUp"
      @pointercancel="onPointerUp"
      @wheel="onWheel"
      @contextmenu.prevent
    >
      <!-- Grid Layer -->
      <div class="dot-grid-layer absolute inset-0 pointer-events-none" :style="gridStyle"></div>

      <!-- Canvas Content -->
      <div class="canvas-content absolute top-0 left-0 origin-top-left will-change-transform" :style="contentStyle" ref="canvas">
        <article
          v-for="card in cards"
          :key="card.id"
          class="info-card about-card absolute border border-white/10 rounded-lg p-6 bg-black/60 backdrop-blur-md shadow-2xl text-white transition-all duration-500 flex flex-col"
          :class="[card.className, { 'is-dragging': drag.cardId === card.id, 'is-armed': drag.armed && drag.cardId === card.id, 'is-focus-mode': isFocusMode }]"
          :style="{
            '--card-grid-w': card.w,
            width: `calc(${card.w} * var(--grid-size))`,
            transform: `translate3d(${card.x}px, ${card.y}px, 0)`,
            zIndex: drag.cardId === card.id ? 50 : 1,
          }"
          :data-id="card.id"
          @pointerdown.stop="onCardDown($event, card)"
          @click="onCardClick($event, card)"
        >
          <div :class="['card-accent absolute left-0 top-6 w-1 h-10', card.accent === 'cyan' ? 'bg-[#00d4ff]' : card.accent === 'magenta' ? 'bg-[#ff00ff]' : 'bg-[#00ffd5]']"></div>
          <header class="card-header mb-4 pl-4 pointer-events-none select-none">
            <span class="tag text-[#00d4ff] text-xs font-bold tracking-widest uppercase">{{ card.tag }}</span>
            <h2 class="text-xl font-extrabold tracking-wider uppercase mt-1">{{ card.heading }}</h2>
          </header>
          <div class="card-body markdown-body pl-4 text-gray-300 text-sm leading-relaxed flex-1 select-none pointer-events-none" v-html="card.content"></div>
        </article>
      </div>
    </div>

    <!-- Controls -->
    <div
      class="canvas-controls absolute bottom-8 left-1/2 -translate-x-1/2 flex gap-4 bg-black/50 backdrop-blur-lg border border-white/10 p-2 rounded-full shadow-2xl z-50 items-center"
    >
      <button @click="resetView" class="px-6 py-2 text-xs font-bold text-gray-300 hover:text-white uppercase tracking-widest transition-colors rounded-full hover:bg-white/10">
        RESET
      </button>
      <button @click="fitView" class="px-6 py-2 text-xs font-bold text-[#00d4ff] hover:text-white uppercase tracking-widest transition-colors rounded-full hover:bg-white/10">
        FIT
      </button>
    </div>

    <!-- Standalone Fullscreen Button -->
    <button
      @click="toggleFullscreen"
      class="absolute bottom-8 right-8 w-12 h-12 flex items-center justify-center bg-black/50 backdrop-blur-lg border border-white/10 text-gray-300 hover:text-white hover:bg-white/10 transition-colors rounded-full shadow-2xl z-50"
      title="全屏切换"
    >
      <svg v-if="!isFullscreen" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
      </svg>
      <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 14h4v4m0-4l-5 5m15-1v4h-4m4-4l-5 5M4 10h4V6m0 4l-5-5m15 1V6h-4m4 4l-5-5" />
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { getGamesCount } from '@/api/games';

const props = defineProps<{ isFullscreen: boolean }>();
const emit = defineEmits(['toggle-fullscreen']);

const gridSize = 24;

const viewport = ref<HTMLElement | null>(null);
const canvas = ref<HTMLElement | null>(null);

const state = reactive({
  panX: 0,
  panY: 0,
  scale: 1,
});

const isFocusMode = ref(false);

const edge = reactive({
  active: false,
  mouseX: 0,
  mouseY: 0,
  threshold: 72,
  speed: 18,
  reqFrame: 0,
});

const drag = reactive({
  cardId: null as number | null,
  pointerId: -1,
  startClientX: 0,
  startClientY: 0,
  startWorldX: 0,
  startWorldY: 0,
  startX: 0,
  startY: 0,
  dragging: false,
  mayDrag: false,
  armed: false,
  holdMs: 160,
  holdTimer: null as number | null,
  wasDragging: false,
});

const pan = reactive({
  active: false,
  pointerId: -1,
  startX: 0,
  startY: 0,
  startPanX: 0,
  startPanY: 0,
  middlePressed: false,
});

const cards = ref([
  {
    id: 1,
    tag: '01_INVENTORY',
    heading: '库容 / INVENTORY',
    accent: 'cyan',
    initX: 120,
    initY: 120,
    x: 120,
    y: 120,
    w: 14,
    className: 'card-inventory',
    content:
      "<div class='text-center py-4'><p class='text-gray-400 text-sm tracking-widest uppercase mb-2'>已收录游戏</p><p class='text-6xl font-black text-transparent bg-clip-text bg-gradient-to-br from-[#00d4ff] to-[#00ffd5]' id='games-count'>--</p></div>",
  },
]);

// Computed Styles
const gridStyle = computed(() => ({
  '--grid-size': `${gridSize}px`,
  '--pan-x': state.panX,
  '--pan-y': state.panY,
  '--zoom': state.scale,
}));

const contentStyle = computed(() => ({
  '--grid-size': `${gridSize}px`,
  transform: `translate3d(${state.panX}px, ${state.panY}px, 0) scale(${state.scale})`,
}));

// Helpers
const snap = (value: number) => Math.round(value / gridSize) * gridSize;
const clampScale = (v: number) => Math.min(2.4, Math.max(0.45, v));

const getCardRect = (cardId: number) => {
  const card = cards.value.find((c) => c.id === cardId);
  const el = document.querySelector(`[data-id="${cardId}"]`) as HTMLElement;
  if (!card || !el) return { x: 0, y: 0, w: 0, h: 0, cx: 0, cy: 0 };
  return {
    x: card.x,
    y: card.y,
    w: el.offsetWidth,
    h: el.offsetHeight,
    cx: card.x + el.offsetWidth / 2,
    cy: card.y + el.offsetHeight / 2,
  };
};

const resetDragState = () => {
  if (drag.holdTimer) clearTimeout(drag.holdTimer);
  if (drag.cardId !== null) {
    const el = document.querySelector(`[data-id="${drag.cardId}"]`) as HTMLElement;
    if (el && el.hasPointerCapture(drag.pointerId)) {
      try {
        el.releasePointerCapture(drag.pointerId);
      } catch (e) {}
    }
  }
  drag.cardId = null;
  drag.pointerId = -1;
  drag.dragging = false;
  drag.mayDrag = false;
  drag.armed = false;
};

// Events
const onViewportPointerDown = (ev: PointerEvent) => {
  // Middle click (1) or Left click + Alt for panning
  if (ev.button === 1 || (ev.button === 0 && ev.altKey)) {
    ev.preventDefault();
    pan.active = true;
    pan.pointerId = ev.pointerId;
    pan.startX = ev.clientX;
    pan.startY = ev.clientY;
    pan.startPanX = state.panX;
    pan.startPanY = state.panY;
    pan.middlePressed = ev.button === 1;
    if (viewport.value) viewport.value.setPointerCapture(ev.pointerId);
  }
};

const onCardDown = (ev: PointerEvent, card: any) => {
  if (pan.active) return;
  if (ev.button !== 0) return; // Only left click for dragging cards

  drag.cardId = card.id;
  drag.pointerId = ev.pointerId;
  drag.startClientX = ev.clientX;
  drag.startClientY = ev.clientY;
  drag.startWorldX = (ev.clientX - state.panX) / state.scale;
  drag.startWorldY = (ev.clientY - state.panY) / state.scale;
  drag.startX = card.x;
  drag.startY = card.y;
  drag.mayDrag = true;
  drag.armed = false;

  drag.holdTimer = window.setTimeout(() => {
    if (!drag.mayDrag || drag.dragging) return;
    drag.armed = true;
  }, drag.holdMs);
};

const onPointerMove = (ev: PointerEvent) => {
  edge.mouseX = ev.clientX;
  edge.mouseY = ev.clientY;

  // Handle Canvas Panning
  if (pan.active && ev.pointerId === pan.pointerId) {
    state.panX = pan.startPanX + (ev.clientX - pan.startX);
    state.panY = pan.startPanY + (ev.clientY - pan.startY);
    return;
  }

  if (!drag.mayDrag) return;

  const dist = Math.hypot(ev.clientX - drag.startClientX, ev.clientY - drag.startClientY);

  // Hold-to-arm mechanism
  if (!drag.armed) {
    if (dist > 6) {
      drag.wasDragging = false;
      resetDragState();
    }
    return;
  }

  // Small jitter ignore
  if (!drag.dragging && dist < 2) return;

  // Start dragging
  if (!drag.dragging) {
    drag.dragging = true;
    drag.wasDragging = true;
    isFocusMode.value = false;
    const el = document.querySelector(`[data-id="${drag.cardId}"]`) as HTMLElement;
    if (el) el.setPointerCapture(ev.pointerId);
  }

  // World coordinate projection
  const worldX = (ev.clientX - state.panX) / state.scale;
  const worldY = (ev.clientY - state.panY) / state.scale;
  const nextX = snap(drag.startX + (worldX - drag.startWorldX));
  const nextY = snap(drag.startY + (worldY - drag.startWorldY));

  const card = cards.value.find((c) => c.id === drag.cardId);
  if (card) {
    card.x = nextX;
    card.y = nextY;
  }
};

const resolveCollisions = (movedCardId: number, startX: number, startY: number) => {
  let iterations = 0;
  const maxIterations = 60;
  let hasCollision = true;
  let curX = startX;
  let curY = startY;

  const movedCard = cards.value.find((c) => c.id === movedCardId);
  if (!movedCard) return { x: startX, y: startY };

  movedCard.x = curX;
  movedCard.y = curY;

  while (hasCollision && iterations < maxIterations) {
    hasCollision = false;
    iterations++;

    const r1 = getCardRect(movedCardId);

    for (const other of cards.value) {
      if (other.id === movedCardId) continue;
      const r2 = getCardRect(other.id);

      // AABB Collision Detection
      if (r1.x < r2.x + r2.w && r1.x + r1.w > r2.x && r1.y < r2.y + r2.h && r1.y + r1.h > r2.y) {
        hasCollision = true;
        const dx = r1.cx - r2.cx;
        const dy = r1.cy - r2.cy;

        // Push along the dominant axis
        if (Math.abs(dx) / r1.w >= Math.abs(dy) / r1.h) {
          curX = dx >= 0 ? r2.x + r2.w + gridSize : r2.x - r1.w - gridSize;
        } else {
          curY = dy >= 0 ? r2.y + r2.h + gridSize : r2.y - r1.h - gridSize;
        }

        curX = snap(curX);
        curY = snap(curY);

        movedCard.x = curX;
        movedCard.y = curY;
        break; // Recalculate all overlaps with new position
      }
    }
  }
  return { x: curX, y: curY };
};

const onPointerUp = (ev: PointerEvent) => {
  if (pan.active && ev.pointerId === pan.pointerId) {
    pan.active = false;
    pan.middlePressed = false;
    if (viewport.value && viewport.value.hasPointerCapture(ev.pointerId)) {
      viewport.value.releasePointerCapture(ev.pointerId);
    }
    return;
  }

  if (drag.cardId !== null) {
    if (drag.dragging) {
      const card = cards.value.find((c) => c.id === drag.cardId);
      if (card) {
        // Resolve collisions and snap to final grid position
        const finalPos = resolveCollisions(card.id, card.x, card.y);
        card.x = finalPos.x;
        card.y = finalPos.y;
      }
    }
    // Defer resetting wasDragging to allow click event to be blocked
    setTimeout(() => {
      drag.wasDragging = false;
    }, 50);
    resetDragState();
  }
};

const onWheel = (ev: WheelEvent) => {
  if (pan.middlePressed) return;
  ev.preventDefault();

  const beforeX = (ev.clientX - state.panX) / state.scale;
  const beforeY = (ev.clientY - state.panY) / state.scale;

  const zoomFactor = Math.exp(-ev.deltaY * 0.00035);
  const nextScale = clampScale(state.scale * zoomFactor);
  state.scale = nextScale;

  state.panX = ev.clientX - beforeX * nextScale;
  state.panY = ev.clientY - beforeY * nextScale;
};

const edgeScroll = () => {
  if (drag.dragging) {
    edge.active = true;
  } else {
    edge.active = false;
  }

  if (edge.active && viewport.value) {
    const rect = viewport.value.getBoundingClientRect();
    const localX = edge.mouseX - rect.left;
    const localY = edge.mouseY - rect.top;

    let dx = 0,
      dy = 0;
    if (localX < edge.threshold) dx = edge.speed;
    if (localX > rect.width - edge.threshold) dx = -edge.speed;
    if (localY < edge.threshold) dy = edge.speed;
    if (localY > rect.height - edge.threshold) dy = -edge.speed;

    if (dx !== 0 || dy !== 0) {
      state.panX += dx;
      state.panY += dy;
    }
  }
  edge.reqFrame = window.requestAnimationFrame(edgeScroll);
};

const onCardClick = (ev: MouseEvent, card: any) => {
  if (drag.wasDragging) {
    ev.preventDefault();
    ev.stopPropagation();
    return;
  }
  focusOnCard(card.id);
};

const focusOnCard = (cardId: number) => {
  isFocusMode.value = true;
  const rect = getCardRect(cardId);
  const targetW = window.innerWidth * 0.45;
  const targetScale = clampScale(targetW / rect.w);

  state.scale = Math.min(targetScale, 1.8);

  const vW = viewport.value?.offsetWidth || window.innerWidth;
  const vH = viewport.value?.offsetHeight || window.innerHeight;

  state.panX = vW / 2 - rect.cx * state.scale;
  state.panY = vH / 2 - rect.cy * state.scale;
};

const fitView = () => {
  isFocusMode.value = false;
  let minX = Infinity,
    minY = Infinity,
    maxX = -Infinity,
    maxY = -Infinity;
  cards.value.forEach((card) => {
    const rect = getCardRect(card.id);
    if (rect.w === 0) return;
    minX = Math.min(minX, rect.x);
    minY = Math.min(minY, rect.y);
    maxX = Math.max(maxX, rect.x + rect.w);
    maxY = Math.max(maxY, rect.y + rect.h);
  });

  if (minX === Infinity) return;

  const contentW = maxX - minX;
  const contentH = maxY - minY;
  const availableW = (viewport.value?.offsetWidth || window.innerWidth) - 120;
  const availableH = (viewport.value?.offsetHeight || window.innerHeight) - 120;

  const targetScale = Math.min(availableW / contentW, availableH / contentH, 1.2);
  state.scale = clampScale(targetScale);

  const vW = viewport.value?.offsetWidth || window.innerWidth;
  const vH = viewport.value?.offsetHeight || window.innerHeight;

  state.panX = (vW - contentW * state.scale) / 2 - minX * state.scale;
  state.panY = (vH - contentH * state.scale) / 2 - minY * state.scale;
};

const resetView = () => {
  isFocusMode.value = false;
  cards.value.forEach((card) => {
    card.x = card.initX;
    card.y = card.initY;
  });
  state.panX = 0;
  state.panY = 0;
  state.scale = 1;
};

const toggleFullscreen = () => {
  emit('toggle-fullscreen');
};

const fetchGamesCount = async () => {
  try {
    const count = await getGamesCount();
    const el = document.getElementById('games-count');
    if (el) {
      el.textContent = count.toString();
    }
  } catch (error) {
    console.error('Failed to fetch games count:', error);
  }
};

onMounted(() => {
  edge.reqFrame = window.requestAnimationFrame(edgeScroll);
  fetchGamesCount();
});

onUnmounted(() => {
  window.cancelAnimationFrame(edge.reqFrame);
  if (drag.holdTimer) clearTimeout(drag.holdTimer);
});
</script>

<style scoped>
.dot-grid-layer {
  background-color: #121212;
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.15) 1.1px, transparent 1.1px);
  background-size: calc(var(--grid-size) * var(--zoom)) calc(var(--grid-size) * var(--zoom));
  background-position: calc(var(--pan-x) * 1px) calc(var(--pan-y) * 1px);
}

.canvas-content {
  /* We remove transform transition here to avoid lag on pan and drag. Reactivity is fast enough */
}

.about-card {
  min-height: 200px;
  transition:
    box-shadow 0.2s ease,
    border-color 0.2s ease,
    transform 0.4s cubic-bezier(0.16, 1, 0.3, 1),
    z-index 0s;
  cursor: grab;
}

.about-card.is-dragging {
  transition: none !important;
  cursor: grabbing;
}

.about-card.is-armed {
  box-shadow:
    0 0 0 1px rgba(61, 220, 192, 0.42),
    0 20px 44px rgba(0, 0, 0, 0.56);
}

.about-card.is-focus-mode {
  animation: breathe 3s ease-in-out infinite;
}

@keyframes breathe {
  0% {
    box-shadow: 0 0 10px rgba(0, 212, 255, 0.1);
  }
  50% {
    box-shadow: 0 0 25px rgba(0, 212, 255, 0.3);
  }
  100% {
    box-shadow: 0 0 10px rgba(0, 212, 255, 0.1);
  }
}
</style>
