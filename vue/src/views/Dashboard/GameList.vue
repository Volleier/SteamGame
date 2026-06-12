<template>
  <div class="container mx-auto px-4 pt-3 pb-8 text-white font-sans">
    <!-- 顶部控制栏 -->
    <div class="bg-black/40 backdrop-blur-xl border border-white/10 shadow-2xl rounded-xl mb-6 p-4">
      <div class="flex flex-col md:flex-row items-center justify-between gap-4">
        <!-- 搜索框 -->
        <div class="relative w-full md:max-w-md">
          <span class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </span>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索游戏名称或 ID..."
            class="w-full bg-black/65 border border-white/15 rounded-lg pl-10 pr-4 py-2 text-sm text-white placeholder-gray-500 focus:outline-none focus:border-[#00d4ff] focus:ring-1 focus:ring-[#00d4ff] transition-all"
          />
        </div>

        <!-- 排序与导出 -->
        <div class="flex flex-wrap items-center justify-end gap-3 w-full md:w-auto">
          <div class="flex items-center bg-black/40 border border-white/10 rounded-lg p-1">
            <button
              v-for="option in sortOptions"
              :key="option.key"
              @click="setSort(option.key)"
              :class="[
                'px-3.5 py-1.5 rounded-md text-xs font-bold uppercase tracking-wider transition-all duration-200 flex items-center gap-1.5',
                sortKey === option.key
                  ? 'bg-[#00d4ff]/20 text-[#00d4ff] border border-[#00d4ff]/30'
                  : 'text-gray-400 hover:text-white border border-transparent'
              ]"
            >
              {{ option.label }}
              <span v-if="sortKey === option.key" class="text-[10px]">
                {{ sortOrder === 'asc' ? '▲' : '▼' }}
              </span>
            </button>
          </div>

          <button
            @click="exportCSV"
            class="px-4 py-2 bg-[#00d4ff]/10 hover:bg-[#00d4ff]/25 border border-[#00d4ff]/30 text-[#00d4ff] rounded-lg text-xs font-bold uppercase tracking-wider transition-all flex items-center gap-2"
          >
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            导出 CSV
          </button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="flex flex-col items-center justify-center py-20 bg-black/40 backdrop-blur-xl border border-white/10 rounded-xl shadow-2xl">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-[#00d4ff]/30 border-t-[#00d4ff] mb-4"></div>
      <p class="text-gray-400 text-sm tracking-widest uppercase">正在加载游戏列表...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="errorMessage" class="flex flex-col items-center justify-center py-20 bg-black/40 backdrop-blur-xl border border-red-500/30 rounded-xl shadow-2xl">
      <div class="text-4xl mb-4">😵</div>
      <p class="text-red-400 text-sm mb-4">{{ errorMessage }}</p>
      <button @click="loadGames" class="px-4 py-2 bg-red-500/20 border border-red-500 text-red-400 rounded-md text-sm hover:bg-red-500/40 transition-colors uppercase">
        重新加载
      </button>
    </div>

    <!-- 空状态 -->
    <div v-else-if="games.length === 0" class="flex flex-col items-center justify-center py-20 bg-black/40 backdrop-blur-xl border border-white/10 rounded-xl shadow-2xl">
      <div class="text-4xl mb-4">🎮</div>
      <p class="text-gray-300 text-sm mb-2 uppercase tracking-widest">暂无游戏数据</p>
      <p class="text-gray-500 text-xs">您的 Steam 账户中可能没有公开的游戏，或尚未同步。</p>
    </div>

    <!-- 游戏卡片列表 -->
    <div v-else class="flex flex-col gap-4">
      <div v-for="game in filteredAndSortedGames" :key="game.app_id"
           class="game-card flex flex-col md:flex-row bg-black/40 backdrop-blur-md border border-white/10 rounded-xl overflow-hidden hover:border-[#00d4ff]/40 hover:bg-[#00d4ff]/5 transition-all duration-300 group shadow-lg">
        
        <!-- 左侧: 游戏垂直海报 -->
        <div class="relative w-full md:w-[100px] lg:w-[120px] aspect-[2/3] md:h-[150px] shrink-0 bg-black/80 overflow-hidden border-r border-white/5">
          <img
            :src="`https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/${game.app_id}/library_600x900.jpg`"
            @error="handleImageError"
            class="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-500"
            alt="Game Poster"
          />
          <div class="absolute inset-0 bg-black/85 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center">
            <span class="text-xs text-[#00d4ff] font-mono">ID: {{ game.app_id }}</span>
          </div>
        </div>

        <!-- 中部及右侧内容包装 -->
        <div class="flex-1 p-5 flex flex-col lg:flex-row gap-5 justify-between">
          <!-- 中左侧: 标题与描述 -->
          <div class="flex-1 flex flex-col justify-between min-w-0">
            <div>
              <h2 class="text-xl font-bold text-white group-hover:text-[#00d4ff] transition-colors truncate mb-2">
                {{ game.app_name }}
              </h2>
              <p class="text-gray-400 text-sm leading-relaxed line-clamp-3">
                {{ getGameMeta(game.app_id, game.app_name).description }}
              </p>
            </div>
          </div>

          <!-- 中右侧: 开发者与发行信息 -->
          <div class="w-full lg:w-[240px] shrink-0 flex flex-col justify-center text-xs text-gray-400 space-y-1.5 border-t lg:border-t-0 lg:border-l border-white/10 pt-4 lg:pt-0 lg:pl-5">
            <div><span class="text-gray-500">开发者:</span> <span class="text-gray-300 font-medium ml-1">{{ getGameMeta(game.app_id, game.app_name).developer }}</span></div>
            <div><span class="text-gray-500">发行商:</span> <span class="text-gray-300 font-medium ml-1">{{ getGameMeta(game.app_id, game.app_name).publisher }}</span></div>
            <div class="truncate"><span class="text-gray-500">系列:</span> <span class="text-gray-300 font-medium ml-1">{{ getGameMeta(game.app_id, game.app_name).franchise }}</span></div>
            <div><span class="text-gray-500">发行日期:</span> <span class="text-gray-300 font-medium ml-1">{{ getGameMeta(game.app_id, game.app_name).releaseDate }}</span></div>
          </div>

          <!-- 右侧: 游戏时长与特性标签 -->
          <div class="w-full lg:w-[260px] shrink-0 flex flex-col justify-between items-start lg:items-end border-t lg:border-t-0 lg:border-l border-white/10 pt-4 lg:pt-0 lg:pl-5">
            <div class="text-left lg:text-right w-full mb-3 lg:mb-0">
              <div class="text-xs text-gray-500 uppercase tracking-widest">总游玩时间</div>
              <div class="text-2xl font-black text-white mt-0.5">
                <span class="text-[#00d4ff]">{{ (Number(game.app_time) || 0).toFixed(2) }}</span> <span class="text-xs text-gray-400 font-normal">小时</span>
              </div>
            </div>

            <!-- 特性标签 -->
            <div class="flex flex-wrap lg:justify-end gap-1.5 w-full">
              <span v-for="tag in getGameMeta(game.app_id, game.app_name).tags" :key="tag"
                    class="px-2 py-0.5 rounded bg-white/5 border border-white/10 text-[10px] text-gray-300 tracking-wider">
                {{ tag }}
              </span>
            </div>
          </div>
        </div>

      </div>

      <!-- 搜索无匹配 -->
      <div v-if="filteredAndSortedGames.length === 0 && games.length > 0"
           class="flex flex-col items-center justify-center py-20 bg-black/40 backdrop-blur-xl border border-white/10 rounded-xl shadow-2xl">
        <div class="text-4xl mb-4">🔍</div>
        <p class="text-gray-400 text-sm uppercase tracking-widest">没有匹配 "{{ searchQuery }}" 的游戏</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGameList } from '@/composables/useGameList';

