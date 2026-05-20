<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import { nhaXuatBanService } from '@/services/danhMucService'
import { sachService } from '@/services/sachService'

const searchQuery = ref('')
const publishers = ref<any[]>([])
const isLoading = ref(true)
const isError = ref(false)

const loadPublishers = async () => {
  isLoading.value = true
  isError.value = false
  try {
    const list = await nhaXuatBanService.danhSach()
    const fetchedBooks = await sachService.getAll({ size: 1000 })
    const allBooks = fetchedBooks?.content || []

    publishers.value = list.map((p: any) => {
      const pubBooks = allBooks.filter((b: any) => b.nhaXuatBan?.maNhaXuatBan === p.maNhaXuatBan)
      return {
        id: p.maNhaXuatBan,
        name: p.tenNhaXuatBan,
        description: p.diaChi ? `Địa chỉ: ${p.diaChi}. Email: ${p.email || 'đang cập nhật'}.` : 'Nhà xuất bản uy tín cung cấp nguồn tri thức chất lượng cao.',
        bookCount: pubBooks.length
      }
    })
  } catch (err) {
    console.error(err)
    isError.value = true
  } finally {
    isLoading.value = false
  }
}

onMounted(loadPublishers)

const filteredPublishers = computed(() => {
  return publishers.value.filter(p => 
    p.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

</script>

<template>
  <div class="publishers-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <header class="page-header text-center">
          <h1>Nhà <span class="text-accent">Xuất bản</span></h1>
          <p>Các đối tác chiến lược cung cấp nguồn tri thức dồi dào cho thư viện</p>
          
          <div class="search-bar">
            <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Tìm kiếm nhà xuất bản..." 
            />
          </div>
        </header>

        <!-- Loading State -->
        <div v-if="isLoading" class="loading-state text-center" style="padding: 40px; color: var(--mau-chu-mo);">
          <font-awesome-icon icon="fa-solid fa-spinner" class="fa-spin fa-2x" />
          <p style="margin-top: 10px;">Đang tải danh sách nhà xuất bản...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="isError" class="error-state text-center" style="padding: 40px; color: var(--color-danger);">
          <font-awesome-icon icon="fa-solid fa-circle-exclamation" class="fa-2x" />
          <p style="margin-top: 10px;">Không thể tải danh sách nhà xuất bản. Vui lòng thử lại.</p>
          <button @click="loadPublishers" class="btn btn-outline" style="margin-top: 10px;">Thử lại</button>
        </div>

        <!-- Empty State -->
        <div v-else-if="filteredPublishers.length === 0" class="no-results text-center">
          <font-awesome-icon icon="fa-solid fa-building-columns" />
          <p>Không tìm thấy nhà xuất bản nào phù hợp</p>
        </div>

        <!-- Grid Results -->
        <div v-else class="publisher-grid">
          <RouterLink 
            v-for="pub in filteredPublishers" 
            :key="pub.id" 
            :to="`/publisher/${pub.id}`"
            class="publisher-card"
          >
            <div class="pub-info">
              <h3>{{ pub.name }}</h3>
              <p>{{ pub.description }}</p>
              <div class="pub-footer">
                <span class="count-badge">
                  <font-awesome-icon icon="fa-solid fa-book-bookmark" /> {{ pub.bookCount }}+ đầu sách
                </span>
                <span class="link-text">Xem chi tiết <font-awesome-icon icon="fa-solid fa-angle-right" /></span>
              </div>
            </div>
          </RouterLink>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/PublishersListView.css"></style>
