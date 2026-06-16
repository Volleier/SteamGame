<template>
  <div class="container mx-auto px-4 pt-3 pb-8 text-white font-sans max-w-4xl">
    <div class="bg-black/40 backdrop-blur-xl border border-white/10 shadow-2xl rounded-xl p-6 relative overflow-hidden">
      <div class="card-accent absolute left-0 top-6 w-1 h-10 bg-[#00d4ff]"></div>
      <div class="pl-4 flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div class="flex-1">
          <h3 class="text-xl font-extrabold text-white mb-2 flex items-center gap-2.5 uppercase tracking-wider">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="w-6 h-6 text-[#00d4ff] shrink-0">
              <path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"/>
            </svg>
            同步游戏数据库 / SYNC GAME DATABASE
          </h3>
          <p class="text-gray-400 text-sm leading-relaxed mt-2">
            触发从 Steam 平台的数据强制同步。系统将调用 Steam API 获取您当前账号所拥有的全部游戏数据与游戏时长，并写入本地数据库以供展示。
          </p>
          <div class="flex flex-wrap items-center gap-4 mt-4 text-xs text-gray-500">
            <span class="flex items-center gap-1.5"><span class="w-2 h-2 rounded-full bg-green-500 shadow-[0_0_8px_rgba(34,197,94,0.5)]"></span>系统状态: 正常连接</span>
            <span v-if="lastSyncTime" class="flex items-center gap-1.5"><span class="w-2 h-2 rounded-full bg-[#00d4ff] shadow-[0_0_8px_rgba(0,212,255,0.5)]"></span>上次同步: {{ lastSyncTime }}</span>
          </div>
        </div>
        <div class="w-full md:w-auto shrink-0 self-stretch md:self-center flex items-center">
          <button @click="handleSync" :disabled="isSyncing"
            class="w-full md:w-auto px-6 py-3 bg-[#00d4ff]/10 hover:bg-[#00d4ff]/25 disabled:bg-gray-500/10 disabled:text-gray-500 disabled:border-gray-500/20 border border-[#00d4ff]/30 text-[#00d4ff] hover:text-white rounded-lg text-sm font-bold uppercase tracking-wider transition-all duration-300 flex items-center justify-center gap-2 shadow-[0_0_15px_rgba(0,212,255,0.1)] hover:shadow-[0_0_20px_rgba(0,212,255,0.25)] cursor-pointer disabled:cursor-not-allowed">
            <svg v-if="isSyncing" class="animate-spin h-5 w-5 text-[#00d4ff] shrink-0" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="w-5 h-5 text-[#00d4ff] shrink-0"><path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"/></svg>
            {{ isSyncing ? '正在同步中...' : '同步游戏库' }}
          </button>
        </div>
      </div>
      <transition name="fade">
        <div v-if="syncStatus" class="mt-6 ml-4 p-4 rounded-lg border text-sm backdrop-blur-md transition-all duration-300" :class="statusClass">
          <div class="flex items-center gap-3"><span class="text-lg">{{ syncStatusIcon }}</span><div><p class="font-bold">{{ syncStatusTitle }}</p><p class="text-xs text-gray-400 mt-1">{{ syncStatusMsg }}</p></div></div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGameSync } from '@/features/dashboard/composables/useGameSync';
const { isSyncing, syncStatus, syncCount, syncErrorMsg, lastSyncTime, statusClass, syncStatusIcon, syncStatusTitle, syncStatusMsg, handleSync } = useGameSync();
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
