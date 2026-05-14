/**
 * Pinia Store quản lý trạng thái xác thực (Authentication State).
 * [DEV MODE] Mock auth — không cần token thật, chỉ dùng mock user + role.
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { AuthResponse, ThongTinNguoiDung } from '@/types/auth'

// ===== TYPES & CONSTANTS =====

export type UserRole = 'ADMIN' | 'LIBRARIAN'

const LIBRARIAN_PERMISSIONS = [
  // Quản lý độc giả
  'doc-gia:view', 'doc-gia:activate', 'doc-gia:lock',
  // Quản lý sách vật lý
  'sach:view', 'sach:create', 'sach:edit', 'sach:import-excel',
  'cuon-sach:view', 'cuon-sach:update-status', 'cuon-sach:print-barcode',
  'cuon-sach:update-location',
  // Mượn/Trả
  'muon-sach:view', 'muon-sach:create', 'muon-sach:approve',
  'tra-sach:process', 'dat-cho:view', 'dat-cho:approve', 'dat-cho:reject',
  'gia-han:approve', 'gia-han:reject',
  // Phạt
  'phat:view', 'phat:create', 'phat:confirm', 'phat:mark-paid',
] as const

const ADMIN_PERMISSIONS = [
  ...LIBRARIAN_PERMISSIONS,
  // Danh mục master data
  'danh-muc:view', 'danh-muc:create', 'danh-muc:edit', 'danh-muc:delete',
  'tac-gia:view', 'tac-gia:create', 'tac-gia:edit', 'tac-gia:delete',
  'nha-xuat-ban:view', 'nha-xuat-ban:create', 'nha-xuat-ban:edit', 'nha-xuat-ban:delete',
  'the-loai:view', 'the-loai:create', 'the-loai:edit', 'the-loai:delete',
  'vi-tri:view', 'vi-tri:create', 'vi-tri:edit', 'vi-tri:delete',
  // Quản trị hệ thống
  'system:settings',
  // Nhân sự
  'nhan-su:view', 'nhan-su:create', 'nhan-su:lock', 'nhan-su:unlock',
  // Sách: admin được phép xóa, librarian không
  'sach:delete', 'cuon-sach:delete',
  // Dashboard nâng cao
  'dashboard:full-stats',
] as const

export const ROLE_PERMISSIONS: Record<UserRole, readonly string[]> = {
  ADMIN: ADMIN_PERMISSIONS,
  LIBRARIAN: LIBRARIAN_PERMISSIONS,
}

export const useAuthStore = defineStore('auth', () => {
  // ===== STATE =====

  /** JWT access token — lấy từ localStorage khi khởi động */
  const token = ref<string | null>(localStorage.getItem('accessToken'))

  /** Thông tin người dùng đang đăng nhập */
  const thongTinNguoiDung = ref<ThongTinNguoiDung | null>(
    (() => {
      // Khôi phục thông tin người dùng từ localStorage nếu có
      const saved = localStorage.getItem('userInfo')
      return saved ? (JSON.parse(saved) as ThongTinNguoiDung) : null
    })(),
  )

  /** Cờ bắt buộc đổi mật khẩu sau đăng nhập lần đầu */
  const mustChangePassword = ref<boolean>(localStorage.getItem('mustChangePassword') === 'true')

  /** [DEV MODE] Role hiện tại — dùng để test phân quyền */
  const currentRole = ref<UserRole>('ADMIN')

  /** [DEV MODE] Mock user data */
  const mockUser = ref({
    id: 1,
    hoTen: 'Nguyễn Dev',
    email: 'dev@library.com',
    role: currentRole.value,
  })

  // ===== GETTERS =====

  /** Kiểm tra người dùng đã đăng nhập hay chưa */
  const daXacThuc = computed(() => {
    if (!token.value) return false

    try {
      const parts = token.value.split('.')
      const payloadBase64 = parts[1]
      if (!payloadBase64) return false

      // JWT uses base64url encoding — chuẩn hoá về base64 trước khi decode
      let base64 = payloadBase64.replace(/-/g, '+').replace(/_/g, '/')
      // padding nếu cần
      while (base64.length % 4 !== 0) base64 += '='

      // Decode phần payload của JWT (base64)
      const payload = JSON.parse(atob(base64))
      const chuaHetHan = payload.exp * 1000 > Date.now()

      if (!chuaHetHan) {
        token.value = null
        thongTinNguoiDung.value = null
        localStorage.removeItem('accessToken')
        localStorage.removeItem('userInfo')
      }

      return chuaHetHan
    } catch {
      // Token không đúng định dạng JWT → coi như chưa đăng nhập
      return false
    }
  })

  /** Lấy tên đầy đủ của người dùng */
  const tenDayDu = computed(() => {
    if (!thongTinNguoiDung.value) return mockUser.value.hoTen
    return `${thongTinNguoiDung.value.hoDem} ${thongTinNguoiDung.value.ten}`
  })

  /** [DEV MODE] Kiểm tra xem role hiện tại là ADMIN */
  const isAdmin = computed(() => currentRole.value === 'ADMIN')

  /** [DEV MODE] Kiểm tra xem role hiện tại là LIBRARIAN */
  const isLibrarian = computed(() => currentRole.value === 'LIBRARIAN')

  /** [DEV MODE] Kiểm tra người dùng có permission nào đó không */
  const hasPermission = (permission: string): boolean => {
    return ROLE_PERMISSIONS[currentRole.value]?.includes(permission as never) ?? false
  }

  // ===== ACTIONS =====

  /**
   * Lưu thông tin xác thực sau khi đăng nhập / đăng ký thành công.
   * @param response - Dữ liệu trả về từ API backend
   */
  function luuXacThuc(response: AuthResponse): void {
    // Lưu token vào state và localStorage
    token.value = response.accessToken
    localStorage.setItem('accessToken', response.accessToken)

    // Lưu thông tin người dùng vào state và localStorage
    const userInfo: ThongTinNguoiDung = {
      hoDem: response.hoDem,
      ten: response.ten,
      email: response.email,
      vaiTro: response.vaiTro,
      trangThaiTaiKhoan: response.trangThaiTaiKhoan,
    }
    thongTinNguoiDung.value = userInfo
    localStorage.setItem('userInfo', JSON.stringify(userInfo))

    mustChangePassword.value = Boolean(response.mustChangePassword ?? response.isDefaultPassword)
    localStorage.setItem('mustChangePassword', String(mustChangePassword.value))
  }

  /**
   * Xóa toàn bộ thông tin xác thực khi đăng xuất.
   */
  function xoaXacThuc(): void {
    token.value = null
    thongTinNguoiDung.value = null
    mustChangePassword.value = false
    localStorage.removeItem('accessToken')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('mustChangePassword')
  }

  function daDoiMatKhauBatBuoc(): void {
    mustChangePassword.value = false
    localStorage.setItem('mustChangePassword', 'false')
  }

  /** [DEV MODE] Chuyển đổi role để test phân quyền */
  function switchRole(role: UserRole): void {
    currentRole.value = role
    mockUser.value.role = role
  }

  return {
    // State
    token,
    thongTinNguoiDung,
    mustChangePassword,
    currentRole,
    mockUser,
    // Getters
    daXacThuc,
    tenDayDu,
    isAdmin,
    isLibrarian,
    hasPermission,
    // Actions
    luuXacThuc,
    xoaXacThuc,
    daDoiMatKhauBatBuoc,
    switchRole,
  }
})
