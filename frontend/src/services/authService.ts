/**
 * Service gọi API xác thực từ backend Spring Boot qua Axios apiClient.
 */
import apiClient from './apiClient'
import type { DangNhapRequest, DangKyRequest, AuthResponse } from '@/types/auth'

/**
 * Gọi API đăng nhập.
 */
export async function login(request: DangNhapRequest): Promise<AuthResponse> {
  return apiClient.post<AuthResponse>('/api/auth/login', request)
}

/**
 * Gọi API đăng ký tài khoản mới.
 */
export async function register(request: DangKyRequest): Promise<AuthResponse> {
  return apiClient.post<AuthResponse>('/api/auth/register', request)
}

/**
 * Gọi API đăng xuất.
 */
export async function logout(): Promise<void> {
  return apiClient.post<void>('/api/auth/logout')
}

/**
 * Lấy thông tin tài khoản hiện tại.
 */
export async function getMe(): Promise<any> {
  return apiClient.get<any>('/api/auth/me')
}

// ===== VIETNAMESE ALIASES & DEPRECATED UTILS FOR BACKWARD COMPATIBILITY =====
export const dangNhap = login
export const dangKy = register

export async function doiMatKhau(matKhauCu: string, matKhauMoi: string): Promise<void> {
  return apiClient.put('/api/v1/nguoi-dung/me/password', {
    matKhauCu,
    matKhauMoi,
    xacNhanMatKhau: matKhauMoi
  })
}

export async function quenMatKhau(email: string): Promise<any> {
  return apiClient.post('/api/auth/forgot-password', { email })
}

export async function datLaiMatKhau(email: string, token: string, matKhauMoi: string): Promise<any> {
  return apiClient.post('/api/auth/reset-password', {
    email,
    token,
    matKhauMoi,
    xacNhanMatKhau: matKhauMoi
  })
}

export async function guiLaiXacNhan(email: string): Promise<any> {
  // Mock successful resend since backend currently auto-activates or doesn't verify
  return Promise.resolve({ success: true, message: 'Đã gửi lại email xác nhận' })
}