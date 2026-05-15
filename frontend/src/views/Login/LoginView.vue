<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import * as authService from '../../services/authService'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const rememberMe = ref(false)
const isLoading = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  isLoading.value = true
  errorMessage.value = ''
  
  try {
    const response = await authService.dangNhap({
      email: email.value,
      matKhau: password.value
    })
    
    // Lưu thông tin vào store
    authStore.luuXacThuc(response)
    
    // Điều hướng về trang chủ
    router.push('/')
  } catch (err: any) {
    console.error('Login failed:', err)
    errorMessage.value = err.message || 'Email hoặc mật khẩu không chính xác'
  } finally {
    isLoading.value = false
  }
}

</script>

<template>
  <div class="login-page">
    <Navbar />
    
    <main class="main-content">
      <div class="auth-container">
        <div class="auth-card">
          <div class="auth-image">
            <div class="image-overlay">
              <div class="overlay-content">
                <h2>Chào mừng bạn quay trở lại</h2>
                <p>Tiếp tục khám phá kho tàng tri thức và quản lý tủ sách cá nhân của bạn.</p>
              </div>
            </div>
          </div>
          
          <div class="auth-form-container">
            <div class="form-header">
              <h1>Đăng nhập</h1>
              <p>Chào mừng bạn trở lại! Vui lòng đăng nhập</p>
            </div>
            
            <form @submit.prevent="handleLogin" class="auth-form">
              <div v-if="errorMessage" class="error-alert">
                <i class="fas fa-exclamation-circle"></i>
                {{ errorMessage }}
              </div>

              <div class="form-group">
                <label for="email">Email</label>
                <div class="input-wrapper">
                  <input 
                    id="email" 
                    v-model="email" 
                    type="email" 
                    placeholder="Nhập địa chỉ email" 
                    required 
                    :disabled="isLoading"
                  />
                </div>
              </div>
              
              <div class="form-group">
                <label for="password">Mật khẩu</label>
                <div class="input-wrapper">
                  <input 
                    id="password" 
                    v-model="password" 
                    type="password" 
                    placeholder="Nhập mật khẩu" 
                    required 
                    :disabled="isLoading"
                  />
                </div>
              </div>
              
              <div class="form-options">
                <label class="checkbox-container">
                  <input type="checkbox" v-model="rememberMe" :disabled="isLoading" />
                  <span class="checkmark"></span>
                  Ghi nhớ đăng nhập
                </label>
                <a href="#" class="link forgot-password">Quên mật khẩu?</a>
              </div>
              
              <button type="submit" class="btn btn-primary btn-block" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-small"></span>
                {{ isLoading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/LoginView.css"></style>