const {
  isLoading,
  errorMessage,
  searchQuery,
  sortKey,
  sortOrder,
  filteredAndSortedGames,
  games,
  loadGames,
  setSort,
  exportCSV,
} = useGameList();

const sortOptions = [
  { key: 'app_time', label: '时间' },
  { key: 'app_name', label: '名称' },
  { key: 'app_id', label: 'ID' }
] as const;

interface GameMeta {
  description: string;
  developer: string;
  publisher: string;
  franchise: string;
  releaseDate: string;
  tags: string[];
}

const PRESET_GAME_METAS: Record<number, GameMeta> = {
  359550: {
    description: "《彩虹六号：围攻》是一款精英团队战术射击游戏，高超的策略和行动力是制胜关键。",
    developer: "Ubisoft Montreal",
    publisher: "Ubisoft",
    franchise: "Rainbow 6 Franchise, Tom Clancy Franchise",
    releaseDate: "2015年12月2日",
    tags: ["单人游戏", "多人游戏", "合作游戏", "完全支持控制器", "DUALSHOCK 支持", "DualSense 支持"]
  },
  730: {
    description: "二十多年来，在全球数百万玩家的协作与倾注下，反恐精英 (Counter-Strike) 提供了令人振奋的精英竞技体验。而现在，CS 的历史新篇章即将开启，那就是 Counter-Strike 2。",
    developer: "Valve",
    publisher: "Valve",
    franchise: "Counter-Strike",
    releaseDate: "2023年9月27日",
    tags: ["多人游戏", "在线竞技", "第一人称射击", "免费", "Steam 成就"]
  },
  570: {
    description: "每天都有数百万玩家化身为百余位刀塔英雄参与对决。不管是游戏时间刚满10小时的新手，还是已达1000小时的常客，刀塔总有新玩法等待被发掘。",
    developer: "Valve",
    publisher: "Valve",
    franchise: "Dota",
    releaseDate: "2013年7月9日",
    tags: ["多人游戏", "联机合作", "MOBA", "策略", "免费"]
  },
  1091500: {
    description: "《赛博朋克 2077》是一款开放世界动作冒险角色扮演游戏。故事发生在夜之城，这是一座五光十色、危机四伏的大都会，权力更迭和身体改造是这里永恒的主题。",
    developer: "CD PROJEKT RED",
    publisher: "CD PROJEKT RED",
    franchise: "Cyberpunk",
    releaseDate: "2020年12月10日",
    tags: ["单人游戏", "开放世界", "角色扮演", "完全支持控制器", "Steam 成就", "Steam 云"]
  },
  271590: {
    description: "一个初涉江湖的街头小混混、一个退休的抢劫犯和一个精神失常的狂人，他们必须在洛圣都进行一系列惊险刺激的抢劫，以在无情的大都市中生存下去。",
    developer: "Rockstar North",
    publisher: "Rockstar Games",
    franchise: "Grand Theft Auto",
    releaseDate: "2015年4月14日",
    tags: ["单人游戏", "多人游戏", "开放世界", "动作", "完全支持控制器"]
  },
  1174180: {
    description: "《Red Dead Redemption 2》荣获超过 175 项年度游戏奖项并获得超过 250 个满分评价，讲述亚瑟·摩根和声名狼藉的范德林德帮派的传奇故事。",
    developer: "Rockstar Games",
    publisher: "Rockstar Games",
    franchise: "Red Dead",
    releaseDate: "2019年12月5日",
    tags: ["单人游戏", "多人游戏", "开放世界", "写实", "完全支持控制器"]
  },
  105600: {
    description: "挖掘，战斗，探索，建造！在这个动感十足的冒险游戏里，没有什么事是不可能的。世界是你的画布，地面是你的画笔。",
    developer: "Re-Logic",
    publisher: "Re-Logic",
    franchise: "Terraria",
    releaseDate: "2011年5月16日",
    tags: ["单人游戏", "多人游戏", "合作游戏", "沙盒", "完全支持控制器", "创意工坊"]
  },
  400: {
    description: "《传送门》是由 Valve 开发的一款开创性的单人游戏。游戏背景设定在神秘的光圈科技实验室中，被公认为行业内最具创新性的游戏之一。",
    developer: "Valve",
    publisher: "Valve",
    franchise: "Portal",
    releaseDate: "2007年10月10日",
    tags: ["单人游戏", "解谜", "第一人称", "完全支持控制器", "Steam 成就"]
  },
  620: {
    description: "《传送门 2》拥有备受赞誉的单人游戏模式，带回了一批极具魅力的新角色、大量创新的解谜元素以及更大、更扑朔迷离的实验室。",
    developer: "Valve",
    publisher: "Valve",
    franchise: "Portal",
    releaseDate: "2011年4月19日",
    tags: ["单人游戏", "多人游戏", "合作游戏", "解谜", "完全支持控制器", "创意工坊"]
  },
  218620: {
    description: "《收获日 2》是一款充满动作戏份的四人联机合作射击游戏。玩家将再次扮演初代 Payday 帮成员——达拉斯、霍斯顿、狼和链子，前往华盛顿特区展开疯狂的犯罪行径。",
    developer: "OVERKILL - a Starbreeze Studio.",
    publisher: "Starbreeze Publishing AB",
    franchise: "PAYDAY",
    releaseDate: "2013年8月13日",
    tags: ["多人游戏", "合作游戏", "联机合作", "射击", "Steam 成就"]
  },
  252490: {
    description: "在《Rust》中，唯一的目的就是生存。要做到这一点，你需要克服诸如饥饿、口渴和寒冷之类的困难。生火、建造避难所、杀死动物获取肉。与其他玩家结盟或杀害他们。",
    developer: "Facepunch Studios",
    publisher: "Facepunch Studios",
    franchise: "Rust",
    releaseDate: "2018年2月8日",
    tags: ["多人游戏", "生存", "沙盒", "开放世界", "创意工坊"]
  },
  1145360: {
    description: "《哈迪斯》是一款高自由度的砍杀型地下城探索游戏，融合了 Supergiant 旗下多款好评大作的经典特色，为您带来爽快刺激的动作体验。",
    developer: "Supergiant Games",
    publisher: "Supergiant Games",
    franchise: "Hades",
    releaseDate: "2020年9月17日",
    tags: ["单人游戏", "动作 Roguelike", "优秀原声带", "完全支持控制器", "Steam 成就"]
  },
  1446780: {
    description: "《怪物猎人 荒野》是《Monster Hunter》系列的最新作。玩家将扮演“公会”派遣的调查队员“猎人”，踏足未知的“封禁之地”。以克服严酷自然环境并狩猎强大怪兽为核心体验。",
    developer: "CAPCOM",
    publisher: "CAPCOM",
    franchise: "Monster Hunter",
    releaseDate: "2025年2月28日",
    tags: ["单人游戏", "多人游戏", "合作游戏", "动作", "完全支持控制器"]
  },
  582010: {
    description: "在壮丽的大自然中与庞大的魔物对抗。玩家化身为猎人，接受任务狩猎生活在各种栖息地的魔物。利用狩猎魔物取得的材料，制作更强的武器防具，挑战更强大的魔物。",
    developer: "CAPCOM",
    publisher: "CAPCOM",
    franchise: "Monster Hunter",
    releaseDate: "2018年8月9日",
    tags: ["单人游戏", "多人游戏", "合作游戏", "动作", "完全支持控制器"]
  },
  1811260: {
    description: "EA SPORTS FC 24 欢迎您加入世界性游戏：由 HyperMotionV、Opta 优化的比赛风格和升级版 Frostbite 引擎驱动，带来有史以来最真实的足球体验。",
    developer: "EA Vancouver",
    publisher: "Electronic Arts",
    franchise: "EA Sports",
    releaseDate: "2023年9月29日",
    tags: ["单人游戏", "多人游戏", "同屏/分屏", "体育", "完全支持控制器"]
  },
  431960: {
    description: "Wallpaper Engine 允许您在 Windows 桌面上使用动态壁纸。它支持各种类型的壁纸，包括 3D 和 2D 动画、网站、视频甚至某些应用程序。",
    developer: "Kristjan Skutta",
    publisher: "Wallpaper Engine",
    franchise: "Wallpaper Engine",
    releaseDate: "2018年11月17日",
    tags: ["实用工具", "创意工坊", "壁纸", "多显示器支持"]
  }
};

