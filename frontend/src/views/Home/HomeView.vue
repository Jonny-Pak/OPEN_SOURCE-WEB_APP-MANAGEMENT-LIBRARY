<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Hero from '../../components/Hero/Hero.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import Footer from '../../components/Footer/Footer.vue'
import { sachService } from '@/services/sachService'
import { danhMucService } from '@/services/danhMucService'

const featuredBooks = ref<any[]>([])
const categories = ref<any[]>([])
const isLoading = ref(true)
const isError = ref(false)
const errorMessage = ref('')

const staticCategories = [
  { name: 'Kỹ năng', icon: 'fa-solid fa-lightbulb', color: '#f59e0b', bg: '#fef3c7' },
  { name: 'Văn học', icon: 'fa-solid fa-book-open', color: '#10b981', bg: '#d1fae5' },
  { name: 'Công nghệ', icon: 'fa-solid fa-laptop-code', color: '#0ea5e9', bg: '#e0f2fe' },
  { name: 'Kinh tế', icon: 'fa-solid fa-chart-line', color: '#6366f1', bg: '#e0e7ff' },
  { name: 'Lịch sử', icon: 'fa-solid fa-landmark', color: '#f43f5e', bg: '#ffe4e6' },
  { name: 'Nghệ thuật', icon: 'fa-solid fa-palette', color: '#d946ef', bg: '#fae8ff' }
]

const loadData = async () => {
  isLoading.value = true
  isError.value = false
  errorMessage.value = ''
  try {
    // 1. Fetch books
    const fetchedBooks = await sachService.getAll({ size: 4 })
    if (fetchedBooks && fetchedBooks.content) {
      featuredBooks.value = fetchedBooks.content.slice(0, 4).map((b: any) => ({
        id: b.maSach,
        title: b.tenSach,
        author: b.danhSachTacGia?.map((t: any) => `${t.hoDem} ${t.ten}`).join(', ') || 'Đang cập nhật',
        category: b.danhSachTheLoai?.[0]?.tenTheLoai || 'Chưa phân loại',
        image: b.danhSachHinhAnh?.[0]?.duongDan || 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400',
        rating: 5
      }))
    }

    // 2. Fetch real categories
    const fetchedCats = await danhMucService.getAll()
    if (fetchedCats && fetchedCats.length > 0) {
      categories.value = fetchedCats.slice(0, 6).map((c: any, index: number) => {
        const fallback = (staticCategories[index % staticCategories.length] || staticCategories[0]) as { name: string; icon: string; color: string; bg: string }
        return {
          id: c.maTheLoai || c.maDanhMuc || index,
          name: c.tenTheLoai || c.tenDanhMuc || c.ten || 'Thể loại',
          icon: fallback.icon,
          color: fallback.color,
          bg: fallback.bg
        }
      })
    } else {
      categories.value = staticCategories
    }
  } catch (error: any) {
    console.error('Lỗi khi tải dữ liệu trang chủ:', error)
    isError.value = true
    errorMessage.value = error.message || 'Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại kết nối.'
    categories.value = staticCategories
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadData()
})

</script>

<template>
  <div class="home-page">
    <Navbar />
    
    <Hero />
    
    <!-- Error State Display -->
    <div v-if="isError" class="container" style="margin-top: 20px;">
      <div class="error-banner" style="background: rgba(239, 68, 68, 0.1); border: 1px solid rgba(239, 68, 68, 0.2); padding: 15px 20px; border-radius: 12px; display: flex; align-items: center; justify-content: space-between; color: #ef4444;">
        <div style="display: flex; align-items: center; gap: 12px;">
          <font-awesome-icon icon="fa-solid fa-triangle-exclamation" class="fa-lg" />
          <span>{{ errorMessage }}</span>
        </div>
        <button @click="loadData" class="btn btn-secondary" style="padding: 6px 12px; font-size: 0.875rem;">
          Thử lại <font-awesome-icon icon="fa-solid fa-rotate" style="margin-left: 4px;" />
        </button>
      </div>
    </div>
    
    <section class="section categories-section">
      <div class="container">
        <div class="section-header">
          <h2>Khám phá theo danh mục</h2>
          <p>Tìm kiếm sách theo sở thích của bạn</p>
        </div>
        
        <div class="category-grid">
          <!-- Loading skeleton for categories -->
          <template v-if="isLoading">
            <div v-for="i in 6" :key="i" class="category-card skeleton" style="animation: pulse 1.5s infinite ease-in-out;">
              <span class="cat-icon" style="background: #e2e8f0; width: 64px; height: 64px; border-radius: 50%; display: inline-block;"></span>
              <h3 style="background: #e2e8f0; height: 16px; width: 60%; margin: 12px auto 0; border-radius: 4px;"></h3>
            </div>
          </template>
          
          <template v-else>
            <div v-for="cat in categories" :key="cat.name" class="category-card" :style="{ '--hover-color': cat.color }">
              <span class="cat-icon" :style="{ backgroundColor: cat.bg, color: cat.color }">
                <font-awesome-icon :icon="cat.icon" />
              </span>
              <h3>{{ cat.name }}</h3>
            </div>
          </template>
        </div>
      </div>
    </section>
    
    <section class="section featured-section">
      <div class="container">
        <div class="section-header">
          <h2>Sách nổi bật</h2>
          <p>Những cuốn sách được mượn nhiều nhất tuần qua</p>
          <router-link to="/books" class="view-all">Xem tất cả →</router-link>
        </div>
        
        <!-- Loading skeleton for featured books -->
        <div v-if="isLoading" class="grid grid-cols-4 gap-6">
          <div v-for="i in 4" :key="i" class="book-card-skeleton" style="background: white; border-radius: 12px; padding: 16px; border: 1px solid #f1f5f9; animation: pulse 1.5s infinite ease-in-out;">
            <div style="background: #e2e8f0; height: 260px; border-radius: 8px; margin-bottom: 12px;"></div>
            <div style="background: #e2e8f0; height: 18px; width: 80%; border-radius: 4px; margin-bottom: 8px;"></div>
            <div style="background: #e2e8f0; height: 14px; width: 60%; border-radius: 4px; margin-bottom: 8px;"></div>
            <div style="background: #e2e8f0; height: 14px; width: 40%; border-radius: 4px;"></div>
          </div>
        </div>
        
        <div v-else class="grid grid-cols-4">
          <BookCard 
            v-for="book in featuredBooks" 
            :key="book.id"
            v-bind="book"
          />
        </div>
      </div>
    </section>
    
    <section class="section info-section">
      <div class="container info-grid">
        <div class="info-image">
          <img src="https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&q=80&w=800" alt="Library" />
        </div>
        <div class="info-text">
          <span class="badge">Về chúng tôi</span>
          <h2>Không gian tri thức hiện đại cho mọi người</h2>
          <p>Thư viện của chúng tôi không chỉ là nơi chứa sách, mà còn là một không gian cộng đồng để học tập, sáng tạo và chia sẻ.</p>
          <ul class="features">
            <li>✓ Hơn 50,000 đầu sách đa dạng</li>
            <li>✓ Hệ thống quản lý mượn trả trực tuyến</li>
            <li>✓ Không gian học tập yên tĩnh, hiện đại</li>
            <li>✓ Nhiều sự kiện văn hóa hàng tháng</li>
          </ul>
          <router-link to="/books" class="btn btn-primary" style="display: inline-block; text-align: center;">Khám phá ngay</router-link>
        </div>
      </div>
    </section>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/HomeView.css"></style>

<style scoped>
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .5;
  }
}
</style>
