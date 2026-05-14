<script setup lang="ts">
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const ruleCategories = [
  {
    title: 'Quy định chung',
    icon: 'fa-info-circle',
    rules: [
      'Học sinh, giáo viên và nhân viên nhà trường khi vào thư viện phải mang theo thẻ học sinh/thẻ công chức.',
      'Giữ gìn trật tự, không làm ồn, không nói chuyện điện thoại trong khu vực đọc sách.',
      'Không mang thức ăn, nước uống (trừ nước lọc) vào thư viện.',
      'Trang phục chỉnh tề, lịch sự theo đúng quy định của nhà trường.'
    ]
  },
  {
    title: 'Mượn và Trả sách',
    icon: 'fa-book-reader',
    rules: [
      'Mỗi lần mượn không quá 03 cuốn sách trong thời gian tối đa 14 ngày.',
      'Chỉ được mượn sách bằng thẻ của chính mình, không cho người khác mượn thẻ.',
      'Kiểm tra tình trạng sách trước khi mượn. Nếu phát hiện sách hư hỏng phải báo ngay cho thủ thư.',
      'Phải trả sách đúng hạn. Nếu muốn mượn tiếp phải làm thủ tục gia hạn.'
    ]
  },
  {
    title: 'Sử dụng thiết bị & Tài sản',
    icon: 'fa-desktop',
    rules: [
      'Máy tính chỉ dùng để tra cứu thông tin và phục vụ học tập, không chơi game hay xem nội dung không phù hợp.',
      'Không tự ý thay đổi cấu hình máy tính hoặc cài đặt các phần mềm lạ.',
      'Sử dụng bàn ghế và trang thiết bị khác một cách cẩn thận, không viết vẽ lên bàn.',
      'Trước khi rời khỏi thư viện, phải xếp lại ghế và dọn dẹp khu vực ngồi.'
    ]
  },
  {
    title: 'Xử lý vi phạm',
    icon: 'fa-exclamation-triangle',
    rules: [
      'Trả sách quá hạn sẽ bị phạt tiền theo quy định (1.000đ/ngày/cuốn).',
      'Làm mất sách phải bồi thường theo giá trị thực tế của sách tại thời điểm đó.',
      'Làm hư hỏng sách tùy mức độ sẽ phải bồi thường tiền hoặc mua lại sách mới.',
      'Vi phạm nghiêm trọng nội quy sẽ bị tạm dừng quyền sử dụng thư viện.'
    ]
  }
]

</script>

<template>
  <div class="rules-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <!-- Hero Section -->
        <header class="rules-hero text-center">
          <div class="hero-icon"><i class="fas fa-gavel"></i></div>
          <h1>Nội quy <span class="text-accent">Thư viện</span></h1>
          <p>Vì một môi trường học tập văn minh, hiện đại và công bằng cho tất cả mọi người</p>
        </header>

        <!-- Rules Grid -->
        <div class="rules-grid">
          <section v-for="category in ruleCategories" :key="category.title" class="rule-section">
            <div class="section-header">
              <div class="category-icon">
                <i :class="['fas', category.icon]"></i>
              </div>
              <h2>{{ category.title }}</h2>
            </div>
            <ul class="rule-list">
              <li v-for="(rule, index) in category.rules" :key="index">
                <span class="rule-bullet"></span>
                <p>{{ rule }}</p>
              </li>
            </ul>
          </section>
        </div>

        <!-- Callout/Note -->
        <div class="important-note">
          <div class="note-icon">
            <i class="fas fa-lightbulb"></i>
          </div>
          <div class="note-content">
            <h3>Lưu ý quan trọng</h3>
            <p>Thư viện là không gian chung cho tất cả chúng ta. Việc tuân thủ nội quy không chỉ giúp bảo vệ tài sản nhà trường mà còn thể hiện văn hóa ứng xử của mỗi học sinh. Hãy cùng nhau xây dựng một cộng đồng tri thức văn minh!</p>
          </div>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/RulesView.css"></style>
