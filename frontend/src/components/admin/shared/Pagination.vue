<!--
  Pagination.vue — Component phân trang tái sử dụng.
-->
<script setup lang="ts">
defineProps<{
  trangHienTai: number   // 1-indexed
  tongTrang: number
  tongPhanTu: number
  kichThuocTrang: number
}>()
const emit = defineEmits<{ 'doi-trang': [trang: number] }>()

function danhSachSoTrang(trangHienTai: number, tongTrang: number): (number | '...')[] {
  if (tongTrang <= 7) return Array.from({ length: tongTrang }, (_, i) => i + 1)
  const result: (number | '...')[] = [1]
  if (trangHienTai > 3) result.push('...')
  for (let i = Math.max(2, trangHienTai - 1); i <= Math.min(tongTrang - 1, trangHienTai + 1); i++) {
    result.push(i)
  }
  if (trangHienTai < tongTrang - 2) result.push('...')
  result.push(tongTrang)
  return result
}
</script>

<template>
  <div class="phan-trang" v-if="tongTrang > 1">
    <span class="phan-trang__thong-tin">
      Trang {{ trangHienTai }}/{{ tongTrang }} ({{ tongPhanTu }} kết quả)
    </span>
    <div class="phan-trang__nut-nhom">
      <button class="nut-trang" :disabled="trangHienTai <= 1" @click="emit('doi-trang', trangHienTai - 1)">‹</button>
      <template v-for="so in danhSachSoTrang(trangHienTai, tongTrang)" :key="so">
        <span v-if="so === '...'" class="dau-ba-cham">…</span>
        <button
          v-else
          class="nut-trang"
          :class="{ 'nut-trang--hien-tai': so === trangHienTai }"
          @click="emit('doi-trang', so)"
        >{{ so }}</button>
      </template>
      <button class="nut-trang" :disabled="trangHienTai >= tongTrang" @click="emit('doi-trang', trangHienTai + 1)">›</button>
    </div>
  </div>
</template>

<style scoped>
.phan-trang {
  display: flex; align-items: center; justify-content: space-between;
  padding: 1rem 0; flex-wrap: wrap; gap: 0.75rem;
}
.phan-trang__thong-tin { font-size: 0.825rem; color: var(--mau-chu-mo); }
.phan-trang__nut-nhom { display: flex; gap: 0.25rem; align-items: center; }
.nut-trang {
  width: 34px; height: 34px; border-radius: 8px;
  background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08);
  color: var(--mau-chu-mo); cursor: pointer; font-family: inherit;
  font-size: 0.875rem; transition: all 0.2s; display: flex; align-items: center; justify-content: center;
}
.nut-trang:hover:not(:disabled) { background: rgba(6,182,212,0.12); color: var(--mau-chinh); border-color: var(--mau-chinh); }
.nut-trang:disabled { opacity: 0.3; cursor: not-allowed; }
.nut-trang--hien-tai { background: var(--color-primary); border-color: transparent; color: white; font-weight: 700; }
.dau-ba-cham { color: var(--mau-chu-rat-mo); padding: 0 0.25rem; }
</style>
