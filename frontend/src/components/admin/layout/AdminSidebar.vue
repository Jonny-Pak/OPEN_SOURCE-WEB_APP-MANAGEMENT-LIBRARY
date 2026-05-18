<!--
  AdminSidebar.vue — Thanh điều hướng bên trái của trang Admin.
  Menu hiển thị/ẩn dựa trên role và permission của người dùng.
-->
<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

interface MenuItem {
  label: string
  icon: string         // FontAwesome class
  route?: string
  permission?: string  // permission key cần kiểm tra
  children?: MenuItem[]
  onlyAdmin?: boolean  // shortcut: chỉ ADMIN mới thấy
}

defineProps<{ thuGon: boolean }>()
defineEmits<{ 'cap-nhat-thu-gon': [gia_tri: boolean] }>()

const authStore = useAuthStore()
const route = useRoute()

const menuItems: MenuItem[] = [
  {
    label: 'Xem Website',
    icon: 'fa-solid fa-globe',
    route: '/',
  },
  {
    label: 'Dashboard',
    icon: 'fa-solid fa-gauge-high',
    route: '/admin/dashboard',
    permission: 'dashboard:full-stats', // Chỉ admin thấy full, librarian thấy bản rút gọn nhưng vẫn truy cập được
  },
  {
    label: 'Quản lý Sách',
    icon: 'fa-solid fa-book',
    children: [
      { label: 'Danh sách đầu sách', icon: 'fa-solid fa-book-open-reader', route: '/admin/sach', permission: 'sach:view' },
      { label: 'Quản lý bản sao', icon: 'fa-solid fa-book', route: '/admin/cuon-sach', permission: 'cuon-sach:view' },
    ]
  },
  {
    label: 'Mượn / Trả sách',
    icon: 'fa-solid fa-arrows-rotate',
    children: [
      { label: 'Phiếu mượn', icon: 'fa-solid fa-arrows-rotate', route: '/admin/muon-sach', permission: 'muon-sach:view' },
      { label: 'Trả sách', icon: 'fa-solid fa-rotate-left', route: '/admin/tra-sach', permission: 'tra-sach:process' },
      { label: 'Duyệt gia hạn', icon: 'fa-solid fa-calendar-plus', route: '/admin/duyet-gia-han', permission: 'muon-sach:view' },
    ]
  },
  {
    label: 'Đặt chỗ',
    icon: 'fa-solid fa-calendar-days',
    route: '/admin/dat-cho',
    permission: 'dat-cho:view',
  },
  {
    label: 'Quản lý Phạt',
    icon: 'fa-solid fa-gavel',
    route: '/admin/phat',
    permission: 'phat:view',
  },
  {
    label: 'Độc giả',
    icon: 'fa-solid fa-users',
    route: '/admin/doc-gia',
    permission: 'doc-gia:view',
  },
  // ---- CHỈ ADMIN ----
  {
    label: 'Danh mục',
    icon: 'fa-solid fa-layer-group',
    onlyAdmin: true,
    children: [
      { label: 'Tác giả', icon: 'fa-solid fa-pen-nib', route: '/admin/danh-muc/tac-gia', permission: 'tac-gia:view' },
      { label: 'Nhà xuất bản', icon: 'fa-solid fa-building-columns', route: '/admin/danh-muc/nha-xuat-ban', permission: 'nha-xuat-ban:view' },
      { label: 'Thể loại', icon: 'fa-solid fa-layer-group', route: '/admin/danh-muc/the-loai', permission: 'the-loai:view' },
      { label: 'Vị trí kệ sách', icon: 'fa-solid fa-location-dot', route: '/admin/danh-muc/vi-tri', permission: 'vi-tri:view' },
    ]
  },
  {
    label: 'Cài đặt hệ thống',
    icon: 'fa-solid fa-screwdriver-wrench',
    route: '/admin/settings',
    onlyAdmin: true,
    permission: 'system:settings',
  },
]

const visibleMenuItems = computed(() =>
  menuItems
    .filter(item => {
      if (item.onlyAdmin && !authStore.isAdmin) return false
      if (item.permission && !authStore.hasPermission(item.permission)) return false
      return true
    })
    .map(item => ({
      ...item,
      children: item.children?.filter(child =>
        !child.permission || authStore.hasPermission(child.permission)
      )
    }))
)

const duongDanHienTai = computed(() => route.path)

