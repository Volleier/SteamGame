<template>
  <article
    class="info-card about-card absolute border border-white/10 rounded-lg p-6 bg-black/60 backdrop-blur-md shadow-2xl text-white transition-all duration-500 flex flex-col"
    :class="[card.className, {
      'is-dragging': isDragging,
      'is-armed': isArmed,
      'is-focus-mode': isFocusMode,
    }]"
    :style="{
      '--card-grid-w': card.w,
      width: `calc(${card.w} * var(--grid-size))`,
      transform: `translate3d(${card.x}px, ${card.y}px, 0)`,
      zIndex: isDragging ? 50 : 1,
    }"
    :data-id="card.id"
    @pointerdown.stop="$emit('cardDown', $event, card)"
    @click="$emit('cardClick', $event, card)"
  >
    <div :class="['card-accent absolute left-0 top-6 w-1 h-10', accentClass]"></div>
    <header class="card-header mb-4 pl-4 pointer-events-none select-none">
      <span class="tag text-[#00d4ff] text-xs font-bold tracking-widest uppercase">{{ card.tag }}</span>
      <h2 class="text-xl font-extrabold tracking-wider uppercase mt-1">{{ card.heading }}</h2>
    </header>
    <div class="card-body pl-4 text-gray-300 text-sm leading-relaxed flex-1 select-none pointer-events-none">
      <slot />
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { DashboardCardConfig } from '@/features/dashboard/types';

const props = defineProps<{
  card: DashboardCardConfig;
  isDragging?: boolean;
  isArmed?: boolean;
  isFocusMode?: boolean;
}>();

defineEmits(['cardDown', 'cardClick']);

const accentClass = computed(() => {
  if (props.card.accent === 'cyan') return 'bg-[#00d4ff]';
  if (props.card.accent === 'magenta') return 'bg-[#ff00ff]';
  return 'bg-[#00ffd5]';
});
</script>
