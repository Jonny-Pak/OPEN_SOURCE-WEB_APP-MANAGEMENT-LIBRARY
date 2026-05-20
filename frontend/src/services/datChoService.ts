/**
 * datChoService.ts — Service quản lý Đặt chỗ mượn sách.
 */
import apiClient from './apiClient'
import { useAuthStore } from '@/stores/auth'
import type { DatCho, HuyDatChoRequest, TrangThaiDatCho } from '@/types/datcho'

function mapResponseToDatCho(res: any): DatCho {
  if (!res) return res
  if (res.nguoiDung && res.sach) {
    return res
  }

  let status: TrangThaiDatCho = 'CHO_DUYET'
  const rawStatus = res.trangThaiDatCho || res.trangThai
  if (rawStatus === 'DANG_CHO') {
    status = 'CHO_DUYET'
  } else if (rawStatus === 'DA_NHAN_SACH' || rawStatus === 'DA_MUON') {
    status = 'DA_MUON'
  } else if (rawStatus === 'DA_HUY' || rawStatus === 'HET_HAN') {
    status = 'DA_HUY'
  } else if (rawStatus === 'DA_SAN_SANG') {
    status = 'DA_SAN_SANG'
  }

  return {
    maDatCho: res.maDatCho,
    nguoiDung: {
      maNguoiDung: res.maNguoiDung || '',
      hoDem: res.hoDemNguoiDung || '',
      ten: res.tenNguoiDung || 'Độc giả',
      email: res.emailNguoiDung || 'N/A'
    },
    sach: {
      maSach: res.maSach || 0,
      tenSach: res.tenSach || 'Đầu sách',
      maIsbn: res.maIsbn || 'N/A'
    },
    ngayDatCho: res.thoiGianDatCho || res.ngayDatCho || new Date().toISOString(),
    ngayHetHan: res.hanGiuCho || res.ngayHetHan || new Date().toISOString(),
    trangThai: status
  }
}

const mapList = (list: any[]): DatCho[] => (Array.isArray(list) ? list.map(mapResponseToDatCho) : [])
const mapSingle = (item: any): DatCho => mapResponseToDatCho(item)

export const datChoService = {
  // Existing methods
  danhSach: () =>
    apiClient.get<any[]>('/api/dat-cho').then(mapList),

  taoChoDocGia: (sachId: number) => {
    const authStore = useAuthStore()
    return apiClient.post<any>('/api/dat-cho', {
      maSach: sachId,
      maNguoiDung: authStore.thongTinNguoiDung?.maNguoiDung
    }).then(mapSingle)
  },

  duyet: (id: string | number) =>
    apiClient.put<any>(`/api/v1/dat-cho/${id}/duyet`).then(mapSingle),

  huy: async (id: string | number, body: HuyDatChoRequest) => {
    const ghiChu = body.lyDo || 'Độc giả yêu cầu hủy đặt chỗ'
    await apiClient.put<void>(`/api/dat-cho/${id}/huy?ghiChu=${encodeURIComponent(ghiChu)}`)
  },

  // Requested English methods
  getAll: () => {
    return apiClient.get<any[]>('/api/dat-cho').then(mapList)
  },

  getMy: () => {
    return apiClient.get<any[]>('/api/dat-cho/cua-toi').then(mapList)
  },

  reserve: (sachId: number) => {
    const authStore = useAuthStore()
    return apiClient.post<any>('/api/dat-cho', {
      maSach: sachId,
      maNguoiDung: authStore.thongTinNguoiDung?.maNguoiDung
    }).then(mapSingle)
  },

  cancel: async (datChoId: string | number) => {
    const ghiChu = encodeURIComponent('Hủy qua website')
    await apiClient.put<void>(`/api/dat-cho/${datChoId}/huy?ghiChu=${ghiChu}`)
  }
}

export default datChoService