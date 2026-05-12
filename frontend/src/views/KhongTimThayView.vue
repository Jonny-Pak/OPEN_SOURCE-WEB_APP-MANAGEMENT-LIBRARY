<!--
  KhongTimThayView.vue — Trang 404: không tìm thấy.
-->
<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="err-page">
    <div class="err-decor err-decor-1"></div>
    <div class="err-decor err-decor-2"></div>
    <div class="err-content">
      <div class="err-code">404</div>
      <div class="err-icon">🔍</div>
      <h1 class="err-title">Không tìm thấy trang</h1>
      <p class="err-desc">Trang bạn đang tìm kiếm không tồn tại hoặc đã bị di chuyển.</p>
      <div class="err-actions">
        <button class="err-btn-primary" @click="router.push({ name: 'trang-chu' })">
          🏠 Về trang chủ
        </button>
        <button class="err-btn-ghost" @click="router.back()">
          ← Quay lại
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.err-page {
  min-height: 70vh; display: flex; align-items: center; justify-content: center;
  padding: 4rem 1.5rem; position: relative; overflow: hidden; text-align: center;
}
.err-decor { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.12; pointer-events: none; }
.err-decor-1 { width: 400px; height: 400px; background: var(--mau-chinh); top: -100px; right: -100px; }
.err-decor-2 { width: 350px; height: 350px; background: var(--mau-phu); bottom: -80px; left: -80px; }
.err-content { position: relative; z-index: 1; animation: fadeInUp 0.6s cubic-bezier(0.16,1,0.3,1); }
.err-code { font-size: 7rem; font-weight: 900; color: var(--color-primary); line-height: 1; margin-bottom: 0.5rem; }
.err-icon { font-size: 3rem; margin-bottom: 1rem; }
.err-title { font-size: 1.75rem; font-weight: 700; color: var(--mau-chu); margin-bottom: 0.75rem; }
.err-desc { color: var(--mau-chu-mo); font-size: 1rem; margin-bottom: 2rem; max-width: 400px; margin-left: auto; margin-right: auto; }
.err-actions { display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap; }
.err-btn-primary { padding: 0.75rem 1.75rem; background: var(--color-primary); border: none; border-radius: 50px; color: #fff; font-size: 0.95rem; font-weight: 700; cursor: pointer; font-family: inherit; transition: all 0.2s; }
.err-btn-primary:hover { transform: translateY(-2px); background: var(--color-primary-hover); }
.err-btn-ghost { padding: 0.75rem 1.75rem; background: none; border: 1px solid rgba(255,255,255,0.2); border-radius: 50px; color: var(--mau-chu); font-size: 0.95rem; font-weight: 500; cursor: pointer; font-family: inherit; transition: all 0.2s; }
.err-btn-ghost:hover { border-color: var(--mau-chinh); color: var(--mau-chinh); }
</style>
