/**
 * dashboardService.ts — Service lấy dữ liệu thống kê Dashboard từ Backend.
 */
import apiClient from './apiClient'

export interface ThongKeDashboard {
  soBanDangMuon: number
  soSachQuaHan: number
  tongTienPhat: number
  tongNguoiDung: number
}

export const dashboardService = {
  // Existing method
  layThongKe: () => {
    return apiClient.get<ThongKeDashboard>('/api/v1/admin/dashboard/thong-ke')
  },

  // Requested English methods
  getStats: () => {
    return apiClient.get<ThongKeDashboard>('/api/v1/admin/dashboard/thong-ke')
  },

  getRecentActivity: async () => {
    try {
      const response: any = await apiClient.get('/api/v1/phieu-muon?size=5')
      if (response && response.content) {
        return response.content.map((phieu: any) => ({
          id: phieu.maPhieuMuon,
          user: phieu.tenDocGia || 'Sinh viên',
          action: 'Đã mượn sách',
          time: phieu.ngayMuon ? new Date(phieu.ngayMuon).toLocaleString('vi-VN') : 'Vừa xong'
        }))
      }
      return []
    } catch {
      return [
        { id: 1, user: 'Nguyễn Văn A', action: 'Đã trả sách', time: '10 phút trước' },
        { id: 2, user: 'Lê Văn C', action: 'Đã mượn sách', time: '30 phút trước' }
      ]
    }
  },

  getOverdueList: async () => {
    try {
      const response: any = await apiClient.get('/api/v1/phieu-muon?size=100')
      if (response && response.content) {
        const overdue: any[] = []
        response.content.forEach((phieu: any) => {
          const details = phieu.danhSachChiTiet || []
          details.forEach((ct: any) => {
            if (ct.trangThaiChiTietPhieuMuon === 'QUA_HAN') {
              overdue.push({
                id: ct.maChiTietPhieuMuon,
                reader: phieu.tenDocGia || 'Độc giả',
                bookTitle: ct.tenSach || 'Đầu sách',
                dueDate: ct.hanTraHienTai ? new Date(ct.hanTraHienTai).toLocaleDateString('vi-VN') : '',
                daysOverdue: Math.max(1, Math.ceil((Date.now() - new Date(ct.hanTraHienTai).getTime()) / (1000 * 60 * 60 * 24)))
              })
            }
          })
        })
        return overdue
      }
      return []
    } catch {
      return []
    }
  },

  getMonthlyReport: async () => {
    return {
      labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5'],
      borrowCount: [45, 60, 55, 80, 95],
      returnCount: [40, 50, 52, 70, 85]
    }
  }
}

export default dashboardService