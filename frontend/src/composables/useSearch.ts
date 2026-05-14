/**
 * useSearch.ts — Composable tìm kiếm có debounce.
 * Tránh gọi API liên tục khi người dùng đang gõ.
 */
import { ref, watch } from 'vue'

export function useSearch(treSoMs = 300) {
  /** Giá trị search tức thời (ràng buộc với input) */
  const tuKhoaTimKiem = ref('')

  /** Giá trị search sau debounce (dùng để gọi API) */
  const tuKhoaDebounced = ref('')

  let boDem: ReturnType<typeof setTimeout> | null = null

  watch(tuKhoaTimKiem, (giaTriMoi) => {
    if (boDem) clearTimeout(boDem)
    boDem = setTimeout(() => {
      tuKhoaDebounced.value = giaTriMoi
    }, treSoMs)
  })

  /** Đặt lại ô tìm kiếm */
  function xoaTimKiem(): void {
    if (boDem) clearTimeout(boDem)
    tuKhoaTimKiem.value = ''
    tuKhoaDebounced.value = ''
  }

  return { tuKhoaTimKiem, tuKhoaDebounced, xoaTimKiem }
}