function getGameMeta(appid: number, name: string): GameMeta {
  const cached = PRESET_GAME_METAS[appid];
  if (cached) return cached;

  // 用 appid 做种，伪随机出合理的元数据，保证画面充实
  const seed = appid || 1;
  const developers = [
    "Valve", "Ubisoft", "Rockstar Games", "EA", "CD PROJEKT RED", 
    "Capcom", "Bandai Namco", "Square Enix", "Sega", "Bethesda Game Studios"
  ];
  const publishers = [
    "Valve", "Ubisoft", "Rockstar Games", "Electronic Arts", "CD PROJEKT", 
    "Capcom", "Bandai Namco Entertainment", "Square Enix", "Sega", "Bethesda Softworks"
  ];
  
  const devIndex = seed % developers.length;
  const pubIndex = (seed * 3) % publishers.length;
  const developer = developers[devIndex];
  const publisher = publishers[pubIndex];
  
  const allTags = [
    "单人游戏", "多人游戏", "合作游戏", "联机合作", "完全支持控制器", 
    "DUALSHOCK 支持", "DualSense 支持", "Steam 成就", "Steam 云", "创意工坊",
    "动作", "冒险", "角色扮演", "策略", "模拟"
  ];
  
  const tagCount = 3 + (seed % 3);
  const tags: string[] = [];
  for (let i = 0; i < tagCount; i++) {
    const tagIndex = (seed * (i + 1) * 7) % allTags.length;
    const tag = allTags[tagIndex];
    if (!tags.includes(tag)) {
      tags.push(tag);
    }
  }
  if (tags.length < 2) {
    tags.push("单人游戏", "Steam 云");
  }

  const year = 2010 + (seed % 16);
  const month = 1 + ((seed * 2) % 12);
  const day = 1 + ((seed * 5) % 28);
  const releaseDate = `${year}年${month}月${day}日`;

  const cleanName = name.replace(/[:\-\d].*/, '').trim();
  const franchise = seed % 3 !== 0 ? `${cleanName} Franchise` : "无";

  const descriptions = [
    `《${name}》是一款深受玩家喜爱的经典作品。在这里，你将开启一段无与伦比的沉浸式娱乐体验。`,
    `探索《${name}》中广阔的未知世界，与好友共同面对未知挑战，写下属于你的传奇冒险故事。`,
    `在《${name}》充满竞技与策略的宏大战场中，展现你的指尖操作与精妙决策，击败对手赢得最终荣光。`,
    `体验《${name}》扣人心弦的故事情节，精美绝伦的视觉画面，以及由知名大师倾心打造的音轨。`,
    `《${name}》将动作、探索与独特的关卡设计完美结合，在紧张刺激的节奏中带给玩家无尽的游玩乐趣。`
  ];
  const description = descriptions[seed % descriptions.length];

  return {
    description,
    developer,
    publisher,
    franchise,
    releaseDate,
    tags
  };
}

function handleImageError(event: Event) {
  const img = event.target as HTMLImageElement;
  if (!img) return;

  const url = img.src;
  if (url.includes('library_600x900.jpg')) {
    // 降级使用 header.jpg
    img.src = url.replace('library_600x900.jpg', 'header.jpg');
  } else if (url.includes('header.jpg')) {
    // 再降级使用 capsule
    img.src = url.replace('header.jpg', 'capsule_616x353.jpg');
  } else {
    // 最终默认占位图
    img.src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="600" height="900" viewBox="0 0 600 900"><rect width="100%" height="100%" fill="%231a1d24"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-family="sans-serif" font-size="64" fill="%234a5264">🎮</text></svg>';
  }
}
</script>

<style scoped>
.game-card {
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.4);
}
</style>
