<!--
  QuenMatKhauView.vue — Trang quên mật khẩu.
  Luồng 2 bước: Nhập email → Nhập OTP + mật khẩu mới.
  NOTE: Dùng mock logic, xem comment TODO để tích hợp API thật.
-->
<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { quenMatKhau, datLaiMatKhau } from '@/services/authService'
import { kiemTraEmail, kiemTraMatKhau } from '@/composables/useForm'

const router = useRouter()

/** Bước hiện tại: 1 = nhập email, 2 = nhập OTP + mật khẩu mới */
const buocHienTai = ref<1 | 2>(1)

// ===== BƯỚC 1: NHẬP EMAIL =====
const emailDatLai = ref('')
const loiEmail = ref('')
const dangGuiBuoc1 = ref(false)

/** Xử lý gửi yêu cầu đặt lại mật khẩu */
async function xuLyGuiEmail(): Promise<void> {
  loiEmail.value = ''

  if (!emailDatLai.value) {
    loiEmail.value = 'Email không được để trống'
    return
  }
  if (!kiemTraEmail(emailDatLai.value)) {
    loiEmail.value = 'Email không đúng định dạng'
    return
  }

  dangGuiBuoc1.value = true
  try {
    // TODO: Tích hợp API POST /api/auth/quen-mat-khau khi backend sẵn sàng
    await quenMatKhau(emailDatLai.value)
    buocHienTai.value = 2
  } catch {
    loiEmail.value = 'Không thể gửi mã xác thực, vui lòng thử lại'
  } finally {
    dangGuiBuoc1.value = false
  }
}

// ===== BƯỚC 2: NHẬP OTP + MẬT KHẨU MỚI =====
interface FormBuoc2 {
  otp: string
  matKhauMoi: string
  xacNhanMatKhauMoi: string
}

const formBuoc2 = reactive<FormBuoc2>({ otp: '', matKhauMoi: '', xacNhanMatKhauMoi: '' })
const loiBuoc2 = reactive<Partial<Record<keyof FormBuoc2, string>>>({})
const dangGuiBuoc2 = ref(false)
const thanhCong = ref(false)
const hienMatKhauMoi = ref(false)
const hienXacNhanMoi = ref(false)

/** Kiểm tra hợp lệ bước 2 */
function kiemTraBuoc2(): boolean {
  loiBuoc2.otp = undefined
  loiBuoc2.matKhauMoi = undefined
  loiBuoc2.xacNhanMatKhauMoi = undefined

  let hopLe = true

  if (!formBuoc2.otp || formBuoc2.otp.length !== 6) {
    loiBuoc2.otp = 'Mã xác thực phải có đúng 6 chữ số'
    hopLe = false
  }
  if (!formBuoc2.matKhauMoi) {
    loiBuoc2.matKhauMoi = 'Mật khẩu mới không được để trống'
    hopLe = false
  } else if (!kiemTraMatKhau(formBuoc2.matKhauMoi)) {
    loiBuoc2.matKhauMoi = 'Mật khẩu phải có ít nhất 6 ký tự'
    hopLe = false
  }
  if (!formBuoc2.xacNhanMatKhauMoi) {
    loiBuoc2.xacNhanMatKhauMoi = 'Vui lòng xác nhận mật khẩu mới'
    hopLe = false
  } else if (formBuoc2.matKhauMoi !== formBuoc2.xacNhanMatKhauMoi) {
    loiBuoc2.xacNhanMatKhauMoi = 'Mật khẩu xác nhận không khớp'
    hopLe = false
  }

  return hopLe
}

/** Xử lý đặt lại mật khẩu */
async function xuLyDatLai(): Promise<void> {
  if (!kiemTraBuoc2()) return

  dangGuiBuoc2.value = true
  try {
    // TODO: Tích hợp API POST /api/auth/dat-lai-mat-khau khi backend sẵn sàng
    await datLaiMatKhau(emailDatLai.value, formBuoc2.otp, formBuoc2.matKhauMoi)
    thanhCong.value = true

    // Chờ 2 giây rồi redirect về trang đăng nhập
    setTimeout(() => router.push('/login'), 2000)
  } catch {
    loiBuoc2.otp = 'Mã xác thực không hợp lệ hoặc đã hết hạn'
  } finally {
    dangGuiBuoc2.value = false
  }
}

