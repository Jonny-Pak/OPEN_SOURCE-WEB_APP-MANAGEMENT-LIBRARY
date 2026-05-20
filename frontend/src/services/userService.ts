/**
 * userService.ts — Service quản lý thông tin cá nhân của người dùng.
 */
import apiClient from './apiClient'
import type { NguoiDung, SuaNguoiDungRequest, ChangePasswordRequest } from '@/types/nguoidung'

export const userService = {
  /** Lấy thông tin cá nhân của người dùng đang đăng nhập */
  getMyProfile: () => 
    apiClient.get<NguoiDung>('/api/v1/nguoi-dung/me'),

  /** Cập nhật thông tin cá nhân */
  updateProfile: (body: SuaNguoiDungRequest) =>
    apiClient.put<NguoiDung>('/api/v1/nguoi-dung/me', body),

  /** Đổi mật khẩu */
  changePassword: (body: ChangePasswordRequest) =>
    apiClient.put<void>('/api/v1/nguoi-dung/me/password', body)
}

export default userService
