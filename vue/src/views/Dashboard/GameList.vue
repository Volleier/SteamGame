<template>
  <div class="container mx-auto px-4 py-8">
    <div class="overflow-x-auto rounded-lg shadow">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">游戏ID</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">游戏名称</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">游戏时长</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="game in games" :key="game.app_id" class="hover:bg-gray-100">
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
              {{ game.app_id }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ game.app_name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ game.app_time }} 小时</td>
          </tr>
          <!-- 没有数据时显示 -->
          <tr v-if="games.length === 0">
            <td colspan="3" class="px-6 py-4 text-center text-sm text-gray-500">暂无游戏数据</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GameList',
  data() {
    return {
      games: [],
    };
  },
  mounted() {
    // 从后端获取已保存的 owned games
    fetch('/api/ownedgames/list')
      .then((res) => res.json())
      .then((data) => {
        // 后端返回字段: appid, name, playtimeForever
        this.games = data.map((g) => ({
          app_id: g.appid,
          app_name: g.name,
          // 转换分钟为小时并保留整数小时
          app_time: Math.round((g.playtimeForever || 0) / 60),
        }));
      })
      .catch((err) => {
        console.error('Failed to load games', err);
      });
  },
};
</script>
