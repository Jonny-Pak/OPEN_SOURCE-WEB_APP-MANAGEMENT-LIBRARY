<!--
  XacNhanEmailView.vue — Trang xác nhận email sau khi đăng ký.
  Hiển thị thông báo và cho phép gửi lại email xác nhận.
  NOTE: API backend chưa có — dùng mock logic, xem comment TODO.
-->
<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { guiLaiXacNhan } from '@/services/authService'

const authStore = useAuthStore()

/** Lấy email từ store (đã đăng ký thành công) */
const email = computed(() => authStore.thongTinNguoiDung?.email ?? 'email của bạn')

/** Trạng thái đang gửi lại */
const dangGuiLai = ref(false)

/** Thông báo sau khi gửi */
const thongBaoGuiLai = ref('')
const loaiThongBao = ref<'thanh-cong' | 'loi'>('thanh-cong')

/** Đếm ngược (giây) trước khi có thể gửi lại */
const demNguoc = ref(0)
let boDemId: ReturnType<typeof setInterval> | null = null

/** Nút gửi lại bị vô hiệu khi đang đếm ngược */
const nutBiVoHieu = computed(() => dangGuiLai.value || demNguoc.value > 0)

/** Bắt đầu đếm ngược 60 giây */
function batDauDemNguoc(): void {
  demNguoc.value = 60
  boDemId = setInterval(() => {
    demNguoc.value--
    if (demNguoc.value <= 0) {
      clearInterval(boDemId!)
      boDemId = null
    }
  }, 1000)
}

/**
 * Xử lý gửi lại email xác nhận.
 * TODO: Tích hợp API POST /api/auth/gui-lai-xac-nhan khi backend sẵn sàng.
 */
async function xuLyGuiLai(): Promise<void> {
  dangGuiLai.value = true
  thongBaoGuiLai.value = ''

  try {
    // TODO: Thay bằng API thật khi backend sẵn sàng
    await guiLaiXacNhan(email.value)

    thongBaoGuiLai.value = 'Đã gửi lại email xác nhận thành công!'
    loaiThongBao.value = 'thanh-cong'
    batDauDemNguoc()
  } catch {
    thongBaoGuiLai.value = 'Gửi lại thất bại, vui lòng thử lại sau'
    loaiThongBao.value = 'loi'
  } finally {
    dangGuiLai.value = false
  }
}

// Dọn dẹp interval khi component bị hủy
onUnmounted(() => {
  if (boDemId) clearInterval(boDemId)
})
</script>

<template>
  <div class="trang-xac-nhan">
    <!-- Nền trang trí -->
    <div class="hinh-trang-tri hinh-1" aria-hidden="true"></div>
    <div class="hinh-trang-tri hinh-2" aria-hidden="true"></div>

    <div class="khung-noi-dung">
      <!-- Biểu tượng phong bì động -->
      <div class="bieu-tuong-phong-bi">
        <span class="phong-bi">📧</span>
        <div class="vong-suc-song vong-1"></div>
        <div class="vong-suc-song vong-2"></div>
        <div class="vong-suc-song vong-3"></div>
      </div>

      <h1 class="tieu-de">Kiểm tra hộp thư!</h1>

      <p class="mo-ta">
        Chúng tôi đã gửi email xác nhận đến
      </p>
      <p class="email-hien-thi">{{ email }}</p>

      <p class="huong-dan">
        Vui lòng kiểm tra hộp thư (kể cả thư mục Spam) và nhấp vào liên kết
        xác nhận để kích hoạt tài khoản.
      </p>

      <!-- Thông báo sau khi gửi lại -->
      <div
        v-if="thongBaoGuiLai"
        class="thong-bao"
        :class="loaiThongBao"
        role="alert"
      >
        <span>{{ loaiThongBao === 'thanh-cong' ? '✅' : '⚠️' }}</span>
        {{ thongBaoGuiLai }}
      </div>

      <!-- Nút gửi lại -->
      <button class="nut-gui-lai" :disabled="nutBiVoHieu" @click="xuLyGuiLai">
        <span v-if="dangGuiLai" class="spinner" aria-hidden="true"></span>
        <span v-if="dangGuiLai">Đang gửi...</span>
        <span v-else-if="demNguoc > 0">Gửi lại sau {{ demNguoc }}s</span>
        <span v-else>🔄 Gửi lại email</span>
      </button>

      <!-- Thanh phân cách -->
      <div class="duong-phan-cach">
        <span class="van-ban-phan-cach">hoặc</span>
      </div>

      <!-- Link quay về đăng nhập -->
      <RouterLink to="/login" class="lien-ket-doi-email">
        ← Quay về đăng nhập
      </RouterLink>
    </div>
  </div>
