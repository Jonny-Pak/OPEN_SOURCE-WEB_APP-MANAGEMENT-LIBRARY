<!--
  StatusBadge.vue — Badge trạng thái màu sắc theo semantic.
-->
<script setup lang="ts">
defineProps<{ nhanHien: string; loai: 'xanh' | 'do' | 'vang' | 'xam' | 'xanh-duong' }>()
</script>

<template>
  <span class="badge" :class="`badge--${loai}`">{{ nhanHien }}</span>
</template>

<style scoped>
.badge {
  display: inline-flex; align-items: center;
  padding: 0.25rem 0.65rem;
  border-radius: 20px; font-size: 0.775rem; font-weight: 600;
  white-space: nowrap;
}
.badge--xanh { background: rgba(81,207,102,0.15); color: #51cf66; border: 1px solid rgba(81,207,102,0.3); }
.badge--do { background: rgba(255,107,107,0.15); color: #ff6b6b; border: 1px solid rgba(255,107,107,0.3); }
.badge--vang { background: rgba(255,212,59,0.15); color: #ffd43b; border: 1px solid rgba(255,212,59,0.3); }
.badge--xam { background: rgba(148,163,184,0.1); color: #94a3b8; border: 1px solid rgba(148,163,184,0.2); }
.badge--xanh-duong { background: rgba(6,182,212,0.14); color: var(--color-primary); border: 1px solid rgba(6,182,212,0.35); }
</style>
