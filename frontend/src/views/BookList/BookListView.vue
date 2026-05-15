<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import { sachService } from '../../services/sachService'
import { theLoaiService } from '../../services/danhMucService'

const categories = ref<any[]>([{ name: 'Tất cả', count: 0 }])
const books = ref<any[]>([])
const isLoading = ref(true)
const error = ref<string | null>(null)

const activeCategory = ref('Tất cả')
const searchQuery = ref('')
const sortBy = ref('Mới nhất')

const fetchData = async () => {
  isLoading.value = true
  error.value = null
  try {
    const [booksData, theLoaiData] = await Promise.all([
      sachService.danhSach(),
      theLoaiService.danhSach()
    ])

    // Map books
    books.value = (booksData as any[]).map(book => ({
      id: book.maSach,
      title: book.tenSach,
      author: book.tacGias?.[0]?.tenTacGia || 'Ẩn danh',
      category: book.theLoais?.[0]?.tenTheLoai || 'Chưa phân loại',
      image: book.anhBiaUrl || 'https://images.unsplash.com/photo-1543004629-ff56ec21d2e2?auto=format&fit=crop&q=80&w=400',
      rating: 5
    }))

    // Map categories
    const mappedCategories = (theLoaiData as any[]).map(tl => ({
      name: tl.tenTheLoai,
      count: books.value.filter(b => b.category === tl.tenTheLoai).length
    }))
    
    categories.value = [
      { name: 'Tất cả', count: books.value.length },
      ...mappedCategories
    ]
  } catch (err: any) {
    console.error('Failed to fetch books:', err)
    error.value = 'Không thể tải danh sách sách. Vui lòng thử lại sau.'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})

const filteredBooks = computed(() => {
  return books.value.filter(book => {
    const matchesCategory = activeCategory.value === 'Tất cả' || book.category === activeCategory.value
    const matchesSearch = book.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                        book.author.toLowerCase().includes(searchQuery.value.toLowerCase())
    return matchesCategory && matchesSearch
  })
})

</script>

<template>
  <div class="book-list-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container content-grid">
        <!-- Sidebar -->
        <aside class="sidebar">
          <div class="filter-section">
            <h3>Thể loại</h3>
            <div v-if="isLoading" class="loading-mini">
              <div class="spinner-small"></div>
            </div>
            <ul v-else class="category-list">
              <li 
                v-for="cat in categories" 
                :key="cat.name"
                :class="{ active: activeCategory === cat.name }"
                @click="activeCategory = cat.name"
              >
                <span>{{ cat.name }}</span>
                <span class="count">{{ cat.count }}</span>
              </li>
            </ul>
          </div>
          
          <div class="filter-section">
            <h3>Trạng thái</h3>
            <div class="checkbox-group">
              <label class="checkbox-container">
                <input type="checkbox" checked />
                <span class="checkmark"></span>
                Còn sách
              </label>
              <label class="checkbox-container">
                <input type="checkbox" />
                <span class="checkmark"></span>
                Đã được mượn
              </label>
            </div>
          </div>
        </aside>
        
        <!-- Main Content -->
        <section class="main-list">
          <div class="list-header">
            <div class="search-bar">
              <input 
                v-model="searchQuery" 
                type="text" 
                placeholder="Tìm kiếm sách hoặc tác giả..." 
              />
            </div>
            <div class="sort-options">
              <span>Sắp xếp theo:</span>
              <select v-model="sortBy">
                <option>Mới nhất</option>
                <option>Phổ biến</option>
                <option>Đánh giá cao</option>
              </select>
            </div>
          </div>
          
          <div v-if="isLoading" class="loading-state">
            <div class="spinner"></div>
            <p>Đang tải danh sách sách...</p>
          </div>

          <div v-else-if="error" class="error-state">
            <i class="fas fa-exclamation-circle"></i>
            <p>{{ error }}</p>
            <button @click="fetchData" class="btn btn-outline">Thử lại</button>
          </div>

          <template v-else>
            <div class="results-info">
              <p>Tìm thấy <span>{{ filteredBooks.length }}</span> kết quả cho "{{ activeCategory }}"</p>
            </div>
            
            <div class="grid grid-cols-3">
              <BookCard 
                v-for="book in filteredBooks" 
                :key="book.id"
                v-bind="book"
              />
            </div>
            
            <div v-if="filteredBooks.length === 0" class="no-results">
              <div class="no-results-icon"><i class="fas fa-search"></i></div>
              <h3>Không tìm thấy sách nào</h3>
              <p>Hãy thử thay đổi từ khóa hoặc bộ lọc của bạn</p>
              <button @click="activeCategory = 'Tất cả'; searchQuery = ''" class="btn btn-outline">Xóa bộ lọc</button>
            </div>
            
            <div v-if="filteredBooks.length > 0" class="pagination">
              <button class="btn btn-outline" disabled><i class="fas fa-chevron-left"></i> Trước</button>
              <div class="pages">
                <button class="page-btn active">1</button>
                <button class="page-btn">2</button>
                <button class="page-btn">3</button>
              </div>
              <button class="btn btn-outline">Sau <i class="fas fa-chevron-right"></i></button>
            </div>
          </template>
        </section>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/BookListView.css"></style>
