<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import { tacGiaService } from '@/services/danhMucService'
import { sachService } from '@/services/sachService'

const route = useRoute()
const authorId = Number(route.params.id)

const author = ref<any>(null)
const books = ref<any[]>([])
const isLoading = ref(true)
const isError = ref(false)

const loadData = async () => {
  isLoading.value = true
  isError.value = false
  try {
    const list = await tacGiaService.danhSach()
    const found = list.find((a: any) => a.maTacGia === authorId)
    if (!found) {
      isError.value = true
      return
    }

    author.value = {
      id: found.maTacGia,
      name: `${found.hoDem} ${found.ten}`.trim(),
      bio: found.tieuSu || 'Chưa có thông tin tiểu sử của tác giả này.',
      image: (found as any).hinhAnh || '',
      nationality: (found as any).quocTich || 'Việt Nam',
      born: (found as any).ngaySinh ? new Date((found as any).ngaySinh).getFullYear() : '1970',
      died: ''
    }

    // Fetch related books written by this author
    const fetchedBooks = await sachService.getAll({ size: 100 })
    if (fetchedBooks && fetchedBooks.content) {
      books.value = fetchedBooks.content
        .filter((b: any) => b.danhSachTacGia?.some((t: any) => t.maTacGia === authorId))
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
  <div class="author-detail-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <!-- Loading State -->
        <div v-if="isLoading" class="loading-state text-center" style="padding: 60px; color: var(--mau-chu-mo);">
          <font-awesome-icon icon="fa-solid fa-spinner" class="fa-spin fa-3x" />
          <p style="margin-top: 15px;">Đang tải chi tiết tác giả...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="isError || !author" class="error-state text-center" style="padding: 60px; color: var(--color-danger);">
          <font-awesome-icon icon="fa-solid fa-circle-exclamation" class="fa-3x" />
          <p style="margin-top: 15px;">Không tìm thấy tác giả hoặc xảy ra lỗi.</p>
          <RouterLink to="/authors" class="btn btn-primary" style="margin-top: 15px;">Quay lại danh sách</RouterLink>
        </div>

        <template v-else>
          <!-- Author Profile Header -->
          <section class="author-profile">
            <div class="author-visual">
              <img v-if="author.image" :src="author.image" :alt="author.name" />
              <div v-else class="author-avatar-placeholder-big">
                <font-awesome-icon icon="fa-solid fa-user-nib" />
              </div>
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
                  <span class="label">Năm sinh</span>
                  <span class="value">{{ author.born }}</span>
                </div>
                <div v-if="author.died" class="meta-item">
                  <span class="label">Năm mất</span>
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

            <!-- Empty State for Books -->
            <div v-if="books.length === 0" class="empty-state text-center" style="padding: 40px; color: var(--mau-chu-mo);">
              <font-awesome-icon icon="fa-solid fa-book-open" class="fa-2x" />
              <p style="margin-top: 10px;">Chưa có tác phẩm nào của tác giả này trong thư viện.</p>
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

<style scoped src="../../assets/css/views/AuthorDetailView.css"></style>
