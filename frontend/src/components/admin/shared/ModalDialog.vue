<!--
  ModalDialog.vue — Modal wrapper tái sử dụng với backdrop blur và animation.
  Dùng slot để chứa nội dung tùy ý bên trong.
-->
<script setup lang="ts">
defineProps<{
  tieuDe: string
  dangMo: boolean
  chieuRong?: string  // Ví dụ: '600px', '800px'
}>()

const emit = defineEmits<{ dong: [] }>()
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="dangMo" class="overlay" @click.self="emit('dong')">
        <div class="modal" :style="{ maxWidth: chieuRong ?? '560px' }" role="dialog" aria-modal="true">
          <!-- Header -->
          <div class="modal__header">
            <h2 class="modal__tieu-de">{{ tieuDe }}</h2>
            <button class="modal__nut-dong" @click="emit('dong')" aria-label="Đóng">✕</button>
          </div>
          <!-- Body -->
          <div class="modal__body">
            <slot />
          </div>
          <!-- Footer (optional) -->
          <div v-if="$slots.footer" class="modal__footer">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.overlay {
  position: fixed; inset: 0; z-index: 1000;
  background: rgba(17, 24, 39, 0.32);
  backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
  padding: 1rem;
}
.modal {
  width: 100%;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  box-shadow: 0 24px 64px rgba(15, 23, 42, 0.18);
  overflow: hidden;
}
.modal__header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}
.modal__tieu-de { font-size: 1.1rem; font-weight: 700; color: var(--color-text); }
.modal__nut-dong {
  background: none; border: none; color: #6b7280;
  font-size: 1.1rem; cursor: pointer; padding: 0.25rem 0.5rem;
  border-radius: 6px; transition: all 0.2s;
}
.modal__nut-dong:hover { background: rgba(6,182,212,0.1); color: var(--color-primary); }
.modal__body { padding: 1.5rem; }
.modal__footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #e5e7eb;
  display: flex; justify-content: flex-end; gap: 0.75rem;
}
/* Animations */
.modal-enter-active { animation: modalVao 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.modal-leave-active { animation: modalRa 0.2s ease forwards; }
@keyframes modalVao {
  from { opacity: 0; transform: scale(0.92) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
@keyframes modalRa {
  from { opacity: 1; transform: scale(1); }
  to { opacity: 0; transform: scale(0.95); }
}
</style>
