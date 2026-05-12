/**
 * usePagination.ts — Composable quản lý trạng thái phân trang.
 * Tái sử dụng cho tất cả màn hình có danh sách.
 */
import { ref, computed } from 'vue'

export function usePagination(defaultSize = 10) {
  /** Trang hiện tại (0-indexed, theo Spring Data) */
  const trangHienTai = ref(0)
  const kichThuocTrang = ref(defaultSize)
  const tongPhanTu = ref(0)

  /** Tổng số trang */
  const tongTrang = computed(() =>
    Math.ceil(tongPhanTu.value / kichThuocTrang.value)
  )

  /** Số trang hiển thị cho người dùng (1-indexed) */
  const trangHienThiChoNguoiDung = computed(() => trangHienTai.value + 1)

  const coTrangTruoc = computed(() => trangHienTai.value > 0)
  const coTrangSau = computed(() => trangHienTai.value < tongTrang.value - 1)

  /** Chuyển đến trang cụ thể (1-indexed từ UI) */
  function denTrang(trang: number): void {
    const trang0Indexed = trang - 1
    if (trang0Indexed >= 0 && trang0Indexed < tongTrang.value) {
      trangHienTai.value = trang0Indexed
    }
  }

  function trangTiepTheo(): void {
    if (coTrangSau.value) trangHienTai.value++
  }

  function trangTruoc(): void {
    if (coTrangTruoc.value) trangHienTai.value--
  }

  /** Đặt lại về trang đầu (dùng khi search/filter thay đổi) */
  function datLaiTrang(): void {
    trangHienTai.value = 0
  }

  /** Cập nhật tổng số phần tử từ response */
  function capNhatTong(total: number): void {
    tongPhanTu.value = total
  }

  return {
    trangHienTai,
    kichThuocTrang,
    tongPhanTu,
    tongTrang,
    trangHienThiChoNguoiDung,
    coTrangTruoc,
    coTrangSau,
    denTrang,
    trangTiepTheo,
    trangTruoc,
    datLaiTrang,
    capNhatTong,
  }
}
