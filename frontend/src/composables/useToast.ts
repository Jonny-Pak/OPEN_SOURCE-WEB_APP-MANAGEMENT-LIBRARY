/**
 * useToast.ts — Composable hiển thị thông báo Toast.
 * Kết nối với toastStore để hiển thị thông báo toàn cục.
 */
import { useToastStore } from '@/stores/toast'

export function useToast() {
  const toastStore = useToastStore()

  return {
    thanhCong: (noiDung: string) => toastStore.them('success', noiDung),
    loi: (noiDung: string) => toastStore.them('error', noiDung),
    canhBao: (noiDung: string) => toastStore.them('warning', noiDung),
    thongBao: (noiDung: string) => toastStore.them('info', noiDung),
  }
}
