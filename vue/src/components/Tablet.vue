<template>
    <div>
        <table>
            <thead>
                <tr>
                    <th>游戏ID</th>
                    <th>游戏名称</th>
                    <th>游戏时长（小时）</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="game in games" :key="game.id">
                    <td>{{ game.id }}</td>
                    <td>{{ game.gameName }}</td>
                    <td>{{ game.gamePlayTime }}</td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            games: [],
        };
    },
    created() {
        this.fetchGames();
    },
    methods: {
        async fetchGames() {
            try {
                const response = await axios.get('http://localhost:8080/games');
                this.games = response.data;
            } catch (error) {
                console.error('Error fetching games:', error);
            }
        },
    },
};
</script>

<style scoped>
table {
    width: 100%;
    border-collapse: collapse;
}

th,
td {
    border: 1px solid #ddd;
    padding: 8px;
}

th {
    background-color: #000000;
    text-align: left;
}
</style>