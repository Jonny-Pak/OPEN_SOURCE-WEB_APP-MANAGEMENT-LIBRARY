/**
 * authService.ts — Service gọi API xác thực từ backend Spring Boot.
 * Dùng fetch API gốc, KHÔNG dùng axios hay thư viện ngoài.
 * Base URL: http://localhost:8080/api/auth
 */
import type { DangNhapRequest, DangKyRequest, AuthResponse, ErrorResponse } from '@/types/auth'

/** Địa chỉ gốc của backend API */
const BASE_URL = 'http://localhost:8080/api/auth'

/**
 * Hàm tiện ích xử lý response từ fetch.
 * Tự động parse JSON và ném lỗi nếu response không OK.
 */
async function xuLyResponse<T>(response: Response): Promise<T> {
  const data = await response.json().catch(() => ({}))

  if (!response.ok) {
    // Ném lỗi cùng với error body từ backend
    throw data as ErrorResponse
  }

  return data as T
}

/**
 * Gọi API đăng nhập.
 * @param request - Dữ liệu đăng nhập (email + matKhau)
 * @returns AuthResponse chứa JWT token và thông tin người dùng
 * @throws ErrorResponse nếu thông tin sai hoặc tài khoản bị khóa
 */
export async function dangNhap(request: DangNhapRequest): Promise<AuthResponse> {
  const response = await fetch(`${BASE_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  return xuLyResponse<AuthResponse>(response)
}

/**
 * Gọi API đăng ký tài khoản mới.
 * @param request - Thông tin đăng ký
 * @returns AuthResponse chứa JWT token và thông tin người dùng mới
 * @throws ErrorResponse nếu email/SĐT đã tồn tại
 */
export async function dangKy(request: DangKyRequest): Promise<AuthResponse> {
  const response = await fetch(`${BASE_URL}/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  return xuLyResponse<AuthResponse>(response)
}

/** Gửi lại xác nhận email */
export async function guiLaiXacNhan(email: string): Promise<void> {
  const response = await fetch(`${BASE_URL}/gui-lai-xac-nhan`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email }),
  })
  return xuLyResponse<void>(response)
}

/** Quên mật khẩu */
export async function quenMatKhau(email: string): Promise<void> {
  const response = await fetch(`${BASE_URL}/quen-mat-khau`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email }),
  })
  return xuLyResponse<void>(response)
}

/** Đặt lại mật khẩu */
export async function datLaiMatKhau(
  email: string,
  otp: string,
  matKhauMoi: string,
): Promise<void> {
  const response = await fetch(`${BASE_URL}/dat-lai-mat-khau`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, otp, matKhauMoi }),
  })
  return xuLyResponse<void>(response)
}

/** Đổi mật khẩu */
export async function doiMatKhau(matKhauCu: string, matKhauMoi: string): Promise<void> {
  const token = localStorage.getItem('accessToken')
  const response = await fetch(`${BASE_URL}/doi-mat-khau`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ matKhauCu, matKhauMoi }),
  })

  return xuLyResponse<void>(response)
}