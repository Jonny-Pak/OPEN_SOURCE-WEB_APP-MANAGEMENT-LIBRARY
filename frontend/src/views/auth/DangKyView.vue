<!--
  DangKyView.vue — Trang đăng ký tài khoản mới.
  Tích hợp với API POST /api/auth/register.
  Sau đăng ký thành công → redirect /xac-nhan-email.
-->
<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { dangKy } from '@/services/authService'
import {
  useForm,
  kiemTraEmail,
  kiemTraSoDienThoai,
  kiemTraMatKhau,
  type LoiForm,
} from '@/composables/useForm'
import type { ErrorResponse } from '@/types/auth'

const router = useRouter()
const authStore = useAuthStore()

/** Kiểu dữ liệu form đăng ký */
interface FormDangKy extends Record<string, unknown> {
  hoDem: string
  ten: string
  email: string
  matKhau: string
  xacNhanMatKhau: string
  soDienThoai: string
}

/** Hàm kiểm tra hợp lệ form đăng ký */
function kiemTraDangKy(values: FormDangKy): LoiForm<FormDangKy> {
  const loi: LoiForm<FormDangKy> = {}

  if (!values.hoDem.trim()) loi.hoDem = 'Họ đệm không được để trống'

  if (!values.ten.trim()) loi.ten = 'Tên không được để trống'

  if (!values.email) {
    loi.email = 'Email không được để trống'
  } else if (!kiemTraEmail(values.email)) {
    loi.email = 'Email không đúng định dạng'
  }

  if (!values.matKhau) {
    loi.matKhau = 'Mật khẩu không được để trống'
  } else if (!kiemTraMatKhau(values.matKhau)) {
    loi.matKhau = 'Mật khẩu phải có ít nhất 6 ký tự'
  }

  if (!values.xacNhanMatKhau) {
    loi.xacNhanMatKhau = 'Vui lòng xác nhận mật khẩu'
  } else if (values.matKhau !== values.xacNhanMatKhau) {
    loi.xacNhanMatKhau = 'Mật khẩu xác nhận không khớp'
  }

  if (!values.soDienThoai) {
    loi.soDienThoai = 'Số điện thoại không được để trống'
  } else if (!kiemTraSoDienThoai(values.soDienThoai)) {
    loi.soDienThoai = 'Số điện thoại phải có 10 chữ số và bắt đầu bằng 0'
  }

  return loi
}

const { form, loi, dangGui, loiServer, kiemTraForm, xoaLoi } = useForm<FormDangKy>(
  { hoDem: '', ten: '', email: '', matKhau: '', xacNhanMatKhau: '', soDienThoai: '' },
  kiemTraDangKy,
)

/** Trạng thái hiện/ẩn mật khẩu */
const hienMatKhau = ref(false)
const hienXacNhan = ref(false)

/** Xử lý submit form đăng ký */
async function xuLyDangKy(): Promise<void> {
  if (!kiemTraForm()) return

  dangGui.value = true
  loiServer.value = ''

  try {
    // Integrate directly with authStore register action
    await authStore.register({
      hoDem: form.hoDem,
      ten: form.ten,
      email: form.email,
      matKhau: form.matKhau,
      soDienThoai: form.soDienThoai,
    })

    await router.push('/xac-nhan-email')
  } catch (err: any) {
    const errorResponse = err.response?.data || err
    if (errorResponse.error === 'EMAIL_DA_TON_TAI' || errorResponse.message?.includes('Email')) {
      loi.email = 'Email này đã được sử dụng'
    } else if (errorResponse.error === 'SO_DIEN_THOAI_DA_TON_TAI' || errorResponse.message?.includes('Số điện thoại')) {
      loi.soDienThoai = 'Số điện thoại này đã được sử dụng'
    } else if (errorResponse.error === 'INVALID_INPUT') {
      loiServer.value = 'Dữ liệu không hợp lệ, vui lòng kiểm tra lại'
    } else {
      loiServer.value = errorResponse.message || 'Đã xảy ra lỗi, vui lòng thử lại'
    }
  } finally {
    dangGui.value = false
  }
}
</script>

