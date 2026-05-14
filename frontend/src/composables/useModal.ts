/**
 * useModal.ts — Composable quản lý trạng thái Modal.
 * Hỗ trợ cả mode Thêm mới và Sửa (có dữ liệu sẵn).
 */
import { ref } from 'vue'

export function useModal<T = unknown>() {
  /** Modal có đang mở không */
  const dangMo = ref(false)

  /** Item đang được sửa (null = đang thêm mới) */
  const itemDangSua = ref<T | null>(null)

  /** Mở modal thêm mới */
  function moModalThem(): void {
    itemDangSua.value = null
    dangMo.value = true
  }

  /** Mở modal sửa với dữ liệu có sẵn */
  function moModalSua(item: T): void {
    itemDangSua.value = item
    dangMo.value = true
  }

  /** Đóng modal và xóa trạng thái */
  function dongModal(): void {
    dangMo.value = false
    // Delay xóa item để tránh nhấp nháy animation
    setTimeout(() => {
      itemDangSua.value = null
    }, 300)
  }

  const dangThem = () => itemDangSua.value === null

  return {
    dangMo,
    itemDangSua,
    dangThem,
    moModalThem,
    moModalSua,
    dongModal,
  }
}
