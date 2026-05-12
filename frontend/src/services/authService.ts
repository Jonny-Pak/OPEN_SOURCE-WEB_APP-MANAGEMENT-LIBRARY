// /**
//  * Service gọi API xác thực từ backend Spring Boot.
//  * Dùng fetch API gốc, KHÔNG dùng axios hay thư viện ngoài.
//  * Base URL: http://localhost:8080
//  */
// import type { DangNhapRequest, DangKyRequest, AuthResponse, ErrorResponse } from '@/types/auth'

// /** Địa chỉ gốc của backend API */
// const BASE_URL = 'http://localhost:8080/api/auth'

// /**
//  * Hàm tiện ích xử lý response từ fetch.
//  * Tự động parse JSON và ném lỗi nếu response không OK.
//  */
// async function xuLyResponse<T>(response: Response): Promise<T> {
//   const data = await response.json()

//   if (!response.ok) {
//     // Ném lỗi cùng với error body từ backend
//     throw data as ErrorResponse
//   }

//   return data as T
// }

// /**
//  * Gọi API đăng nhập.
//  * @param request - Dữ liệu đăng nhập (email + matKhau)
//  * @returns AuthResponse chứa JWT token và thông tin người dùng
//  * @throws ErrorResponse nếu thông tin sai hoặc tài khoản bị khóa
//  */
// export async function dangNhap(request: DangNhapRequest): Promise<AuthResponse> {
//   const response = await fetch(`${BASE_URL}/login`, {
//     method: 'POST',
//     headers: { 'Content-Type': 'application/json' },
//     body: JSON.stringify(request),
//   })

//   return xuLyResponse<AuthResponse>(response)
// }

// /**
//  * Gọi API đăng ký tài khoản mới.
//  * @param request - Thông tin đăng ký
//  * @returns AuthResponse chứa JWT token và thông tin người dùng mới
//  * @throws ErrorResponse nếu email/SĐT đã tồn tại
//  */
// export async function dangKy(request: DangKyRequest): Promise<AuthResponse> {
//   const response = await fetch(`${BASE_URL}/register`, {
//     method: 'POST',
//     headers: { 'Content-Type': 'application/json' },
//     body: JSON.stringify(request),
//   })

//   return xuLyResponse<AuthResponse>(response)
// }

// /**
//  * TODO: Tích hợp API POST /api/auth/gui-lai-xac-nhan khi backend sẵn sàng.
//  * Hiện tại dùng mock để giả lập.
//  */
// export async function guiLaiXacNhan(_email: string): Promise<void> {
//   // Mock: giả lập delay 1.5 giây
//   await new Promise((resolve) => setTimeout(resolve, 1500))
//   // TODO: Thay bằng fetch thực tế:
//   // const response = await fetch(`${BASE_URL}/gui-lai-xac-nhan`, {
//   //   method: 'POST',
//   //   headers: { 'Content-Type': 'application/json' },
//   //   body: JSON.stringify({ email }),
//   // })
//   // return xuLyResponse<void>(response)
// }

// /**
//  * TODO: Tích hợp API POST /api/auth/quen-mat-khau khi backend sẵn sàng.
//  * Hiện tại dùng mock để giả lập.
//  */
// export async function quenMatKhau(_email: string): Promise<void> {
//   await new Promise((resolve) => setTimeout(resolve, 1500))
//   // TODO: Thay bằng fetch thực tế:
//   // const response = await fetch(`${BASE_URL}/quen-mat-khau`, {
//   //   method: 'POST',
//   //   headers: { 'Content-Type': 'application/json' },
//   //   body: JSON.stringify({ email }),
//   // })
//   // return xuLyResponse<void>(response)
// }

// /**
//  * TODO: Tích hợp API POST /api/auth/dat-lai-mat-khau khi backend sẵn sàng.
//  * Hiện tại dùng mock để giả lập.
//  */
// export async function datLaiMatKhau(
//   _email: string,
//   _otp: string,
//   _matKhauMoi: string,
// ): Promise<void> {
//   await new Promise((resolve) => setTimeout(resolve, 1500))
//   // TODO: Thay bằng fetch thực tế:
//   // const response = await fetch(`${BASE_URL}/dat-lai-mat-khau`, {
//   //   method: 'POST',
//   //   headers: { 'Content-Type': 'application/json' },
//   //   body: JSON.stringify({ email, otp, matKhauMoi }),
//   // })
//   // return xuLyResponse<void>(response)
// }

