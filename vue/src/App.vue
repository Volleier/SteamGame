<script setup>
import { ref } from 'vue';
const msg = ref('Begin here');
import Home from './components/Home.vue';
import Dashboard from './Dashboard.vue';
import TabletVue from './components/Tablet.vue';
import SayHello from './components/SayHello.vue';
import Login from './components/Login.vue'; 
</script>

<template>
  <Login />
</template>

<style scoped>
h1 {
  color: #42b983;
}
</style>