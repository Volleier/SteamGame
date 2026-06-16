/**
 * Dashboard 画布交互逻辑 —— pan / zoom / drag / edge scroll。
 */
import { ref, reactive, computed, type Ref } from 'vue';
import type { DashboardCardConfig } from '@/features/dashboard/types';

const GRID_SIZE = 24;

export function useDashboardCanvas(cards: Ref<DashboardCardConfig[]>) {
  const viewport = ref<HTMLElement | null>(null);
  const canvas = ref<HTMLElement | null>(null);

  const state = reactive({ panX: 0, panY: 0, scale: 1 });
  const isFocusMode = ref(false);

  const pan = reactive({
    active: false, pointerId: -1, startX: 0, startY: 0,
    startPanX: 0, startPanY: 0, middlePressed: false,
  });

  const drag = reactive({
    cardId: null as number | null, pointerId: -1,
    startClientX: 0, startClientY: 0, startWorldX: 0, startWorldY: 0,
    startX: 0, startY: 0, dragging: false, mayDrag: false,
    armed: false, holdMs: 160, holdTimer: null as number | null,
    wasDragging: false,
  });

  const edge = reactive({
    active: false, mouseX: 0, mouseY: 0,
    threshold: 72, speed: 18, reqFrame: 0,
  });

  const snap = (v: number) => Math.round(v / GRID_SIZE) * GRID_SIZE;
  const clampScale = (v: number) => Math.min(2.4, Math.max(0.45, v));

  const gridStyle = computed(() => ({
    '--grid-size': `${GRID_SIZE}px`,
    '--pan-x': `${state.panX}`,
    '--pan-y': `${state.panY}`,
    '--zoom': `${state.scale}`,
  }));

  const contentStyle = computed(() => ({
    '--grid-size': `${GRID_SIZE}px`,
    transform: `translate3d(${state.panX}px, ${state.panY}px, 0) scale(${state.scale})`,
  }));

  function resetDragState() {
    if (drag.holdTimer) clearTimeout(drag.holdTimer);
    drag.cardId = null; drag.pointerId = -1;
    drag.dragging = false; drag.mayDrag = false; drag.armed = false;
  }

  function onViewportPointerDown(ev: PointerEvent) {
    if (ev.button === 1 || (ev.button === 0 && ev.altKey)) {
      ev.preventDefault();
      pan.active = true; pan.pointerId = ev.pointerId;
      pan.startX = ev.clientX; pan.startY = ev.clientY;
      pan.startPanX = state.panX; pan.startPanY = state.panY;
      pan.middlePressed = ev.button === 1;
      if (viewport.value) viewport.value.setPointerCapture(ev.pointerId);
    }
  }

  function onCardDown(ev: PointerEvent, card: DashboardCardConfig) {
    if (pan.active || ev.button !== 0) return;
    drag.cardId = card.id; drag.pointerId = ev.pointerId;
    drag.startClientX = ev.clientX; drag.startClientY = ev.clientY;
    drag.startWorldX = (ev.clientX - state.panX) / state.scale;
    drag.startWorldY = (ev.clientY - state.panY) / state.scale;
    drag.startX = card.x; drag.startY = card.y;
    drag.mayDrag = true; drag.armed = false;
    drag.holdTimer = window.setTimeout(() => {
      if (!drag.mayDrag || drag.dragging) return;
      drag.armed = true;
    }, drag.holdMs);
  }

  function onPointerMove(ev: PointerEvent) {
    edge.mouseX = ev.clientX; edge.mouseY = ev.clientY;
    if (pan.active && ev.pointerId === pan.pointerId) {
      state.panX = pan.startPanX + (ev.clientX - pan.startX);
      state.panY = pan.startPanY + (ev.clientY - pan.startY);
      return;
    }
    if (!drag.mayDrag) return;
    const dist = Math.hypot(ev.clientX - drag.startClientX, ev.clientY - drag.startClientY);
    if (!drag.armed) { if (dist > 6) { drag.wasDragging = false; resetDragState(); } return; }
    if (!drag.dragging && dist < 2) return;
    if (!drag.dragging) { drag.dragging = true; drag.wasDragging = true; isFocusMode.value = false; }
    const worldX = (ev.clientX - state.panX) / state.scale;
    const worldY = (ev.clientY - state.panY) / state.scale;
    const card = cards.value.find(c => c.id === drag.cardId);
    if (card) { card.x = snap(drag.startX + (worldX - drag.startWorldX)); card.y = snap(drag.startY + (worldY - drag.startWorldY)); }
  }

  function onPointerUp() {
    if (pan.active) { pan.active = false; pan.middlePressed = false; }
    if (drag.cardId !== null) {
      setTimeout(() => { drag.wasDragging = false; }, 50);
      resetDragState();
    }
  }

  function onWheel(ev: WheelEvent) {
    if (pan.middlePressed) return;
    ev.preventDefault();
    const beforeX = (ev.clientX - state.panX) / state.scale;
    const beforeY = (ev.clientY - state.panY) / state.scale;
    state.scale = clampScale(state.scale * Math.exp(-ev.deltaY * 0.00035));
    state.panX = ev.clientX - beforeX * state.scale;
    state.panY = ev.clientY - beforeY * state.scale;
  }

  function edgeScroll() {
    if (drag.dragging) {
      edge.active = true;
    } else {
      edge.active = false;
    }
    if (edge.active && viewport.value) {
      const rect = viewport.value.getBoundingClientRect();
      const localX = edge.mouseX - rect.left;
      const localY = edge.mouseY - rect.top;
      let dx = 0, dy = 0;
      if (localX < edge.threshold) dx = edge.speed;
      if (localX > rect.width - edge.threshold) dx = -edge.speed;
      if (localY < edge.threshold) dy = edge.speed;
      if (localY > rect.height - edge.threshold) dy = -edge.speed;
      if (dx !== 0 || dy !== 0) { state.panX += dx; state.panY += dy; }
    }
    edge.reqFrame = window.requestAnimationFrame(edgeScroll);
  }

  function startEdgeScroll() {
    edge.reqFrame = window.requestAnimationFrame(edgeScroll);
  }

  function stopEdgeScroll() {
    window.cancelAnimationFrame(edge.reqFrame);
  }

  function resetView() { state.panX = 0; state.panY = 0; state.scale = 1; isFocusMode.value = false; }
  function fitView() { isFocusMode.value = false; }

  return {
    viewport, canvas, state, gridStyle, contentStyle,
    isFocusMode, drag, pan, edge, GRID_SIZE,
    onViewportPointerDown, onCardDown, onPointerMove, onPointerUp, onWheel,
    resetView, fitView, clampScale, snap, resetDragState,
    startEdgeScroll, stopEdgeScroll,
  };
}