/** Check if menu item is active */
const isActive = (item: MenuItem): boolean => {
  if (!item.route) return false
  if (item.route === '/') return duongDanHienTai.value === '/'
  return duongDanHienTai.value === item.route || (item.route !== '/' && duongDanHienTai.value.startsWith(item.route))
}


/** Check if parent has active child */
const hasActiveChild = (item: MenuItem): boolean => {
  if (!item.children) return false
  return item.children.some(child => isActive(child))
}
</script>

<template>
  <aside class="sidebar" :class="{ 'sidebar--thu-gon': thuGon }">
    <!-- Logo -->
    <div class="sidebar__logo">
      <span class="sidebar__logo-icon"><font-awesome-icon icon="fa-solid fa-book-open" /></span>
      <Transition name="fade-text">
        <span v-if="!thuGon" class="sidebar__logo-text">LibraryAdmin</span>
      </Transition>
    </div>

    <!-- Menu điều hướng -->
    <nav class="sidebar__nav">
      <template v-for="item in visibleMenuItems" :key="item.label">
        <!-- Menu item không có children (là link trực tiếp) -->
        <RouterLink
          v-if="!item.children"
          :to="item.route!"
          class="sidebar__menu-item"
          :class="{ 'sidebar__menu-item--active': isActive(item) }"
          :title="thuGon ? item.label : ''"
        >
          <span class="sidebar__menu-icon"><font-awesome-icon :icon="item.icon" /></span>
          <Transition name="fade-text">
            <span v-if="!thuGon" class="sidebar__menu-text">{{ item.label }}</span>
          </Transition>
          <!-- Tooltip khi thu gọn -->
          <span v-if="thuGon" class="sidebar__tooltip">{{ item.label }}</span>
        </RouterLink>

        <!-- Menu item có children (dropdown) -->
        <details
          v-else
          class="sidebar__menu-group"
          :class="{ 'sidebar__menu-group--active': hasActiveChild(item) }"
          :open="hasActiveChild(item)"
        >
          <summary class="sidebar__menu-item sidebar__menu-item--parent">
            <span class="sidebar__menu-icon"><font-awesome-icon :icon="item.icon" /></span>
            <Transition name="fade-text">
              <span v-if="!thuGon" class="sidebar__menu-text">{{ item.label }}</span>
            </Transition>
            <span v-if="!thuGon" class="sidebar__menu-chevron">
              <font-awesome-icon icon="fa-solid fa-angle-right" />
            </span>
            <!-- Tooltip khi thu gọn -->
            <span v-if="thuGon" class="sidebar__tooltip">{{ item.label }}</span>
          </summary>

          <!-- Children menu -->
          <div class="sidebar__submenu" v-if="!thuGon">
            <RouterLink
              v-for="child in item.children"
              :key="child.label"
              :to="child.route!"
              class="sidebar__submenu-item"
              :class="{ 'sidebar__submenu-item--active': isActive(child) }"
            >
              <span class="sidebar__submenu-icon"><font-awesome-icon :icon="child.icon" /></span>
              <span class="sidebar__submenu-text">{{ child.label }}</span>
            </RouterLink>
          </div>
        </details>
      </template>
    </nav>

    <!-- Nút toggle thu gọn -->
    <button
      class="sidebar__nut-toggle"
      @click="$emit('cap-nhat-thu-gon', !thuGon)"
      :aria-label="thuGon ? 'Mở rộng sidebar' : 'Thu gọn sidebar'"
    >
      <span :style="{ transform: thuGon ? 'rotate(180deg)' : 'none', display: 'inline-block', transition: 'transform 0.3s' }">
        <font-awesome-icon icon="fa-solid fa-angle-left" />
      </span>
      <Transition name="fade-text">
        <span v-if="!thuGon" style="margin-left: 0.5rem;">Thu gọn</span>
      </Transition>
    </button>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 240px;
  min-height: 100vh;
  background: #ffffff; /* Nền trắng */
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
  transition: width 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
  backdrop-filter: blur(20px);
}
.sidebar--thu-gon { width: 64px; }

