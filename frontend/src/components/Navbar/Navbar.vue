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
const logoUrl = ref('')

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

const loadLogo = () => {
  const savedSettings = localStorage.getItem('library_settings')
  if (savedSettings) {
    try {
      const parsed = JSON.parse(savedSettings)
      logoUrl.value = parsed.logo || ''
    } catch {}
  } else {
    logoUrl.value = ''
  }
}

const handleLogout = () => {
  wishlist.clearWishlist()
  authStore.xoaXacThuc()
  router.push('/login')
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  window.addEventListener('library-settings-updated', loadLogo)
  loadLogo()
  if (authStore.daXacThuc) {
    wishlist.fetchWishlist()
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('library-settings-updated', loadLogo)
})

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
        <img v-if="logoUrl" :src="logoUrl" alt="Logo" class="navbar-logo-img" />
        <span v-else class="logo-text">THƯ VIỆN <span class="text-accent">ĐIỆN TỬ</span></span>
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

        <template v-if="authStore.daXacThuc">
          <div class="user-menu-container">
            <div class="user-profile-trigger">
              <span class="user-name-small">{{ authStore.tenDayDu }}</span>
              <span class="user-avatar-small">
                <img 
                  v-if="authStore.thongTinNguoiDung?.avatar" 
                  :src="authStore.thongTinNguoiDung.avatar" 
                  alt="Avatar" 
                  class="navbar-avatar-img"
                />
                <i v-else class="fas fa-user"></i>
              </span>
              <i class="fas fa-chevron-down chevron-icon"></i>
            </div>
            
            <div class="user-dropdown">
              <div class="dropdown-header">
                <p class="user-email">{{ authStore.thongTinNguoiDung?.email }}</p>
                <span class="user-role-badge">{{ authStore.thongTinNguoiDung?.vaiTro }}</span>
              </div>
              
              <div class="dropdown-menu-list">
                <RouterLink to="/profile" class="dropdown-item">
                  <span class="item-icon"><i class="far fa-user-circle"></i></span>
                  Trang cá nhân
                </RouterLink>
                
                <RouterLink 
                  v-if="authStore.isAdmin || authStore.isLibrarian" 
                  to="/admin" 
                  class="dropdown-item"
                >
                  <span class="item-icon"><i class="fas fa-cog"></i></span>
                  Quản trị
                </RouterLink>
                
                <div class="dropdown-divider"></div>
                
                <button @click="handleLogout" class="dropdown-item logout-item">
                  <span class="item-icon"><i class="fas fa-sign-out-alt"></i></span>
                  Đăng xuất
                </button>
              </div>
            </div>
          </div>
        </template>
        <RouterLink v-else to="/login" class="btn btn-primary">Đăng nhập</RouterLink>
      </div>
    </div>
  </nav>
</template>

<style scoped src="../../assets/css/components/Navbar.css"></style>
