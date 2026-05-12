<!--
  KhongCoQuyenView.vue — Trang 403, hiển thị khi không đủ quyền truy cập.
-->
<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>
<template>
  <div class="trang-loi">
    <div class="hop-loi">
      <div class="so-loi">403</div>
      <div class="loi-icon">🚫</div>
      <h1 class="tieu-de">Không có quyền truy cập</h1>
      <p class="mo-ta">Bạn không có quyền truy cập trang này. Chỉ Quản trị viên mới có thể vào đây.</p>
      <div class="nhom-nut">
        <button class="nut-ve" @click="router.back()">← Quay lại</button>
        <button class="nut-trang-chu" @click="router.push({ name: 'trang-chu' })">🏠 Trang chủ</button>
      </div>
    </div>
  </div>
</template>
<style scoped>
.trang-loi { min-height: 70vh; display: flex; align-items: center; justify-content: center; padding: 2rem; }
.hop-loi { text-align: center; max-width: 480px; animation: fadeInUp 0.6s cubic-bezier(0.16,1,0.3,1); }
.so-loi { font-size: 7rem; font-weight: 900; color: var(--color-primary); line-height: 1; margin-bottom: 0.5rem; }
.loi-icon { font-size: 2.5rem; margin-bottom: 1rem; }
.tieu-de { font-size: 1.75rem; font-weight: 700; margin-bottom: 1rem; color: var(--mau-chu); }
.mo-ta { color: var(--mau-chu-mo); margin-bottom: 2rem; line-height: 1.7; }
.nhom-nut { display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap; }
.nut-ve { padding: 0.75rem 1.5rem; border-radius: 50px; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1); color: var(--mau-chu); cursor: pointer; font-family: inherit; font-size: 0.9rem; transition: all 0.2s; }
.nut-ve:hover { border-color: var(--mau-chinh); color: var(--mau-chinh); }
.nut-trang-chu { padding: 0.75rem 1.5rem; border-radius: 50px; background: var(--color-primary); border: none; color: white; cursor: pointer; font-family: inherit; font-size: 0.9rem; font-weight: 600; transition: all 0.2s; }
.nut-trang-chu:hover { transform: translateY(-2px); background: var(--color-primary-hover); }
</style>
