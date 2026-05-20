<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'


const features = [
  {
    title: 'Tra cứu thông minh',
    description: 'Hệ thống tìm kiếm hiện đại giúp bạn tìm thấy cuốn sách yêu thích chỉ trong vài giây.',
    icon: 'fa-solid fa-magnifying-glass'
  },
  {
    title: 'Mượn trả trực tuyến',
    description: 'Quy trình mượn và trả sách được số hóa hoàn toàn, nhanh chóng và tiện lợi.',
    icon: 'fa-solid fa-rotate'
  },
  {
    title: 'Không gian hiện đại',
    description: 'Thư viện cung cấp không gian học tập yên tĩnh với đầy đủ tiện nghi hiện đại.',
    icon: 'fa-solid fa-laptop'
  }
]

const settings = ref({
  aboutHeroTitle: 'Lan tỏa tri thức, Kết nối đam mê',
  aboutHeroSubtitle: 'LibManage không chỉ là một hệ thống quản lý thư viện, mà là cầu nối đưa tri thức đến gần hơn với cộng đồng học sinh và giáo viên.',
  aboutMissionBadge: 'Sứ mệnh của chúng tôi',
  aboutMissionTitle: 'Xây dựng nền tảng tri thức vững chắc cho tương lai',
  aboutMissionDesc1: 'Được thành lập từ năm 2010, thư viện của chúng tôi luôn nỗ lực không ngừng để trở thành trung tâm học tập và nghiên cứu hàng đầu. Chúng tôi tin rằng mỗi cuốn sách là một cánh cửa mở ra thế giới mới.',
  aboutMissionDesc2: 'Với hệ thống quản lý điện tử LibManage, chúng tôi cam kết mang lại trải nghiệm mượn sách hiện đại, minh bạch và hiệu quả nhất cho mọi người dùng.',
  aboutMissionImage: 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&q=80&w=800'
})

onMounted(() => {
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
</script>

<template>
  <div class="about-page">
    <Navbar />
    
    <main class="main-content">
      <!-- Hero Section -->
      <section class="about-hero">
        <div class="container">
          <div class="hero-content">
            <h1>{{ settings.aboutHeroTitle }}</h1>
            <p v-html="settings.aboutHeroSubtitle"></p>
          </div>
        </div>
      </section>

      <!-- Mission Section -->
      <section class="section mission-section">
        <div class="container">
          <div class="mission-grid">
            <div class="mission-image">
              <img :src="settings.aboutMissionImage" alt="Library Mission" />
            </div>
            <div class="mission-text">
              <span class="section-badge">{{ settings.aboutMissionBadge }}</span>
              <h2>{{ settings.aboutMissionTitle }}</h2>
              <div v-html="settings.aboutMissionDesc1" class="rich-text-block"></div>
              <div v-html="settings.aboutMissionDesc2" class="rich-text-block"></div>
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
              <div class="feature-icon"><font-awesome-icon :icon="feature.icon" /></div>
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
