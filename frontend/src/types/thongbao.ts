export type LoaiThongBao = 'HE_THONG' | 'NHAC_NHO' | 'CANH_BAO' | 'DAT_CHO'

export interface ThongBao {
  maThongBao: string
  tieuDe: string
  noiDung: string
  loaiThongBao: LoaiThongBao
  daDoc: boolean
  ngayTao: string
  maNguoiDung?: string
}

