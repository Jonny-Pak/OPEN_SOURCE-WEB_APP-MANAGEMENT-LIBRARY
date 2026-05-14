// /**
//  * dashboardService.ts — Service lấy dữ liệu thống kê Dashboard.
//  */
// import apiClient from './apiClient'

// export interface ThongKeDashboard {
//   soBanDangMuon: number
//   soSachQuaHan: number
//   tongTienPhat: number
//   tongNguoiDung: number
// }

// /** Lấy số liệu thống kê tổng quan */
// // TODO: Implement dashboard controller on backend
// // export async function layThongKe(): Promise<ThongKeDashboard> {
// //   return apiClient.get<ThongKeDashboard>('/api/v1/admin/dashboard/thong-ke')
// // }
/**
 * dashboardService.ts — (MOCK DATA) Service lấy dữ liệu thống kê Dashboard.
 */

export interface ThongKeDashboard {
  soBanDangMuon: number
  soSachQuaHan: number
  tongTienPhat: number
  tongNguoiDung: number
}

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

export const dashboardService = {
  /** Lấy số liệu thống kê tổng quan giả lập */
  layThongKe: async (): Promise<ThongKeDashboard> => {
    await delay(700) // Giả lập query DB hơi lâu một chút

    return {
      soBanDangMuon: 145,
      soSachQuaHan: 12,
      tongTienPhat: 350000,
      tongNguoiDung: 1250
    }
  }
}