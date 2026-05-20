import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useAuthStore } from './auth'
import { datChoService } from '@/services/datChoService'

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

  // Requested English actions
  function addBook(book: Book) {
    return addItem(book)
  }

  function removeBook(bookId: number) {
    removeItem(bookId)
  }

  async function borrowAll() {
    const authStore = useAuthStore()
    if (!authStore.daXacThuc) {
      alert('Vui lòng đăng nhập để thực hiện mượn sách!')
      return
    }

    // Process borrow book for each item in the cart (reserving them)
    for (const item of items.value) {
      await datChoService.reserve(item.id)
    }
    clearCart()
  }

  return {
    items,
    itemCount,
    isFull,
    addItem,
    removeItem,
    clearCart,
    addBook,
    removeBook,
    borrowAll
  }
})
export default useCartStore
