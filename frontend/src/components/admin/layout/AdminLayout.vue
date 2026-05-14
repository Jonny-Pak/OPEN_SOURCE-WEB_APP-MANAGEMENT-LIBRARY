<!--
  AdminLayout.vue — Shell layout cho toàn bộ trang Admin.
  Kết hợp Sidebar + Topbar + RouterView + ToastMessage.
-->
<script setup lang="ts">
import { ref } from 'vue'
import AdminSidebar from './AdminSidebar.vue'
import AdminTopbar from './AdminTopbar.vue'
import ToastMessage from '@/components/admin/shared/ToastMessage.vue'

/** Trạng thái thu gọn sidebar */
const sidebarThuGon = ref(false)
/** Trạng thái mở sidebar trên mobile */
const sidebarMoMobile = ref(false)

function toggleSidebar(): void {
  // Desktop: toggle thu gọn
  if (window.innerWidth >= 1024) {
    sidebarThuGon.value = !sidebarThuGon.value
  } else {
    // Mobile: toggle mở/đóng
    sidebarMoMobile.value = !sidebarMoMobile.value
  }
}
</script>

<template>
  <div class="admin-shell">
    <!-- Sidebar -->
    <AdminSidebar
      :thu-gon="sidebarThuGon"
      :class="{ 'sidebar--mo': sidebarMoMobile }"
      @cap-nhat-thu-gon="sidebarThuGon = $event"
    />

    <!-- Overlay mobile khi sidebar mở -->
    <div
      v-if="sidebarMoMobile"
      class="overlay-mobile"
      @click="sidebarMoMobile = false"
    ></div>

    <!-- Vùng nội dung chính -->
    <div
      class="admin-shell__noi-dung"
      :class="{ 'admin-shell__noi-dung--thu-gon': sidebarThuGon }"
    >
      <AdminTopbar @toggle-sidebar="toggleSidebar" />

      <main class="admin-shell__main">
        <RouterView />
      </main>
    </div>

    <!-- Toast notifications -->
    <ToastMessage />
  </div>
</template>

<style scoped>
.admin-shell {
  display: flex;
  min-height: 100vh;
  background: var(--color-background);
}

/* Vùng nội dung dịch phải theo độ rộng sidebar */
.admin-shell__noi-dung {
  flex: 1;
  margin-left: 240px;
  transition: margin-left 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  min-width: 0;
}
.admin-shell__noi-dung--thu-gon { margin-left: 64px; }

.admin-shell__main {
  padding: 1.5rem;
  min-height: calc(100vh - 64px);
}

.overlay-mobile {
  display: none;
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 99;
}

@media (max-width: 1023px) {
  .admin-shell__noi-dung { margin-left: 0; }
  .overlay-mobile { display: block; }
}
</style>
