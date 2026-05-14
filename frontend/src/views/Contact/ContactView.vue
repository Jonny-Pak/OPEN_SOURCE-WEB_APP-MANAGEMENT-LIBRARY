<script setup lang="ts">
import { ref } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const form = ref({
  name: '',
  email: '',
  subject: '',
  message: ''
})

const isSubmitting = ref(false)
const isSuccess = ref(false)

const handleSubmit = () => {
  isSubmitting.value = true
  // Simulate API call
  setTimeout(() => {
    isSubmitting.value = false
    isSuccess.value = true
    form.value = { name: '', email: '', subject: '', message: '' }
  }, 1500)
}

const contactInfo = [
  {
    icon: 'fa-map-marker-alt',
    title: 'Địa chỉ',
    content: 'Số 123, Đường Tri Thức, Quận Cầu Giấy, Hà Nội'
  },
  {
    icon: 'fa-phone-alt',
    title: 'Điện thoại',
    content: '(024) 3456 7890'
  },
  {
    icon: 'fa-envelope',
    title: 'Email',
    content: 'thuvien@school.edu.vn'
  },
  {
    icon: 'fa-clock',
    title: 'Giờ làm việc',
    content: 'Thứ 2 - Thứ 7: 07:30 - 21:00'
  }
]

</script>

<template>
  <div class="contact-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <header class="contact-header text-center">
          <h1>Liên hệ với <span class="text-accent">chúng tôi</span></h1>
          <p>Mọi góp ý hoặc thắc mắc của bạn đều là động lực để thư viện hoàn thiện hơn mỗi ngày</p>
        </header>

        <div class="contact-grid">
          <!-- Contact Info -->
          <aside class="contact-info">
            <div v-for="info in contactInfo" :key="info.title" class="info-card">
              <div class="info-icon">
                <i :class="['fas', info.icon]"></i>
              </div>
              <div class="info-content">
                <h3>{{ info.title }}</h3>
                <p>{{ info.content }}</p>
              </div>
            </div>

            <div class="map-placeholder">
              <img src="https://images.unsplash.com/photo-1524666041070-9d87656c25bb?auto=format&fit=crop&q=80&w=600" alt="Map Location" />
              <div class="map-overlay">
                <i class="fas fa-map-marked-alt"></i>
                <span>Xem trên bản đồ</span>
              </div>
            </div>
          </aside>

          <!-- Contact Form -->
          <section class="contact-form-section">
            <div v-if="isSuccess" class="success-message">
              <div class="success-icon">
                <i class="fas fa-check-circle"></i>
              </div>
              <h2>Cảm ơn bạn đã liên hệ!</h2>
              <p>Chúng tôi đã nhận được tin nhắn và sẽ phản hồi sớm nhất có thể.</p>
              <button @click="isSuccess = false" class="btn btn-primary">Gửi tin nhắn mới</button>
            </div>

            <form v-else @submit.prevent="handleSubmit" class="contact-form">
              <div class="form-row">
                <div class="form-group">
                  <label>Họ và tên</label>
                  <input v-model="form.name" type="text" placeholder="Nhập tên của bạn" required />
                </div>
                <div class="form-group">
                  <label>Email học sinh</label>
                  <input v-model="form.email" type="email" placeholder="example@school.edu.vn" required />
                </div>
              </div>
              <div class="form-group">
                <label>Chủ đề</label>
                <input v-model="form.subject" type="text" placeholder="Bạn cần hỗ trợ về vấn đề gì?" required />
              </div>
              <div class="form-group">
                <label>Nội dung tin nhắn</label>
                <textarea v-model="form.message" rows="6" placeholder="Nhập nội dung chi tiết..." required></textarea>
              </div>
              <button type="submit" class="btn btn-primary submit-btn" :disabled="isSubmitting">
                <i v-if="isSubmitting" class="fas fa-spinner fa-spin"></i>
                <span v-else>Gửi tin nhắn</span>
              </button>
            </form>
          </section>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/ContactView.css"></style>
