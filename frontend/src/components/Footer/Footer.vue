<script setup lang="ts">
import { ref, onMounted } from 'vue'

const logoUrl = ref('')

onMounted(() => {
  const savedSettings = localStorage.getItem('library_settings')
  if (savedSettings) {
    try {
      const parsed = JSON.parse(savedSettings)
      if (parsed.logo) {
        logoUrl.value = parsed.logo
      }
    } catch {}
  }
})
</script>

<template>
  <footer class="footer">
    <div class="container footer-content">
      <div class="footer-brand">
        <div class="logo">
          <img v-if="logoUrl" :src="logoUrl" alt="Logo" class="footer-logo-img" />
          <span v-else class="logo-text">THƯ VIỆN <span class="text-accent">ĐIỆN TỬ</span></span>
        </div>
        <p>Hệ thống quản lý thư viện mã nguồn mở hiện đại, giúp kết nối tri thức và cộng đồng.</p>
        <div class="social-links">
          <a href="#" title="Facebook"><font-awesome-icon :icon="['fab', 'facebook']" /></a>
          <a href="#" title="GitHub"><font-awesome-icon :icon="['fab', 'github']" /></a>
          <a href="#" title="YouTube"><font-awesome-icon :icon="['fab', 'youtube']" /></a>
        </div>
      </div>
      
      <div class="footer-links">
        <h4>Khám phá</h4>
        <ul>
          <li><RouterLink to="/books">Sách mới</RouterLink></li>
          <li><RouterLink to="/books">Tác giả</RouterLink></li>
          <li><RouterLink to="/books">Chủ đề</RouterLink></li>
          <li><RouterLink to="/books">Bộ sưu tập</RouterLink></li>
        </ul>
      </div>
      
      <div class="footer-links">
        <h4>Hỗ trợ</h4>
        <ul>
          <li><RouterLink to="/guide">Hướng dẫn mượn</RouterLink></li>
          <li><RouterLink to="/rules">Nội quy</RouterLink></li>
          <li><RouterLink to="/faq">Câu hỏi thường gặp</RouterLink></li>
          <li><RouterLink to="/contact">Liên hệ</RouterLink></li>
        </ul>
      </div>
      
      <div class="footer-newsletter">
        <h4>Bản tin</h4>
        <p>Đăng ký để nhận thông tin về sách mới nhất.</p>
        <div class="newsletter-form">
          <input type="email" placeholder="Email của bạn" />
          <button class="btn btn-primary">Gửi</button>
        </div>
      </div>
    </div>
    
    <div class="footer-bottom">
      <div class="container">
        <p>&copy; 2026 LibManage. All rights reserved. Designed for excellence.</p>
      </div>
    </div>
  </footer>
</template>

<style scoped src="../../assets/css/components/Footer.css"></style>
