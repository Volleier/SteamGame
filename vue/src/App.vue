<script setup>
import { ref } from 'vue';
const msg = ref('Hello Vue 3 + Vite');
import Apivue from './Api.vue';
</script>

<template>
  <h1>{{ msg }}</h1>
  <br>
  <Apivue />
</template>

<style scoped>
h1 {
  color: #42b983;
}
</style>
