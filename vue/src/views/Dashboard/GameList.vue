<template>
    <div class="container mx-auto px-4 py-8">
        <div class="overflow-x-auto rounded-lg shadow">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th scope="col"
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            游戏ID
                        </th>
                        <th scope="col"
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            游戏名称
                        </th>
                        <th scope="col"
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            游戏时长
                        </th>
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
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {{ game.app_time }} 小时
                        </td>
                    </tr>
                    <!-- 没有数据时显示 -->
                    <tr v-if="games.length === 0">
                        <td colspan="3" class="px-6 py-4 text-center text-sm text-gray-500">
                            暂无游戏数据
                        </td>
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
            games: [
                { app_id: 105600, app_name: "Terraria", app_time: 120 },
                { app_id: 413150, app_name: "Stardew Valley", app_time: 85 },
                { app_id: 588650, app_name: "Dead Cells", app_time: 42 },
                { app_id: 219740, app_name: "Don't Starve", app_time: 67 },
                { app_id: 646570, app_name: "Slay the Spire", app_time: 93 }
            ]
        }
    }
}
</script>