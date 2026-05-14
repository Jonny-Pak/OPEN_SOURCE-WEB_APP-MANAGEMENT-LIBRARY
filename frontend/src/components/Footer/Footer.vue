<template>
  <footer class="footer">
    <div class="container footer-content">
      <div class="footer-brand">
        <div class="logo">
          <span class="logo-text">THƯ VIỆN <span class="text-accent">ĐIỆN TỬ</span></span>
        </div>
        <p>Hệ thống quản lý thư viện mã nguồn mở hiện đại, giúp kết nối tri thức và cộng đồng.</p>
        <div class="social-links">
          <a href="#"><i class="fab fa-facebook-f"></i></a>
          <a href="#"><i class="fab fa-twitter"></i></a>
          <a href="#"><i class="fab fa-instagram"></i></a>
          <a href="#"><i class="fab fa-linkedin-in"></i></a>
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
