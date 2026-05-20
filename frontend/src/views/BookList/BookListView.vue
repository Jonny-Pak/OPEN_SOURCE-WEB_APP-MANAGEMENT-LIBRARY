<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import { sachService } from '@/services/sachService'
import { danhMucService } from '@/services/danhMucService'
import { useSearch } from '@/composables/useSearch'
import { usePagination } from '@/composables/usePagination'

const route = useRoute()
const router = useRouter()

// 1. Search composable
const { tuKhoaTimKiem: searchQuery, tuKhoaDebounced } = useSearch()

// 2. Pagination composable
const { currentPage, totalPages, capNhatTong } = usePagination(9) // 9 books per page

// 3. Category state synced with URL
const activeCategoryId = ref<number | null>(
  route.query.category ? Number(route.query.category) : null
)

const rawCategories = ref<any[]>([])
const books = ref<any[]>([])
const isLoading = ref(true)
const isError = ref(false)

// Sync category to URL and reset page
watch(activeCategoryId, (newCatId) => {
  if (router && route) {
    router.replace({
      query: {
        ...route.query,
        category: newCatId || undefined,
        page: 1
      }
    })
  }
  currentPage.value = 1
})

// Sync page change to URL
watch(currentPage, (newPage) => {
  if (router && route) {
    router.replace({
      query: {
        ...route.query,
        page: newPage || undefined
      }
    })
  }
})

const categoriesWithCount = computed(() => {
  return [
    { id: null, name: 'Tất cả' },
    ...rawCategories.value.map(c => ({
      id: c.maTheLoai || c.maDanhMuc,
      name: c.tenTheLoai || c.tenDanhMuc || c.ten
    }))
  ]
})

const loadCategories = async () => {
  try {
    const fetched = await danhMucService.getAll()
    rawCategories.value = fetched || []
  } catch (err) {
    console.error('Lỗi khi tải danh mục:', err)
  }
}

const loadBooks = async () => {
  isLoading.value = true
  isError.value = false
  try {
    const fetched = await sachService.advancedSearch({
      page: currentPage.value - 1, // Backend is 0-indexed
      size: 9,
      keyword: tuKhoaDebounced.value || undefined,
      maTheLoai: activeCategoryId.value || undefined
    })

    if (fetched && fetched.content) {
      books.value = fetched.content.map((b: any) => ({
        id: b.maSach,
        title: b.tenSach,
        author: b.danhSachTacGia?.map((t: any) => `${t.hoDem} ${t.ten}`).join(', ') || 'Đang cập nhật',
        category: b.danhSachTheLoai?.[0]?.tenTheLoai || 'Chưa phân loại',
        allCategories: b.danhSachTheLoai?.map((t: any) => t.tenTheLoai) || [],
        image: b.danhSachHinhAnh?.[0]?.duongDan || 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400',
        rating: 5,
        stockStatus: b.soLuongKho > 0 ? 'ConSach' : 'HetSach'
      }))
      capNhatTong(fetched.totalElements || 0)
    } else {
      books.value = []
      capNhatTong(0)
    }
  } catch (err) {
    console.error('Lỗi khi tải đầu sách:', err)
    isError.value = true
  } finally {
    isLoading.value = false
  }
}

// Watch for search or page or category change to refetch
watch([tuKhoaDebounced, activeCategoryId, currentPage], () => {
  loadBooks()
})

onMounted(() => {
  loadCategories()
  loadBooks()
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
                v-for="cat in categoriesWithCount" 
                :key="cat.id || 'all'"
                :class="{ active: activeCategoryId === cat.id }"
                @click="activeCategoryId = cat.id"
              >
                <span>{{ cat.name }}</span>
              </li>
            </ul>
          </div>
        </aside>
        
        <!-- Main Content -->
        <section class="main-list">
          <div class="list-header">
            <div class="search-bar">
              <font-awesome-icon icon="fa-solid fa-magnifying-glass" class="search-icon" />
              <input 
                v-model="searchQuery" 
                type="text" 
                placeholder="Tìm kiếm sách hoặc tác giả..." 
              />
            </div>
          </div>
          
          <div class="results-info">
            <p>Tìm thấy <span>{{ books.length }}</span> kết quả</p>
          </div>
          
          <div v-if="isLoading" class="loading-state" style="padding: 40px; text-align: center; color: var(--mau-chu-mo);">
            <font-awesome-icon icon="fa-solid fa-spinner" class="fa-spin fa-2x" />
            <p style="margin-top: 10px;">Đang tải dữ liệu...</p>
          </div>
          
          <div v-else class="grid grid-cols-3">
            <BookCard 
              v-for="book in books" 
              :key="book.id"
              v-bind="book"
            />
          </div>
          
          <div v-if="!isLoading && books.length === 0" class="no-results">
            <div class="no-results-icon"><font-awesome-icon icon="fa-solid fa-magnifying-glass" /></div>
            <h3>Không tìm thấy sách nào</h3>
            <p>Hãy thử thay đổi từ khóa hoặc bộ lọc của bạn</p>
            <button @click="activeCategoryId = null; searchQuery = ''" class="btn btn-outline">Xóa bộ lọc</button>
          </div>
          
          <!-- Dynamic Pagination -->
          <div v-if="totalPages > 1" class="pagination">
            <button class="btn btn-outline" :disabled="currentPage === 1" @click="currentPage--">
              <font-awesome-icon icon="fa-solid fa-angle-left" /> Trước
            </button>
            <div class="pages">
              <button 
                v-for="p in totalPages" 
                :key="p" 
                class="page-btn"
                :class="{ active: currentPage === p }"
                @click="currentPage = p"
              >
                {{ p }}
              </button>
            </div>
            <button class="btn btn-outline" :disabled="currentPage === totalPages" @click="currentPage++">
              Sau <font-awesome-icon icon="fa-solid fa-angle-right" />
            </button>
          </div>
        </section>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/BookListView.css"></style>
