<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import { useAuthStore } from '../../stores/auth'
import { lienHeService } from '../../services/lienHeService'

const authStore = useAuthStore()

const form = ref({
  name: '',
  email: '',
  subject: '',
  message: ''
})

const settings = ref({
  contactTitle: 'Liên hệ với chúng tôi',
  contactSubtitle: 'Mọi góp ý hoặc thắc mắc của bạn đều là động lực để thư viện hoàn thiện hơn mỗi ngày',
  contactDiaChi: 'Số 123, Đường Tri Thức, Quận Cầu Giấy, Hà Nội',
  contactDienThoai: '(024) 3456 7890',
  contactEmail: 'thuvien@school.edu.vn',
  contactGioLamViec: 'Thứ 2 - Thứ 7: 07:30 - 21:00',
  contactMapImage: 'https://images.unsplash.com/photo-1524666041070-9d87656c25bb?auto=format&fit=crop&q=80&w=600'
})

onMounted(() => {
  if (authStore.daXacThuc && authStore.thongTinNguoiDung) {
    form.value.name = `${authStore.thongTinNguoiDung.hoDem} ${authStore.thongTinNguoiDung.ten}`
    form.value.email = authStore.thongTinNguoiDung.email
  }

  const saved = localStorage.getItem('library_settings')
  if (saved) {
    try {
      const parsed = JSON.parse(saved)
      Object.assign(settings.value, parsed)
    } catch (e) {
      console.error(e)
    }
  }
})

const isSubmitting = ref(false)
const isSuccess = ref(false)
const errorMessage = ref<string | null>(null)

const handleSubmit = async () => {
  isSubmitting.value = true
  errorMessage.value = null
  try {
    await lienHeService.guiLienHe({
      hoTen: form.value.name,
      email: form.value.email,
      tieuDe: form.value.subject,
      noiDung: form.value.message
    })
    isSuccess.value = true
    form.value = { name: '', email: '', subject: '', message: '' }
  } catch (err: any) {
    console.error('Failed to send contact:', err)
    errorMessage.value = err.message || (err.errors ? Object.values(err.errors)[0] : null) || 'Đã có lỗi xảy ra. Vui lòng thử lại sau.'
  } finally {
    isSubmitting.value = false
  }
}

const contactInfo = computed(() => [
  {
    icon: 'fa-solid fa-location-dot',
    title: 'Địa chỉ',
    content: settings.value.contactDiaChi
  },
  {
    icon: 'fa-solid fa-phone',
    title: 'Điện thoại',
    content: settings.value.contactDienThoai
  },
  {
    icon: 'fa-solid fa-envelope',
    title: 'Email',
    content: settings.value.contactEmail
  },
  {
    icon: 'fa-solid fa-clock',
    title: 'Giờ làm việc',
    content: settings.value.contactGioLamViec
  }
])
</script>

<template>
  <div class="contact-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <header class="contact-header text-center">
          <h1>{{ settings.contactTitle }}</h1>
          <p>{{ settings.contactSubtitle }}</p>
        </header>

        <div class="contact-grid">
          <!-- Contact Info -->
          <aside class="contact-info">
            <div v-for="info in contactInfo" :key="info.title" class="info-card">
              <div class="info-icon">
                <font-awesome-icon :icon="info.icon" />
              </div>
              <div class="info-content">
                <h3>{{ info.title }}</h3>
                <p>{{ info.content }}</p>
              </div>
            </div>

            <div class="map-placeholder">
              <img :src="settings.contactMapImage" alt="Map Location" />
              <div class="map-overlay">
                <font-awesome-icon icon="fa-solid fa-location-dot" />
                <span>Xem trên bản đồ</span>
              </div>
            </div>
          </aside>

          <!-- Contact Form -->
          <section class="contact-form-section">
            <div v-if="isSuccess" class="success-message">
              <div class="success-icon">
                <font-awesome-icon icon="fa-solid fa-circle-check" />
              </div>
              <h2>Cảm ơn bạn đã liên hệ!</h2>
              <p>Chúng tôi đã nhận được tin nhắn và sẽ phản hồi sớm nhất có thể.</p>
              <button @click="isSuccess = false" class="btn btn-primary">Gửi tin nhắn mới</button>
            </div>

            <div v-else class="contact-form-container">
              <div v-if="errorMessage" class="error-message-alert">
                <i class="fas fa-exclamation-triangle"></i>
                {{ errorMessage }}
              </div>
              <form @submit.prevent="handleSubmit" class="contact-form">
              <div class="form-row">
                <div class="form-group">
                  <label>Họ và tên</label>
                  <input v-model="form.name" type="text" placeholder="Nhập tên của bạn" required :readonly="authStore.daXacThuc" :class="{ 'input-disabled': authStore.daXacThuc }" />
                </div>
                <div class="form-group">
                  <label>Email</label>
                  <input v-model="form.email" type="email" placeholder="example@school.edu.vn" required :readonly="authStore.daXacThuc" :class="{ 'input-disabled': authStore.daXacThuc }" />
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
                <font-awesome-icon v-if="isSubmitting" icon="fa-solid fa-spinner" class="fa-spin" />
                <span v-else>Gửi tin nhắn</span>
              </button>
              </form>
            </div>
          </section>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/ContactView.css"></style>
