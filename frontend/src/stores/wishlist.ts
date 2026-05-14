import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface Book {
  id: number
  title: string
  author: string
  image: string
  category: string
}

export const useWishlistStore = defineStore('wishlist', () => {
  const items = ref<Book[]>([])

  const itemCount = computed(() => items.value.length)

  function toggleWishlist(book: Book) {
    const index = items.value.findIndex(item => item.id === book.id)
    if (index > -1) {
      items.value.splice(index, 1)
      return false // Removed
    } else {
      items.value.push(book)
      return true // Added
    }
  }

  function isInWishlist(bookId: number) {
    return items.value.some(item => item.id === bookId)
  }

  return {
    items,
    itemCount,
    toggleWishlist,
    isInWishlist
  }
})
