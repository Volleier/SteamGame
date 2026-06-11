<template>
  <footer class="footer-container">
    <div class="footer-content">
      <span class="footer-text">SteamGame &copy; {{ currentYear }}</span>
      <span class="footer-separator">|</span>
      <span class="footer-text">Powered by Volleier</span>
    </div>
  </footer>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'TheFooter',
  data() {
    return {
      currentYear: new Date().getFullYear(),
    };
  },
});
</script>

<style scoped>
.footer-container {
  width: 100%;
  padding: 0.8rem 1.5rem;
  background-color: rgba(0, 0, 0, 0.3);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  text-align: center;
}

.footer-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.footer-text {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.45);
}

.footer-separator {
  color: rgba(255, 255, 255, 0.2);
}
</style>
