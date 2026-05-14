<script setup lang="ts">
import { ref, computed } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const searchQuery = ref('')

const authors = [
  {
    id: 1,
    name: 'Dale Carnegie',
    image: 'https://images.unsplash.com/photo-1544717297-fa95b3ee21f3?auto=format&fit=crop&q=80&w=300',
    bio: 'Dale Carnegie là một nhà văn và diễn giả người Mỹ, người đã phát triển các khóa học nổi tiếng về tự cải thiện, bán hàng, huấn luyện doanh nghiệp, nói trước công chúng và kỹ năng giao tiếp.',
    bookCount: 12
  },
  {
    id: 2,
    name: 'Paulo Coelho',
    image: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&q=80&w=300',
    bio: 'Paulo Coelho là một tiểu thuyết gia và nhạc sĩ người Brazil. Ông được biết đến nhiều nhất với cuốn tiểu thuyết Nhà giả kim.',
    bookCount: 30
  },
  {
    id: 3,
    name: 'Robert C. Martin',
    image: 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&q=80&w=300',
    bio: 'Robert Cecil Martin, thường được gọi là "Uncle Bob", là một kỹ sư phần mềm, tác giả và người hướng dẫn người Mỹ.',
    bookCount: 15
  },
  {
    id: 4,
    name: 'Yuval Noah Harari',
    image: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300',
    bio: 'Yuval Noah Harari là một nhà sử học người Israel và là giáo sư tại Khoa Lịch sử của Đại học Hebrew ở Jerusalem.',
    bookCount: 8
  },
  {
    id: 5,
    name: 'J.K. Rowling',
    image: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&q=80&w=300',
    bio: 'Joanne Rowling, bút danh J. K. Rowling, là một nhà văn, nhà từ thiện, nhà sản xuất phim và truyền hình người Anh.',
    bookCount: 25
  },
  {
    id: 6,
    name: 'Higashino Keigo',
    image: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=300',
    bio: 'Higashino Keigo là một nhà văn Nhật Bản được biết đến nhiều nhất với các tiểu thuyết trinh thám.',
    bookCount: 80
  }
]

const filteredAuthors = computed(() => {
  return authors.filter(author => 
    author.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

</script>

<template>
  <div class="authors-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <header class="page-header">
          <h1>Tác giả <span class="text-accent">Nổi tiếng</span></h1>
          <p>Khám phá tri thức qua góc nhìn của những tác giả hàng đầu</p>
          
          <div class="search-bar">
            <i class="fas fa-search"></i>
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Tìm kiếm tác giả bạn yêu thích..." 
            />
          </div>
        </header>

        <div v-if="filteredAuthors.length === 0" class="no-results text-center">
          <i class="fas fa-user-slash"></i>
          <p>Không tìm thấy tác giả nào phù hợp với từ khóa "{{ searchQuery }}"</p>
        </div>

        <div v-else class="author-grid">
          <RouterLink 
            v-for="author in filteredAuthors" 
            :key="author.id" 
            :to="`/author/${author.id}`"
            class="author-card"
          >
            <div class="author-image">
              <img :src="author.image" :alt="author.name" />
            </div>
            <div class="author-info">
              <h3>{{ author.name }}</h3>
              <p class="bio">{{ author.bio }}</p>
              <div class="stats">
                <span class="book-tag">
                  <i class="fas fa-book"></i> {{ author.bookCount }} tác phẩm
                </span>
                <span class="view-btn">Xem chi tiết <i class="fas fa-arrow-right"></i></span>
              </div>
            </div>
          </RouterLink>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/AuthorsListView.css"></style>
