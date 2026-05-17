<script setup lang="ts">
import { ref } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const activeFaq = ref<number | null>(null)

const toggleFaq = (index: number) => {
  activeFaq.value = activeFaq.value === index ? null : index
}

const steps = [
  {
    title: 'Tìm kiếm sách',
    description: 'Sử dụng thanh tìm kiếm hoặc duyệt qua danh mục để tìm cuốn sách bạn cần.',
    icon: 'fa-solid fa-magnifying-glass'
  },
  {
    title: 'Đăng ký mượn',
    description: 'Nhấn nút "Mượn sách" tại trang chi tiết. Hệ thống sẽ ghi nhận yêu cầu của bạn.',
    icon: 'fa-solid fa-hand-holding-heart'
  },
  {
    title: 'Nhận sách',
    description: 'Đến quầy thủ thư, xuất trình mã số học sinh để nhận sách vật lý.',
    icon: 'fa-solid fa-book-open-reader'
  },
  {
    title: 'Trả sách đúng hạn',
    description: 'Hoàn trả sách trước ngày hết hạn để duy trì điểm tin cậy của bạn.',
    icon: 'fa-solid fa-calendar-check'
  }
]

const faqs = [
  {
    question: 'Tôi có thể mượn tối đa bao nhiêu cuốn sách?',
    answer: 'Mỗi học sinh được mượn tối đa 03 cuốn sách cùng một lúc.'
  },
  {
    question: 'Thời gian mượn sách tối đa là bao lâu?',
    answer: 'Thời gian mượn mặc định là 14 ngày. Bạn có thể gia hạn thêm 07 ngày nếu không có ai khác đang đặt chỗ.'
  },
  {
    question: 'Nếu tôi làm mất hoặc hỏng sách thì sao?',
    answer: 'Trường hợp làm mất hoặc hỏng sách, bạn cần bồi thường theo giá trị hiện hành của cuốn sách cộng với phí xử lý nghiệp vụ.'
  },
  {
    question: 'Làm thế nào để gia hạn sách?',
    answer: 'Bạn có thể gia hạn trực tuyến tại trang "Cá nhân" hoặc đến trực tiếp quầy thủ thư.'
  }
]

const rules = [
  'Giữ gìn sách cẩn thận, không viết vẽ lên sách.',
  'Không cho người khác mượn lại thẻ học sinh của mình.',
  'Trả sách đúng hạn quy định để tránh bị phạt.',
  'Giữ trật tự và vệ sinh chung khi đọc sách tại thư viện.'
]

</script>

<template>
  <div class="guide-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <!-- Header -->
        <header class="guide-header text-center">
          <h1>Hướng dẫn <span class="text-accent">Mượn sách</span></h1>
          <p>Mọi thứ bạn cần biết để bắt đầu hành trình khám phá tri thức tại thư viện</p>
        </header>

        <!-- Stepper Section -->
        <section class="section steps-section">
          <div class="steps-container">
            <div v-for="(step, index) in steps" :key="index" class="step-item">
              <div class="step-number">{{ index + 1 }}</div>
              <div class="step-card">
                <div class="step-icon"><font-awesome-icon :icon="step.icon" /></div>
                <h3>{{ step.title }}</h3>
                <p>{{ step.description }}</p>
              </div>
              <div v-if="index < steps.length - 1" class="step-connector"></div>
            </div>
          </div>
        </section>

        <div class="guide-content-grid">
          <!-- FAQ Section -->
          <section class="section faq-section">
            <div class="section-title">
              <font-awesome-icon icon="fa-solid fa-circle-question" />
              <h2>Câu hỏi thường gặp</h2>
            </div>
            
            <div class="faq-list">
              <div 
                v-for="(faq, index) in faqs" 
                :key="index" 
                class="faq-item"
                :class="{ active: activeFaq === index }"
              >
                <div class="faq-question" @click="toggleFaq(index)">
                  <h3>{{ faq.question }}</h3>
                  <font-awesome-icon icon="fa-solid fa-chevron-down" />
                </div>
                <div class="faq-answer">
                  <p>{{ faq.answer }}</p>
                </div>
              </div>
            </div>
          </section>

          <!-- Rules Section -->
          <aside class="section rules-section">
            <div class="rules-card">
              <div class="section-title">
                <font-awesome-icon icon="fa-solid fa-scale-balanced" />
                <h2>Nội quy thư viện</h2>
              </div>
              <ul class="rules-list">
                <li v-for="(rule, index) in rules" :key="index">
                  <font-awesome-icon icon="fa-solid fa-circle-check" />
                  {{ rule }}
                </li>
              </ul>
              <div class="rules-note">
                <font-awesome-icon icon="fa-solid fa-circle-info" />
                <p>Việc vi phạm nội quy có thể dẫn đến việc tạm dừng quyền mượn sách.</p>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/GuideView.css"></style>
