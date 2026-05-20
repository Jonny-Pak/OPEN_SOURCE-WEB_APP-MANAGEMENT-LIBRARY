/**
 * Composable tái sử dụng logic xác thực form (validation).
 * Dùng cho tất cả các form trong module xác thực.
 */
import { ref, reactive, computed } from 'vue'

export type LoiForm<T extends Record<string, unknown>> = Partial<Record<keyof T, string>>

export function useForm<T extends Record<string, unknown>>(
  giaTriKhoiTao: T,
  hamKiemTra: (values: T) => LoiForm<T>,
) {
  const form = reactive<T>({ ...giaTriKhoiTao }) as T
  const loi = reactive<LoiForm<T>>({})
  const dangGui = ref(false)
  const loiServer = ref('')

  function kiemTraForm(): boolean {
    const ketQuaKiemTra = hamKiemTra(form)

    Object.keys(loi).forEach((key) => {
      delete (loi as Record<string, string | undefined>)[key]
    })

    Object.assign(loi, ketQuaKiemTra)

    return Object.keys(ketQuaKiemTra).length === 0
  }

  function xoaLoi(field: keyof T): void {
    delete (loi as Record<string, string | undefined>)[field as string]
    loiServer.value = ''
  }

  function resetForm(): void {
    Object.assign(form, giaTriKhoiTao)
    Object.keys(loi).forEach((key) => {
      delete (loi as Record<string, string | undefined>)[key]
    })
    loiServer.value = ''
    dangGui.value = false
  }

  // Requested English interface
  const isLoading = dangGui
  const errors = loi

  function reset(): void {
    resetForm()
  }

  async function handleSubmit(onSubmit: (values: T) => Promise<void> | void): Promise<void> {
    if (!kiemTraForm()) return

    dangGui.value = true
    loiServer.value = ''
    try {
      await onSubmit(form)
    } catch (err: any) {
      loiServer.value = err.message || 'Có lỗi xảy ra từ máy chủ'
    } finally {
      dangGui.value = false
    }
  }

  return {
    form,
    loi,
    dangGui,
    loiServer,
    kiemTraForm,
    xoaLoi,
    resetForm,

    // English Interface
    isLoading,
    errors,
    reset,
    handleSubmit
  }
}

// ===== HÀM VALIDATE TIỆN ÍCH =====
export function kiemTraEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

export function kiemTraSoDienThoai(sdt: string): boolean {
  return /^0\d{9}$/.test(sdt)
}

export function kiemTraMatKhau(matKhau: string): boolean {
  return matKhau.length >= 6
}

export default useForm
