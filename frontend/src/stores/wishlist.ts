import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { yeuThichService } from '@/services/yeuThichService'
import { useAuthStore } from './auth'
import type { Sach } from '@/types/sach'

export interface Book {
  id: number
  title: string
  author: string
  image: string
  category: string
}

export const useWishlistStore = defineStore('wishlist', () => {
  const books = ref<Book[]>(
    (() => {
      const saved = localStorage.getItem('wishlist')
      return saved ? (JSON.parse(saved) as Book[]) : []
    })()
  )
  const isLoading = ref(false)
  const authStore = useAuthStore()

  const items = computed(() => books.value)
  const itemCount = computed(() => books.value.length)

  /** Chuyển đổi từ dữ liệu Backend (Sach) sang dữ liệu Frontend (Book) */
  function mapToBook(sach: Sach): Book {
    return {
      id: sach.maSach,
      title: sach.tenSach,
      author: sach.danhSachTacGia.map(t => `${t.hoDem} ${t.ten}`).join(', '),
      image: sach.danhSachHinhAnh?.[0]?.duongDan || 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=200',
      category: sach.danhSachTheLoai.map(t => t.tenTheLoai).join(', ')
    }
  }

  /** Lấy danh sách yêu thích từ server */
  async function fetchWishlist() {
    if (!authStore.thongTinNguoiDung?.maNguoiDung) return
    
    isLoading.value = true
    try {
      const data = await yeuThichService.getDanhSach(authStore.thongTinNguoiDung.maNguoiDung)
      books.value = data.map(item => mapToBook(item.sach))
      localStorage.setItem('wishlist', JSON.stringify(books.value))
    } catch (error) {
      console.error('Lỗi khi tải danh sách yêu thích:', error)
    } finally {
      isLoading.value = false
    }
  }

  /** Thêm/Xóa sách khỏi danh sách yêu thích (gọi API) */
  async function toggleWishlist(book: Book) {
    if (!authStore.daXacThuc || !authStore.thongTinNguoiDung?.maNguoiDung) {
      alert('Vui lòng đăng nhập để sử dụng tính năng này!')
      return false
    }

    const maNguoiDung = authStore.thongTinNguoiDung.maNguoiDung
    const index = books.value.findIndex(item => item.id === book.id)

    try {
      if (index > -1) {
        // Đã có trong list -> Xóa
        await yeuThichService.xoa(maNguoiDung, book.id)
        books.value.splice(index, 1)
        localStorage.setItem('wishlist', JSON.stringify(books.value))
        return false
      } else {
        // Chưa có -> Thêm
        await yeuThichService.them(maNguoiDung, book.id)
        books.value.push(book)
        localStorage.setItem('wishlist', JSON.stringify(books.value))
        return true
      }
    } catch (error) {
      console.error('Lỗi khi cập nhật danh sách yêu thích:', error)
      return index > -1
    }
  }

  async function addToWishlist(book: Book) {
    if (!isInWishlist(book.id)) {
      await toggleWishlist(book)
    }
  }

  async function removeFromWishlist(bookId: number) {
    const item = books.value.find(i => i.id === bookId)
    if (item) {
      await toggleWishlist(item)
    }
  }

  function isInWishlist(bookId: number) {
    return books.value.some(item => item.id === bookId)
  }

  function clearWishlist() {
    books.value = []
    localStorage.removeItem('wishlist')
  }

  return {
    books,
    items,
    itemCount,
    isLoading,
    fetchWishlist,
    toggleWishlist,
    addToWishlist,
    removeFromWishlist,
    isInWishlist,
    clearWishlist
  }
})

export default useWishlistStore
