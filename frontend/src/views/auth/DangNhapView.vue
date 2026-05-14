<!--
  DangNhapView.vue — Trang đăng nhập.
  Cho phép người dùng đăng nhập bằng email và mật khẩu.
  Tích hợp trực tiếp với API POST /api/auth/login.
-->
<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { dangNhap } from '@/services/authService'
import { useForm, kiemTraEmail, type LoiForm } from '@/composables/useForm'
import type { ErrorResponse } from '@/types/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

/** Kiểu dữ liệu form đăng nhập */
interface FormDangNhap extends Record<string, unknown> {
  email: string
  matKhau: string
  ghiNhoToi: boolean
}

/** Hàm kiểm tra hợp lệ dữ liệu đăng nhập */
function kiemTraDangNhap(values: FormDangNhap): LoiForm<FormDangNhap> {
  const loi: LoiForm<FormDangNhap> = {}
  if (!values.email) {
    loi.email = 'Email không được để trống'
  } else if (!kiemTraEmail(values.email)) {
    loi.email = 'Email không đúng định dạng'
  }
  if (!values.matKhau) {
    loi.matKhau = 'Mật khẩu không được để trống'
  }
  return loi
}

const { form, loi, dangGui, loiServer, kiemTraForm, xoaLoi } = useForm<FormDangNhap>(
  { email: '', matKhau: '', ghiNhoToi: false },
  kiemTraDangNhap,
)

/** Trạng thái hiện/ẩn mật khẩu */
const hienMatKhau = ref(false)

/** Xử lý submit form đăng nhập */
async function xuLyDangNhap(): Promise<void> {
  // Kiểm tra hợp lệ phía client trước
  if (!kiemTraForm()) return

  dangGui.value = true
  loiServer.value = ''

  try {
    const ketQua = await dangNhap({
      email: form.email,
      matKhau: form.matKhau,
    })

    // Lưu thông tin xác thực vào store
    authStore.luuXacThuc(ketQua)

    // Redirect sau đăng nhập: ưu tiên ?redirect=, sau đó theo role
    const redirectPath = route.query.redirect as string | undefined
    if (redirectPath) {
      await router.push(redirectPath)
    } else if (ketQua.vaiTro === 'QUAN_TRI_VIEN') {
      await router.push('/admin/dashboard')
    } else {
      await router.push('/403')
    }
  } catch (err) {
    const loi = err as ErrorResponse
    // Xử lý từng mã lỗi từ backend
    switch (loi.error) {
      case 'DANG_NHAP_THAT_BAI':
        loiServer.value = 'Email hoặc mật khẩu không đúng'
        break
      case 'TAI_KHOAN_BI_KHOA':
        loiServer.value = 'Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.'
        break
      default:
        loiServer.value = loi.message || 'Đã xảy ra lỗi, vui lòng thử lại'
    }
  } finally {
    dangGui.value = false
  }
}
</script>

<template>
  <div class="trang-dang-nhap">
    <!-- Vòng trang trí nền -->
    <div class="hinh-trang-tri hinh-1" aria-hidden="true"></div>
    <div class="hinh-trang-tri hinh-2" aria-hidden="true"></div>
    <div class="hinh-trang-tri hinh-3" aria-hidden="true"></div>

    <div class="khung-form">
      <!-- Logo & Tiêu đề -->
      <div class="dau-trang">
        <div class="bieu-tuong-ung-dung">📚</div>
        <h1 class="tieu-de">Đăng nhập</h1>
        <p class="mo-ta">Chào mừng trở lại! Vui lòng đăng nhập để tiếp tục.</p>
      </div>

      <!-- Thông báo lỗi server -->
      <div v-if="loiServer" class="thong-bao-loi-server" role="alert">
        <span class="bieu-tuong-loi">⚠️</span>
        {{ loiServer }}
      </div>

      <!-- Form đăng nhập -->
      <form class="form" @submit.prevent="xuLyDangNhap" novalidate>
        <!-- Field Email -->
        <div class="nhom-field" :class="{ 'co-loi': loi.email }">
          <label for="email" class="nhan">Email</label>
          <div class="bao-input">
            <span class="bieu-tuong-input">✉️</span>
            <input
              id="email"
              v-model="form.email"
              type="email"
              class="input"
              placeholder="example@gmail.com"
              autocomplete="email"
              :disabled="dangGui"
              @input="xoaLoi('email')"
            />
          </div>
          <p v-if="loi.email" class="thong-bao-loi">{{ loi.email }}</p>
        </div>

        <!-- Field Mật khẩu -->
        <div class="nhom-field" :class="{ 'co-loi': loi.matKhau }">
          <label for="matKhau" class="nhan">Mật khẩu</label>
          <div class="bao-input">
            <span class="bieu-tuong-input">🔒</span>
            <input
              id="matKhau"
              v-model="form.matKhau"
              :type="hienMatKhau ? 'text' : 'password'"
              class="input"
              placeholder="Nhập mật khẩu"
              autocomplete="current-password"
              :disabled="dangGui"
              @input="xoaLoi('matKhau')"
            />
            <button
              type="button"
              class="nut-hien-mat-khau"
              @click="hienMatKhau = !hienMatKhau"
              :aria-label="hienMatKhau ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'"
            >
              {{ hienMatKhau ? '🙈' : '👁️' }}
            </button>
          </div>
          <p v-if="loi.matKhau" class="thong-bao-loi">{{ loi.matKhau }}</p>
        </div>

        <!-- Dòng tùy chọn -->
        <div class="dong-tuy-chon">
          <label class="nhan-checkbox">
            <input v-model="form.ghiNhoToi" type="checkbox" class="checkbox" />
            <span class="van-ban-checkbox">Ghi nhớ đăng nhập</span>
          </label>
          <RouterLink to="/forgot-password" class="lien-ket-quen">Quên mật khẩu?</RouterLink>
        </div>

        <!-- Nút đăng nhập -->
        <button type="submit" class="nut-chinh" :disabled="dangGui">
          <span v-if="dangGui" class="spinner" aria-hidden="true"></span>
          <span>{{ dangGui ? 'Đang đăng nhập...' : 'Đăng nhập' }}</span>
        </button>
      </form>

      <!-- Không có tự đăng ký — chỉ admin tạo tài khoản -->
      <p class="dong-chuyen-trang">
        Liên hệ quản trị viên để được cấp tài khoản.
      </p>
    </div>
  </div>
