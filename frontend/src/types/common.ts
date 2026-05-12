/**
 * common.ts — Các kiểu dữ liệu dùng chung cho toàn bộ ứng dụng.
 * Bao gồm: phân trang, response chuẩn, tham số truy vấn.
 */

/** Response phân trang chuẩn từ Spring Data */
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number       // trang hiện tại (0-indexed)
  size: number
  first: boolean
  last: boolean
  empty: boolean
}

/** Response thành công bọc data */
export interface ApiResponse<T> {
  data: T
  message: string
  status: number
}

/** Tham số phân trang và tìm kiếm chung */
export interface PaginationParams {
  page: number
  size: number
  search?: string
}

/** Trạng thái loading của một màn hình */
export type TrangThaiTai = 'idle' | 'loading' | 'success' | 'error'
