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
  // Load initial books from localStorage if present
  const books = ref<Book[]>(
    (() => {
      const saved = localStorage.getItem('wishlist_books')
      return saved ? JSON.parse(saved) : []
    })()
  )

  const items = computed(() => books.value)
  const itemCount = computed(() => books.value.length)

  function saveToLocalStorage() {
    localStorage.setItem('wishlist_books', JSON.stringify(books.value))
  }

  function addToWishlist(book: Book) {
    if (!books.value.some(item => item.id === book.id)) {
      books.value.push(book)
      saveToLocalStorage()
    }
  }

  function removeFromWishlist(bookId: number) {
    books.value = books.value.filter(item => item.id !== bookId)
    saveToLocalStorage()
  }

  function isInWishlist(bookId: number) {
    return books.value.some(item => item.id === bookId)
  }

  // Backward compatibility alias
  function toggleWishlist(book: Book) {
    if (isInWishlist(book.id)) {
      removeFromWishlist(book.id)
      return false
    } else {
      addToWishlist(book)
      return true
    }
  }

  return {
    books,
    items,
    itemCount,
    addToWishlist,
    removeFromWishlist,
    isInWishlist,
    toggleWishlist
  }
})
export default useWishlistStore
