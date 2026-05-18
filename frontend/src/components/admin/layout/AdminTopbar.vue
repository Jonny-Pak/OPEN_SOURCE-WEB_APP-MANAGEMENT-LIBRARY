<!--
  AdminTopbar.vue — Thanh tiêu đề phía trên của trang Admin.
  Hiển thị: hamburger (mobile), tên trang hiện tại, chuông thông báo, thông tin user, nút logout.
-->
<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { thongBaoService } from '@/services/thongBaoService'
import type { ThongBao } from '@/types/thongbao'

defineProps<{ sidebarMo?: boolean }>()
defineEmits<{ 'toggle-sidebar': [] }>()

const authStore = useAuthStore()
const route = useRoute()

/** Bản đồ tên route → tiêu đề trang */
const BAN_DO_TIEU_DE: Record<string, string> = {
  dashboard: 'Dashboard',
  'danh-muc': 'Quản lý Danh mục',
  'sach-list': 'Quản lý Đầu sách',
  'sach-them': 'Thêm Đầu sách',
  'sach-sua': 'Chỉnh sửa Đầu sách',
  'cuon-sach': 'Quản lý Cuốn sách',
  'doc-gia': 'Quản lý Độc giả',
  'dat-cho': 'Quản lý Đặt chỗ',
  'muon-sach': 'Quản lý Mượn sách',
  'tao-phieu-muon': 'Tạo Phiếu mượn',
  'tra-sach': 'Trả sách & Gia hạn',
  phat: 'Quản lý Phạt',
  settings: 'Cài đặt hệ thống',
}

const tieuDeTrang = computed(() => BAN_DO_TIEU_DE[route.name as string] ?? 'Admin')

/** Chữ cái đầu tên người dùng cho avatar */
const kyTuDau = computed(() => {
  const info = authStore.thongTinNguoiDung
  return info ? `${info.hoDem.charAt(0)}${info.ten.charAt(0)}`.toUpperCase() : 'AD'
})

// ===== Chuông thông báo =====
const hienThiThongBao = ref(false)
const danhSachThongBao = ref<ThongBao[]>([])
const soThongBaoChuaDoc = ref(0)
const dangTai = ref(false)
const chuongRef = ref<HTMLElement | null>(null)

const maNguoiDung = computed(() => {
  const id = authStore.thongTinNguoiDung?.maNguoiDung
  return id != null ? String(id) : ''
})

async function taiThongBao() {
  if (!maNguoiDung.value) return
  dangTai.value = true
  try {
    const [danhSach, soLuong] = await Promise.all([
      thongBaoService.layDanhSach(maNguoiDung.value),
      thongBaoService.demChuaDoc(maNguoiDung.value),
    ])
    danhSachThongBao.value = danhSach
    soThongBaoChuaDoc.value = soLuong
  } catch {
    // silent fail
  } finally {
    dangTai.value = false
  }
}

async function toggleThongBao() {
  hienThiThongBao.value = !hienThiThongBao.value
  if (hienThiThongBao.value) {
    await taiThongBao()
  }
}

async function danhDauDaDoc(maThongBao: string) {
  try {
    await thongBaoService.danhDauDaDoc(maThongBao)
    const tb = danhSachThongBao.value.find(t => t.maThongBao === maThongBao)
    if (tb && !tb.daDoc) {
      tb.daDoc = true
      soThongBaoChuaDoc.value = Math.max(0, soThongBaoChuaDoc.value - 1)
    }
  } catch {}
}

async function docHet() {
  if (!maNguoiDung.value) return
  try {
    await thongBaoService.danhDauTatCaDaDoc(maNguoiDung.value)
    danhSachThongBao.value.forEach(tb => (tb.daDoc = true))
    soThongBaoChuaDoc.value = 0
  } catch {}
}

function iconLoai(loai: string) {
  switch (loai) {
    case 'HE_THONG': return '🔔'
    case 'NHAC_NHO': return '⏰'
    case 'CANH_BAO': return '⚠️'
    case 'DAT_CHO': return '📅'
    default: return '📣'
  }
}

function formatNgay(ngayTao: string) {
  try {
    const d = new Date(ngayTao)
    const now = new Date()
    const diffMs = now.getTime() - d.getTime()
    const diffPhut = Math.floor(diffMs / 60000)
    if (diffPhut < 1) return 'Vừa xong'
    if (diffPhut < 60) return `${diffPhut} phút trước`
    const diffGio = Math.floor(diffPhut / 60)
    if (diffGio < 24) return `${diffGio} giờ trước`
    const diffNgay = Math.floor(diffGio / 24)
    return `${diffNgay} ngày trước`
  } catch {
    return ngayTao
  }
}

