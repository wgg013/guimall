<template>
  <img :src="resolvedSrc" :alt="alt" :class="imgClass" />
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  src: { type: String, default: '' },
  seed: { type: String, default: 'Farmer' },
  alt: { type: String, default: 'farmer-avatar' },
  imgClass: { type: String, default: '' },
})

const resolvedSrc = computed(() => {
  return props.src || `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(props.seed || 'Farmer')}`
})
</script>
