<!--
  AdminTopbar.vue — Thanh tiêu đề phía trên của trang Admin.
  Hiển thị: hamburger (mobile), tên trang hiện tại, thông tin user, nút logout.
-->
<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

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
}

const tieuDeTrang = computed(() => BAN_DO_TIEU_DE[route.name as string] ?? 'Admin')

/** Chữ cái đầu tên người dùng cho avatar */
const kyTuDau = computed(() => {
  const info = authStore.thongTinNguoiDung
  return info ? `${info.hoDem.charAt(0)}${info.ten.charAt(0)}`.toUpperCase() : 'AD'
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

    <!-- Phần phải: thông tin người dùng -->
    <div class="topbar__nguoi-dung">
      <!-- [DEV] Switch Role -->
      <div class="switch-role-wrapper">
        <span class="switch-role-label">
          <font-awesome-icon icon="fa-solid fa-screwdriver-wrench" /> DEV
        </span>
        <div class="role-toggle">
          <button
            :class="['role-btn', authStore.currentRole === 'ADMIN' && 'active']"
            @click="authStore.switchRole('ADMIN')"
            title="Chuyển sang role Admin"
          >
            <font-awesome-icon icon="fa-solid fa-user-shield" /> Admin
          </button>
          <button
            :class="['role-btn', authStore.currentRole === 'LIBRARIAN' && 'active']"
            @click="authStore.switchRole('LIBRARIAN')"
            title="Chuyển sang role Thủ thư"
          >
            <font-awesome-icon icon="fa-solid fa-user-shield" /> Thủ thư
          </button>
        </div>
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
  gap: 1rem;
}

.switch-role-wrapper {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  background: #f0f4f8;
  border-radius: 8px;
}

.switch-role-label {
  font-size: 0.7rem;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.role-toggle {
  display: flex;
  gap: 0.25rem;
}

.role-btn {
  padding: 0.35rem 0.6rem;
  font-size: 0.75rem;
  font-weight: 600;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  background: #ffffff;
  color: #94a3b8;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.3rem;
  white-space: nowrap;
}

.role-btn:hover {
  background: #f1f5f9;
  color: #64748b;
}

.role-btn.active {
  background: #1e40af;
  color: #ffffff;
}

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
}
</style>
