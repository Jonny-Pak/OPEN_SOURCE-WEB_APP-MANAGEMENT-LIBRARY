<!--
  ToastMessage.vue — Component hiển thị toast notification toàn cục.
  Đặt trong AdminLayout, tự render từ toastStore.
-->
<script setup lang="ts">
import { useToastStore } from '@/stores/toast'

const toastStore = useToastStore()

const BIEU_TUONG: Record<string, string> = {
  success: '✅',
  error: '❌',
  warning: '⚠️',
  info: 'ℹ️',
}
</script>

<template>
  <Teleport to="body">
    <div class="vung-toast" aria-live="polite">
      <TransitionGroup name="toast">
        <div
          v-for="toast in toastStore.danhSachToast"
          :key="toast.id"
          class="toast"
          :class="`toast--${toast.loai}`"
          @click="toastStore.xoa(toast.id)"
        >
          <span class="toast__bieu-tuong">{{ BIEU_TUONG[toast.loai] }}</span>
          <span class="toast__noi-dung">{{ toast.noiDung }}</span>
          <button class="toast__dong" aria-label="Đóng">✕</button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style scoped>
.vung-toast {
  position: fixed;
  top: 1.25rem;
  right: 1.25rem;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 0.625rem;
  max-width: 380px;
  width: 100%;
}

.toast {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.875rem 1rem;
  border-radius: 12px;
  cursor: pointer;
  border: 1px solid transparent;
  backdrop-filter: blur(16px);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.2);
}
.toast--success {
  background: rgba(81, 207, 102, 0.15);
  border-color: rgba(81, 207, 102, 0.3);
  color: #51cf66;
}
.toast--error {
  background: rgba(255, 107, 107, 0.15);
  border-color: rgba(255, 107, 107, 0.3);
  color: #ff6b6b;
}
.toast--warning {
  background: rgba(255, 212, 59, 0.15);
  border-color: rgba(255, 212, 59, 0.3);
  color: #ffd43b;
}
.toast--info {
  background: rgba(6, 182, 212, 0.15);
  border-color: rgba(6, 182, 212, 0.3);
  color: var(--color-primary);
}

.toast__bieu-tuong { font-size: 1.1rem; flex-shrink: 0; }
.toast__noi-dung { flex: 1; font-size: 0.875rem; font-weight: 500; color: var(--mau-chu); }
.toast__dong {
  background: none; border: none; color: var(--mau-chu-mo);
  cursor: pointer; font-size: 0.875rem; flex-shrink: 0;
}

/* Transition animations */
.toast-enter-active { animation: toastVao 0.35s cubic-bezier(0.16, 1, 0.3, 1); }
.toast-leave-active { animation: toastRa 0.25s ease forwards; }
@keyframes toastVao {
  from { opacity: 0; transform: translateX(100%) scale(0.9); }
  to { opacity: 1; transform: translateX(0) scale(1); }
}
@keyframes toastRa {
  from { opacity: 1; transform: translateX(0) scale(1); }
  to { opacity: 0; transform: translateX(100%) scale(0.9); }
}
</style>
