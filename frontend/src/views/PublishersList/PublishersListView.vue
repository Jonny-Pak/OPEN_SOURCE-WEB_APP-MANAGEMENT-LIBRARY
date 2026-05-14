<script setup lang="ts">
import { ref, computed } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const searchQuery = ref('')

const publishers = [
  {
    id: 1,
    name: 'NXB Trẻ',
    logo: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=200',
    description: 'Nhà xuất bản Trẻ là một trong những nhà xuất bản uy tín hàng đầu tại Việt Nam, chuyên về văn học, kỹ năng và sách thiếu nhi.',
    bookCount: 1200
  },
  {
    id: 2,
    name: 'NXB Kim Đồng',
    logo: 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&q=80&w=200',
    description: 'Nhà xuất bản Kim Đồng chuyên xuất bản sách cho thiếu nhi và thanh thiếu niên lớn nhất Việt Nam.',
    bookCount: 2500
  },
  {
    id: 3,
    name: 'NXB Tổng hợp TP.HCM',
    logo: 'https://images.unsplash.com/photo-1491841573634-28140fc7ced7?auto=format&fit=crop&q=80&w=200',
    description: 'Chuyên xuất bản các loại sách về chính trị, văn hóa, giáo dục và khoa học kỹ thuật.',
    bookCount: 800
  },
  {
    id: 4,
    name: 'NXB Nhã Nam',
    logo: 'https://images.unsplash.com/photo-1543003919-a995d01a5d92?auto=format&fit=crop&q=80&w=200',
    description: 'Nổi tiếng với việc chọn lọc và dịch thuật các tác phẩm văn học nước ngoài kinh điển và hiện đại.',
    bookCount: 1500
  },
  {
    id: 5,
    name: 'NXB Phụ Nữ',
    logo: 'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&q=80&w=200',
    description: 'Tập trung vào các lĩnh vực về gia đình, phụ nữ, nuôi dạy con cái và văn học nữ quyền.',
    bookCount: 600
  },
  {
    id: 6,
    name: 'NXB Giáo Dục',
    logo: 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&q=80&w=200',
    description: 'Đơn vị lớn nhất cung cấp sách giáo khoa, giáo trình và tài liệu tham khảo cho mọi cấp học.',
    bookCount: 5000
  }
]

const filteredPublishers = computed(() => {
  return publishers.filter(p => 
    p.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

</script>

<template>
  <div class="publishers-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <header class="page-header text-center">
          <h1>Nhà <span class="text-accent">Xuất bản</span></h1>
          <p>Các đối tác chiến lược cung cấp nguồn tri thức dồi dào cho thư viện</p>
          
          <div class="search-bar">
            <i class="fas fa-search"></i>
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Tìm kiếm nhà xuất bản..." 
            />
          </div>
        </header>

        <div v-if="filteredPublishers.length === 0" class="no-results text-center">
          <i class="fas fa-building-circle-exclamation"></i>
          <p>Không tìm thấy nhà xuất bản nào phù hợp</p>
        </div>

        <div v-else class="publisher-grid">
          <RouterLink 
            v-for="pub in filteredPublishers" 
            :key="pub.id" 
            :to="`/publisher/${pub.id}`"
            class="publisher-card"
          >
            <div class="pub-logo">
              <img :src="pub.logo" :alt="pub.name" />
            </div>
            <div class="pub-info">
              <h3>{{ pub.name }}</h3>
              <p>{{ pub.description }}</p>
              <div class="pub-footer">
                <span class="count-badge">
                  <i class="fas fa-book-bookmark"></i> {{ pub.bookCount }}+ đầu sách
                </span>
                <span class="link-text">Xem chi tiết <i class="fas fa-chevron-right"></i></span>
              </div>
            </div>
          </RouterLink>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/PublishersListView.css"></style>
