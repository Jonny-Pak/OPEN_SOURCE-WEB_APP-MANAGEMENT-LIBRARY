<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const searchQuery = ref('')
const activeFaq = ref<string | null>(null)

const toggleFaq = (id: string) => {
  activeFaq.value = activeFaq.value === id ? null : id
}

const defaultFaqs = [
  {
    title: 'Tài khoản & Đăng nhập',
    icon: 'fa-solid fa-circle-user',
    questions: [
      {
        id: 'acc-1',
        question: 'Làm thế nào để tôi có tài khoản thư viện?',
        answer: 'Tài khoản thư viện sẽ được quản trị viên nhà trường cấp riêng cho từng học sinh. Bạn không cần phải tự đăng ký. Hãy liên hệ văn phòng nhà trường hoặc thủ thư nếu bạn chưa nhận được thông tin đăng nhập.'
      },
      {
        id: 'acc-2',
        question: 'Tôi quên mật khẩu thì phải làm sao?',
        answer: 'Trường hợp quên mật khẩu, bạn hãy mang thẻ học sinh đến trực tiếp quầy thủ thư để yêu cầu cấp lại mật khẩu mới.'
      }
    ]
  },
  {
    title: 'Mượn & Trả sách',
    icon: 'fa-solid fa-book',
    questions: [
      {
        id: 'borrow-1',
        question: 'Quy trình mượn sách trực tuyến như thế nào?',
        answer: 'Bạn tìm cuốn sách muốn mượn, nhấn nút "Mượn sách" tại trang chi tiết. Sau đó, hãy đến thư viện trong vòng 24h để nhận sách vật lý từ thủ thư.'
      },
      {
        id: 'borrow-2',
        question: 'Tôi có thể mượn sách trong bao lâu?',
        answer: 'Thời hạn mượn sách thông thường là 14 ngày. Bạn có thể gia hạn thêm nếu sách đó không có người khác đang đặt trước.'
      },
      {
        id: 'borrow-3',
        question: 'Tôi có bị phạt nếu trả sách quá hạn không?',
        answer: 'Có, theo nội quy thư viện, học sinh trả sách quá hạn sẽ bị phạt tiền 1.000đ/ngày/cuốn sách.'
      }
    ]
  },
  {
    title: 'Dịch vụ khác',
    icon: 'fa-solid fa-bell',
    questions: [
      {
        id: 'svc-1',
        question: 'Thư viện có cung cấp chỗ ngồi học không?',
        answer: 'Có, thư viện có khu vực tự học hiện đại với đầy đủ ổ cắm điện và wifi miễn phí phục vụ học sinh.'
      },
      {
        id: 'svc-2',
        question: 'Tôi có thể đề xuất thư viện mua thêm sách mới không?',
        answer: 'Chắc chắn rồi! Bạn có thể gửi tên sách và tác giả vào mục "Góp ý" hoặc trao đổi trực tiếp với thủ thư.'
      }
    ]
  }
]

const settings = ref({
  faqTitle: 'Chúng tôi có thể giúp gì cho bạn?',
  faqSubtitle: 'Tìm kiếm câu trả lời nhanh chóng cho các thắc mắc thường gặp nhất',
  faqsJson: JSON.stringify(defaultFaqs, null, 2)
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

const faqCategories = computed(() => {
  try {
    return JSON.parse(settings.value.faqsJson)
  } catch (e) {
    return defaultFaqs
  }
})

const filteredFaqs = computed(() => {
  if (!searchQuery.value) return faqCategories.value
  
  return faqCategories.value.map((cat: any) => ({
    ...cat,
    questions: cat.questions.filter((q: any) => 
      q.question.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      q.answer.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  })).filter((cat: any) => cat.questions.length > 0)
})
</script>

<template>
  <div class="faq-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <!-- Hero Section -->
        <header class="faq-hero text-center">
          <h1>{{ settings.faqTitle }}</h1>
          <p>{{ settings.faqSubtitle }}</p>
          
          <div class="faq-search">
            <font-awesome-icon icon="fa-solid fa-magnifying-glass" class="search-icon" />
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="Nhập từ khóa tìm kiếm câu hỏi..." 
            />
          </div>
        </header>

        <!-- FAQ Categories -->
        <div class="faq-container">
          <div v-if="filteredFaqs.length === 0" class="no-results">
            <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
            <p>Không tìm thấy câu hỏi nào phù hợp với "{{ searchQuery }}"</p>
            <button @click="searchQuery = ''" class="btn btn-outline">Xem tất cả câu hỏi</button>
          </div>

          <div v-for="category in filteredFaqs" :key="category.title" class="faq-category">
            <div class="category-header">
              <font-awesome-icon :icon="category.icon" />
              <h2>{{ category.title }}</h2>
            </div>
            
            <div class="faq-list">
              <div 
                v-for="faq in category.questions" 
                :key="faq.id" 
                class="faq-item"
                :class="{ active: activeFaq === faq.id }"
              >
                <div class="faq-question" @click="toggleFaq(faq.id)">
                  <h3>{{ faq.question }}</h3>
                  <div class="toggle-icon">
                    <font-awesome-icon icon="fa-solid fa-plus" />
                  </div>
                </div>
                <div class="faq-answer">
                  <div class="answer-content">
                    <p>{{ faq.answer }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Contact Support -->
        <section class="support-cta">
          <div class="support-card">
            <div class="support-icon">
              <font-awesome-icon icon="fa-solid fa-headset" />
            </div>
            <div class="support-text">
              <h3>Vẫn còn thắc mắc?</h3>
              <p>Nếu bạn không tìm thấy câu trả lời, đừng ngần ngại liên hệ trực tiếp với chúng tôi qua hotline hoặc gửi email.</p>
            </div>
            <button class="btn btn-primary">Gửi yêu cầu hỗ trợ</button>
          </div>
        </section>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/FaqView.css"></style>
