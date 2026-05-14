<script setup lang="ts">
import { ref, computed } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'

const categories = [
  { name: 'Tất cả', count: 120 },
  { name: 'Kỹ năng sống', count: 25 },
  { name: 'Văn học', count: 40 },
  { name: 'Công nghệ', count: 15 },
  { name: 'Kinh tế', count: 20 },
  { name: 'Lịch sử', count: 12 },
  { name: 'Nghệ thuật', count: 8 }
]

const books = ref([
  {
    id: 1,
    title: 'Đắc Nhân Tâm',
    author: 'Dale Carnegie',
    category: 'Kỹ năng sống',
    image: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400',
    rating: 5
  },
  {
    id: 2,
    title: 'Nhà Giả Kim',
    author: 'Paulo Coelho',
    category: 'Văn học',
    image: 'https://images.unsplash.com/photo-1541963463532-d68292c34b19?auto=format&fit=crop&q=80&w=400',
    rating: 4
  },
  {
    id: 3,
    title: 'Clean Code',
    author: 'Robert C. Martin',
    category: 'Công nghệ',
    image: 'https://images.unsplash.com/photo-1516116216624-53e697fedbea?auto=format&fit=crop&q=80&w=400',
    rating: 5
  },
  {
    id: 4,
    title: 'Sapiens',
    author: 'Yuval Noah Harari',
    category: 'Lịch sử',
    image: 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&q=80&w=400',
    rating: 5
  },
  {
    id: 5,
    title: 'Tư duy nhanh và chậm',
    author: 'Daniel Kahneman',
    category: 'Kinh tế',
    image: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&q=80&w=400',
    rating: 5
  },
  {
    id: 6,
    title: 'Code dạo ký sự',
    author: 'Phạm Huy Hoàng',
    category: 'Công nghệ',
    image: 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&q=80&w=400',
    rating: 4
  }
])

const activeCategory = ref('Tất cả')
const searchQuery = ref('')
const sortBy = ref('Mới nhất')

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
            <ul class="category-list">
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
        </section>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/BookListView.css"></style>
