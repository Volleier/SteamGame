<template>
  <CyberGlassCard compact>
    <div class="about-content">
      <h1 class="about-title">About SteamGame</h1>
      <p class="about-description">SteamGame 是一个 Steam 游戏管理工具，帮助您查看和管理您的 Steam 游戏库。</p>
      <div class="about-info">
        <div class="info-item">
          <span class="info-label">版本</span>
          <span class="info-value">0.1.0</span>
        </div>
        <div class="info-item">
          <span class="info-label">开发者</span>
          <span class="info-value">Volleier</span>
        </div>
      </div>
      <router-link to="/" class="back-btn">
        <div class="back-slider"></div>
        <div class="back-text-wrapper">
          <span>返回首页</span>
        </div>
      </router-link>
    </div>
  </CyberGlassCard>
</template>

<script setup lang="ts">
import CyberGlassCard from '@/components/CyberGlassCard.vue';
</script>

<style scoped lang="scss">
@use '@/assets/styles/default' as *;

.about-content {
  text-align: center;
  padding: 2rem 1rem;
  max-width: 500px;
}

.about-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, #00d4ff, #2196f3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.about-description {
  color: rgba(255, 255, 255, 0.7);
  font-size: 1rem;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.about-info {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-bottom: 2.5rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

.info-label {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.4);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.info-value {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
}

// 复刻 credential-btn 风格，但适合小号文字
.back-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  width: 10rem;
  height: 3rem;
  background-color: #ffffff;
  color: #000000;
  font-weight: 800;
  font-size: 1.1rem;
  letter-spacing: 0.1em;
  transform: skewX(-10deg);
  border-bottom: 4px solid #ff00ff;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  border-left: none;
  border-right: none;
  border-top: none;
  cursor: pointer;
  text-decoration: none;

  .back-slider {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    width: 25%;
    background-color: #00d4ff;
    transition: all 0.5s cubic-bezier(0.25, 1, 0.5, 1);
  }

  .back-text-wrapper {
    position: absolute;
    right: 0;
    width: 75%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 10;
    pointer-events: none;

    span {
      transform: skewX(10deg);
      text-transform: uppercase;
      white-space: nowrap;
    }
  }

  &:hover {
    .back-slider {
      width: 100%;
      background-color: #2196f3;
    }
  }
}
</style>
