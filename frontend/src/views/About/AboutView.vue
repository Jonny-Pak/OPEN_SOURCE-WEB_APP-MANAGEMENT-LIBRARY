<script setup lang="ts">
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const stats = [
  { label: 'Đầu sách', value: '50,000+', icon: 'fa-book' },
  { label: 'Bạn đọc', value: '10,000+', icon: 'fa-users' },
  { label: 'Năm hoạt động', value: '15+', icon: 'fa-calendar-check' },
  { label: 'Giải thưởng', value: '05', icon: 'fa-award' }
]

const features = [
  {
    title: 'Tra cứu thông minh',
    description: 'Hệ thống tìm kiếm hiện đại giúp bạn tìm thấy cuốn sách yêu thích chỉ trong vài giây.',
    icon: 'fa-search'
  },
  {
    title: 'Mượn trả trực tuyến',
    description: 'Quy trình mượn và trả sách được số hóa hoàn toàn, nhanh chóng và tiện lợi.',
    icon: 'fa-sync-alt'
  },
  {
    title: 'Không gian hiện đại',
    description: 'Thư viện cung cấp không gian học tập yên tĩnh với đầy đủ tiện nghi hiện đại.',
    icon: 'fa-laptop'
  }
]

</script>

<template>
  <div class="about-page">
    <Navbar />
    
    <main class="main-content">
      <!-- Hero Section -->
      <section class="about-hero">
        <div class="container">
          <div class="hero-content">
            <h1>Lan tỏa tri thức, <br><span class="text-accent">Kết nối đam mê</span></h1>
            <p>LibManage không chỉ là một hệ thống quản lý thư viện, mà là cầu nối đưa tri thức đến gần hơn với cộng đồng học sinh và giáo viên.</p>
          </div>
        </div>
      </section>

      <!-- Mission Section -->
      <section class="section mission-section">
        <div class="container">
          <div class="mission-grid">
            <div class="mission-image">
              <img src="https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&q=80&w=800" alt="Library Mission" />
            </div>
            <div class="mission-text">
              <span class="section-badge">Sứ mệnh của chúng tôi</span>
              <h2>Xây dựng nền tảng tri thức vững chắc cho tương lai</h2>
              <p>Được thành lập từ năm 2010, thư viện của chúng tôi luôn nỗ lực không ngừng để trở thành trung tâm học tập và nghiên cứu hàng đầu. Chúng tôi tin rằng mỗi cuốn sách là một cánh cửa mở ra thế giới mới.</p>
              <p>Với hệ thống quản lý điện tử LibManage, chúng tôi cam kết mang lại trải nghiệm mượn sách hiện đại, minh bạch và hiệu quả nhất cho mọi người dùng.</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Stats Section -->
      <section class="section stats-section">
        <div class="container">
          <div class="stats-grid">
            <div v-for="stat in stats" :key="stat.label" class="stat-card">
              <div class="stat-icon"><i :class="['fas', stat.icon]"></i></div>
              <div class="stat-info">
                <span class="stat-value">{{ stat.value }}</span>
                <span class="stat-label">{{ stat.label }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="section features-section">
        <div class="container">
          <div class="section-header text-center">
            <h2>Tại sao chọn chúng tôi?</h2>
            <p>Những ưu điểm vượt trội khiến LibManage trở thành lựa chọn hàng đầu</p>
          </div>
          
          <div class="features-grid">
            <div v-for="feature in features" :key="feature.title" class="feature-card">
              <div class="feature-icon"><i :class="['fas', feature.icon]"></i></div>
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.description }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Contact CTA -->
      <section class="section contact-cta">
        <div class="container">
          <div class="cta-card">
            <div class="cta-content">
              <h2>Bạn cần hỗ trợ?</h2>
              <p>Đội ngũ thủ thư luôn sẵn sàng giải đáp mọi thắc mắc của bạn về quy trình mượn trả và tra cứu tài liệu.</p>
              <div class="cta-actions">
                <button class="btn btn-primary">Liên hệ ngay</button>
                <button class="btn btn-outline-white">Xem nội quy</button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/AboutView.css"></style>