/** Chỉ cho phép nhập số vào ô OTP */
function chiNhapSo(e: Event): void {
  const input = e.target as HTMLInputElement
  input.value = input.value.replace(/\D/g, '').slice(0, 6)
  formBuoc2.otp = input.value
}
</script>

<template>
  <div class="trang-quen-mat-khau">
    <!-- Trang trí nền -->
    <div class="hinh-trang-tri hinh-1" aria-hidden="true"></div>
    <div class="hinh-trang-tri hinh-2" aria-hidden="true"></div>

    <div class="khung-form">

      <!-- ===== TRẠNG THÁI THÀNH CÔNG ===== -->
      <div v-if="thanhCong" class="man-hinh-thanh-cong">
        <div class="bieu-tuong-thanh-cong">✅</div>
        <h1 class="tieu-de">Đặt lại thành công!</h1>
        <p class="mo-ta">Mật khẩu đã được cập nhật. Đang chuyển đến trang đăng nhập...</p>
        <div class="spinner-lon" aria-hidden="true"></div>
      </div>

      <!-- ===== BƯỚC 1: NHẬP EMAIL ===== -->
      <div v-else-if="buocHienTai === 1">
        <!-- Đầu trang -->
        <div class="dau-trang">
          <div class="bieu-tuong-ung-dung">🔐</div>
          <h1 class="tieu-de">Quên mật khẩu?</h1>
          <p class="mo-ta">Nhập email để chúng tôi gửi mã đặt lại mật khẩu.</p>
        </div>

        <!-- Chỉ báo bước -->
        <div class="chi-bao-buoc">
          <div class="buoc buoc-hien-tai">
            <span class="so-buoc">1</span>
            <span class="ten-buoc">Nhập email</span>
          </div>
          <div class="duong-noi"></div>
          <div class="buoc">
            <span class="so-buoc">2</span>
            <span class="ten-buoc">Đặt lại mật khẩu</span>
          </div>
        </div>

        <!-- Form bước 1 -->
        <form @submit.prevent="xuLyGuiEmail" novalidate>
          <div class="nhom-field" :class="{ 'co-loi': loiEmail }">
            <label for="emailDatLai" class="nhan">Email đã đăng ký</label>
            <div class="bao-input">
              <span class="bieu-tuong-input">✉️</span>
              <input
                id="emailDatLai"
                v-model="emailDatLai"
                type="email"
                class="input"
                placeholder="example@gmail.com"
                autocomplete="email"
                :disabled="dangGuiBuoc1"
                @input="loiEmail = ''"
              />
            </div>
            <p v-if="loiEmail" class="thong-bao-loi">{{ loiEmail }}</p>
          </div>

          <button type="submit" class="nut-chinh" :disabled="dangGuiBuoc1">
            <span v-if="dangGuiBuoc1" class="spinner" aria-hidden="true"></span>
            <span>{{ dangGuiBuoc1 ? 'Đang gửi mã...' : '📨 Gửi mã đặt lại' }}</span>
          </button>
        </form>

        <p class="dong-chuyen-trang">
          <RouterLink to="/login" class="lien-ket-phu">← Quay lại đăng nhập</RouterLink>
        </p>
      </div>

      <!-- ===== BƯỚC 2: OTP + MẬT KHẨU MỚI ===== -->
      <div v-else>
        <!-- Đầu trang -->
        <div class="dau-trang">
          <div class="bieu-tuong-ung-dung">🔑</div>
          <h1 class="tieu-de">Đặt mật khẩu mới</h1>
          <p class="mo-ta">
            Nhập mã xác thực đã gửi đến<br />
            <strong class="email-highlight">{{ emailDatLai }}</strong>
          </p>
        </div>

        <!-- Chỉ báo bước -->
        <div class="chi-bao-buoc">
          <div class="buoc buoc-hoan-thanh">
            <span class="so-buoc">✓</span>
            <span class="ten-buoc">Nhập email</span>
          </div>
          <div class="duong-noi duong-hoan-thanh"></div>
          <div class="buoc buoc-hien-tai">
            <span class="so-buoc">2</span>
            <span class="ten-buoc">Đặt lại mật khẩu</span>
          </div>
        </div>

        <!-- Form bước 2 -->
        <form @submit.prevent="xuLyDatLai" novalidate>
          <!-- Field OTP -->
          <div class="nhom-field" :class="{ 'co-loi': loiBuoc2.otp }" style="margin-bottom: 1.125rem;">
            <label for="otp" class="nhan">Mã xác thực (6 chữ số)</label>
            <div class="bao-input">
              <span class="bieu-tuong-input">🔢</span>
              <input
                id="otp"
                :value="formBuoc2.otp"
                type="text"
                inputmode="numeric"
                class="input input-otp"
                placeholder="123456"
                maxlength="6"
                :disabled="dangGuiBuoc2"
                @input="chiNhapSo"
                @change="loiBuoc2.otp = undefined"
              />
            </div>
            <p v-if="loiBuoc2.otp" class="thong-bao-loi">{{ loiBuoc2.otp }}</p>
          </div>

          <!-- Field mật khẩu mới -->
          <div class="nhom-field" :class="{ 'co-loi': loiBuoc2.matKhauMoi }" style="margin-bottom: 1.125rem;">
            <label for="matKhauMoi" class="nhan">Mật khẩu mới</label>
            <div class="bao-input">
              <span class="bieu-tuong-input">🔒</span>
              <input
                id="matKhauMoi"
                v-model="formBuoc2.matKhauMoi"
                :type="hienMatKhauMoi ? 'text' : 'password'"
                class="input"
                placeholder="Ít nhất 6 ký tự"
                autocomplete="new-password"
                :disabled="dangGuiBuoc2"
                @input="loiBuoc2.matKhauMoi = undefined"
              />
              <button
                type="button"
                class="nut-hien-mat-khau"
                @click="hienMatKhauMoi = !hienMatKhauMoi"
              >
                {{ hienMatKhauMoi ? '🙈' : '👁️' }}
              </button>
            </div>
            <p v-if="loiBuoc2.matKhauMoi" class="thong-bao-loi">{{ loiBuoc2.matKhauMoi }}</p>
          </div>

          <!-- Field xác nhận mật khẩu mới -->
          <div class="nhom-field" :class="{ 'co-loi': loiBuoc2.xacNhanMatKhauMoi }" style="margin-bottom: 1.5rem;">
            <label for="xacNhanMatKhauMoi" class="nhan">Xác nhận mật khẩu mới</label>
            <div class="bao-input">
              <span class="bieu-tuong-input">🔑</span>
              <input
                id="xacNhanMatKhauMoi"
                v-model="formBuoc2.xacNhanMatKhauMoi"
                :type="hienXacNhanMoi ? 'text' : 'password'"
                class="input"
                placeholder="Nhập lại mật khẩu mới"
                autocomplete="new-password"
                :disabled="dangGuiBuoc2"
                @input="loiBuoc2.xacNhanMatKhauMoi = undefined"
              />
              <button
                type="button"
                class="nut-hien-mat-khau"
                @click="hienXacNhanMoi = !hienXacNhanMoi"
              >
                {{ hienXacNhanMoi ? '🙈' : '👁️' }}
              </button>
            </div>
            <p v-if="loiBuoc2.xacNhanMatKhauMoi" class="thong-bao-loi">{{ loiBuoc2.xacNhanMatKhauMoi }}</p>
          </div>

          <button type="submit" class="nut-chinh" :disabled="dangGuiBuoc2">
            <span v-if="dangGuiBuoc2" class="spinner" aria-hidden="true"></span>
            <span>{{ dangGuiBuoc2 ? 'Đang xử lý...' : '🔓 Đặt lại mật khẩu' }}</span>
          </button>
        </form>

        <!-- Gửi lại mã -->
        <p class="dong-gui-lai">
          Không nhận được mã?
          <button class="nut-text" @click="buocHienTai = 1">Gửi lại</button>
        </p>
      </div>

    </div>
  </div>
