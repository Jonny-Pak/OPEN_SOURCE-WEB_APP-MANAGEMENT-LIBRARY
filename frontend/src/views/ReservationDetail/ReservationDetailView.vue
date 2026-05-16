<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import { phieuMuonService } from '@/services/phieuMuonService'

const router = useRouter()
const route = useRoute()
const isLoading = ref(true)

// Mock reservation data (Initially empty, will be filled from API)
const reservation = ref<any>(null)

const fetchReservation = async () => {
  const id = route.params.id as string
  if (!id) return
  
  isLoading.value = true
  try {
    const data = await phieuMuonService.getById(id)
    // Map backend response to the structure used in template
    reservation.value = {
      id: data.maPhieuMuon,
      userName: data.tenNguoiDung,
      userId: data.maNguoiDung,
      bookingTime: new Date(data.ngayMuon).toLocaleString('vi-VN'),
      expiryTime: new Date(new Date(data.ngayMuon).getTime() + 24 * 60 * 60 * 1000).toLocaleString('vi-VN'),
      status: data.trangThaiPhieu,
      books: data.danhSachChiTiet.map((ct: any) => ({
        id: ct.maSach,
        title: ct.tenSach,
        author: ct.tacGia || 'Đang cập nhật',
        image: ct.anhBiaUrl || 'https://via.placeholder.com/200x300?text=No+Image',
        category: ct.theLoai || 'Chưa phân loại'
      }))
    }
  } catch (error) {
    console.error('Lỗi khi tải thông tin phiếu mượn:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchReservation()
})

const printTicket = () => {
  window.print()
}

</script>

<template>
  <div class="reservation-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container small-container">
        <div v-if="isLoading" class="loading-state text-center" style="padding: 100px 0;">
          <div class="spinner"></div>
          <p>Đang tải thông tin phiếu mượn...</p>
        </div>

        <div v-else-if="!reservation" class="error-state text-center" style="padding: 100px 0;">
          <i class="fas fa-exclamation-circle fa-3x" style="color: var(--danger-color); margin-bottom: 20px;"></i>
          <h2>Không tìm thấy thông tin phiếu mượn</h2>
          <p>Phiếu mượn có thể không tồn tại hoặc bạn không có quyền xem.</p>
          <button @click="router.push('/profile')" class="btn btn-primary" style="margin-top: 20px;">Về trang cá nhân</button>
        </div>

        <div v-else class="reservation-ticket">
          <header class="ticket-header text-center">
            <div class="success-badge">
              <i class="fas fa-check-circle"></i>
              <span>Đặt chỗ thành công</span>
            </div>
            <h1>Chi tiết phiếu đặt sách</h1>
            <p class="ticket-id">Mã phiếu: <strong>{{ reservation.id }}</strong></p>
          </header>

          <div class="ticket-body">
            <!-- User Info Section -->
            <section class="ticket-section">
              <h3 class="section-title"><i class="fas fa-user-tag"></i> Thông tin người đặt</h3>
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">Họ và tên</span>
                  <span class="value">{{ reservation.userName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Mã người dùng</span>
                  <span class="value">{{ reservation.userId }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Thời gian đặt</span>
                  <span class="value">{{ reservation.bookingTime }}</span>
                </div>
                <div class="info-item highlight">
                  <span class="label">Hết hạn nhận sách</span>
                  <span class="value">{{ reservation.expiryTime }}</span>
                </div>
              </div>
            </section>

            <!-- Reserved Books Section -->
            <section class="ticket-section">
              <h3 class="section-title"><i class="fas fa-book"></i> Danh sách sách đặt ({{ reservation.books.length }}/3)</h3>
              <div class="reserved-books-list">
                <div v-for="book in reservation.books" :key="book.id" class="reserved-book-item">
                  <img :src="book.image" :alt="book.title" />
                  <div class="book-details">
                    <h4>{{ book.title }}</h4>
                    <p>{{ book.author }}</p>
                    <span class="category">{{ book.category }}</span>
                  </div>
                </div>
              </div>
            </section>

            <!-- Important Instructions -->
            <section class="instruction-box">
              <div class="instruction-header">
                <i class="fas fa-exclamation-triangle"></i>
                <h3>Hướng dẫn nhận sách</h3>
              </div>
              <ul class="instruction-list">
                <li>Vui lòng mang theo <strong>Thẻ học sinh</strong> đến quầy thủ thư để nhận sách.</li>
                <li>Bạn có <strong>24 tiếng</strong> kể từ thời điểm đặt để đến nhận sách vật lý.</li>
                <li class="warning">Sau {{ reservation.expiryTime }}, nếu bạn không đến nhận, yêu cầu đặt chỗ sẽ <strong>tự động hủy</strong>.</li>
                <li>Bạn có thể xem lại mã phiếu này trong phần <strong>Cá nhân > Lịch sử đặt chỗ</strong>.</li>
              </ul>
            </section>
          </div>

          <footer class="ticket-footer">
            <button @click="printTicket" class="btn btn-outline">
              <i class="fas fa-print"></i> In phiếu
            </button>
            <button @click="router.push('/profile')" class="btn btn-primary">
              Về trang cá nhân
            </button>
          </footer>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/ReservationDetailView.css"></style>
