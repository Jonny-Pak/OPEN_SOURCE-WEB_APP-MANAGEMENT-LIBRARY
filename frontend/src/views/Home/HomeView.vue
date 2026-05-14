<script setup lang="ts">
import Navbar from '../../components/Navbar/Navbar.vue'
import Hero from '../../components/Hero/Hero.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import Footer from '../../components/Footer/Footer.vue'

const featuredBooks = [
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
  }
]

const categories = [
  { name: 'Kỹ năng', icon: 'fa-lightbulb', color: '#f59e0b', bg: '#fef3c7' },
  { name: 'Văn học', icon: 'fa-book-open', color: '#10b981', bg: '#d1fae5' },
  { name: 'Công nghệ', icon: 'fa-laptop-code', color: '#0ea5e9', bg: '#e0f2fe' },
  { name: 'Kinh tế', icon: 'fa-chart-line', color: '#6366f1', bg: '#e0e7ff' },
  { name: 'Lịch sử', icon: 'fa-landmark', color: '#f43f5e', bg: '#ffe4e6' },
  { name: 'Nghệ thuật', icon: 'fa-palette', color: '#d946ef', bg: '#fae8ff' }
]

</script>

<template>
  <div class="home-page">
    <Navbar />
    
    <Hero />
    
    <section class="section categories-section">
      <div class="container">
        <div class="section-header">
          <h2>Khám phá theo danh mục</h2>
          <p>Tìm kiếm sách theo sở thích của bạn</p>
        </div>
        
        <div class="category-grid">
          <div v-for="cat in categories" :key="cat.name" class="category-card" :style="{ '--hover-color': cat.color }">
            <span class="cat-icon" :style="{ backgroundColor: cat.bg, color: cat.color }">
              <i :class="['fas', cat.icon]"></i>
            </span>
            <h3>{{ cat.name }}</h3>
          </div>
        </div>
      </div>
    </section>
    
    <section class="section featured-section">
      <div class="container">
        <div class="section-header">
          <h2>Sách nổi bật</h2>
          <p>Những cuốn sách được mượn nhiều nhất tuần qua</p>
          <a href="#" class="view-all">Xem tất cả →</a>
        </div>
        
        <div class="grid grid-cols-4">
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
          <button class="btn btn-primary">Khám phá ngay</button>
        </div>
      </div>
    </section>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/HomeView.css"></style>
