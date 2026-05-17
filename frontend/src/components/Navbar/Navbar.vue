<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../../stores/cart'
import { useWishlistStore } from '../../stores/wishlist'
import { useAuthStore } from '../../stores/auth'

const cart = useCartStore()
const wishlist = useWishlistStore()
const authStore = useAuthStore()
const router = useRouter()

const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleLogout = () => {
  authStore.xoaXacThuc()
  router.push('/login')
}

const userInitials = computed(() => {
  const info = authStore.thongTinNguoiDung
  if (!info) return 'U'
  return `${info.hoDem.charAt(0)}${info.ten.charAt(0)}`.toUpperCase()
})
</script>

<template>
  <nav :class="['navbar', { 'navbar-scrolled': isScrolled }]">
    <div class="container navbar-content">
      <div class="logo">
        <span class="logo-text">THƯ VIỆN <span class="text-accent">ĐIỆN TỬ</span></span>
      </div>
      
      <ul class="nav-links">
        <li><RouterLink to="/" active-class="active">Trang chủ</RouterLink></li>
        <li><RouterLink to="/books" active-class="active">Sách</RouterLink></li>
        <li><RouterLink to="/authors" active-class="active">Tác giả</RouterLink></li>
        <li><RouterLink to="/publishers" active-class="active">Nhà xuất bản</RouterLink></li>
        <li><RouterLink to="/about" active-class="active">Giới thiệu</RouterLink></li>
        <li><RouterLink to="/contact" active-class="active">Liên hệ</RouterLink></li>
      </ul>
      
      <div class="nav-actions">
        <RouterLink to="/favorites" class="cart-link" title="Sách yêu thích">
          <font-awesome-icon :icon="['far', 'heart']" />
          <span v-if="wishlist.itemCount > 0" class="cart-badge">{{ wishlist.itemCount }}</span>
        </RouterLink>
        <RouterLink to="/borrow/cart" class="cart-link" title="Giỏ sách">
          <font-awesome-icon icon="fa-solid fa-book-bookmark" />
          <span v-if="cart.itemCount > 0" class="cart-badge">{{ cart.itemCount }}</span>
        </RouterLink>

        <!-- Khi đã đăng nhập -->
        <template v-if="authStore.daXacThuc">
          <!-- Link Trang Quản Trị dành cho Admin/Thủ thư -->
          <RouterLink 
            v-if="authStore.isAdmin || authStore.isLibrarian" 
            to="/admin/dashboard" 
            class="btn btn-admin-panel"
            title="Trang quản trị"
          >
            <font-awesome-icon icon="fa-solid fa-screwdriver-wrench" />
            <span class="admin-text">Quản trị</span>
          </RouterLink>

          <!-- Avatar / Link cá nhân -->
          <RouterLink to="/profile" class="user-profile-link" :title="'Hồ sơ: ' + authStore.tenDayDu">
            <span class="user-avatar-text">{{ userInitials }}</span>
          </RouterLink>

          <!-- Nút Đăng xuất -->
          <button @click="handleLogout" class="btn btn-logout" title="Đăng xuất">
            <font-awesome-icon icon="fa-solid fa-right-from-bracket" />
            <span class="logout-text">Đăng xuất</span>
          </button>
        </template>

        <!-- Khi chưa đăng nhập -->
        <template v-else>
          <RouterLink to="/login" class="btn btn-primary">Đăng nhập</RouterLink>
        </template>
      </div>
    </div>
  </nav>
</template>

<style scoped src="../../assets/css/components/Navbar.css"></style>

