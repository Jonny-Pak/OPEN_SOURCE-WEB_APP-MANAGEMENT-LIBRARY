<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import { tacGiaService } from '../../services/danhMucService'

const searchQuery = ref('')
const authors = ref<any[]>([])
const isLoading = ref(true)
const error = ref<string | null>(null)

const fetchData = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await tacGiaService.danhSach()
    authors.value = (data as any[]).map(author => ({
      id: author.maTacGia,
      name: author.tenTacGia,
      // Create a unique-ish avatar using ui-avatars if no image
      image: `https://ui-avatars.com/api/?name=${encodeURIComponent(author.tenTacGia)}&background=random&size=300`,
      bio: author.tieuSu || 'Thông tin về tác giả đang được cập nhật.',
      bookCount: 0 // Backend currently doesn't provide this in list
    }))
  } catch (err: any) {
    console.error('Failed to fetch authors:', err)
    error.value = 'Không thể tải danh sách tác giả.'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})

const filteredAuthors = computed(() => {
  return authors.value.filter(author => 
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

        <div v-if="isLoading" class="loading-state text-center py-5">
          <div class="spinner"></div>
          <p>Đang tải danh sách tác giả...</p>
        </div>

        <div v-else-if="error" class="error-state text-center py-5">
          <i class="fas fa-exclamation-circle text-danger mb-3" style="font-size: 2rem;"></i>
          <p>{{ error }}</p>
          <button @click="fetchData" class="btn btn-outline">Thử lại</button>
        </div>

        <template v-else>
          <div v-if="filteredAuthors.length === 0" class="no-results text-center py-5">
            <i class="fas fa-user-slash mb-3" style="font-size: 2rem;"></i>
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
        </template>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/AuthorsListView.css"></style>