// Đóng dropdown khi click ngoài
function handleClickOutside(e: MouseEvent) {
  if (chuongRef.value && !chuongRef.value.contains(e.target as Node)) {
    hienThiThongBao.value = false
  }
}

// Poll số thông báo chưa đọc mỗi 60 giây
let pollInterval: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  if (maNguoiDung.value) {
    thongBaoService.demChuaDoc(maNguoiDung.value)
      .then(n => { soThongBaoChuaDoc.value = n })
      .catch(() => {})
    pollInterval = setInterval(async () => {
      if (maNguoiDung.value) {
        try {
          soThongBaoChuaDoc.value = await thongBaoService.demChuaDoc(maNguoiDung.value)
        } catch {}
      }
    }, 60000)
  }
  document.addEventListener('mousedown', handleClickOutside)
})

onUnmounted(() => {
  if (pollInterval) clearInterval(pollInterval)
  document.removeEventListener('mousedown', handleClickOutside)
})

async function dangXuat(): Promise<void> {
  authStore.xoaXacThuc()
  window.location.replace('/login')
}
</script>

<template>
  <header class="topbar">
    <!-- Hamburger (mobile) -->
    <button class="topbar__hamburger" @click="$emit('toggle-sidebar')" aria-label="Mở menu">
      <font-awesome-icon icon="fa-solid fa-bars" />
    </button>

    <!-- Tiêu đề trang hiện tại -->
    <h1 class="topbar__tieu-de">{{ tieuDeTrang }}</h1>

    <!-- Phần phải: chuông + thông tin người dùng -->
    <div class="topbar__nguoi-dung">

      <!-- ===== CHUÔNG THÔNG BÁO ===== -->
      <div class="chuong-wrapper" ref="chuongRef">
        <button
          class="nut-chuong"
          :class="{ 'nut-chuong--co-moi': soThongBaoChuaDoc > 0 }"
          @click="toggleThongBao"
          title="Thông báo"
          id="admin-notification-bell"
        >
          <font-awesome-icon icon="fa-solid fa-bell" />
          <span v-if="soThongBaoChuaDoc > 0" class="badge-so">
            {{ soThongBaoChuaDoc > 99 ? '99+' : soThongBaoChuaDoc }}
          </span>
        </button>

        <!-- Dropdown thông báo -->
        <Transition name="dropdown">
          <div v-if="hienThiThongBao" class="thong-bao-dropdown" id="admin-notification-panel">
            <div class="tb-header">
              <span class="tb-tieu-de">🔔 Thông báo</span>
              <button
                v-if="soThongBaoChuaDoc > 0"
                class="nut-doc-het"
                @click="docHet"
              >Đánh dấu đọc hết</button>
            </div>

            <div class="tb-body" v-if="!dangTai && danhSachThongBao.length > 0">
              <div
                v-for="tb in danhSachThongBao"
                :key="tb.maThongBao"
                class="tb-item"
                :class="{ 'tb-item--chua-doc': !tb.daDoc }"
                @click="danhDauDaDoc(tb.maThongBao)"
              >
                <span class="tb-icon">{{ iconLoai(tb.loaiThongBao) }}</span>
                <div class="tb-noi-dung">
                  <p class="tb-tieu-de-item">{{ tb.tieuDe }}</p>
                  <p class="tb-text">{{ tb.noiDung }}</p>
                  <span class="tb-thoi-gian">{{ formatNgay(tb.ngayTao) }}</span>
                </div>
                <span v-if="!tb.daDoc" class="dot-xanh"></span>
              </div>
            </div>

            <div v-else-if="dangTai" class="tb-empty">
              <span>Đang tải...</span>
            </div>

            <div v-else class="tb-empty">
              <span>🎉 Không có thông báo mới</span>
            </div>
          </div>
        </Transition>
      </div>

      <div class="topbar__ten-nguoi-dung">
        <span class="topbar__ten">{{ authStore.tenDayDu }}</span>
        <span class="topbar__vai-tro" :class="`vai-tro-${authStore.currentRole.toLowerCase()}`">
          {{ authStore.currentRole === 'ADMIN' ? 'Quản trị viên' : 'Thủ thư' }}
        </span>
      </div>
      <div class="topbar__avatar">{{ kyTuDau }}</div>
      <RouterLink to="/" class="topbar__nut-website" title="Xem Website">
        <font-awesome-icon icon="fa-solid fa-globe" />
      </RouterLink>
      <button class="topbar__nut-dang-xuat" @click="dangXuat" title="Đăng xuất">
        <font-awesome-icon icon="fa-solid fa-right-from-bracket" />
      </button>
    </div>
  </header>
</template>

