/**
 * toast.ts — Pinia Store quản lý Toast Notifications toàn cục.
 * Tự động xóa thông báo sau 3 giây.
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'

export type LoaiToast = 'success' | 'error' | 'warning' | 'info'

export interface ToastItem {
  id: number
  loai: LoaiToast
  noiDung: string
}

export const useToastStore = defineStore('toast', () => {
  const danhSachToast = ref<ToastItem[]>([])
  let soIdTiepTheo = 0

  /**
   * Thêm thông báo mới, tự xóa sau 3 giây.
   */
  function them(loai: LoaiToast, noiDung: string): void {
    const id = soIdTiepTheo++
    danhSachToast.value.push({ id, loai, noiDung })

    // Tự động xóa sau 3 giây
    setTimeout(() => xoa(id), 3000)
  }

  /** Xóa thông báo theo ID */
  function xoa(id: number): void {
    const viTri = danhSachToast.value.findIndex((t) => t.id === id)
    if (viTri !== -1) danhSachToast.value.splice(viTri, 1)
  }

  return { danhSachToast, them, xoa }
})