</template>

<style scoped>
.trang-quen-mat-khau {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 1rem;
  position: relative;
  overflow: hidden;
}

.hinh-trang-tri {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.12;
  pointer-events: none;
}
.hinh-1 {
  width: 400px;
  height: 400px;
  background: var(--mau-phu);
  top: -100px;
  left: -80px;
}
.hinh-2 {
  width: 350px;
  height: 350px;
  background: var(--mau-phu);
  bottom: -80px;
  right: -60px;
}

/* Khung form */
.khung-form {
  width: 100%;
  max-width: 440px;
  background: var(--glass-nen);
  border: 1px solid var(--glass-vien);
  border-radius: var(--radius-lon);
  padding: 2.5rem;
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4);
  animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  z-index: 1;
}

/* Màn hình thành công */
.man-hinh-thanh-cong {
  text-align: center;
  padding: 1rem 0;
}
.bieu-tuong-thanh-cong {
  font-size: 4rem;
  margin-bottom: 1.5rem;
}
.spinner-lon {
  width: 36px;
  height: 36px;
  border: 3px solid rgba(6, 182, 212, 0.2);
  border-top-color: var(--mau-chinh);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 1.5rem auto 0;
}

/* Đầu trang */
.dau-trang {
  text-align: center;
  margin-bottom: 1.75rem;
}
.bieu-tuong-ung-dung {
  font-size: 2.75rem;
  display: block;
  margin-bottom: 0.75rem;
}
.tieu-de {
  font-size: 1.6rem;
  font-weight: 800;
  background: var(--color-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
}
.mo-ta {
  color: var(--mau-chu-mo);
  font-size: 0.875rem;
  line-height: 1.7;
}
.email-highlight {
  color: var(--mau-chinh);
}

/* Chỉ báo bước */
.chi-bao-buoc {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2rem;
  gap: 0;
}
.buoc {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.3rem;
}
.so-buoc {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  border: 1.5px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--mau-chu-mo);
}
.ten-buoc {
  font-size: 0.7rem;
  color: var(--mau-chu-rat-mo);
  white-space: nowrap;
}
.buoc-hien-tai .so-buoc {
  background: var(--color-primary);
  border-color: transparent;
  color: white;
}
.buoc-hien-tai .ten-buoc {
  color: var(--mau-chinh);
  font-weight: 600;
}
.buoc-hoan-thanh .so-buoc {
  background: rgba(81, 207, 102, 0.15);
  border-color: var(--mau-thanh-cong);
  color: var(--mau-thanh-cong);
}
.duong-noi {
  flex: 1;
  height: 1.5px;
  background: rgba(255, 255, 255, 0.1);
  margin: 0 0.75rem;
  margin-bottom: 1.2rem;
  min-width: 40px;
}
.duong-hoan-thanh {
  background: var(--mau-thanh-cong);
  opacity: 0.4;
}

/* Form fields */
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
/* OTP input đặc biệt — font to hơn, căn giữa */
.input-otp {
  letter-spacing: 0.5rem;
  font-size: 1.2rem;
  font-weight: 700;
  text-align: center;
  padding-left: 2.6rem;
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
  margin-bottom: 1rem;
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
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

.dong-chuyen-trang, .dong-gui-lai {
  text-align: center;
  font-size: 0.875rem;
  color: var(--mau-chu-mo);
  margin-top: 0.5rem;
}
.lien-ket-phu {
  color: var(--mau-chu-mo);
  text-decoration: none;
  transition: color 0.2s;
}
.lien-ket-phu:hover {
  color: var(--mau-chinh);
}
.nut-text {
  background: none;
  border: none;
  color: var(--mau-chinh);
  font-family: inherit;
  font-size: inherit;
  font-weight: 600;
  cursor: pointer;
  padding: 0;
  margin-left: 0.25rem;
  transition: color 0.2s;
}
.nut-text:hover {
  color: var(--mau-phu);
}

@media (max-width: 480px) {
  .khung-form {
    padding: 2rem 1.5rem;
  }
}
</style>