// export async function doiMatKhau(matKhauCu: string, matKhauMoi: string): Promise<void> {
//   const token = localStorage.getItem('accessToken')
//   const response = await fetch(`${BASE_URL}/doi-mat-khau`, {
//     method: 'PUT',
//     headers: {
//       'Content-Type': 'application/json',
//       ...(token ? { Authorization: `Bearer ${token}` } : {}),
//     },
//     body: JSON.stringify({ matKhauCu, matKhauMoi }),
//   })

//   return xuLyResponse<void>(response)
// }
/**
 * authService.ts — (MOCK DATA) Service giả lập API xác thực.
 * Không cần kết nối Backend. Dùng để test UI và Phân quyền.
 */
import type { DangNhapRequest, DangKyRequest, AuthResponse, ErrorResponse } from '@/types/auth'

const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

/**
 * MOCK: Đăng nhập giả lập
 * Tài khoản Admin: admin@school.edu.vn / 123456
 * Tài khoản Thủ thư: thuthu@school.edu.vn / 123456
 */
export async function dangNhap(request: DangNhapRequest): Promise<AuthResponse> {
  await delay(800) // Giả lập mạng

  if (request.email === 'admin@school.edu.vn' && request.matKhau === '123456') {
    return {
      accessToken: 'mock-jwt-token-admin-123',
      tokenType: 'Bearer',
      hoDem: 'Quản trị',
      ten: 'Viên',
      email: request.email,
      vaiTro: 'QUAN_TRI_VIEN',
      trangThaiTaiKhoan: 'da_kich_hoat'
    }
  }

  if (request.email === 'thuthu@school.edu.vn' && request.matKhau === '123456') {
    return {
      accessToken: 'mock-jwt-token-librarian-456',
      tokenType: 'Bearer',
      hoDem: 'Thủ',
      ten: 'Thư',
      email: request.email,
      vaiTro: 'THU_THU',
      trangThaiTaiKhoan: 'da_kich_hoat'
    }
  }

  // Giả lập ném lỗi nếu sai tài khoản
  throw {
    timestamp: new Date().toISOString(),
    status: 401,
    error: 'Unauthorized',
    message: 'Tài khoản hoặc mật khẩu không chính xác (Thử: admin@school.edu.vn / 123456)',
    path: '/api/auth/login'
  } as ErrorResponse
}

/**
 * MOCK: Đăng ký tài khoản
 */
export async function dangKy(request: DangKyRequest): Promise<AuthResponse> {
  await delay(1000)
  return {
    accessToken: 'mock-jwt-token-new-user',
    tokenType: 'Bearer',
    hoDem: request.hoDem,
    ten: request.ten,
    email: request.email,
    vaiTro: 'DOC_GIA',
    trangThaiTaiKhoan: 'chua_kich_hoat'
  }
}

export async function guiLaiXacNhan(_email: string): Promise<void> {
  await delay(1000)
  console.log(`Đã giả lập gửi lại email xác nhận cho: ${_email}`)
}

export async function quenMatKhau(_email: string): Promise<void> {
  await delay(1000)
  console.log(`Đã giả lập gửi OTP quên mật khẩu cho: ${_email}`)
}

export async function datLaiMatKhau(
  _email: string,
  _otp: string,
  _matKhauMoi: string,
): Promise<void> {
  await delay(1000)
  console.log(`Đã giả lập đặt lại mật khẩu thành công!`)
}

export async function doiMatKhau(matKhauCu: string, matKhauMoi: string): Promise<void> {
  await delay(1000)
  if (matKhauCu === matKhauMoi) {
    throw {
      timestamp: new Date().toISOString(),
      status: 400,
      error: 'Bad Request',
      message: 'Mật khẩu mới không được trùng mật khẩu cũ',
      path: '/api/auth/doi-mat-khau'
    } as ErrorResponse
  }
  console.log('Đã giả lập đổi mật khẩu thành công!')
}