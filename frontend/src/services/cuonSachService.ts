/**
 * cuonSachService.ts — Service quản lý Cuốn sách (bản sao vật lý).
 */
import apiClient from './apiClient'
import type { CuonSach, TaoCuonSachRequest, SuaCuonSachRequest, TinhTrangVatLy, TrangThaiCuonSach } from '@/types/sach'

interface BackendCuonSachResponse {
  maCuonSach: number
  maSach: number
  tenSach: string
  maVach: string
  viTriKe: string
  trangThai: string
  tinhTrangVatLy: string
  ghiChuBaoTri: string
}

function mapToCuonSach(item: BackendCuonSachResponse): CuonSach {
  let tinhTrangVatLy: TinhTrangVatLy = 'TOT'
  if (item.tinhTrangVatLy === 'BINH_THUONG' || item.tinhTrangVatLy === 'TOT') {
    tinhTrangVatLy = 'TOT'
  } else if (
    item.tinhTrangVatLy === 'RACH_TRANG' ||
    item.tinhTrangVatLy === 'MAT_BIA' ||
    item.tinhTrangVatLy === 'HONG_NANG' ||
    item.tinhTrangVatLy === 'HU_HONG'
  ) {
    tinhTrangVatLy = 'HU_HONG'
  } else if (item.tinhTrangVatLy === 'DA_MAT' || item.tinhTrangVatLy === 'MAT') {
    tinhTrangVatLy = 'MAT'
  }

  let trangThai: TrangThaiCuonSach = 'SAN_SANG'
  if (item.trangThai === 'SAN_SANG') {
    trangThai = 'SAN_SANG'
  } else if (item.trangThai === 'DANG_MUON') {
    trangThai = 'DANG_MUON'
  } else if (item.trangThai === 'BAO_TRI' || item.trangThai === 'CHO_MUON') {
    trangThai = 'CHO_MUON'
  } else if (item.trangThai === 'DA_MAT' || item.trangThai === 'BAO_MAT') {
    trangThai = 'BAO_MAT'
  }

  return {
    maCuonSach: item.maCuonSach,
    maBarcodeVatLy: item.maVach || '',
    viTriKe: item.viTriKe || '',
    tinhTrangVatLy,
    trangThai,
    sach: {
      maSach: item.maSach || 0,
      tenSach: item.tenSach || 'Chưa rõ',
      maIsbn: ''
    }
  }
}

export const cuonSachService = {
  // Existing methods
  danhSach: async (): Promise<CuonSach[]> => {
    const list = await apiClient.get<BackendCuonSachResponse[]>('/api/v1/cuon-sach')
    return list.map(mapToCuonSach)
  },

  taoCai: async (body: TaoCuonSachRequest): Promise<CuonSach> => {
    let tinhTrangVatLy = 'BINH_THUONG'
    if (body.tinhTrangVatLy === 'HU_HONG') tinhTrangVatLy = 'RACH_TRANG'
    else if (body.tinhTrangVatLy === 'MAT') tinhTrangVatLy = 'DA_MAT'

    const randomSuffix = Math.floor(1000 + Math.random() * 9000)
    const maVach = `CS-${body.sachId}-${randomSuffix}`

    const backendBody = {
      maSach: body.sachId,
      viTriKe: body.viTriKe,
      tinhTrangVatLy: tinhTrangVatLy,
      trangThai: 'SAN_SANG',
      maVach: maVach,
      ghiChuBaoTri: ''
    }

    const res = await apiClient.post<BackendCuonSachResponse>('/api/v1/cuon-sach', backendBody)
    return mapToCuonSach(res)
  },

  capNhat: async (id: number, body: SuaCuonSachRequest): Promise<CuonSach> => {
    let tinhTrangVatLy = 'BINH_THUONG'
    if (body.tinhTrangVatLy === 'HU_HONG') tinhTrangVatLy = 'RACH_TRANG'
    else if (body.tinhTrangVatLy === 'MAT') tinhTrangVatLy = 'DA_MAT'

    const existing = await apiClient.get<BackendCuonSachResponse>(`/api/v1/cuon-sach/${id}`)

    const backendBody = {
      maSach: existing.maSach,
      viTriKe: body.viTriKe,
      tinhTrangVatLy: tinhTrangVatLy,
      trangThai: existing.trangThai,
      maVach: existing.maVach,
      ghiChuBaoTri: existing.ghiChuBaoTri || ''
    }

    const res = await apiClient.put<BackendCuonSachResponse>(`/api/v1/cuon-sach/${id}`, backendBody)
    return mapToCuonSach(res)
  },

  xoa: (id: number) =>
    apiClient.delete<void>(`/api/v1/cuon-sach/${id}`),

  layMaVach: (id: number): string =>
    `http://localhost:8080/api/v1/cuon-sach/${id}/ma-vach`,

  // Requested English methods
  getBySach: async (sachId: number): Promise<CuonSach[]> => {
    const list = await apiClient.get<BackendCuonSachResponse[]>(`/api/v1/cuon-sach/sach/${sachId}`)
    return list.map(mapToCuonSach)
  },

  updateStatus: async (cuonSachId: number, status: string): Promise<CuonSach> => {
    const existing = await apiClient.get<BackendCuonSachResponse>(`/api/v1/cuon-sach/${cuonSachId}`)
    const backendBody = {
      maSach: existing.maSach,
      viTriKe: existing.viTriKe,
      tinhTrangVatLy: existing.tinhTrangVatLy,
      trangThai: status,
      maVach: existing.maVach,
      ghiChuBaoTri: existing.ghiChuBaoTri || ''
    }
    const res = await apiClient.put<BackendCuonSachResponse>(`/api/v1/cuon-sach/${cuonSachId}`, backendBody)
    return mapToCuonSach(res)
  }
}

export default cuonSachService