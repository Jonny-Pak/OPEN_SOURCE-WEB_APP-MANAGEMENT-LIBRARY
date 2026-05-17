/**
 * muonSachService.ts — Service quản lý Mượn sách.
 */
import apiClient from './apiClient'
import { useAuthStore } from '@/stores/auth'
import { cuonSachService } from '@/services/cuonSachService'
import type { PageResponse } from '@/types/common'
import type { PhieuMuon, TaoPhieuMuonRequest } from '@/types/muonsach'

function normalizePhieuMuon(response: any): PhieuMuon {
  if (!response) return response

  // If it's already in the expected frontend format, return it
  if (response.nguoiDung && response.chiTietList) {
    return response
  }

  // Map the details
  const details = response.danhSachChiTiet || []
  const normalizedDetails = details.map((ct: any) => ({
    maChiTiet: ct.maChiTietPhieuMuon || ct.maChiTiet,
    maBarcodeVatLy: ct.maVach || ct.maBarcodeVatLy,
    tenSach: ct.tenSach || '',
    hanTra: ct.hanTraHienTai || ct.hanTraBanDau || ct.hanTra || '',
    ngayTra: ct.ngayTraThucTe || ct.ngayTra,
    tinhTrangTraSach: ct.tinhTrangLucTra || ct.tinhTrangTraSach,
    quaHan: ct.trangThaiChiTietPhieuMuon === 'QUA_HAN' || ct.quaHan || false,
  }))

  // Extract name parts safely
  const tenFullName = response.tenDocGia || ''
  const parts = tenFullName.split(' ')
  const ten = parts.length > 0 ? parts[parts.length - 1] : ''
  const hoDem = parts.length > 1 ? parts.slice(0, -1).join(' ') : ''

  // Build the expected interface
  return {
    maPhieuMuon: response.maPhieuMuon,
    nguoiDung: {
      maNguoiDung: response.maNguoiDung || '',
      hoDem: hoDem,
      ten: ten,
      email: 'N/A', // No email field in PhieuMuonResponse
    },
    ngayMuon: response.ngayMuon,
    hanTra: normalizedDetails.length > 0 ? normalizedDetails[0].hanTra : response.ngayMuon,
    trangThai: response.trangThaiPhieu === 'DA_HOAN_TAT' ? 'DA_TRA' :
               response.trangThaiPhieu === 'DA_HUY' ? 'DA_HUY' :
               normalizedDetails.some((d: any) => d.quaHan) ? 'QUA_HAN' : 'DANG_MUON',
    soLuongCuon: normalizedDetails.length,
    chiTietList: normalizedDetails,
  }
}

export const muonSachService = {
  // Existing methods
  danhSach: async (page: number = 0, size: number = 10, keyword: string = '', sortBy: string = '', direction: string = '') => {
    const params = new URLSearchParams()
    params.append('page', page.toString())
    params.append('size', size.toString())
    if (keyword) params.append('keyword', keyword)
    if (sortBy) params.append('sortBy', sortBy)
    if (direction) params.append('direction', direction)
    const response = await apiClient.get<PageResponse<any>>(`/api/v1/phieu-muon?${params.toString()}`)
    if (response && response.content) {
      response.content = response.content.map(normalizePhieuMuon)
    }
    return response as PageResponse<PhieuMuon>
  },

  layChiTiet: async (id: string | number) => {
    const response = await apiClient.get<any>(`/api/v1/phieu-muon/${id}`)
    return normalizePhieuMuon(response)
  },

  taoCai: async (body: TaoPhieuMuonRequest) => {
    // Map frontend field name to backend field name
    const backendBody = {
      maNguoiDung: body.maNguoiDung,
      danhSachMaVach: body.danhSachMaBarcodeVatLy
    }
    const response = await apiClient.post<any>('/api/v1/phieu-muon', backendBody)
    return normalizePhieuMuon(response)
  },

  // Requested methods
  getAll: async (params?: any) => {
    return apiClient.get<any>('/api/v1/phieu-muon', { params })
  },

  getMyBorrows: async () => {
    return apiClient.get<any>('/api/muon-sach/cua-toi')
  },

  borrowBook: async (sachId: string) => {
    const authStore = useAuthStore()
    if (!authStore.thongTinNguoiDung?.maNguoiDung) {
      throw new Error('Người dùng chưa xác thực')
    }

    // The backend expects barcode(s) (maVach). If the caller passed a book id,
    // resolve to an available copy's barcode first.
    const sachIdNum = parseInt(String(sachId), 10)
    let danhSachMaVach: string[] = []
    try {
      const cuonSachList = await cuonSachService.getBySach(sachIdNum)
      if (cuonSachList && cuonSachList.length > 0) {
        // prefer a copy that is available (SAN_SANG) if present
        const available = cuonSachList.find(c => c.trangThai === 'SAN_SANG')
        danhSachMaVach = [available ? available.maBarcodeVatLy : (cuonSachList[0]?.maBarcodeVatLy || String(sachId))]
      } else {
        // fallback: treat sachId as barcode if no copies returned
        danhSachMaVach = [String(sachId)]
      }
    } catch (err) {
      // If look-up fails, fall back to sending the original id as barcode (server will respond)
      danhSachMaVach = [String(sachId)]
    }

    const request = {
      maNguoiDung: authStore.thongTinNguoiDung?.maNguoiDung,
      danhSachMaVach
    }

    return apiClient.post<any>('/api/v1/phieu-muon', request)
  },

  returnBook: async (muonId: string) => {
    return apiClient.put<any>(`/api/v1/phieu-muon/${muonId}/tra`)
  },

  extendBorrow: async (muonId: string) => {
    const authStore = useAuthStore()
    return apiClient.post('/api/v1/phieu-muon/gia-han', {
      maChiTietPhieuMuon: muonId,
      maNguoiThucHien: authStore.thongTinNguoiDung?.maNguoiDung,
      lyDo: 'Gia hạn qua website'
    })
  }
}

export default muonSachService