</template>

<style scoped>
/* ===== LAYOUT ===== */
.trang-dang-nhap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 1rem;
  position: relative;
  overflow: hidden;
}

/* ===== TRANG TRÍ NỀN ===== */
.hinh-trang-tri {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
  pointer-events: none;
}
.hinh-1 {
  width: 400px;
  height: 400px;
  background: var(--mau-chinh);
  top: -100px;
  right: -100px;
}
.hinh-2 {
  width: 300px;
  height: 300px;
  background: var(--mau-phu);
  bottom: -80px;
  left: -80px;
}
.hinh-3 {
  width: 200px;
  height: 200px;
  background: var(--mau-phu);
  top: 50%;
  left: 30%;
}

/* ===== KHUNG FORM (GLASSMORPHISM) ===== */
.khung-form {
  width: 100%;
  max-width: 440px;
  background: var(--glass-nen);
  border: 1px solid var(--glass-vien);
  border-radius: var(--radius-lon);
  padding: 2.5rem;
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  box-shadow:
    0 25px 50px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.05);
  animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  z-index: 1;
}

/* ===== ĐẦU TRANG ===== */
.dau-trang {
  text-align: center;
  margin-bottom: 2rem;
}
.bieu-tuong-ung-dung {
  font-size: 3rem;
  margin-bottom: 0.75rem;
  display: block;
}
.tieu-de {
  font-size: 1.75rem;
  font-weight: 800;
  background: var(--color-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
}
.mo-ta {
  color: var(--mau-chu-mo);
  font-size: 0.9rem;
}

/* ===== THÔNG BÁO LỖI SERVER ===== */
.thong-bao-loi-server {
  background: rgba(255, 107, 107, 0.1);
  border: 1px solid rgba(255, 107, 107, 0.3);
  border-radius: var(--radius-vua);
  padding: 0.875rem 1rem;
  margin-bottom: 1.5rem;
  color: var(--mau-loi);
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  animation: slideDown 0.3s ease;
}

/* ===== FORM ===== */
.form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

/* ===== NHÓM FIELD ===== */
.nhom-field {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}
.nhan {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--mau-chu);
}
.bao-input {
  position: relative;
  display: flex;
  align-items: center;
}
.bieu-tuong-input {
  position: absolute;
  left: 1rem;
  font-size: 1rem;
  pointer-events: none;
  z-index: 1;
}
.input {
  width: 100%;
  padding: 0.875rem 1rem 0.875rem 2.75rem;
  background: rgba(255, 255, 255, 0.05);
  border: 1.5px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-vua);
  color: var(--mau-chu);
  font-family: inherit;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  outline: none;
}
.input::placeholder {
  color: var(--mau-chu-rat-mo);
}
.input:focus {
  border-color: var(--mau-chinh);
  box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.2), var(--glow-chinh);
  background: rgba(6, 182, 212, 0.05);
}
.input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.co-loi .input {
  border-color: var(--mau-loi);
}
.co-loi .input:focus {
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.2);
}

/* Nút hiện/ẩn mật khẩu */
.nut-hien-mat-khau {
  position: absolute;
  right: 0.75rem;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
  padding: 0.25rem;
  opacity: 0.7;
  transition: opacity 0.2s;
}
.nut-hien-mat-khau:hover {
  opacity: 1;
}

/* Thông báo lỗi field */
.thong-bao-loi {
  font-size: 0.8rem;
  color: var(--mau-loi);
  animation: slideDown 0.25s ease;
}

/* ===== DÒNG TÙY CHỌN ===== */
.dong-tuy-chon {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.nhan-checkbox {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}
.checkbox {
  width: 16px;
  height: 16px;
  accent-color: var(--mau-chinh);
  cursor: pointer;
}
.van-ban-checkbox {
  font-size: 0.875rem;
  color: var(--mau-chu-mo);
}
.lien-ket-quen {
  font-size: 0.875rem;
  color: var(--mau-chinh);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}
.lien-ket-quen:hover {
  color: var(--mau-phu);
}

/* ===== NÚT CHÍNH ===== */
.nut-chinh {
  width: 100%;
  padding: 0.9rem;
  background: var(--color-primary);
  border: none;
  border-radius: var(--radius-tron);
  color: white;
  font-family: inherit;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
  letter-spacing: 0.3px;
  margin-top: 0.5rem;
}
.nut-chinh:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--glow-chinh), 0 8px 25px rgba(6, 182, 212, 0.3);
}
.nut-chinh:active:not(:disabled) {
  transform: translateY(0);
}
.nut-chinh:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Spinner loading */
.spinner {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 2.5px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

/* ===== DÒNG CHUYỂN TRANG ===== */
.dong-chuyen-trang {
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.9rem;
  color: var(--mau-chu-mo);
}
.lien-ket-chinh {
  color: var(--mau-chinh);
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s;
  margin-left: 0.25rem;
}
.lien-ket-chinh:hover {
  color: var(--mau-phu);
}

/* ===== RESPONSIVE ===== */
@media (max-width: 480px) {
  .khung-form {
    padding: 2rem 1.5rem;
  }
}
</style>
