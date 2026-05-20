<!--
  EmptyState.vue — Hiển thị khi không có dữ liệu.
-->
<script setup lang="ts">
defineProps<{ thongDiep?: string; moTa?: string }>()
</script>
<template>
  <div class="empty">
    <div class="empty__hinh">
      <svg width="80" height="80" viewBox="0 0 80 80" fill="none" style="color:var(--color-icon)">
        <circle cx="40" cy="40" r="40" fill="currentColor" opacity="0.08"/>
        <path d="M25 55V28a2 2 0 012-2h26a2 2 0 012 2v27L40 62 25 55z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/>
        <path d="M32 35h16M32 41h10" stroke="var(--color-icon-hover)" stroke-width="1.5" stroke-linecap="round"/>
        <circle cx="54" cy="54" r="10" fill="#ffffff" stroke="currentColor" stroke-width="1.5"/>
        <path d="M51 54h6M54 51v6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      </svg>
    </div>
    <p class="empty__thong-diep">{{ thongDiep ?? 'Không có dữ liệu' }}</p>
    <p v-if="moTa" class="empty__mo-ta">{{ moTa }}</p>
    <slot />
  </div>
</template>
<style scoped>
.empty { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 3rem 1rem; gap: 0.75rem; }
.empty__hinh { margin-bottom: 0.5rem; }
.empty__thong-diep { font-size: 1rem; font-weight: 600; color: var(--mau-chu); }
.empty__mo-ta { font-size: 0.875rem; color: var(--mau-chu-mo); text-align: center; }
</style>
