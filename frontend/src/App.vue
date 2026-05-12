<!--
  App.vue — Component gốc của ứng dụng.
  Chứa RouterView để hiển thị các trang theo route hiện tại.
  Import font Inter từ Google Fonts và CSS biến toàn cục.
-->
<script setup lang="ts"></script>

<template>
  <RouterView />
</template>

<style>
/* ===== IMPORT GOOGLE FONT ===== */
@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap');

/* ===== CSS VARIABLES - HỆ THỐNG MÀU SẮC ===== */
:root {
  /* Primary — Xanh trời (đồng bộ) */
  --color-primary: #06b6d4;
  --color-primary-light: #38e6f0;
  --color-primary-hover: #0891b2;

  /* Icon colors */
  --color-icon: #06b6d4;
  --color-icon-hover: #0891b2;
  --color-icon-muted: #94a3b8;

  /* Success — Xanh lá */
  --color-success: #16a34a;
  --color-success-light: #dcfce7;

  /* Warning — Cam */
  --color-warning: #d97706;
  --color-warning-light: #fef3c7;

  /* Danger — Đỏ */
  --color-danger: #dc2626;
  --color-danger-light: #fee2e2;

  /* Info — Xanh dương nhạt */
  --color-info: #0284c7;
  --color-info-light: #e0f2fe;

  /* Neutral — Xám */
  --color-surface: #ffffff;
  --color-bg: #f1f5f9;
  --color-border: #e2e8f0;
  --color-text-primary: #0f172a;
  --color-text-secondary: #64748b;
  --color-text-muted: #94a3b8;

  /* Sidebar */
  --sidebar-bg: #1e3a5f;
  --sidebar-text: #cbd5e1;
  --sidebar-active-bg: #2563eb;
  --sidebar-active-text: #ffffff;
  --sidebar-hover-bg: #1e40af;

  /* Legacy aliases — giữ để không break existing code */
  --color-background: #ffffff;
  --mau-nen: var(--color-background);
  --mau-nen-thu-cap: #f8fafc;
  --mau-nen-thu-ba: #f1f5f9;
  --mau-chinh: var(--color-primary);
  --mau-phu: var(--color-primary-hover);
  /* Use flat colors instead of holographic gradients */
  --gradient-chinh: var(--color-primary);
  --gradient-nen: var(--color-background);
  --mau-chu: var(--color-text-primary);
  --mau-chu-mo: #6b7280;
  --mau-chu-rat-mo: #9ca3af;
  --glass-nen: #ffffff;
  --glass-vien: #e5e7eb;
  --glass-blur: none;

  --glow-chinh: none;
  --glow-phu: none;

  /* Lỗi */
  --mau-loi: #dc2626;
  --mau-thanh-cong: #16a34a;
  --mau-canh-bao: #d97706;

  /* Khoảng cách */
  --radius-nho: 8px;
  --radius-vua: 12px;
  --radius-lon: 20px;
  --radius-tron: 50px;
}

/* ===== RESET & BASE ===== */
*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html {
  font-size: 16px;
  scroll-behavior: smooth;
}

body {
  font-family: 'Manrope', -apple-system, BlinkMacSystemFont, sans-serif;
  background: var(--gradient-nen);
  min-height: 100vh;
  color: var(--mau-chu);
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
}

/* ===== ANIMATIONS TOÀN CỤC ===== */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-8px);
    max-height: 0;
  }
  to {
    opacity: 1;
    transform: translateY(0);
    max-height: 60px;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

/* ===== SCROLLBAR TÙY CHỈNH ===== */
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-track {
  background: #f3f4f6;
}
::-webkit-scrollbar-thumb {
  background: var(--mau-chinh);
  border-radius: 3px;
}

/* ===== INPUT AUTOFILL FIX ===== */
input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus {
  -webkit-text-fill-color: var(--mau-chu);
  -webkit-box-shadow: 0 0 0px 1000px #ffffff inset;
  transition: background-color 5000s ease-in-out 0s;
}
</style>
