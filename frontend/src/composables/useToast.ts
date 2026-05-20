/**
 * useToast.ts — Composable hiển thị thông báo Toast.
 * Kết nối với toastStore để hiển thị thông báo toàn cục.
 */
import { useToastStore } from '@/stores/toast'

export function useToast() {
  const toastStore = useToastStore()

  return {
    // Existing Vietnamese methods
    thanhCong: (noiDung: string) => toastStore.them('success', noiDung),
    loi: (noiDung: string) => toastStore.them('error', noiDung),
    canhBao: (noiDung: string) => toastStore.them('warning', noiDung),
    thongBao: (noiDung: string) => toastStore.them('info', noiDung),

    // English Interface shorthand methods
    success: (message: string) => toastStore.showSuccess(message),
    error: (message: string) => toastStore.showError(message),
    warning: (message: string) => toastStore.showWarning(message),
    info: (message: string) => toastStore.showInfo(message)
  }
}

export default useToast
