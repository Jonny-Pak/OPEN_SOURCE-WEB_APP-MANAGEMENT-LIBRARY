<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import { nhaXuatBanService } from '@/services/danhMucService'
import { sachService } from '@/services/sachService'

const route = useRoute()
const pubId = Number(route.params.id)

const publisher = ref<any>(null)
const books = ref<any[]>([])
const isLoading = ref(true)
const isError = ref(false)

const loadData = async () => {
  isLoading.value = true
  isError.value = false
  try {
    const list = await nhaXuatBanService.danhSach()
    const found = list.find((p: any) => p.maNhaXuatBan === pubId)
    if (!found) {
      isError.value = true
      return
    }

    publisher.value = {
      id: found.maNhaXuatBan,
      name: found.tenNhaXuatBan,
      description: `Nhà xuất bản ${found.tenNhaXuatBan} là một đối tác xuất bản chiến lược quan trọng của thư viện, đem lại nhiều tác phẩm giá trị cao cho độc giả.`,
      address: found.diaChi || '161B Lý Chính Thắng, Quận 3, TP. Hồ Chí Minh',
      website: 'www.publibmanage.vn',
      email: found.email || 'contact@publibmanage.vn',
      founded: '1985'
    }

    // Fetch related books written by this author
    const fetchedBooks = await sachService.getAll({ size: 100 })
    if (fetchedBooks && fetchedBooks.content) {
      books.value = fetchedBooks.content
        .filter((b: any) => b.nhaXuatBan?.maNhaXuatBan === pubId)
        .map((b: any) => ({
          id: b.maSach,
          title: b.tenSach,
          author: b.danhSachTacGia?.map((t: any) => `${t.hoDem} ${t.ten}`).join(', ') || 'Đang cập nhật',
          category: b.danhSachTheLoai?.[0]?.tenTheLoai || 'Chưa phân loại',
          image: b.danhSachHinhAnh?.[0]?.duongDan || 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400',
          rating: 5
        }))
    }
  } catch (err) {
    console.error(err)
    isError.value = true
  } finally {
    isLoading.value = false
  }
}

onMounted(loadData)

</script>

<template>
  <div class="pub-detail-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <!-- Loading State -->
        <div v-if="isLoading" class="loading-state text-center" style="padding: 60px; color: var(--mau-chu-mo);">
          <font-awesome-icon icon="fa-solid fa-spinner" class="fa-spin fa-3x" />
          <p style="margin-top: 15px;">Đang tải chi tiết nhà xuất bản...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="isError || !publisher" class="error-state text-center" style="padding: 60px; color: var(--color-danger);">
          <font-awesome-icon icon="fa-solid fa-circle-exclamation" class="fa-3x" />
          <p style="margin-top: 15px;">Không tìm thấy nhà xuất bản hoặc xảy ra lỗi.</p>
          <RouterLink to="/publishers" class="btn btn-primary" style="margin-top: 15px;">Quay lại danh sách</RouterLink>
        </div>

        <template v-else>
          <section class="pub-profile">
            <div class="pub-content" style="width: 100%;">
              <nav class="breadcrumb">
                <RouterLink to="/publishers">Nhà xuất bản</RouterLink>
                <span class="separator">/</span>
                <span class="current">{{ publisher.name }}</span>
              </nav>
              <h1>{{ publisher.name }}</h1>
              
              <!-- Quick Info Row -->
              <div class="pub-quick-info-row" style="display: flex; gap: 2rem; margin-bottom: 2rem; background: #f8fafc; padding: 1rem 1.5rem; border-radius: 12px; width: fit-content; flex-wrap: wrap;">
                <div class="info-row" style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.9rem; color: var(--secondary);">
                  <font-awesome-icon icon="fa-solid fa-calendar-days" style="color: var(--accent);" />
                  <span>Thành lập: <b>{{ publisher.founded }}</b></span>
                </div>
                <div class="info-row" style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.9rem; color: var(--secondary);">
                  <font-awesome-icon icon="fa-solid fa-globe" style="color: var(--accent);" />
                  <a :href="'https://' + publisher.website" target="_blank" style="color: var(--accent); font-weight: 600; text-decoration: none;">{{ publisher.website }}</a>
                </div>
              </div>

              <div class="pub-description">
                <h3>Về chúng tôi</h3>
                <p>{{ publisher.description }}</p>
              </div>

              <div class="pub-contact-grid">
                <div class="contact-item">
                  <font-awesome-icon icon="fa-solid fa-location-dot" style="color: var(--accent); font-size: 1.25rem; margin-top: 0.25rem;" />
                  <div>
                    <label>Địa chỉ</label>
                    <p>{{ publisher.address }}</p>
                  </div>
                </div>
                <div class="contact-item">
                  <font-awesome-icon icon="fa-solid fa-envelope" style="color: var(--accent); font-size: 1.25rem; margin-top: 0.25rem;" />
                  <div>
                    <label>Email</label>
                    <p>{{ publisher.email }}</p>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section class="pub-books">
            <div class="section-header">
              <h2>Sách từ {{ publisher.name }}</h2>
              <p>Khám phá kho tri thức được phát hành bởi {{ publisher.name }}</p>
            </div>

            <!-- Empty State for Books -->
            <div v-if="books.length === 0" class="empty-state text-center" style="padding: 40px; color: var(--mau-chu-mo);">
              <font-awesome-icon icon="fa-solid fa-book-open" class="fa-2x" />
              <p style="margin-top: 10px;">Chưa có sách nào của nhà xuất bản này trong thư viện.</p>
            </div>

            <div v-else class="grid grid-cols-4">
              <BookCard 
                v-for="book in books" 
                :key="book.id"
                v-bind="book"
                :rating="5"
              />
            </div>
          </section>
        </template>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/PublisherDetailView.css"></style>
