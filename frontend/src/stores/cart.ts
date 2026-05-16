import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { phieuMuonService } from '@/services/phieuMuonService'
import { useAuthStore } from './auth'

export interface Book {
  id: number
  title: string
  author: string
  image: string
  category: string
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<Book[]>([])
  const maxItems = 3
  const authStore = useAuthStore()
  const isSubmitting = ref(false)

  const itemCount = computed(() => items.value.length)
  const isFull = computed(() => items.value.length >= maxItems)

  function addItem(book: Book) {
    if (items.value.length >= maxItems) {
      alert(`Bạn chỉ có thể mượn tối đa ${maxItems} cuốn sách cùng lúc.`)
      return false
    }
    
    if (items.value.find(item => item.id === book.id)) {
      alert('Sách này đã có trong danh sách mượn của bạn.')
      return false
    }

    items.value.push(book)
    return true
  }

  function removeItem(bookId: number) {
    items.value = items.value.filter(item => item.id !== bookId)
  }

  function clearCart() {
    items.value = []
  }

  async function checkout() {
    if (!authStore.daXacThuc || !authStore.thongTinNguoiDung?.maNguoiDung) {
      alert('Vui lòng đăng nhập để mượn sách!')
      return null
    }

    if (items.value.length === 0) return null

    isSubmitting.value = true
    try {
      const request = {
        maNguoiDung: authStore.thongTinNguoiDung.maNguoiDung,
        danhSachMaSach: items.value.map(item => item.id)
      }
      const response = await phieuMuonService.createPhieuMuon(request)
      clearCart()
      return response
    } catch (error: any) {
      console.error('Lỗi khi mượn sách:', error)
      alert(error.message || 'Có lỗi xảy ra khi mượn sách. Vui lòng thử lại.')
      return null
    } finally {
      isSubmitting.value = false
    }
  }

  return {
    items,
    itemCount,
    isFull,
    isSubmitting,
    addItem,
    removeItem,
    clearCart,
    checkout
  }
})
