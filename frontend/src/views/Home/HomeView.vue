<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Hero from '../../components/Hero/Hero.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import Footer from '../../components/Footer/Footer.vue'
import { sachService } from '@/services/sachService'
import { danhMucService } from '@/services/danhMucService'

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
    // 1. Fetch real categories/genres
    const fetchedCats = await danhMucService.getAll()
    let listCats: any[] = []
    if (fetchedCats && fetchedCats.length > 0) {
      listCats = fetchedCats.slice(0, 6).map((c: any, index: number) => {
        const fallback = staticCategories[index % staticCategories.length] || staticCategories[0]
        return {
          id: c.maTheLoai || c.maDanhMuc || index,
          name: c.tenTheLoai || c.tenDanhMuc || c.ten || 'Thể loại',
          icon: fallback ? fallback.icon : 'fa-solid fa-book',
          color: fallback ? fallback.color : '#0ea5e9',
          bg: fallback ? fallback.bg : '#e0f2fe',
          books: [],
          loading: true
        }
      })
    } else {
      listCats = staticCategories.map((c, index) => ({
        ...c,
        id: index + 1,
        books: [],
        loading: true
      }))
    }
    categories.value = listCats

    // 2. Fetch up to 5 books in parallel for each category
    await Promise.all(
      categories.value.map(async (cat: any) => {
        try {
          const res = await sachService.advancedSearch({
            maTheLoai: cat.id,
            size: 5
          })
          if (res && res.content) {
            cat.books = res.content.map((b: any) => ({
              id: b.maSach,
              title: b.tenSach,
              author: b.danhSachTacGia?.map((t: any) => `${t.hoDem} ${t.ten}`).join(', ') || 'Đang cập nhật',
              category: b.danhSachTheLoai?.[0]?.tenTheLoai || 'Chưa phân loại',
              image: b.danhSachHinhAnh?.[0]?.duongDan || 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400',
              rating: 5
            }))
          }
        } catch (err) {
          console.error(`Lỗi tải sách cho danh mục ${cat.name}:`, err)
        } finally {
          cat.loading = false
        }
      })
    )
  } catch (error: any) {
    console.error('Lỗi khi tải dữ liệu trang chủ:', error)
    isError.value = true
    errorMessage.value = error.message || 'Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại kết nối.'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadData()
})

const genreTrack = ref<HTMLElement | null>(null)

const scrollPrev = () => {
  if (genreTrack.value) {
    genreTrack.value.scrollBy({ left: -240, behavior: 'smooth' })
  }
}

const scrollNext = () => {
  if (genreTrack.value) {
    genreTrack.value.scrollBy({ left: 240, behavior: 'smooth' })
  }
}

const scrollToCategory = (catId: any) => {
  const el = document.getElementById(`category-sec-${catId}`)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}
</script>

<template>
  <div class="home-page">
    <Navbar />
    
    <Hero />
    
    <!-- Dynamic Book Categories Horizontal Carousel Bar -->
    <h3 v-if="categories && categories.length > 0" class="genre-carousel-title container">Một số thể loại bạn có thể quan tâm</h3>
    <div v-if="categories && categories.length > 0" class="genre-carousel-container container">
      <button class="carousel-btn prev-btn" @click="scrollPrev">
        <font-awesome-icon icon="fa-solid fa-chevron-left" />
      </button>
      <div class="genre-carousel-viewport" ref="genreTrack">
        <div class="genre-carousel-track">
          <div 
            v-for="cat in categories" 
            :key="cat.id" 
            class="genre-carousel-card"
            @click="scrollToCategory(cat.id)"
          >
            <div class="genre-card-inner">
              <h4>{{ cat.name }}</h4>
            </div>
          </div>
        </div>
      </div>
      <button class="carousel-btn next-btn" @click="scrollNext">
        <font-awesome-icon icon="fa-solid fa-chevron-right" />
      </button>
    </div>
    
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
    
    <!-- Loading Screen if Categories haven't initialized -->
    <div v-if="isLoading && categories.length === 0" class="container text-center" style="padding: 4rem 0;">
      <div class="tai-indicator" style="font-size: 1.25rem; color: var(--text-muted);">
        <font-awesome-icon icon="fa-solid fa-spinner" spin style="margin-right: 8px;" />
        Đang tải dữ liệu thư viện...
      </div>
    </div>

    <!-- Book Categories Sections -->
    <template v-else>
      <section v-for="cat in categories" :key="cat.id" :id="'category-sec-' + cat.id" class="section category-books-section">
        <div class="container">
          <div class="section-header">
            <div class="category-title-wrapper">
              <div>
                <h2>{{ cat.name }}</h2>
                <p>Khám phá sách thuộc thể loại {{ cat.name }}</p>
              </div>
            </div>
            <router-link :to="{ name: 'books', query: { category: cat.id } }" class="view-all">
              Xem thêm <font-awesome-icon icon="fa-solid fa-arrow-right" />
            </router-link>
          </div>

          <!-- Loading skeleton for category books -->
          <div v-if="cat.loading" class="grid grid-cols-5 gap-6">
            <div v-for="i in 5" :key="i" class="book-card-skeleton" style="background: white; border-radius: 12px; padding: 16px; border: 1px solid #f1f5f9; animation: pulse 1.5s infinite ease-in-out;">
              <div style="background: #e2e8f0; height: 260px; border-radius: 8px; margin-bottom: 12px;"></div>
              <div style="background: #e2e8f0; height: 18px; width: 80%; border-radius: 4px; margin-bottom: 8px;"></div>
              <div style="background: #e2e8f0; height: 14px; width: 60%; border-radius: 4px; margin-bottom: 8px;"></div>
              <div style="background: #e2e8f0; height: 14px; width: 40%; border-radius: 4px;"></div>
            </div>
          </div>

          <div v-else>
            <div v-if="cat.books.length === 0" class="empty-category">
              <div class="empty-icon-box">
                <font-awesome-icon icon="fa-solid fa-book-open" class="empty-icon" />
              </div>
              <p>Hiện tại chưa có sách nào thuộc thể loại {{ cat.name }}</p>
            </div>
            <div v-else class="grid grid-cols-5 gap-6">
              <BookCard 
                v-for="book in cat.books" 
                :key="book.id"
                v-bind="book"
              />
            </div>
          </div>
        </div>
      </section>
    </template>
    

    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/HomeView.css"></style>

