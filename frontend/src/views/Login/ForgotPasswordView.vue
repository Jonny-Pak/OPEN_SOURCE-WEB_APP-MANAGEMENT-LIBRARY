<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { quenMatKhau, datLaiMatKhau } from '@/services/authService'
import type { ErrorResponse } from '@/types/auth'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const email = ref('')
const token = ref('')
const matKhauMoi = ref('')
const xacNhanMatKhau = ref('')

const isSubmitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const currentStep = ref(1)
const showPassword = ref(false)

const router = useRouter()

const handleGetOTP = async () => {
  if (!email.value) {
    errorMessage.value = 'Vui lòng nhập email'
    return
  }
  isSubmitting.value = true
  errorMessage.value = ''
  successMessage.value = ''
  
  try {
    await quenMatKhau(email.value)
    successMessage.value = 'Mã OTP đã được gửi đến email của bạn'
    currentStep.value = 2
  } catch (error: any) {
    errorMessage.value = error.message || 'Không thể gửi OTP. Vui lòng kiểm tra lại email.'
  } finally {
    isSubmitting.value = false
  }
}

const handleResetPassword = async () => {
  if (matKhauMoi.value !== xacNhanMatKhau.value) {
    errorMessage.value = 'Mật khẩu xác nhận không khớp'
    return
  }
  isSubmitting.value = true
  errorMessage.value = ''
  successMessage.value = ''
  
  try {
    await datLaiMatKhau(email.value, token.value, matKhauMoi.value)
    successMessage.value = 'Đặt lại mật khẩu thành công. Đang chuyển hướng...'
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (error: any) {
    errorMessage.value = error.message || 'Lỗi đặt lại mật khẩu. Vui lòng thử lại.'
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
                <h2>Quên mật khẩu?</h2>
                <p>Khôi phục tài khoản của bạn để tiếp tục sử dụng hệ thống.</p>
              </div>
            </div>
          </div>
          
          <div class="auth-form-container">
            <div class="form-header">
              <h1>Khôi phục mật khẩu</h1>
              <p v-if="currentStep === 1">Nhập email để nhận mã xác nhận OTP</p>
              <p v-else>Nhập mã OTP và mật khẩu mới</p>
            </div>
            
            <form v-if="currentStep === 1" @submit.prevent="handleGetOTP" class="auth-form">
              <div v-if="errorMessage" class="error-message" style="color: #ef4444; background: #fee2e2; padding: 10px; border-radius: 8px; margin-bottom: 15px; font-size: 0.9rem;">
                <font-awesome-icon icon="fa-solid fa-circle-xmark" /> {{ errorMessage }}
              </div>
              <div v-if="successMessage" class="success-message" style="color: #10b981; background: #d1fae5; padding: 10px; border-radius: 8px; margin-bottom: 15px; font-size: 0.9rem;">
                <font-awesome-icon icon="fa-solid fa-circle-check" /> {{ successMessage }}
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
              
              <button type="submit" class="btn btn-primary btn-block" :disabled="isSubmitting" style="margin-top: 10px;">
                <span v-if="isSubmitting"><font-awesome-icon icon="fa-solid fa-spinner" class="fa-spin" /> Đang gửi...</span>
                <span v-else>Nhận mã OTP</span>
              </button>

              <div class="form-footer" style="margin-top: 20px; text-align: center;">
                <router-link to="/login" class="link">Quay lại đăng nhập</router-link>
              </div>
            </form>

            <form v-if="currentStep === 2" @submit.prevent="handleResetPassword" class="auth-form">
              <div v-if="errorMessage" class="error-message" style="color: #ef4444; background: #fee2e2; padding: 10px; border-radius: 8px; margin-bottom: 15px; font-size: 0.9rem;">
                <font-awesome-icon icon="fa-solid fa-circle-xmark" /> {{ errorMessage }}
              </div>
              <div v-if="successMessage" class="success-message" style="color: #10b981; background: #d1fae5; padding: 10px; border-radius: 8px; margin-bottom: 15px; font-size: 0.9rem;">
                <font-awesome-icon icon="fa-solid fa-circle-check" /> {{ successMessage }}
              </div>
              
              <div class="form-group">
                <label for="token">Mã OTP</label>
                <div class="input-wrapper">
                  <input 
                    id="token" 
                    v-model="token" 
                    type="text" 
                    placeholder="Nhập mã OTP 6 chữ số" 
                    required 
                  />
                </div>
              </div>

              <div class="form-group">
                <label for="newPassword">Mật khẩu mới</label>
                <div class="input-wrapper">
                  <input 
                    id="newPassword" 
                    v-model="matKhauMoi" 
                    :type="showPassword ? 'text' : 'password'" 
                    placeholder="Nhập mật khẩu mới" 
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

              <div class="form-group">
                <label for="confirmPassword">Xác nhận mật khẩu</label>
                <div class="input-wrapper">
                  <input 
                    id="confirmPassword" 
                    v-model="xacNhanMatKhau" 
                    :type="showPassword ? 'text' : 'password'" 
                    placeholder="Nhập lại mật khẩu mới" 
                    required 
                    style="padding-right: 40px;"
                  />
                </div>
              </div>
              
              <button type="submit" class="btn btn-primary btn-block" :disabled="isSubmitting" style="margin-top: 10px;">
                <span v-if="isSubmitting"><font-awesome-icon icon="fa-solid fa-spinner" class="fa-spin" /> Đang xử lý...</span>
                <span v-else>Đặt lại mật khẩu</span>
              </button>

              <div class="form-footer" style="margin-top: 20px; text-align: center;">
                <a href="#" @click.prevent="currentStep = 1" class="link">Gửi lại OTP</a> | 
                <router-link to="/login" class="link">Quay lại đăng nhập</router-link>
              </div>
            </form>

          </div>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/LoginView.css"></style>
