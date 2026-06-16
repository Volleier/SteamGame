<template>
  <div
    class="drawer-panel fixed right-0 top-0 h-full w-[640px] bg-[#121212]/90 backdrop-blur-xl border-l border-white/10 z-[101] shadow-2xl transition-transform duration-300 flex flex-col"
    :class="{ 'translate-x-0': isOpen, 'translate-x-full': !isOpen }"
  >
    <div class="flex-1 overflow-y-auto p-6">
      <div class="flex flex-col gap-4">
        <template v-for="card in hiddenCards" :key="card.id">
          <div
            @pointerdown.stop="$emit('drawerCardDown', $event, card)"
            class="relative flex items-center p-4 rounded-lg border border-white/10 bg-black/40 backdrop-blur-md shadow-lg text-white transition-all cursor-grab hover:bg-black/60 hover:border-white/30"
          >
            <div :class="['absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 rounded-r', card.accent === 'cyan' ? 'bg-[#00d4ff]' : card.accent === 'magenta' ? 'bg-[#ff00ff]' : 'bg-[#00ffd5]']"></div>
            <div class="pl-4 min-w-0 flex-1 pointer-events-none select-none">
              <p class="text-xs text-gray-500 font-mono tracking-wider mb-1">{{ card.tag }}</p>
              <h4 class="text-sm font-bold text-white truncate uppercase tracking-widest">{{ card.heading }}</h4>
            </div>
          </div>
        </template>
      </div>
    </div>
    <div class="p-6 border-t border-white/10 bg-black/20">
      <button @click="$emit('saveLayout')" class="w-full py-3 bg-gradient-to-r from-[#00d4ff] to-[#00ffd5] text-black font-black uppercase text-xs tracking-widest rounded-lg shadow-[0_0_15px_rgba(0,212,255,0.3)] hover:shadow-[0_0_25px_rgba(0,212,255,0.5)] transition-all hover:scale-[1.02] active:scale-[0.98]">
        保存当前布局
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { DashboardCardConfig } from '@/features/dashboard/types';

const props = defineProps<{ cards: DashboardCardConfig[]; isOpen: boolean }>();
defineEmits(['drawerCardDown', 'saveLayout']);

const hiddenCards = computed(() => props.cards.filter(c => c.visible === false));
</script>
