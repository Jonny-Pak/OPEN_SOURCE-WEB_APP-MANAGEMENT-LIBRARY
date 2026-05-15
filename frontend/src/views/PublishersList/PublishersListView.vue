<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import { nhaXuatBanService } from '../../services/danhMucService'

const searchQuery = ref('')
const publishers = ref<any[]>([])
const isLoading = ref(true)
const error = ref<string | null>(null)

const fetchData = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await nhaXuatBanService.danhSach()
    publishers.value = (data as any[]).map(pub => ({
      id: pub.maNXB,
      name: pub.tenNXB,
      // Create a unique-ish logo using ui-avatars if no image
      logo: `https://ui-avatars.com/api/?name=${encodeURIComponent(pub.tenNXB)}&background=random&size=200&bold=true`,
      description: pub.diaChi ? `Địa chỉ: ${pub.diaChi}. ${pub.website ? 'Website: ' + pub.website : ''}` : 'Thông tin về nhà xuất bản đang được cập nhật.',
      bookCount: 0 // Backend currently doesn't provide this in list
    }))
  } catch (err: any) {
    console.error('Failed to fetch publishers:', err)
    error.value = 'Không thể tải danh sách nhà xuất bản.'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})

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
            <i class="fas fa-search"></i>
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Tìm kiếm nhà xuất bản..." 
            />
          </div>
        </header>

        <div v-if="isLoading" class="loading-state text-center py-5">
          <div class="spinner"></div>
          <p>Đang tải danh sách nhà xuất bản...</p>
        </div>

        <div v-else-if="error" class="error-state text-center py-5">
          <i class="fas fa-exclamation-circle text-danger mb-3" style="font-size: 2rem;"></i>
          <p>{{ error }}</p>
          <button @click="fetchData" class="btn btn-outline">Thử lại</button>
        </div>

        <template v-else>
          <div v-if="filteredPublishers.length === 0" class="no-results text-center py-5">
            <i class="fas fa-building-circle-exclamation mb-3" style="font-size: 2rem;"></i>
            <p>Không tìm thấy nhà xuất bản nào phù hợp</p>
          </div>

          <div v-else class="publisher-grid">
            <RouterLink 
              v-for="pub in filteredPublishers" 
              :key="pub.id" 
              :to="`/publisher/${pub.id}`"
              class="publisher-card"
            >
              <div class="pub-logo">
                <img :src="pub.logo" :alt="pub.name" />
              </div>
              <div class="pub-info">
                <h3>{{ pub.name }}</h3>
                <p>{{ pub.description }}</p>
                <div class="pub-footer">
                  <span class="count-badge">
                    <i class="fas fa-book-bookmark"></i> {{ pub.bookCount }}+ đầu sách
                  </span>
                  <span class="link-text">Xem chi tiết <i class="fas fa-chevron-right"></i></span>
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

<style scoped src="../../assets/css/views/PublishersListView.css"></style>
