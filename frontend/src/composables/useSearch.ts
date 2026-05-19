/**
 * useSearch.ts — Composable tìm kiếm có debounce và đồng bộ URL query.
 */
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

export function useSearch(treSoMs = 300) {
  const route = useRoute()
  const router = useRouter()

  // Read search term from URL query initially
  const tuKhoaTimKiem = ref((route?.query?.search as string) || '')
  const tuKhoaDebounced = ref(tuKhoaTimKiem.value)

  let boDem: ReturnType<typeof setTimeout> | null = null

  watch(tuKhoaTimKiem, (giaTriMoi) => {
    if (boDem) clearTimeout(boDem)
    boDem = setTimeout(() => {
      tuKhoaDebounced.value = giaTriMoi
    }, treSoMs)
  })

  // Synchronize with URL query params
  watch(tuKhoaDebounced, (val) => {
    if (router && route) {
      router.replace({
        query: {
          ...route.query,
          search: val || undefined
        }
      })
    }
  })

  /** Đặt lại ô tìm kiếm */
  function xoaTimKiem(): void {
    if (boDem) clearTimeout(boDem)
    tuKhoaTimKiem.value = ''
    tuKhoaDebounced.value = ''
  }

  return { tuKhoaTimKiem, tuKhoaDebounced, xoaTimKiem }
}

export default useSearch
