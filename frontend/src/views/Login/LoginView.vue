<script setup lang="ts">
import { ref } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const email = ref('')
const password = ref('')
const rememberMe = ref(false)

const handleLogin = () => {
  // Logic for login will go here
  console.log('Logging in:', {
    email: email.value,
    password: password.value,
    rememberMe: rememberMe.value
  })
  alert('Đăng nhập thành công! (Demo)')
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
                    type="password" 
                    placeholder="Nhập mật khẩu" 
                    required 
                  />
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
              
              <button type="submit" class="btn btn-primary btn-block">Đăng nhập</button>
              

            </form>
          </div>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/LoginView.css"></style>
