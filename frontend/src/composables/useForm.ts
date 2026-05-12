/**
 * Composable tái sử dụng logic xác thực form (validation).
 * Dùng cho tất cả các form trong module xác thực.
 */
import { ref, reactive } from 'vue'

/** Kiểu dữ liệu cho đối tượng chứa lỗi của form */
export type LoiForm<T extends Record<string, unknown>> = Partial<Record<keyof T, string>>

/**
 * Composable xử lý form validation.
 * @param giaTriKhoiTao - Giá trị ban đầu của form
 * @param hamKiemTra - Hàm kiểm tra và trả về object lỗi
 */
export function useForm<T extends Record<string, unknown>>(
  giaTriKhoiTao: T,
  hamKiemTra: (values: T) => LoiForm<T>,
) {
  /** Dữ liệu của form */
  const form = reactive<T>({ ...giaTriKhoiTao }) as T

  /** Object chứa các thông báo lỗi theo từng field */
  const loi = reactive<LoiForm<T>>({})

  /** Trạng thái đang gửi request */
  const dangGui = ref(false)

  /** Thông báo lỗi chung từ server */
  const loiServer = ref('')

  /**
   * Kiểm tra tất cả các field của form.
   * @returns true nếu không có lỗi, false nếu có lỗi
   */
  function kiemTraForm(): boolean {
    const ketQuaKiemTra = hamKiemTra(form)

    // Xóa tất cả lỗi cũ
    Object.keys(loi).forEach((key) => {
      delete (loi as Record<string, string | undefined>)[key]
    })

    // Gán lỗi mới
    Object.assign(loi, ketQuaKiemTra)

    // Hợp lệ khi không có lỗi nào
    return Object.keys(ketQuaKiemTra).length === 0
  }

  /**
   * Xóa lỗi của một field cụ thể khi người dùng bắt đầu nhập lại.
   * @param field - Tên field cần xóa lỗi
   */
  function xoaLoi(field: keyof T): void {
    delete (loi as Record<string, string | undefined>)[field as string]
    loiServer.value = ''
  }

  /**
   * Reset form về trạng thái ban đầu.
   */
  function resetForm(): void {
    Object.assign(form, giaTriKhoiTao)
    Object.keys(loi).forEach((key) => {
      delete (loi as Record<string, string | undefined>)[key]
    })
    loiServer.value = ''
    dangGui.value = false
  }

  return {
    form,
    loi,
    dangGui,
    loiServer,
    kiemTraForm,
    xoaLoi,
    resetForm,
  }
}

// ===== HÀM VALIDATE TIỆN ÍCH =====

/** Kiểm tra định dạng email hợp lệ */
export function kiemTraEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

/** Kiểm tra số điện thoại Việt Nam (10 số, bắt đầu bằng 0) */
export function kiemTraSoDienThoai(sdt: string): boolean {
  return /^0\d{9}$/.test(sdt)
}

/** Kiểm tra mật khẩu đủ mạnh (ít nhất 6 ký tự) */
export function kiemTraMatKhau(matKhau: string): boolean {
  return matKhau.length >= 6
}
