<!--
  SkeletonLoader.vue — Loading skeleton với shimmer animation.
  Props: rows (số dòng giả), height (chiều cao mỗi dòng)
-->
<script setup lang="ts">
withDefaults(defineProps<{ rows?: number; height?: string }>(), {
  rows: 5,
  height: '48px',
})
</script>

<template>
  <div class="skeleton-wrap">
    <div
      v-for="i in rows"
      :key="i"
      class="skeleton-item"
      :style="{ height }"
    ></div>
  </div>
</template>

<style scoped>
.skeleton-wrap { display: flex; flex-direction: column; gap: 0.5rem; }
.skeleton-item {
  border-radius: 8px;
  background: linear-gradient(90deg,
    rgba(255,255,255,0.04) 25%,
    rgba(255,255,255,0.09) 50%,
    rgba(255,255,255,0.04) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}
@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