<style scoped>
.category-books-section {
  border-bottom: 1px solid #f1f5f9;
}
.category-books-section:nth-child(even) {
  background-color: #f8fafc;
}
.category-title-wrapper {
  display: flex !important;
  flex-direction: column !important;
  align-items: flex-start !important;
  text-align: left !important;
}
.category-title-wrapper > div {
  text-align: left !important;
}
.section-header {
  display: flex !important;
  flex-direction: row !important;
  justify-content: space-between !important;
  align-items: flex-end !important;
  flex-wrap: wrap !important;
  margin-bottom: var(--spacing-lg) !important;
  gap: 1rem !important;
  text-align: left !important;
}
.section-header h2 {
  font-size: 1.75rem !important;
  font-weight: 800 !important;
  color: var(--primary) !important;
  text-align: left !important;
  margin: 0 !important;
}
.section-header p {
  color: var(--text-muted) !important;
  font-size: 0.95rem !important;
  text-align: left !important;
  margin: 0.25rem 0 0 0 !important;
}
.view-all {
  font-weight: 600;
  color: var(--accent);
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  transition: all 0.2s;
  padding: 0;
  background: none;
  border-radius: 0;
  position: static;
}
.view-all:hover {
  color: var(--accent-hover);
  background: none;
  transform: translateX(3px);
}
.empty-category {
  text-align: center;
  padding: 3rem 1.5rem;
  background: white;
  border-radius: 16px;
  border: 1px dashed #cbd5e1;
  color: #64748b;
}
.empty-icon-box {
  width: 60px;
  height: 60px;
  background: #f1f5f9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
}
.empty-icon {
  font-size: 1.5rem;
  color: #94a3b8;
}

.grid-cols-5 {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: var(--spacing-lg);
}

@media (max-width: 1200px) {
  .grid-cols-5 {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 992px) {
  .grid-cols-5 {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .grid-cols-5 {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .grid-cols-5 {
    grid-template-columns: 1fr;
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .5;
  }
}

/* Genre horizontal carousel slider styles */
.genre-carousel-title {
  font-size: 1.35rem;
  font-weight: 800;
  color: var(--primary);
  margin: 3rem auto 0 auto;
  text-align: left;
  padding: 0 1rem;
}
.genre-carousel-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 0.75rem auto 1.5rem auto;
  position: relative;
  padding: 0 1rem;
}
.genre-carousel-viewport {
  flex: 1;
  overflow-x: auto;
  scrollbar-width: none;
  padding: 8px 4px;
}
.genre-carousel-viewport::-webkit-scrollbar {
  display: none;
}
.genre-carousel-track {
  display: flex;
  gap: 1.25rem;
}
.genre-carousel-card {
  flex: 0 0 calc((100% - 5rem) / 5);
  min-width: 170px;
  cursor: pointer;
}
.genre-card-inner {
  background: white;
  border: 1px solid var(--border);
  padding: 1rem;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}
.genre-carousel-card:hover .genre-card-inner {
  transform: translateY(-3px);
  border-color: var(--accent);
  box-shadow: 0 10px 15px -3px rgba(6, 182, 212, 0.1), 0 4px 6px -2px rgba(6, 182, 212, 0.05);
  background: linear-gradient(185deg, white 0%, rgba(6, 182, 212, 0.02) 100%);
}
.genre-card-inner h4 {
  font-size: 0.95rem;
  font-weight: 700;
  color: var(--primary);
  margin: 0;
  transition: color 0.2s;
}
.genre-carousel-card:hover .genre-card-inner h4 {
  color: var(--accent);
}
.carousel-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: white;
  border: 1px solid var(--border);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.08);
  transition: all 0.2s;
  flex-shrink: 0;
  z-index: 10;
}
.carousel-btn:hover {
  background: var(--accent);
  border-color: var(--accent);
  color: white;
  transform: scale(1.05);
}
</style>