<template>
  <div class="trang-dang-ky">
    <!-- Vòng trang trí nền -->
    <div class="hinh-trang-tri hinh-1" aria-hidden="true"></div>
    <div class="hinh-trang-tri hinh-2" aria-hidden="true"></div>
    <div class="hinh-trang-tri hinh-3" aria-hidden="true"></div>

    <div class="khung-form">
      <!-- Đầu trang -->
      <div class="dau-trang">
        <div class="bieu-tuong-ung-dung">📖</div>
        <h1 class="tieu-de">Tạo tài khoản</h1>
        <p class="mo-ta">Tham gia cùng chúng tôi để trải nghiệm thư viện trực tuyến</p>
      </div>

      <!-- Thông báo lỗi server -->
      <div v-if="loiServer" class="thong-bao-loi-server" role="alert">
        <span class="bieu-tuong-loi">⚠️</span>
        {{ loiServer }}
      </div>

      <!-- Form đăng ký -->
      <form class="form" @submit.prevent="xuLyDangKy" novalidate>
        <!-- Hàng: Họ đệm + Tên -->
        <div class="hang-doi">
          <!-- Field Họ đệm -->
          <div class="nhom-field" :class="{ 'co-loi': loi.hoDem }">
            <label for="hoDem" class="nhan">Họ đệm</label>
            <div class="bao-input">
              <span class="bieu-tuong-input">👤</span>
              <input
                id="hoDem"
                v-model="form.hoDem"
                type="text"
                class="input"
                placeholder="Nguyễn Văn"
                :disabled="dangGui"
                @input="xoaLoi('hoDem')"
              />
            </div>
            <p v-if="loi.hoDem" class="thong-bao-loi">{{ loi.hoDem }}</p>
          </div>

          <!-- Field Tên -->
          <div class="nhom-field" :class="{ 'co-loi': loi.ten }">
            <label for="ten" class="nhan">Tên</label>
            <div class="bao-input">
              <span class="bieu-tuong-input">✏️</span>
              <input
                id="ten"
                v-model="form.ten"
                type="text"
                class="input"
                placeholder="A"
                :disabled="dangGui"
                @input="xoaLoi('ten')"
              />
            </div>
            <p v-if="loi.ten" class="thong-bao-loi">{{ loi.ten }}</p>
          </div>
        </div>

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

        <!-- Field Số điện thoại -->
        <div class="nhom-field" :class="{ 'co-loi': loi.soDienThoai }">
          <label for="soDienThoai" class="nhan">Số điện thoại</label>
          <div class="bao-input">
            <span class="bieu-tuong-input">📱</span>
            <input
              id="soDienThoai"
              v-model="form.soDienThoai"
              type="tel"
              class="input"
              placeholder="0901234567"
              maxlength="10"
              :disabled="dangGui"
              @input="xoaLoi('soDienThoai')"
            />
          </div>
          <p v-if="loi.soDienThoai" class="thong-bao-loi">{{ loi.soDienThoai }}</p>
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
              placeholder="Ít nhất 6 ký tự"
              autocomplete="new-password"
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

        <!-- Field Xác nhận mật khẩu -->
        <div class="nhom-field" :class="{ 'co-loi': loi.xacNhanMatKhau }">
          <label for="xacNhanMatKhau" class="nhan">Xác nhận mật khẩu</label>
          <div class="bao-input">
            <span class="bieu-tuong-input">🔑</span>
            <input
              id="xacNhanMatKhau"
              v-model="form.xacNhanMatKhau"
              :type="hienXacNhan ? 'text' : 'password'"
              class="input"
              placeholder="Nhập lại mật khẩu"
              autocomplete="new-password"
              :disabled="dangGui"
              @input="xoaLoi('xacNhanMatKhau')"
            />
            <button
              type="button"
              class="nut-hien-mat-khau"
              @click="hienXacNhan = !hienXacNhan"
              :aria-label="hienXacNhan ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'"
            >
              {{ hienXacNhan ? '🙈' : '👁️' }}
            </button>
          </div>
          <p v-if="loi.xacNhanMatKhau" class="thong-bao-loi">{{ loi.xacNhanMatKhau }}</p>
        </div>

        <!-- Nút đăng ký -->
        <button type="submit" class="nut-chinh" :disabled="dangGui">
          <span v-if="dangGui" class="spinner" aria-hidden="true"></span>
          <span>{{ dangGui ? 'Đang tạo tài khoản...' : 'Tạo tài khoản' }}</span>
        </button>
      </form>

      <!-- Link sang đăng nhập -->
      <p class="dong-chuyen-trang">
        Đã có tài khoản?
        <RouterLink to="/login" class="lien-ket-chinh">Đăng nhập</RouterLink>
      </p>
    </div>
  </div>
