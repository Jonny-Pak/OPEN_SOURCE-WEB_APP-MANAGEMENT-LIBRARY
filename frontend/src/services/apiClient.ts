import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// Create Axios instance with baseURL from env VITE_API_BASE_URL
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
})

// Request Interceptor: Attach JWT token as Authorization: Bearer {token}
apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response Interceptor: Automatically extract data, and handle 401/403 redirects
apiClient.interceptors.response.use(
  (response) => {
    return response.data
  },
  async (error) => {
    const authStore = useAuthStore()

    if (error.response) {
      const { status } = error.response

      // 401 Redirects to /login
      if (status === 401) {
        const url = error.config?.url || ''
        if (url.includes('/login')) {
           return Promise.reject(error.response.data || new Error('Tài khoản hoặc mật khẩu không chính xác'))
        }

        authStore.xoaXacThuc()
        
        // Save redirect path if possible
        const currentPath = router.currentRoute.value.fullPath
        if (currentPath && currentPath !== '/login') {
          await router.push(`/login?redirect=${encodeURIComponent(currentPath)}`)
        } else {
          // If we are already on the login page and it wasn't a login request, we don't necessarily need to push to /login again, but it's safe.
          if (currentPath !== '/login') {
             await router.push('/login')
          }
        }
        
        return Promise.reject(new Error('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại'))
      }

      // 403 Redirects to /403 (KhongCoQuyen)
      if (status === 403) {
        await router.push('/403')
        return Promise.reject(new Error('Bạn không có quyền thực hiện thao tác này'))
      }

      return Promise.reject(error.response.data || error.message)
    }

    return Promise.reject(error)
  }
)

// Add upload custom method for compatibility with the existing service usages
;(apiClient as any).upload = function <T>(path: string, formData: FormData): Promise<T> {
  return apiClient.post<T>(path, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }) as unknown as Promise<T>
}

import type { AxiosRequestConfig } from 'axios'

interface CustomAxiosInstance {
  defaults: any;
  interceptors: any;
  get<T = any, R = T, D = any>(url: string, config?: AxiosRequestConfig<D>): Promise<R>;
  delete<T = any, R = T, D = any>(url: string, config?: AxiosRequestConfig<D>): Promise<R>;
  head<T = any, R = T, D = any>(url: string, config?: AxiosRequestConfig<D>): Promise<R>;
  options<T = any, R = T, D = any>(url: string, config?: AxiosRequestConfig<D>): Promise<R>;
  post<T = any, R = T, D = any>(url: string, data?: any, config?: AxiosRequestConfig<D>): Promise<R>;
  put<T = any, R = T, D = any>(url: string, data?: any, config?: AxiosRequestConfig<D>): Promise<R>;
  patch<T = any, R = T, D = any>(url: string, data?: any, config?: AxiosRequestConfig<D>): Promise<R>;
  postForm<T = any, R = T, D = any>(url: string, data?: any, config?: AxiosRequestConfig<D>): Promise<R>;
  putForm<T = any, R = T, D = any>(url: string, data?: any, config?: AxiosRequestConfig<D>): Promise<R>;
  patchForm<T = any, R = T, D = any>(url: string, data?: any, config?: AxiosRequestConfig<D>): Promise<R>;
  upload<T = any>(path: string, formData: FormData): Promise<T>;
  request<T = any, R = T, D = any>(config: AxiosRequestConfig<D>): Promise<R>;
}

export default (apiClient as unknown as CustomAxiosInstance)
