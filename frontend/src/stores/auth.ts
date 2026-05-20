/**
 * Pinia Store quản lý trạng thái xác thực (Authentication State).
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { AuthResponse, ThongTinNguoiDung, DangNhapRequest, DangKyRequest } from '@/types/auth'
import * as authService from '@/services/authService'

export type UserRole = 'ADMIN' | 'LIBRARIAN' | 'DOC_GIA'

export const useAuthStore = defineStore('auth', () => {
  // ===== STATE =====
  const token = ref<string | null>(localStorage.getItem('accessToken'))
  
  const thongTinNguoiDung = ref<ThongTinNguoiDung | null>(
    (() => {
      const saved = localStorage.getItem('userInfo')
      return saved ? (JSON.parse(saved) as ThongTinNguoiDung) : null
    })()
  )

  const mustChangePassword = ref<boolean>(localStorage.getItem('mustChangePassword') === 'true')

  // Mapped English State/Getters for the requested interface
  const user = computed(() => thongTinNguoiDung.value)
  const isAuthenticated = computed(() => daXacThuc.value)
  const role = computed<UserRole>(() => currentRole.value)

  /** Ánh xạ vai trò từ backend sang store */
  const currentRole = computed<UserRole>(() => {
    if (!thongTinNguoiDung.value) return 'DOC_GIA'
    const role = thongTinNguoiDung.value.vaiTro
    if (role === 'QUAN_TRI_VIEN') return 'ADMIN'
    if (role === 'THU_THU') return 'LIBRARIAN'
    return 'DOC_GIA'
  })

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
        token.value = null
        thongTinNguoiDung.value = null
        localStorage.removeItem('accessToken')
        localStorage.removeItem('userInfo')
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

  const isAdmin = computed(() => currentRole.value === 'ADMIN')
  const isLibrarian = computed(() => currentRole.value === 'LIBRARIAN')
  const isUser = computed(() => currentRole.value === 'DOC_GIA')

  // ===== ACTIONS =====
  function luuXacThuc(response: AuthResponse): void {
    token.value = response.accessToken
    localStorage.setItem('accessToken', response.accessToken)

    const userInfo: ThongTinNguoiDung = {
      maNguoiDung: response.maNguoiDung,
      hoDem: response.hoDem,
      ten: response.ten,
      email: response.email,
      vaiTro: response.vaiTro,
      trangThaiTaiKhoan: response.trangThaiTaiKhoan,
      avatar: response.avatar,
    }
    thongTinNguoiDung.value = userInfo
    localStorage.setItem('userInfo', JSON.stringify(userInfo))

    mustChangePassword.value = Boolean(response.mustChangePassword ?? response.isDefaultPassword)
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

  // Requested English actions
  async function login(request: DangNhapRequest): Promise<void> {
    const res = await authService.login(request)
    luuXacThuc(res)
  }

  async function register(request: DangKyRequest): Promise<void> {
    const res = await authService.register(request)
    luuXacThuc(res)
  }

  async function logout(): Promise<void> {
    try {
      await authService.logout()
    } finally {
      xoaXacThuc()
    }
  }

  async function fetchMe(): Promise<void> {
    const me = await authService.getMe()
    if (me && thongTinNguoiDung.value) {
      thongTinNguoiDung.value.hoDem = me.hoDem || thongTinNguoiDung.value.hoDem
      thongTinNguoiDung.value.ten = me.ten || thongTinNguoiDung.value.ten
      thongTinNguoiDung.value.email = me.email || thongTinNguoiDung.value.email
      localStorage.setItem('userInfo', JSON.stringify(thongTinNguoiDung.value))
    }
  }

  function checkAuth(): boolean {
    return daXacThuc.value
  }

  function hasPermission(permission: string): boolean {
    if (isAdmin.value) return true
    if (isLibrarian.value) {
      return !permission.startsWith('nhan-su') && !permission.startsWith('system')
    }
    return false
  }

  function switchRole(newRole: 'ADMIN' | 'LIBRARIAN' | 'DOC_GIA'): void {
    if (!thongTinNguoiDung.value) return
    const vaiTroMap = {
      ADMIN: 'QUAN_TRI_VIEN' as const,
      LIBRARIAN: 'THU_THU' as const,
      DOC_GIA: 'DOC_GIA' as const
    }
    thongTinNguoiDung.value.vaiTro = vaiTroMap[newRole]
    localStorage.setItem('userInfo', JSON.stringify(thongTinNguoiDung.value))
  }

  return {
    // State
    token,
    thongTinNguoiDung,
    mustChangePassword,
    currentRole,
    user,
    isAuthenticated,
    role,
    // Getters
    daXacThuc,
    tenDayDu,
    isAdmin,
    isLibrarian,
    isUser,
    // Actions
    luuXacThuc,
    xoaXacThuc,
    daDoiMatKhauBatBuoc,
    login,
    register,
    logout,
    fetchMe,
    checkAuth,
    hasPermission,
    switchRole
  }
})