/* Logo */
.sidebar__logo {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0 1rem;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}
/* ĐỔI MÀU ICON LOGO THÀNH #06b6d4 */
.sidebar__logo-icon { font-size: 1.5rem; flex-shrink: 0; color: #06b6d4; }
.sidebar__logo-text { font-size: 1rem; font-weight: 800; white-space: nowrap; color: #000000; } /* Chữ đen */

/* Nav */
.sidebar__nav {
  flex: 1;
  padding: 0.75rem 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
  overflow-y: auto;
  overflow-x: hidden;
}

/* Menu item styles */
.sidebar__menu-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.7rem 0.75rem;
  border-radius: 10px;
  color: #374151; /* Chữ xám đậm */
  text-decoration: none;
  transition: all 0.2s;
  white-space: nowrap;
  position: relative;
  cursor: pointer;
  background: none;
  border: none;
  font-family: inherit;
  font-size: inherit;
  text-align: left;
}
/* ĐỔI MÀU HOVER THÀNH NỀN XANH NHẠT (rgba của #06b6d4), CHỮ ĐEN */
.sidebar__menu-item:hover {
  background: rgba(6, 182, 212, 0.08);
  color: #000000;
}
/* ĐỔI MÀU ACTIVE THÀNH MÀU HOVER #0891b2 */
.sidebar__menu-item--active {
  background: rgba(6, 182, 212, 0.12);
  color: #0891b2; 
  font-weight: 600;
}
/* VIỀN ACTIVE BÊN TRÁI MÀU #06b6d4 */
.sidebar__menu-item--active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 20%;
  bottom: 20%;
  width: 3px;
  background: #06b6d4;
  border-radius: 0 3px 3px 0;
}
.sidebar__menu-icon {
  font-size: 1rem;
  flex-shrink: 0;
  width: 20px;
  text-align: center;
}
.sidebar__menu-text {
  font-size: 0.875rem;
  font-weight: 500;
}

/* Parent menu item (with dropdown) */
.sidebar__menu-item--parent {
  justify-content: space-between;
}
.sidebar__menu-chevron {
  margin-left: auto;
  color: #94a3b8;
  transition: transform 0.2s;
}
details.sidebar__menu-group[open] .sidebar__menu-chevron {
  transform: rotate(180deg);
}

/* Menu group styles */
.sidebar__menu-group {
  display: contents;
}
/* ĐỔI MÀU ACTIVE CỦA MENU CHA */
.sidebar__menu-group--active .sidebar__menu-item--parent {
  background: rgba(6, 182, 212, 0.08);
  color: #0891b2;
}

/* Submenu */
.sidebar__submenu {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding-left: 0.5rem;
  margin-left: 0.75rem;
  border-left: 2px solid #e5e7eb;
}
.sidebar__submenu-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.6rem 0.75rem;
  border-radius: 8px;
  color: #64748b;
  text-decoration: none;
  transition: all 0.2s;
  white-space: nowrap;
  font-size: 0.8375rem;
}
.sidebar__submenu-item:hover {
  background: rgba(6, 182, 212, 0.06);
  color: #000000;
}
/* ĐỔI MÀU ACTIVE CỦA SUBMENU */
.sidebar__submenu-item--active {
  background: rgba(6, 182, 212, 0.12);
  color: #0891b2;
  font-weight: 600;
}
.sidebar__submenu-icon {
  font-size: 0.85rem;
  flex-shrink: 0;
  width: 16px;
  text-align: center;
}
.sidebar__submenu-text {
  font-size: 0.8125rem;
}

/* Tooltip khi thu gọn */
.sidebar__tooltip {
  display: none;
  position: absolute;
  left: calc(100% + 12px);
  background: #ffffff;
  border: 1px solid #e5e7eb;
  color: #000000;
  padding: 0.4rem 0.75rem;
  border-radius: 8px;
  font-size: 0.8rem;
  white-space: nowrap;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  pointer-events: none;
  z-index: 200;
}
.sidebar--thu-gon .sidebar__menu-item:hover .sidebar__tooltip {
  display: block;
}

/* Nút toggle */
.sidebar__nut-toggle {
  display: flex;
  align-items: center;
  padding: 0.875rem 1rem;
  margin: 0.5rem;
  border-radius: 10px;
  background: none;
  border: 1px solid #d1d5db;
  color: #4b5563;
  cursor: pointer;
  font-family: inherit;
  font-size: 0.875rem;
  transition: all 0.2s;
  white-space: nowrap;
  justify-content: center;
}
.sidebar__nut-toggle:hover {
  background: rgba(6, 182, 212, 0.08);
  color: #0891b2;
  border-color: #06b6d4;
}

/* Transition text fade */
.fade-text-enter-active, .fade-text-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}
.fade-text-enter-from, .fade-text-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

/* Responsive mobile */
@media (max-width: 1023px) {
  .sidebar { transform: translateX(-100%); transition: transform 0.3s, width 0.3s; }
  .sidebar.sidebar--mo { transform: translateX(0); width: 240px; }
}
</style>
