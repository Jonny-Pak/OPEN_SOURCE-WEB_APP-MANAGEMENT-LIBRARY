<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import { useCartStore } from '../../stores/cart'
import { useWishlistStore } from '../../stores/wishlist'
import { useAuthStore } from '../../stores/auth'
import { useToast } from '../../composables/useToast'
import { sachService } from '@/services/sachService'
import { datChoService } from '@/services/datChoService'

const route = useRoute()
const router = useRouter()
const cart = useCartStore()
const wishlist = useWishlistStore()
const authStore = useAuthStore()
const toast = useToast()

const book = ref<any>(null)
const similarBooks = ref<any[]>([])
const isLoading = ref(true)
const isReserving = ref(false)

const inWishlist = computed(() => {
  if (!book.value) return false
  return wishlist.isInWishlist(book.value.id)
})

const addToCart = () => {
  if (!authStore.daXacThuc) {
    toast.error('Vui lòng đăng nhập để mượn sách!')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  if (!book.value) return
  const success = cart.addBook({
    id: book.value.id,
    title: book.value.title,
    author: book.value.author,
    image: book.value.image,
    category: book.value.category
  })
  if (success) {
    toast.success('Đã thêm sách vào giỏ mượn thành công!')
  }
}

const toggleFavorite = () => {
  if (!authStore.daXacThuc) {
    toast.error('Vui lòng đăng nhập để thêm vào danh sách yêu thích!')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  if (!book.value) return
  wishlist.toggleWishlist({
    id: book.value.id,
    title: book.value.title,
    author: book.value.author,
    image: book.value.image,
    category: book.value.category
  })
  if (inWishlist.value) {
    toast.success('Đã thêm vào danh sách yêu thích!')
  } else {
    toast.info('Đã xóa khỏi danh sách yêu thích!')
  }
}

const reserveBook = async () => {
  if (!authStore.daXacThuc) {
    toast.error('Vui lòng đăng nhập để đặt chỗ sách!')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  if (!book.value) return
  isReserving.value = true
  try {
    await datChoService.reserve(book.value.id)
    toast.success('Đặt chỗ sách thành công! Thư viện sẽ giữ cuốn này cho bạn.')
  } catch (error: any) {
    const errorMsg = typeof error === 'string' ? error : (error?.message || error.response?.data?.message || 'Không thể đặt chỗ sách này lúc này.')
    toast.error(errorMsg)
  } finally {
    isReserving.value = false
  }
}

const loadData = async (id: number) => {
  isLoading.value = true
  try {
    const fetchedBook = await sachService.layMotCuon(id)
    book.value = {
      id: fetchedBook.maSach,
      title: fetchedBook.tenSach,
      author: fetchedBook.danhSachTacGia?.map((t: any) => `${t.hoDem} ${t.ten}`).join(', ') || 'Đang cập nhật',
      danhSachTacGia: fetchedBook.danhSachTacGia,
      danhSachTheLoai: fetchedBook.danhSachTheLoai,
      category: fetchedBook.danhSachTheLoai?.[0]?.tenTheLoai || 'Chưa phân loại',
      image: fetchedBook.danhSachHinhAnh?.[0]?.duongDan || 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=600',
      images: fetchedBook.danhSachHinhAnh || [],
      rating: 5,
      description: fetchedBook.moTa || 'Chưa có thông tin mô tả.',
      status: (fetchedBook.soLuongKho || fetchedBook.soLuongCoSan || 0) > 0 ? 'Available' : 'Unavailable',
      publisher: fetchedBook.nhaXuatBan?.tenNhaXuatBan || 'Chưa rõ',
      publishedYear: fetchedBook.namXuatBan || 'Chưa rõ',
      maIsbn: fetchedBook.maIsbn || 'Chưa cập nhật',
      pages: fetchedBook.soTrang || 0,
      language: 'Tiếng Việt',
      soLuongKho: fetchedBook.soLuongCoSan !== undefined ? fetchedBook.soLuongCoSan : 0,
      tongSoLuong: fetchedBook.tongSoLuong !== undefined ? fetchedBook.tongSoLuong : 0
    }

    const fetchedBooks = await sachService.getAll({ size: 20 })
    if (fetchedBooks && fetchedBooks.content) {
      similarBooks.value = fetchedBooks.content
        .filter((b: any) => b.maSach !== id)
        .slice(0, 4)
        .map((b: any) => ({
          id: b.maSach,
          title: b.tenSach,
          author: b.danhSachTacGia?.map((t: any) => `${t.hoDem} ${t.ten}`).join(', ') || 'Đang cập nhật',
          category: b.danhSachTheLoai?.[0]?.tenTheLoai || 'Chưa phân loại',
          image: b.danhSachHinhAnh?.[0]?.duongDan || 'https://images.unsplash.com/photo-1541963463532-d68292c34b19?auto=format&fit=crop&q=80&w=400',
          rating: 4
        }))
    }
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu chi tiết sách:', error)
  } finally {
    isLoading.value = false
  }
}

watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      window.scrollTo(0, 0)
      loadData(Number(newId))
    }
  }
)

