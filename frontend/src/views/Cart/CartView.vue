<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import { useCartStore } from '../../stores/cart'
import { useToast } from '../../composables/useToast'

const cart = useCartStore()
const router = useRouter()
const toast = useToast()

const isSubmitting = ref(false)
const isSuccess = ref(false)

const handleConfirm = async () => {
  if (cart.items.length === 0) return
  
  isSubmitting.value = true
  try {
    await cart.borrowAll()
    isSuccess.value = true
    toast.success('Yêu cầu mượn sách thành công!')
  } catch (error: any) {
    const errorMsg = typeof error === 'string' ? error : (error?.message || 'Có lỗi xảy ra khi thực hiện mượn sách. Vui lòng thử lại.')
    toast.error(errorMsg)
    console.error(error)
  } finally {
    isSubmitting.value = false
  }
}

const dueDate = new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toLocaleDateString('vi-VN')

const removeItem = (id: number) => {
  cart.removeItem(id)
}

</script>

<template>
  <div class="cart-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container small-container">
        <!-- Success State -->
        <div v-if="isSuccess" class="success-card text-center">
          <div class="success-icon">
            <font-awesome-icon icon="fa-solid fa-circle-check" />
          </div>
          <h1>Yêu cầu mượn thành công!</h1>
          <p>Yêu cầu mượn các cuốn sách đã chọn đã được gửi đến hệ thống. Vui lòng đến thư viện trong vòng 24h để nhận sách.</p>
          <div class="success-actions">
            <button @click="router.push('/profile')" class="btn btn-primary">Xem trạng thái mượn</button>
            <button @click="router.push('/')" class="btn btn-outline">Về trang chủ</button>
          </div>
        </div>

        <!-- Cart Content -->
        <div v-else class="cart-container">
          <header class="cart-header">
            <h1>Giỏ sách </h1>
            <h2> Đang có <span>({{ cart.itemCount }}/3)</span> cuốn sách trong giỏ </h2>
            <p>Bạn có thể mượn tối đa 3 cuốn sách cùng lúc.</p>
          </header>

          <div v-if="cart.items.length === 0" class="empty-cart text-center">
            <div class="empty-icon">
              <font-awesome-icon icon="fa-solid fa-book-bookmark" />
            </div>
            <h3>Danh sách mượn đang trống</h3>
            <p>Hãy khám phá kho sách và thêm những cuốn bạn yêu thích vào đây.</p>
            <button @click="router.push('/books')" class="btn btn-primary">Khám phá sách ngay</button>
          </div>

          <div v-else class="cart-content">
            <div class="cart-items-list">
              <div v-for="book in cart.items" :key="book.id" class="cart-item">
                <img :src="book.image" :alt="book.title" />
                <div class="item-info">
                  <h3>{{ book.title }}</h3>
                  <p>{{ book.author }}</p>
                  <span class="badge">{{ book.category }}</span>
                </div>
                <button @click="removeItem(book.id)" class="remove-btn" title="Xóa khỏi danh sách">
                  <font-awesome-icon icon="fa-solid fa-trash-can" />
                </button>
              </div>
            </div>

            <div class="cart-summary">
              <div class="summary-section">
                <h3>Chi tiết mượn</h3>
                <div class="summary-row">
                  <span>Tổng số lượng:</span>
                  <strong>{{ cart.itemCount }} cuốn</strong>
                </div>
                <div class="summary-row">
                  <span>Hạn trả dự kiến:</span>
                  <strong>{{ dueDate }}</strong>
                </div>
              </div>
              
              <div class="notice">
                <font-awesome-icon icon="fa-solid fa-circle-info" />
                <p>Bằng cách nhấn xác nhận, bạn đồng ý tuân thủ nội quy mượn trả của thư viện.</p>
              </div>

              <button 
                @click="handleConfirm" 
                class="btn btn-primary btn-lg btn-block"
                :disabled="isSubmitting"
              >
                <font-awesome-icon v-if="isSubmitting" icon="fa-solid fa-spinner" class="fa-spin" />
                <span v-else>Xác nhận đặt chỗ</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/CartView.css"></style>
