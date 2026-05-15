<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Hero from '../../components/Hero/Hero.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import Footer from '../../components/Footer/Footer.vue'
import { sachService } from '../../services/sachService'
import { theLoaiService, tacGiaService } from '../../services/danhMucService'

const featuredBooks = ref<any[]>([])
const categories = ref<any[]>([])
const topAuthors = ref<any[]>([])
const isLoading = ref(true)
const error = ref<string | null>(null)

// Map categories to add icons and colors for UI
const categoryStyles: Record<string, { icon: string, color: string, bg: string }> = {
  'Văn học trong nước': { icon: 'fa-book-open', color: '#10b981', bg: '#d1fae5' },
  'Văn học nước ngoài': { icon: 'fa-globe', color: '#0ea5e9', bg: '#e0f2fe' },
  'Công nghệ': { icon: 'fa-laptop-code', color: '#6366f1', bg: '#e0e7ff' },
  'Giáo khoa': { icon: 'fa-graduation-cap', color: '#f59e0b', bg: '#fef3c7' },
  'Kỹ năng': { icon: 'fa-lightbulb', color: '#f43f5e', bg: '#ffe4e6' },
  'Nghệ thuật': { icon: 'fa-palette', color: '#d946ef', bg: '#fae8ff' }
}

const fetchData = async () => {
  isLoading.value = true
  error.value = null
  try {
    const [booksData, theLoaiData, authorsData] = await Promise.all([
      sachService.danhSach(),
      theLoaiService.danhSach(),
      tacGiaService.danhSach()
    ])

    // Map books to BookCard format
    featuredBooks.value = (booksData as any[]).slice(0, 4).map(book => {
      const author = book.danhSachTacGia?.[0]
      const authorName = author ? `${author.hoDem} ${author.ten}` : 'Ẩn danh'
      const categoryName = book.danhSachTheLoai?.[0]?.tenTheLoai || 'Chưa phân loại'
      const imageUrl = book.danhSachHinhAnhUrl?.[0] || 'https://images.unsplash.com/photo-1543004629-ff56ec21d2e2?auto=format&fit=crop&q=80&w=400'
      
      return {
        id: book.maSach,
        title: book.tenSach,
        author: authorName,
        category: categoryName,
        image: imageUrl,
        rating: 5
      }
    })

    // Map categories with styles
    categories.value = (theLoaiData as any[]).slice(0, 6).map(tl => {
      const style = categoryStyles[tl.tenTheLoai] || { icon: 'fa-book', color: '#64748b', bg: '#f1f5f9' }
      return {
        name: tl.tenTheLoai,
        ...style
      }
    })

    // Map authors
    topAuthors.value = (authorsData as any[]).slice(0, 4).map(author => ({
      id: author.maTacGia,
      name: `${author.hoDem} ${author.ten}`,
      bio: author.tieuSu || 'Tác giả nổi tiếng với nhiều tác phẩm kinh điển.',
      avatar: `https://ui-avatars.com/api/?name=${author.hoDem}+${author.ten}&background=random`
    }))
  } catch (err: any) {
    console.error('Failed to fetch data:', err)
    error.value = 'Không thể tải dữ liệu từ máy chủ. Vui lòng thử lại sau.'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})

</script>

<template>
  <div class="home-page">
    <Navbar />
    
    <Hero />
    
    <!-- Categories -->
    <section class="section categories-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">Khám phá theo Thể loại</h2>
          <RouterLink to="/books" class="view-all">Xem tất cả <i class="fas fa-arrow-right"></i></RouterLink>
        </div>
        
        <div v-if="isLoading" class="loading-state">
          <div class="spinner"></div>
          <p>Đang tải danh mục...</p>
        </div>
        
        <div v-else class="category-grid">
          <RouterLink 
            v-for="cat in categories" 
            :key="cat.name" 
            :to="{ path: '/books', query: { category: cat.name }}"
            class="category-card"
          >
            <div class="category-icon" :style="{ color: cat.color, backgroundColor: cat.bg }">
              <i :class="['fas', cat.icon]"></i>
            </div>
            <span class="category-name">{{ cat.name }}</span>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- Statistics -->
    <section class="section stats-section">
      <div class="container">
        <div class="stats-container">
          <div class="stat-item">
            <span class="stat-number">50k+</span>
            <span class="stat-text">Đầu sách</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">12k+</span>
            <span class="stat-text">Độc giả</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">30k+</span>
            <span class="stat-text">Lượt mượn</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">500+</span>
            <span class="stat-text">Tác giả</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Books -->
    <section class="section featured-section bg-light">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">Sách mới nổi bật</h2>
          <RouterLink to="/books" class="view-all">Xem tất cả <i class="fas fa-arrow-right"></i></RouterLink>
        </div>
        
        <div v-if="isLoading" class="loading-state">
          <div class="spinner"></div>
          <p>Đang tải sách...</p>
        </div>

        <div v-else-if="error" class="error-state">
          <i class="fas fa-exclamation-circle"></i>
          <p>{{ error }}</p>
          <button @click="fetchData" class="btn btn-outline">Thử lại</button>
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

    <!-- Top Authors -->
    <section class="section authors-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">Tác giả nổi bật</h2>
          <RouterLink to="/authors" class="view-all">Xem tất cả <i class="fas fa-arrow-right"></i></RouterLink>
        </div>

        <div v-if="isLoading" class="loading-state">
          <div class="spinner"></div>
          <p>Đang tải tác giả...</p>
        </div>

        <div v-else class="authors-grid">
          <div v-for="author in topAuthors" :key="author.id" class="author-card">
            <img :src="author.avatar" :alt="author.name" />
            <h3>{{ author.name }}</h3>
            <p>{{ author.bio }}</p>
          </div>
        </div>
      </div>
    </section>

    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/HomeView.css"></style>
