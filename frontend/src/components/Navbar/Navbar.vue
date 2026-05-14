<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useCartStore } from '../../stores/cart'
import { useWishlistStore } from '../../stores/wishlist'

const cart = useCartStore()
const wishlist = useWishlistStore()
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
          <i class="far fa-heart"></i>
          <span v-if="wishlist.itemCount > 0" class="cart-badge">{{ wishlist.itemCount }}</span>
        </RouterLink>
        <RouterLink to="/borrow/cart" class="cart-link" title="Giỏ sách">
          <i class="fas fa-shopping-basket"></i>
          <span v-if="cart.itemCount > 0" class="cart-badge">{{ cart.itemCount }}</span>
        </RouterLink>
        <RouterLink to="/profile" class="user-profile-link">
          <span class="user-avatar-small"><i class="fas fa-user"></i></span>
        </RouterLink>
        <RouterLink to="/login" class="btn btn-primary">Đăng nhập</RouterLink>
      </div>
    </div>
  </nav>
</template>

<style scoped src="../../assets/css/components/Navbar.css"></style>
