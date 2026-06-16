<template>
  <img
    :src="currentSrc"
    @error="onError"
    :alt="alt"
    :class="imgClass"
    loading="lazy"
  />
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import {
  getSteamPosterUrl,
  resolveNextImageFallback,
} from '@/utils/steamImages';

const props = withDefaults(defineProps<{
  appid: number;
  type?: 'poster' | 'header' | 'capsule';
  alt?: string;
  imgClass?: string;
}>(), {
  type: 'poster',
  alt: 'Game Poster',
  imgClass: 'w-full h-full object-cover',
});

const urlFns: Record<string, (appid: number) => string> = {
  poster: getSteamPosterUrl,
  header: (id) => getSteamPosterUrl(id).replace('library_600x900.jpg', 'header.jpg'),
  capsule: (id) => getSteamPosterUrl(id).replace('library_600x900.jpg', 'capsule_616x353.jpg'),
};

const currentSrc = ref(getImageUrl());

function getImageUrl(): string {
  return (urlFns[props.type] || urlFns.poster)(props.appid);
}

function onError(event: Event) {
  const img = event.target as HTMLImageElement;
  if (!img) return;
  img.src = resolveNextImageFallback(img.src);
}

watch(() => props.appid, () => {
  currentSrc.value = getImageUrl();
});
</script>
