<!--
  ConfirmDialog.vue — Modal xác nhận trước khi thực hiện hành động nguy hiểm (Xóa, Khóa...).
-->
<script setup lang="ts">
defineProps<{
  dangMo: boolean
  tieuDe?: string
  thongDiep: string
  loai?: 'nguy-hiem' | 'canh-bao'
  dangXuLy?: boolean
}>()
const emit = defineEmits<{ xacNhan: []; huy: [] }>()
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="dangMo" class="overlay" @click.self="emit('huy')">
        <div class="confirm" role="alertdialog">
          <div class="confirm__bieu-tuong" :class="loai ?? 'nguy-hiem'">
            <i :class="loai === 'canh-bao' ? 'fas fa-triangle-exclamation' : 'fas fa-trash'"></i>
          </div>
          <h3 class="confirm__tieu-de">{{ tieuDe ?? 'Xác nhận thao tác' }}</h3>
          <p class="confirm__thong-diep">{{ thongDiep }}</p>
          <div class="confirm__hanh-dong">
            <button class="nut-huy" @click="emit('huy')" :disabled="dangXuLy">Hủy bỏ</button>
            <button
              class="nut-xac-nhan"
              :class="loai ?? 'nguy-hiem'"
              @click="emit('xacNhan')"
              :disabled="dangXuLy"
            >
              <span v-if="dangXuLy" class="spinner-nho"></span>
              {{ dangXuLy ? 'Đang xử lý...' : 'Xác nhận' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.overlay {
  position: fixed; inset: 0; z-index: 1100;
  background: rgba(17,24,39,0.32); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center; padding: 1rem;
}
.confirm {
  background: #ffffff; border: 1px solid #e5e7eb;
  border-radius: 16px; padding: 2rem; max-width: 400px; width: 100%;
  text-align: center; box-shadow: 0 24px 64px rgba(15,23,42,0.18);
}
.confirm__bieu-tuong { font-size: 3rem; margin-bottom: 1rem; }
.confirm__tieu-de { font-size: 1.15rem; font-weight: 700; color: var(--color-text); margin-bottom: 0.5rem; }
.confirm__thong-diep { color: #6b7280; font-size: 0.9rem; line-height: 1.6; margin-bottom: 1.5rem; }
.confirm__hanh-dong { display: flex; gap: 0.75rem; justify-content: center; }
.nut-huy {
  padding: 0.7rem 1.5rem; border-radius: 8px;
  background: #ffffff; border: 1px solid #d1d5db;
  color: #4b5563; cursor: pointer; font-family: inherit; font-size: 0.9rem;
  transition: all 0.2s;
}
.nut-huy:hover { background: rgba(6,182,212,0.08); color: var(--color-primary); border-color: var(--color-primary); }
.nut-xac-nhan {
  padding: 0.7rem 1.5rem; border-radius: 8px; border: none;
  color: white; cursor: pointer; font-family: inherit; font-size: 0.9rem;
  font-weight: 600; display: flex; align-items: center; gap: 0.4rem;
  transition: all 0.2s;
}
.nut-xac-nhan.nguy-hiem { background: #ef4444; }
.nut-xac-nhan.nguy-hiem:hover:not(:disabled) { background: #dc2626; }
.nut-xac-nhan.canh-bao { background: #f59e0b; }
.nut-xac-nhan:disabled { opacity: 0.6; cursor: not-allowed; }
.spinner-nho {
  display: inline-block; width: 14px; height: 14px;
  border: 2px solid rgba(255,255,255,0.3); border-top-color: white;
  border-radius: 50%; animation: spin 0.7s linear infinite;
}
.modal-enter-active { animation: modalVao 0.25s cubic-bezier(0.16,1,0.3,1); }
.modal-leave-active { animation: modalRa 0.2s ease forwards; }
@keyframes modalVao { from { opacity:0; transform:scale(0.9); } to { opacity:1; transform:scale(1); } }
@keyframes modalRa { from { opacity:1; } to { opacity:0; transform:scale(0.95); } }
</style>
