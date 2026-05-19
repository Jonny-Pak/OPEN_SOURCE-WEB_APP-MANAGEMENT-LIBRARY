<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import { useWishlistStore } from '../../stores/wishlist'

const wishlist = useWishlistStore()
const router = useRouter()

const currentPage = ref(1)
const itemsPerPage = 10

const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return wishlist.items.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(wishlist.itemCount / itemsPerPage)
})

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  scrollToTop()
}

const handlePrevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    scrollToTop()
  }
}

const handleNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    scrollToTop()
  }
}

onMounted(async () => {
  await wishlist.fetchWishlist()
})

</script>

<template>
  <div class="favorites-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <header class="section-header">
          <h1>Sách <span class="text-accent">Yêu thích</span></h1>
          <p>Danh sách những cuốn sách bạn đã lưu để mượn sau</p>
        </header>

        <div v-if="wishlist.itemCount === 0" class="empty-state text-center">
          <div class="empty-icon">
            <font-awesome-icon :icon="['far', 'heart']" />
          </div>
          <h2>Chưa có sách yêu thích</h2>
          <p>Hãy khám phá kho sách và nhấn vào biểu tượng trái tim để lưu lại những cuốn sách bạn thích.</p>
          <button @click="router.push('/books')" class="btn btn-primary">Khám phá ngay</button>
        </div>

        <div v-else>
          <div class="favorites-grid">
            <BookCard 
              v-for="book in paginatedItems" 
              :key="book.id"
              v-bind="book"
              :rating="5" 
            />
          </div>

          <!-- Pagination -->
          <div v-if="totalPages > 1" class="pagination">
            <button class="btn btn-outline" :disabled="currentPage === 1" @click="handlePrevPage">
              <font-awesome-icon icon="fa-solid fa-angle-left" /> Trước
            </button>
            <div class="pages">
              <button 
                v-for="p in totalPages" 
                :key="p" 
                class="page-btn"
                :class="{ active: currentPage === p }"
                @click="handlePageChange(p)"
              >
                {{ p }}
              </button>
            </div>
            <button class="btn btn-outline" :disabled="currentPage === totalPages" @click="handleNextPage">
              Sau <font-awesome-icon icon="fa-solid fa-angle-right" />
            </button>
          </div>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/FavoritesView.css"></style>