<style scoped>
.topbar {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0 1.5rem;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  backdrop-filter: blur(20px);
  position: sticky;
  top: 0;
  z-index: 50;
}
.topbar__hamburger {
  display: none;
  background: none;
  border: none;
  color: var(--mau-chu);
  font-size: 1.25rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 8px;
}
.topbar__tieu-de {
  flex: 1;
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--mau-chu);
}
.topbar__nguoi-dung {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

/* ===== CHUÔNG THÔNG BÁO ===== */
.chuong-wrapper {
  position: relative;
}

.nut-chuong {
  position: relative;
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0.4rem 0.6rem;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
  color: #374151;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nut-chuong:hover {
  background: rgba(6, 182, 212, 0.1);
  border-color: rgba(6, 182, 212, 0.35);
  color: #0891b2;
}

.nut-chuong--co-moi {
  animation: chuong-rung 1.5s ease infinite;
  color: #d97706;
  border-color: rgba(217, 119, 6, 0.3);
  background: rgba(217, 119, 6, 0.06);
}

@keyframes chuong-rung {
  0%, 100% { transform: rotate(0deg); }
  10% { transform: rotate(-8deg); }
  20% { transform: rotate(8deg); }
  30% { transform: rotate(-6deg); }
  40% { transform: rotate(6deg); }
  50% { transform: rotate(0deg); }
}

.badge-so {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #ef4444;
  color: white;
  font-size: 0.65rem;
  font-weight: 700;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 3px;
  border: 2px solid white;
  line-height: 1;
}

/* Dropdown */
.thong-bao-dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  width: 360px;
  max-height: 480px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.12), 0 2px 8px rgba(0,0,0,0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  z-index: 200;
}

.tb-header {
  padding: 1rem 1.25rem 0.75rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f1f5f9;
  flex-shrink: 0;
}

.tb-tieu-de {
  font-size: 0.95rem;
  font-weight: 700;
  color: #111827;
}

.nut-doc-het {
  background: none;
  border: none;
  font-size: 0.75rem;
  color: #0891b2;
  cursor: pointer;
  font-family: inherit;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  transition: background 0.15s;
}
.nut-doc-het:hover { background: rgba(6,182,212,0.08); }

.tb-body {
  overflow-y: auto;
  flex: 1;
}

.tb-item {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 0.875rem 1.25rem;
  cursor: pointer;
  transition: background 0.15s;
  border-bottom: 1px solid #f9fafb;
  position: relative;
}
.tb-item:hover { background: #f8fafc; }

.tb-item--chua-doc {
  background: rgba(6, 182, 212, 0.04);
}
.tb-item--chua-doc:hover { background: rgba(6, 182, 212, 0.08); }

.tb-icon {
  font-size: 1.25rem;
  flex-shrink: 0;
  line-height: 1.4;
}

.tb-noi-dung {
  flex: 1;
  min-width: 0;
}

.tb-tieu-de-item {
  font-size: 0.825rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 0.25rem;
  line-height: 1.4;
}

.tb-text {
  font-size: 0.775rem;
  color: #6b7280;
  margin: 0 0 0.25rem;
  line-height: 1.5;
  /* Cắt bớt nội dung dài */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tb-thoi-gian {
  font-size: 0.7rem;
  color: #9ca3af;
}

.dot-xanh {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #06b6d4;
  flex-shrink: 0;
  margin-top: 4px;
}

.tb-empty {
  padding: 2.5rem 1.25rem;
  text-align: center;
  color: #9ca3af;
  font-size: 0.875rem;
}

/* Transition dropdown */
.dropdown-enter-active, .dropdown-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}
.dropdown-enter-from, .dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.97);
}

/* ===== Phần user info ===== */
.topbar__ten-nguoi-dung {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.topbar__ten {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--mau-chu);
}

.topbar__vai-tro {
  font-size: 0.725rem;
  color: var(--mau-chinh);
}

.topbar__vai-tro.vai-tro-admin {
  color: #1e40af;
  font-weight: 600;
}

.topbar__vai-tro.vai-tro-librarian {
  color: #16a34a;
  font-weight: 600;
}
.topbar__avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
}
.topbar__nut-website {
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0.4rem 0.6rem;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--mau-chu);
  display: flex;
  align-items: center;
  justify-content: center;
}
.topbar__nut-website:hover {
  background: rgba(6, 182, 212, 0.1);
  border-color: rgba(6, 182, 212, 0.35);
  color: #0891b2;
}
.topbar__nut-dang-xuat {
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0.4rem 0.6rem;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
}
.topbar__nut-dang-xuat:hover {
  background: rgba(6, 182, 212, 0.1);
  border-color: rgba(6, 182, 212, 0.35);
}

@media (max-width: 1023px) {
  .topbar__hamburger {
    display: flex;
  }
  .topbar__ten-nguoi-dung {
    display: none;
  }
  .thong-bao-dropdown {
    width: 300px;
    right: -50px;
  }
}
</style>
