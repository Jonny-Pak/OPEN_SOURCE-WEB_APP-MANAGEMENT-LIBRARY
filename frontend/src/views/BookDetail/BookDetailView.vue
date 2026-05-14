<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import { useCartStore } from '../../stores/cart'

const route = useRoute()
const cart = useCartStore()
const bookId = route.params.id

// Mock data for a single book
const book = ref({
  id: Number(bookId),
  title: 'Đắc Nhân Tâm',
  author: 'Dale Carnegie',
  category: 'Kỹ năng sống',
  image: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=600',
  rating: 5,
  description: 'Đắc Nhân Tâm (How to Win Friends and Influence People) là cuốn sách nổi tiếng nhất, có tầm ảnh hưởng nhất mọi thời đại của Dale Carnegie. Cuốn sách đã được dịch sang hầu hết các ngôn ngữ trên thế giới và là cuốn sách bán chạy nhất trong nhiều thập kỷ.',
  status: 'Available',
  publisher: 'NXB Tổng hợp TP.HCM',
  publishedYear: 2021,
  isbn: '978-604-335-123-4',
  pages: 320,
  language: 'Tiếng Việt'
})

const addToCart = () => {
  cart.addItem({
    id: book.value.id,
    title: book.value.title,
    author: book.value.author,
    image: book.value.image,
    category: book.value.category
  })
}

const similarBooks = [
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
  }
]

onMounted(() => {
  window.scrollTo(0, 0)
})

</script>

<template>
  <div class="book-detail-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <nav class="breadcrumb">
          <RouterLink to="/">Trang chủ</RouterLink>
          <span class="separator">/</span>
          <span class="current">{{ book.title }}</span>
        </nav>
        
        <div class="book-detail-grid">
          <!-- Left Column: Image -->
          <div class="book-visual">
            <div class="image-card">
              <img :src="book.image" :alt="book.title" />
            </div>
            <div class="visual-actions">
              <button @click="addToCart" class="btn btn-primary btn-block">
                <i class="fas fa-shopping-basket"></i> Thêm vào giỏ sách
              </button>
              <RouterLink to="/borrow/cart" class="btn btn-outline btn-block">
                Xem giỏ sách
              </RouterLink>
            </div>
          </div>
          
          <!-- Right Column: Info -->
          <div class="book-info-main">
            <div class="header-info">
              <span class="category-badge">{{ book.category }}</span>
              <div class="status-badge" :class="book.status.toLowerCase()">
                {{ book.status === 'Available' ? '● Còn sách' : '○ Đã được mượn' }}
              </div>
            </div>
            
            <h1 class="book-title">{{ book.title }}</h1>
            <p class="book-author">Tác giả: <span>{{ book.author }}</span></p>
            
            <div class="description">
              <h3>Tóm tắt nội dung</h3>
              <p>{{ book.description }}</p>
            </div>
            
            <div class="specs-grid">
              <div class="spec-item">
                <span class="label">Nhà xuất bản</span>
                <span class="value">{{ book.publisher }}</span>
              </div>
              <div class="spec-item">
                <span class="label">Năm xuất bản</span>
                <span class="value">{{ book.publishedYear }}</span>
              </div>
              <div class="spec-item">
                <span class="label">Mã ISBN</span>
                <span class="value">{{ book.isbn }}</span>
              </div>
              <div class="spec-item">
                <span class="label">Số trang</span>
                <span class="value">{{ book.pages }} trang</span>
              </div>
              <div class="spec-item">
                <span class="label">Ngôn ngữ</span>
                <span class="value">{{ book.language }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Related Books -->
        <section class="related-section">
          <div class="section-header">
            <h2>Sách cùng thể loại</h2>
            <RouterLink to="/" class="view-all">Xem tất cả →</RouterLink>
          </div>
          <div class="grid grid-cols-4">
            <BookCard 
              v-for="item in similarBooks" 
              :key="item.id"
              v-bind="item"
            />
          </div>
        </section>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/BookDetailView.css"></style>
