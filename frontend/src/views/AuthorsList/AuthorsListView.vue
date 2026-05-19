<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import { tacGiaService } from '@/services/danhMucService'

const searchQuery = ref('')
const authors = ref<any[]>([])
const isLoading = ref(true)
const isError = ref(false)

const loadAuthors = async () => {
  isLoading.value = true
  isError.value = false
  try {
    const list = await tacGiaService.danhSach()
    authors.value = list.map((a: any, index: number) => ({
      id: a.maTacGia,
      name: `${a.hoDem} ${a.ten}`.trim(),
      bio: a.tieuSu || 'Dale Carnegie là một nhà văn và diễn giả người Mỹ nổi tiếng, chuyên về kỹ năng tự hoàn thiện.',
      image: [
        'https://images.unsplash.com/photo-1544717297-fa95b3ee21f3?auto=format&fit=crop&q=80&w=300',
        'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&q=80&w=300',
        'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&q=80&w=300',
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300',
        'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&q=80&w=300',
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=300'
      ][index % 6],
      bookCount: 5 + (index * 3) % 20
    }))
  } catch (err) {
    console.error(err)
    isError.value = true
  } finally {
    isLoading.value = false
  }
}

onMounted(loadAuthors)

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
            <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Tìm kiếm tác giả bạn yêu thích..." 
            />
          </div>
        </header>

        <!-- Loading State -->
        <div v-if="isLoading" class="loading-state text-center" style="padding: 40px; color: var(--mau-chu-mo);">
          <font-awesome-icon icon="fa-solid fa-spinner" class="fa-spin fa-2x" />
          <p style="margin-top: 10px;">Đang tải danh sách tác giả...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="isError" class="error-state text-center" style="padding: 40px; color: var(--color-danger);">
          <font-awesome-icon icon="fa-solid fa-circle-exclamation" class="fa-2x" />
          <p style="margin-top: 10px;">Không thể tải dữ liệu tác giả. Vui lòng thử lại.</p>
          <button @click="loadAuthors" class="btn btn-outline" style="margin-top: 10px;">Thử lại</button>
        </div>

        <!-- Empty State -->
        <div v-else-if="filteredAuthors.length === 0" class="no-results text-center">
          <font-awesome-icon icon="fa-solid fa-user-slash" />
          <p>Không tìm thấy tác giả nào phù hợp với từ khóa "{{ searchQuery }}"</p>
        </div>

        <!-- Grid Results -->
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
                  <font-awesome-icon icon="fa-solid fa-book" /> {{ author.bookCount }} tác phẩm
                </span>
                <span class="view-btn">Xem chi tiết <font-awesome-icon icon="fa-solid fa-arrow-right" /></span>
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