</template>

<style scoped>
.trang-xac-nhan {
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
  background: var(--mau-chinh);
  top: -100px;
  right: -100px;
}
.hinh-2 {
  width: 350px;
  height: 350px;
  background: var(--mau-phu);
  bottom: -100px;
  left: -80px;
}

/* Khung nội dung */
.khung-noi-dung {
  width: 100%;
  max-width: 460px;
  background: var(--glass-nen);
  border: 1px solid var(--glass-vien);
  border-radius: var(--radius-lon);
  padding: 3rem 2.5rem;
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  text-align: center;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4);
  animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  z-index: 1;
}

/* Biểu tượng với vòng sóng */
.bieu-tuong-phong-bi {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 100px;
  margin: 0 auto 2rem;
}
.phong-bi {
  font-size: 3.5rem;
  z-index: 1;
  position: relative;
}
.vong-suc-song {
  position: absolute;
  border-radius: 50%;
  border: 2px solid var(--mau-chinh);
  animation: soiNuoc 2s ease-out infinite;
}
.vong-1 { width: 60px; height: 60px; animation-delay: 0s; }
.vong-2 { width: 80px; height: 80px; animation-delay: 0.4s; }
.vong-3 { width: 100px; height: 100px; animation-delay: 0.8s; }

@keyframes soiNuoc {
  0% { opacity: 0.8; transform: scale(0.8); }
  100% { opacity: 0; transform: scale(1.3); }
}

.tieu-de {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--color-primary);
  margin-bottom: 1rem;
}

.mo-ta {
  color: var(--mau-chu-mo);
  font-size: 0.95rem;
  margin-bottom: 0.25rem;
}

.email-hien-thi {
  font-weight: 700;
  color: var(--mau-chinh);
  font-size: 1rem;
  margin-bottom: 1rem;
  word-break: break-all;
}

.huong-dan {
  color: var(--mau-chu-mo);
  font-size: 0.875rem;
  line-height: 1.7;
  margin-bottom: 2rem;
}

/* Thông báo */
.thong-bao {
  padding: 0.875rem 1rem;
  border-radius: var(--radius-vua);
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  animation: slideDown 0.3s ease;
}
.thong-bao.thanh-cong {
  background: rgba(81, 207, 102, 0.1);
  border: 1px solid rgba(81, 207, 102, 0.3);
  color: var(--mau-thanh-cong);
}
.thong-bao.loi {
  background: rgba(255, 107, 107, 0.1);
  border: 1px solid rgba(255, 107, 107, 0.3);
  color: var(--mau-loi);
}

/* Nút gửi lại */
.nut-gui-lai {
  width: 100%;
  padding: 0.875rem;
  background: var(--color-primary);
  border: none;
  border-radius: var(--radius-tron);
  color: white;
  font-family: inherit;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
  margin-bottom: 1.5rem;
}
.nut-gui-lai:hover:not(:disabled) {
  transform: translateY(-2px);
  background: var(--color-primary-hover);
}
.nut-gui-lai:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}
.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  display: inline-block;
}

/* Đường phân cách */
.duong-phan-cach {
  position: relative;
  margin-bottom: 1.5rem;
}
.duong-phan-cach::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: var(--glass-vien);
}
.van-ban-phan-cach {
  position: relative;
  background: #1a1a2e;
  padding: 0 1rem;
  color: var(--mau-chu-mo);
  font-size: 0.85rem;
}

/* Link đổi email */
.lien-ket-doi-email {
  color: var(--mau-chu-mo);
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.2s;
}
.lien-ket-doi-email:hover {
  color: var(--mau-chinh);
}
</style>
