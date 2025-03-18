<script setup xmlns="">
import { ref } from 'vue';
const msg = ref('Begin here');
import TabletVue from './components/Tablet.vue';
import SayHello from './components/SayHello.vue';
</script>

<template>
  <h1>{{ msg }}</h1>
  <br>
  <SayHello />
  <br>
  <TabletVue />
</template>

<style scoped>
h1 {
  color: #42b983;
}
</style>
