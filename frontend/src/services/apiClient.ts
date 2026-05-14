/**
 * apiClient.ts — Fetch wrapper dùng chung cho toàn bộ admin module.
 * Tự động đính kèm JWT token, xử lý lỗi 401 (hết phiên) và 403 (không có quyền).
 */
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const BASE_URL = 'http://localhost:8080'

/** Kiểu dữ liệu tuỳ chọn cho request */
interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'
  body?: unknown
  params?: Record<string, string | number | boolean | undefined>
  isFormData?: boolean  // Dùng cho upload file
}

/**
 * Xây dựng URL với query params.
 */
function xayDungUrl(path: string, params?: Record<string, string | number | boolean | undefined>): string {
  const url = new URL(`${BASE_URL}${path}`)
  if (params) {
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        url.searchParams.append(key, String(value))
      }
    })
  }
  return url.toString()
}

/**
 * Hàm gọi API chính.
 * Tự động thêm Authorization header, xử lý response chuẩn.
 */
async function goiApi<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const authStore = useAuthStore()

  // Chuẩn bị headers
  const headers: Record<string, string> = {}

  if (authStore.token) {
    headers['Authorization'] = `Bearer ${authStore.token}`
  }

  if (!options.isFormData) {
    headers['Content-Type'] = 'application/json'
  }

  // Chuẩn bị body
  let body: BodyInit | undefined
  if (options.body) {
    body = options.isFormData
      ? (options.body as FormData)
      : JSON.stringify(options.body)
  }

  const url = xayDungUrl(path, options.params)

  const response = await fetch(url, {
    method: options.method ?? 'GET',
    headers,
    body,
  })

  // Xử lý lỗi 401 — hết phiên đăng nhập
  if (response.status === 401) {
    authStore.xoaXacThuc()
    await router.push('/login')
    throw new Error('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại')
  }

  // Xử lý lỗi 403 — không có quyền
  if (response.status === 403) {
    throw new Error('Bạn không có quyền thực hiện thao tác này')
  }

  // Parse JSON
  const data = await response.json().catch(() => ({}))

  if (!response.ok) {
    throw data
  }

  return data as T
}

// ===== XUẤT CÁC HÀM HTTP TIỆN ÍCH =====

export const apiClient = {
  get: <T>(path: string, params?: Record<string, string | number | boolean | undefined>) =>
    goiApi<T>(path, { method: 'GET', params }),

  post: <T>(path: string, body?: unknown) =>
    goiApi<T>(path, { method: 'POST', body }),

  put: <T>(path: string, body?: unknown) =>
    goiApi<T>(path, { method: 'PUT', body }),

  delete: <T>(path: string) =>
    goiApi<T>(path, { method: 'DELETE' }),

  upload: <T>(path: string, formData: FormData) =>
    goiApi<T>(path, { method: 'POST', body: formData, isFormData: true }),
}

export default apiClient
