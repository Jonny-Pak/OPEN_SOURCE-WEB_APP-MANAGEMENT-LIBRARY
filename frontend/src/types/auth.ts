/**
 * Định nghĩa các kiểu dữ liệu (TypeScript interfaces) cho module xác thực.
 * Tương ứng 1-1 với DTOs của backend Spring Boot.
 */

// ===== REQUEST TYPES =====

/** Dữ liệu gửi lên khi đăng nhập */
export interface DangNhapRequest {
  email: string
  matKhau: string
}

/** Dữ liệu gửi lên khi đăng ký tài khoản mới */
export interface DangKyRequest {
  hoDem: string
  ten: string
  email: string
  matKhau: string
  soDienThoai: string
}

// ===== RESPONSE TYPES =====

/** Thông tin cơ bản của người dùng (lưu trong store) */
export interface ThongTinNguoiDung {
  maNguoiDung: string
  hoDem: string
  ten: string
  email: string
  vaiTro: 'DOC_GIA' | 'THU_THU' | 'QUAN_TRI_VIEN'
  trangThaiTaiKhoan?: 'chua_kich_hoat' | 'da_kich_hoat' | 'bi_khoa'
}

/** Response trả về sau khi đăng nhập / đăng ký thành công */
export interface AuthResponse {
  maNguoiDung: string
  accessToken: string
  tokenType: string
  hoDem: string
  ten: string
  email: string
  vaiTro: 'DOC_GIA' | 'THU_THU' | 'QUAN_TRI_VIEN'
  isDefaultPassword?: boolean
  mustChangePassword?: boolean
  trangThaiTaiKhoan?: 'chua_kich_hoat' | 'da_kich_hoat' | 'bi_khoa'
}

/** Format lỗi chuẩn từ backend */
export interface ErrorResponse {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
}

// ===== ERROR CODES =====

/** Các mã lỗi backend có thể trả về */
export type MaLoi =
  | 'EMAIL_DA_TON_TAI'
  | 'SO_DIEN_THOAI_DA_TON_TAI'
  | 'DANG_NHAP_THAT_BAI'
  | 'TAI_KHOAN_BI_KHOA'
  | 'INVALID_INPUT'
  | 'SERVER_ERROR'
