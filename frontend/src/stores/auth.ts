/**
 * Pinia Store quản lý trạng thái xác thực (Authentication State).
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { AuthResponse, ThongTinNguoiDung } from '@/types/auth'

// ===== TYPES & CONSTANTS =====

export type UserRole = 'QUAN_TRI_VIEN' | 'THU_THU' | 'DOC_GIA'

const LIBRARIAN_PERMISSIONS = [
  'doc-gia:view', 'doc-gia:activate', 'doc-gia:lock',
  'sach:view', 'sach:create', 'sach:edit', 'sach:import-excel',
  'cuon-sach:view', 'cuon-sach:update-status', 'cuon-sach:print-barcode',
  'cuon-sach:update-location',
  'muon-sach:view', 'muon-sach:create', 'muon-sach:approve',
  'tra-sach:process', 'dat-cho:view', 'dat-cho:approve', 'dat-cho:reject',
  'gia-han:approve', 'gia-han:reject',
  'phat:view', 'phat:create', 'phat:confirm', 'phat:mark-paid',
] as const

const ADMIN_PERMISSIONS = [
  ...LIBRARIAN_PERMISSIONS,
  'danh-muc:view', 'danh-muc:create', 'danh-muc:edit', 'danh-muc:delete',
  'tac-gia:view', 'tac-gia:create', 'tac-gia:edit', 'tac-gia:delete',
  'nha-xuat-ban:view', 'nha-xuat-ban:create', 'nha-xuat-ban:edit', 'nha-xuat-ban:delete',
  'the-loai:view', 'the-loai:create', 'the-loai:edit', 'the-loai:delete',
  'vi-tri:view', 'vi-tri:create', 'vi-tri:edit', 'vi-tri:delete',
  'system:settings',
  'nhan-su:view', 'nhan-su:create', 'nhan-su:lock', 'nhan-su:unlock',
  'sach:delete', 'cuon-sach:delete',
  'dashboard:full-stats',
] as const

const USER_PERMISSIONS = [
  'profile:view', 'profile:update', 'my-loans:view'
] as const

export const ROLE_PERMISSIONS: Record<UserRole, readonly string[]> = {
  QUAN_TRI_VIEN: ADMIN_PERMISSIONS,
  THU_THU: LIBRARIAN_PERMISSIONS,
  DOC_GIA: USER_PERMISSIONS,
}

export const useAuthStore = defineStore('auth', () => {
  // ===== STATE =====

  const token = ref<string | null>(localStorage.getItem('accessToken'))
  const thongTinNguoiDung = ref<ThongTinNguoiDung | null>(
    (() => {
      const saved = localStorage.getItem('userInfo')
      return saved ? (JSON.parse(saved) as ThongTinNguoiDung) : null
    })(),
  )

  const mustChangePassword = ref<boolean>(localStorage.getItem('mustChangePassword') === 'true')
  const devRole = ref<string | null>(null)

  // ===== GETTERS =====

  const daXacThuc = computed(() => {
    if (!token.value) return false

    try {
      const parts = token.value.split('.')
      const payloadBase64 = parts[1]
      if (!payloadBase64) return false

      let base64 = payloadBase64.replace(/-/g, '+').replace(/_/g, '/')
      while (base64.length % 4 !== 0) base64 += '='

      const payload = JSON.parse(atob(base64))
      const chuaHetHan = payload.exp * 1000 > Date.now()

      if (!chuaHetHan) {
        xoaXacThuc()
      }

      return chuaHetHan
    } catch {
      return false
    }
  })

  const tenDayDu = computed(() => {
    if (!thongTinNguoiDung.value) return ''
    return `${thongTinNguoiDung.value.hoDem} ${thongTinNguoiDung.value.ten}`
  })

  const currentRole = computed(() => {
    if (devRole.value) return devRole.value
    const role = thongTinNguoiDung.value?.vaiTro
    if (role === 'QUAN_TRI_VIEN') return 'ADMIN'
    if (role === 'THU_THU') return 'LIBRARIAN'
    return 'USER'
  })

  const isAdmin = computed(() => currentRole.value === 'ADMIN')
  const isLibrarian = computed(() => currentRole.value === 'LIBRARIAN' || currentRole.value === 'ADMIN')
  const isUser = computed(() => currentRole.value === 'USER')

  const hasPermission = (permission: string): boolean => {
    const role = thongTinNguoiDung.value?.vaiTro as UserRole
    return ROLE_PERMISSIONS[role]?.includes(permission as never) ?? false
  }

  // ===== ACTIONS =====

  function luuXacThuc(response: AuthResponse): void {
    token.value = response.accessToken
    localStorage.setItem('accessToken', response.accessToken)

    const userInfo: ThongTinNguoiDung = {
      hoDem: response.hoDem,
      ten: response.ten,
      email: response.email,
      vaiTro: response.vaiTro,
      trangThaiTaiKhoan: response.trangThaiTaiKhoan || 'da_kich_hoat',
    }
    thongTinNguoiDung.value = userInfo
    localStorage.setItem('userInfo', JSON.stringify(userInfo))

    mustChangePassword.value = Boolean(response.mustChangePassword)
    localStorage.setItem('mustChangePassword', String(mustChangePassword.value))
  }

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

  function switchRole(role: string) {
    devRole.value = role
  }

  return {
    // State
    token,
    thongTinNguoiDung,
    mustChangePassword,
    devRole,
    // Getters
    daXacThuc,
    tenDayDu,
    currentRole,
    isAdmin,
    isLibrarian,
    isUser,
    hasPermission,
    // Actions
    luuXacThuc,
    xoaXacThuc,
    daDoiMatKhauBatBuoc,
    switchRole,
  }
})
