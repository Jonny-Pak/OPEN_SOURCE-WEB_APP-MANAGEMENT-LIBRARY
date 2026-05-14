<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'

const route = useRoute()
const authorId = route.params.id

// Mock author data
const author = ref({
  id: Number(authorId),
  name: 'Dale Carnegie',
  image: 'https://images.unsplash.com/photo-1544717305-27a7ad515996?auto=format&fit=crop&q=80&w=600',
  bio: 'Dale Carnegie (1888-1955) là một nhà văn và diễn giả người Mỹ, người đã phát triển các khóa học nổi tiếng về tự cải thiện, bán hàng, huấn luyện doanh nghiệp, nói trước công chúng và kỹ năng giao tiếp. Sinh ra trong cảnh nghèo khó trên một trang trại ở Missouri, ông là tác giả của cuốn Đắc Nhân Tâm (How to Win Friends and Influence People), xuất bản lần đầu năm 1936, một trong những cuốn sách bán chạy nhất mọi thời đại.',
  nationality: 'Hoa Kỳ',
  born: '24/11/1888',
  died: '01/11/1955',
  books: [
    {
      id: 1,
      title: 'Đắc Nhân Tâm',
      author: 'Dale Carnegie',
      category: 'Kỹ năng sống',
      image: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400'
    },
    {
      id: 7,
      title: 'Quẳng Gánh Lo Đi Và Vui Sống',
      author: 'Dale Carnegie',
      category: 'Kỹ năng sống',
      image: 'https://images.unsplash.com/photo-1541963463532-d68292c34b19?auto=format&fit=crop&q=80&w=400'
    },
    {
      id: 8,
      title: 'Lợi Thế Ngôn Từ',
      author: 'Dale Carnegie',
      category: 'Kỹ năng sống',
      image: 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&q=80&w=400'
    }
  ]
})

</script>

<template>
  <div class="author-detail-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <!-- Author Profile Header -->
        <section class="author-profile">
          <div class="author-visual">
            <img :src="author.image" :alt="author.name" />
          </div>
          <div class="author-content">
            <nav class="breadcrumb">
              <RouterLink to="/authors">Tác giả</RouterLink>
              <span class="separator">/</span>
              <span class="current">{{ author.name }}</span>
            </nav>
            <h1>{{ author.name }}</h1>
            
            <div class="author-meta">
              <div class="meta-item">
                <span class="label">Quốc tịch</span>
                <span class="value">{{ author.nationality }}</span>
              </div>
              <div class="meta-item">
                <span class="label">Ngày sinh</span>
                <span class="value">{{ author.born }}</span>
              </div>
              <div v-if="author.died" class="meta-item">
                <span class="label">Ngày mất</span>
                <span class="value">{{ author.died }}</span>
              </div>
            </div>

            <div class="author-bio">
              <h3>Tiểu sử</h3>
              <p>{{ author.bio }}</p>
            </div>
          </div>
        </section>

        <!-- Author's Books -->
        <section class="author-books">
          <div class="section-header">
            <h2>Các tác phẩm tiêu biểu</h2>
            <p>Khám phá những cuốn sách hay nhất của {{ author.name }}</p>
          </div>

          <div class="grid grid-cols-4">
            <BookCard 
              v-for="book in author.books" 
              :key="book.id"
              v-bind="book"
              :rating="5"
            />
          </div>
        </section>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/AuthorDetailView.css"></style>
