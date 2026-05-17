<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import { datChoService } from '@/services/datChoService'
import { useToast } from '@/composables/useToast'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const reservation = ref<any>(null)
const isLoading = ref(true)
const isCancelling = ref(false)

const loadData = async () => {
  isLoading.value = true
  try {
    const id = Number(route.params.id)
    const myReservations = await datChoService.getMy()
    const found = myReservations.find((r: any) => r.maDatCho === id)
    if (found) {
      reservation.value = {
        id: found.maDatCho,
        userName: found.nguoiDung ? `${found.nguoiDung.hoDem} ${found.nguoiDung.ten}` : 'Độc giả',
        userId: found.nguoiDung?.maNguoiDung || 'N/A',
        bookingTime: found.ngayDatCho ? new Date(found.ngayDatCho).toLocaleString('vi-VN') : 'Vừa xong',
        expiryTime: found.ngayHetHan ? new Date(found.ngayHetHan).toLocaleString('vi-VN') : 'Chưa rõ',
        status: found.trangThai,
        books: [
          {
            id: found.sach?.maSach || 0,
            title: found.sach?.tenSach || 'Đầu sách',
            author: 'Đang cập nhật',
            image: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=200',
            category: 'Thư viện'
          }
        ]
      }
    } else {
      // If not in personal reservations, try loading all (for librarian role)
      const allReservations = await datChoService.getAll()
      const foundAll = allReservations.find((r: any) => r.maDatCho === id)
      if (foundAll) {
        reservation.value = {
          id: foundAll.maDatCho,
          userName: foundAll.nguoiDung ? `${foundAll.nguoiDung.hoDem} ${foundAll.nguoiDung.ten}` : 'Độc giả',
          userId: foundAll.nguoiDung?.maNguoiDung || 'N/A',
          bookingTime: foundAll.ngayDatCho ? new Date(foundAll.ngayDatCho).toLocaleString('vi-VN') : 'Vừa xong',
          expiryTime: foundAll.ngayHetHan ? new Date(foundAll.ngayHetHan).toLocaleString('vi-VN') : 'Chưa rõ',
          status: foundAll.trangThai,
          books: [
            {
              id: foundAll.sach?.maSach || 0,
              title: foundAll.sach?.tenSach || 'Đầu sách',
              author: 'Đang cập nhật',
              image: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=200',
              category: 'Thư viện'
            }
          ]
        }
      }
    }
  } catch (err) {
    console.error('Lỗi khi tải chi tiết đặt chỗ:', err)
  } finally {
    isLoading.value = false
  }
}

const cancelBooking = async () => {
  if (!reservation.value) return
  isCancelling.value = true
  try {
    await datChoService.cancel(reservation.value.id)
    toast.success('Hủy đặt chỗ thành công!')
    router.push('/profile')
  } catch (err: any) {
    const errorMsg = typeof err === 'string' ? err : (err?.message || err.response?.data?.message || 'Không thể hủy đặt chỗ lúc này.')
    toast.error(errorMsg)
  } finally {
    isCancelling.value = false
  }
}

const printTicket = () => {
  window.print()
}

</script>

<template>
  <div class="reservation-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container small-container">
        <div v-if="isLoading" class="loading-state text-center" style="padding: 40px;">
          <font-awesome-icon icon="fa-solid fa-spinner" class="fa-spin fa-2x" />
          <p style="margin-top: 10px;">Đang tải chi tiết đặt chỗ...</p>
        </div>
        
        <div v-else-if="!reservation" class="empty-state text-center" style="padding: 40px;">
          <font-awesome-icon icon="fa-solid fa-circle-exclamation" class="fa-2x text-rose-500" />
          <h3>Không tìm thấy thông tin đặt chỗ</h3>
          <button @click="router.push('/profile')" class="btn btn-primary" style="margin-top: 20px;">Về trang cá nhân</button>
        </div>

        <div v-else class="reservation-ticket">
          <header class="ticket-header text-center">
            <div class="success-badge" :class="{ 'bg-rose-500': reservation.status === 'DA_HUY' }">
              <font-awesome-icon :icon="reservation.status === 'DA_HUY' ? 'fa-solid fa-circle-xmark' : 'fa-solid fa-circle-check'" />
              <span>{{ reservation.status === 'DA_HUY' ? 'Đã hủy đặt chỗ' : 'Đặt chỗ thành công' }}</span>
            </div>
            <h1>Chi tiết phiếu đặt sách</h1>
            <p class="ticket-id">Mã phiếu: <strong>#{{ reservation.id }}</strong></p>
          </header>

          <div class="ticket-body">
            <!-- User Info Section -->
            <section class="ticket-section">
              <h3 class="section-title"><font-awesome-icon icon="fa-solid fa-id-card" /> Thông tin người đặt</h3>
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">Họ và tên</span>
                  <span class="value">{{ reservation.userName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Mã người dùng</span>
                  <span class="value" style="font-size: 0.8rem; word-break: break-all;">{{ reservation.userId }}</span>
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
              <h3 class="section-title"><font-awesome-icon icon="fa-solid fa-book" /> Danh sách sách đặt</h3>
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
                <font-awesome-icon icon="fa-solid fa-triangle-exclamation" />
                <h3>Hướng dẫn nhận sách</h3>
              </div>
              <ul class="instruction-list">
                <li>Vui lòng mang theo <strong>Thẻ độc giả</strong> đến quầy thủ thư để nhận sách.</li>
                <li>Bạn có <strong>24 tiếng</strong> kể từ thời điểm đặt để đến nhận sách vật lý.</li>
                <li class="warning">Sau {{ reservation.expiryTime }}, nếu bạn không đến nhận, yêu cầu đặt chỗ sẽ <strong>tự động hủy</strong>.</li>
                <li>Bạn có thể xem lại mã phiếu này trong phần <strong>Cá nhân > Lịch sử đặt chỗ</strong>.</li>
              </ul>
            </section>
          </div>

          <footer class="ticket-footer">
            <button @click="printTicket" class="btn btn-outline">
              <font-awesome-icon icon="fa-solid fa-download" /> Tải về
            </button>
            <button 
              v-if="reservation.status === 'CHO_DUYET' || reservation.status === 'DA_SAN_SANG'" 
              @click="cancelBooking" 
              class="btn btn-secondary"
              :disabled="isCancelling"
            >
              <font-awesome-icon icon="fa-solid fa-xmark" /> {{ isCancelling ? 'Đang hủy...' : 'Hủy đặt chỗ' }}
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
