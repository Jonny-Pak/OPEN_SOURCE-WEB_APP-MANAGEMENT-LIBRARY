import apiClient from './apiClient'

export interface NhatKyHoatDong {
  id: string
  maNguoiDung: string | null
  hoTen: string
  vaiTro: string
  hanhDong: string
  chiTiet: string
  thoiGian: string
}

export interface PaginatedLogs {
  content: NhatKyHoatDong[]
  totalPages: number
  totalElements: number
  size: number
  number: number
}

export const nhatKyService = {
  async getLogs(keyword?: string, vaiTro?: string, page = 0, size = 15): Promise<PaginatedLogs> {
    const params: any = { page, size }
    if (keyword && keyword.trim()) {
      params.keyword = keyword.trim()
    }
    if (vaiTro && vaiTro !== 'TAT_CA') {
      params.vaiTro = vaiTro
    }
    return apiClient.get('/api/v1/nhat-ky', { params })
  }
}
