import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

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

  return {
    items,
    itemCount,
    isFull,
    addItem,
    removeItem,
    clearCart
  }
})