</template>

<style scoped>
.trang-dang-ky {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 1rem;
  position: relative;
  overflow: hidden;
}

/* Trang trí nền */
.hinh-trang-tri {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
  pointer-events: none;
}
.hinh-1 {
  width: 500px;
  height: 500px;
  background: var(--mau-phu);
  top: -150px;
  left: -100px;
}
.hinh-2 {
  width: 350px;
  height: 350px;
  background: var(--mau-chinh);
  bottom: -100px;
  right: -80px;
}
.hinh-3 {
  width: 250px;
  height: 250px;
  background: var(--mau-phu);
  top: 40%;
  right: 20%;
}

/* Khung form */
.khung-form {
  width: 100%;
  max-width: 520px;
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

.dau-trang {
  text-align: center;
  margin-bottom: 2rem;
}
.bieu-tuong-ung-dung {
  font-size: 3rem;
  display: block;
  margin-bottom: 0.75rem;
}
.tieu-de {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--color-primary);
  margin-bottom: 0.5rem;
}
.mo-ta {
  color: var(--mau-chu-mo);
  font-size: 0.875rem;
}

/* Lỗi server */
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

.form {
  display: flex;
  flex-direction: column;
  gap: 1.125rem;
}

/* Hàng 2 cột cho họ đệm + tên */
.hang-doi {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.nhom-field {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
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
  left: 0.875rem;
  font-size: 0.9rem;
  pointer-events: none;
  z-index: 1;
}
.input {
  width: 100%;
  padding: 0.8rem 1rem 0.8rem 2.6rem;
  background: rgba(255, 255, 255, 0.05);
  border: 1.5px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-vua);
  color: var(--mau-chu);
  font-family: inherit;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  outline: none;
}
.input::placeholder {
  color: var(--mau-chu-rat-mo);
}
.input:focus {
  border-color: var(--mau-chinh);
  box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.2);
  background: rgba(6, 182, 212, 0.05);
}
.input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.co-loi .input {
  border-color: var(--mau-loi);
}
.thong-bao-loi {
  font-size: 0.775rem;
  color: var(--mau-loi);
  animation: slideDown 0.25s ease;
}
.nut-hien-mat-khau {
  position: absolute;
  right: 0.75rem;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  padding: 0.25rem;
  opacity: 0.7;
  transition: opacity 0.2s;
}
.nut-hien-mat-khau:hover {
  opacity: 1;
}

/* Nút chính */
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
  margin-top: 0.5rem;
}
.nut-chinh:hover:not(:disabled) {
  transform: translateY(-2px);
  background: var(--color-primary-hover);
}
.nut-chinh:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
.spinner {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 2.5px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

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
  margin-left: 0.25rem;
  transition: color 0.2s;
}
.lien-ket-chinh:hover {
  color: var(--mau-phu);
}

@media (max-width: 480px) {
  .khung-form {
    padding: 2rem 1.25rem;
  }
  .hang-doi {
    grid-template-columns: 1fr;
  }
}
</style>
