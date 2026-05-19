<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { dangNhap } from '@/services/authService'
import type { ErrorResponse } from '@/types/auth'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const email = ref('')
const password = ref('')
const rememberMe = ref(false)
const isSubmitting = ref(false)
const errorMessage = ref('')

const showPassword = ref(false)
const router = useRouter()
const authStore = useAuthStore()

const handleLogin = async () => {
  isSubmitting.value = true
  errorMessage.value = ''
  
  try {
    const response = await dangNhap({ email: email.value, matKhau: password.value })
    authStore.luuXacThuc(response)
    
    // Redirect theo role
    if (authStore.isAdmin || authStore.isLibrarian) {
      router.push('/admin/dashboard')
    } else {
      router.push('/')
    }
  } catch (error) {
    const err = error as ErrorResponse
    errorMessage.value = err.message || 'Đăng nhập thất bại. Vui lòng thử lại.'
  } finally {
    isSubmitting.value = false
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
              <div v-if="errorMessage" class="error-message" style="color: #ef4444; background: #fee2e2; padding: 10px; border-radius: 8px; margin-bottom: 15px; font-size: 0.9rem;">
                <font-awesome-icon icon="fa-solid fa-circle-xmark" /> {{ errorMessage }}
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
                  />
                </div>
              </div>
              
              <div class="form-group">
                <label for="password">Mật khẩu</label>
                <div class="input-wrapper">
                  <input 
                    id="password" 
                    v-model="password" 
                    :type="showPassword ? 'text' : 'password'" 
                    placeholder="Nhập mật khẩu" 
                    required 
                    style="padding-right: 40px;"
                  />
                  <button 
                    type="button"
                    @click="showPassword = !showPassword"
                    style="position: absolute; right: 12px; background: none; border: none; color: var(--text-muted); cursor: pointer; font-size: 1rem; display: flex; align-items: center; justify-content: center; padding: 4px;"
                  >
                    <font-awesome-icon :icon="showPassword ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'" />
                  </button>
                </div>
              </div>
              
              <div class="form-options">
                <label class="checkbox-container">
                  <input type="checkbox" v-model="rememberMe" />
                  <span class="checkmark"></span>
                  Ghi nhớ đăng nhập
                </label>
                <a href="#" class="link forgot-password">Quên mật khẩu?</a>
              </div>
              
              <button type="submit" class="btn btn-primary btn-block" :disabled="isSubmitting">
                <span v-if="isSubmitting"><font-awesome-icon icon="fa-solid fa-spinner" class="fa-spin" /> Đang đăng nhập...</span>
                <span v-else>Đăng nhập</span>
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