onMounted(() => {
  window.scrollTo(0, 0)
  loadData(Number(route.params.id))
})

</script>

<template>
  <div class="book-detail-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <nav class="breadcrumb">
          <RouterLink to="/">Trang chủ</RouterLink>
          <span class="separator">/</span>
          <span class="current">{{ book?.title || 'Đang tải...' }}</span>
        </nav>
        <div v-if="isLoading" class="loading-state" style="padding: 40px; text-align: center; color: var(--mau-chu-mo);">
          <font-awesome-icon icon="fa-solid fa-spinner" class="fa-spin fa-2x" />
          <p style="margin-top: 10px;">Đang tải chi tiết sách...</p>
        </div>
        
        <div v-else-if="book" class="book-detail-grid">
          <!-- Left Column: Image -->
          <div class="book-visual">
            <div class="image-card">
              <img :src="book.image" :alt="book.title" />
            </div>
            
            <div class="thumbnails" v-if="book.images && book.images.length > 0">
              <div 
                class="thumbnail" 
                v-for="img in book.images" 
                :key="img.maHinhAnh"
                @click="book.image = img.duongDan"
                :class="{ active: book.image === img.duongDan }"
              >
                <img :src="img.duongDan" :alt="img.loaiHinhAnh" />
                <span class="img-type-badge">{{ img.loaiHinhAnh === 'BIA_TRUOC' ? 'Bìa trước' : img.loaiHinhAnh === 'BIA_SAU' ? 'Bìa sau' : img.loaiHinhAnh === 'MUC_LUC' ? 'Mục lục' : 'Khác' }}</span>
              </div>
            </div>
            
            <div class="visual-actions">
              <button 
                v-if="book.soLuongKho > 0" 
                @click="addToCart" 
                class="btn btn-primary btn-block"
              >
                <font-awesome-icon icon="fa-solid fa-book-bookmark" /> Thêm vào giỏ sách
              </button>
              <button 
                v-else 
                @click="reserveBook" 
                class="btn btn-secondary btn-block"
                :disabled="isReserving || book.tongSoLuong === 0"
              >
                <font-awesome-icon icon="fa-solid fa-calendar-check" /> {{ isReserving ? 'Đang đặt chỗ...' : (book.tongSoLuong === 0 ? 'Hết sách (Chưa có bản sao)' : 'Đặt chỗ sách (Hết sách)') }}
              </button>
              
              <button 
                @click="toggleFavorite" 
                class="btn btn-outline btn-block"
                style="margin-top: 10px;"
              >
                <font-awesome-icon :icon="inWishlist ? ['fas', 'heart'] : ['far', 'heart']" :class="inWishlist ? 'text-rose-500' : ''" />
                {{ inWishlist ? ' Đã yêu thích' : ' Thêm yêu thích' }}
              </button>
            </div>
          </div>
          
          <!-- Right Column: Info -->
          <div class="book-info-main">
            <div class="header-info">
              <span class="category-badge" v-for="t in book.danhSachTheLoai" :key="t.maTheLoai">{{ t.tenTheLoai }}</span>
              <div class="status-badge" :class="book.status.toLowerCase()">
                {{ book.soLuongKho > 0 ? `● Còn sách (${book.soLuongKho} bản)` : '○ Đã hết sách' }}
              </div>
            </div>
            
            <h1 class="book-title">{{ book.title }}</h1>
            <p class="book-author">Tác giả: <span>{{ book.author }}</span></p>

            <div class="rating-stars" style="margin-bottom: 1.5rem; display: flex; align-items: center; gap: 0.5rem;">
              <div class="stars" style="display: flex; gap: 0.25rem;">
                <font-awesome-icon v-for="i in 5" :key="i" icon="fa-solid fa-star" style="color: #f59e0b;" />
              </div>
              <span style="font-size: 0.9rem; font-weight: 600; color: var(--text-muted);">5.0 (Độc giả đánh giá)</span>
            </div>
            
            <div class="description">
              <h3>Tóm tắt nội dung</h3>
              <p>{{ book.description }}</p>
            </div>
            
            <div class="specs-grid">
              <div class="spec-item">
                <span class="spec-icon"><font-awesome-icon icon="fa-solid fa-building-columns" /></span>
                <div class="spec-detail">
                  <span class="label">Nhà xuất bản</span>
                  <span class="value">{{ book.publisher }}</span>
                </div>
              </div>
              <div class="spec-item">
                <span class="spec-icon"><font-awesome-icon icon="fa-solid fa-calendar-days" /></span>
                <div class="spec-detail">
                  <span class="label">Năm xuất bản</span>
                  <span class="value">{{ book.publishedYear }}</span>
                </div>
              </div>
              <div class="spec-item">
                <span class="spec-icon"><font-awesome-icon icon="fa-solid fa-barcode" /></span>
                <div class="spec-detail">
                  <span class="label">Mã ISBN</span>
                  <span class="value">{{ book.maIsbn }}</span>
                </div>
              </div>
              <div class="spec-item">
                <span class="spec-icon"><font-awesome-icon icon="fa-solid fa-file-invoice" /></span>
                <div class="spec-detail">
                  <span class="label">Số trang</span>
                  <span class="value">{{ book.pages }} trang</span>
                </div>
              </div>
              <div class="spec-item">
                <span class="spec-icon"><font-awesome-icon icon="fa-solid fa-language" /></span>
                <div class="spec-detail">
                  <span class="label">Ngôn ngữ</span>
                  <span class="value">{{ book.language }}</span>
                </div>
              </div>
              <div class="spec-item">
                <span class="spec-icon"><font-awesome-icon icon="fa-solid fa-cubes" /></span>
                <div class="spec-detail">
                  <span class="label">Số bản có sẵn</span>
                  <span class="value">{{ book.soLuongKho }} / {{ book.tongSoLuong }} bản</span>
                </div>
              </div>
            </div>

            <!-- Premium Borrowing Policy Alert -->
            <div class="borrow-policy-alert" style="margin-top: 1.5rem; padding: 1.25rem; background: rgba(6, 182, 212, 0.05); border-left: 4px solid var(--accent); border-radius: 8px;">
              <h4 style="margin: 0 0 0.5rem 0; font-size: 0.95rem; font-weight: 700; color: var(--primary); display: flex; align-items: center; gap: 0.5rem;">
                <font-awesome-icon icon="fa-solid fa-circle-info" style="color: var(--accent);" />
                <span>Quy định mượn & nhận sách</span>
              </h4>
              <p style="margin: 0; font-size: 0.85rem; line-height: 1.5; color: var(--secondary);">
                Quý độc giả vui lòng đến quầy thủ thư để nhận sách vật lý trong vòng <b>24 giờ</b> kể từ khi đăng ký mượn trực tuyến hoặc đặt chỗ thành công.
              </p>
            </div>
          </div>
        </div>
        
        <!-- Related Books -->
        <section class="related-section">
          <div class="section-header">
            <h2>Sách cùng thể loại</h2>
            <RouterLink to="/books" class="view-all">Xem tất cả →</RouterLink>
          </div>
          <div class="grid grid-cols-4">
            <BookCard 
              v-for="item in similarBooks" 
              :key="item.id"
              v-bind="item"
            />
          </div>
        </section>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/BookDetailView.css"></style>
