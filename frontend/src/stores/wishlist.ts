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
  const items = ref<Book[]>([])
  const isLoading = ref(false)
  const authStore = useAuthStore()

  const itemCount = computed(() => items.value.length)

  /** Chuyển đổi từ dữ liệu Backend (Sach) sang dữ liệu Frontend (Book) */
  function mapToBook(sach: Sach): Book {
    return {
      id: sach.maSach,
      title: sach.tenSach,
      author: sach.tacGias.map(t => t.tenTacGia).join(', '),
      image: sach.anhBiaUrl || 'https://via.placeholder.com/300x450?text=No+Image',
      category: sach.theLoais.map(t => t.tenTheLoai).join(', ')
    }
  }

  /** Lấy danh sách yêu thích từ server */
  async function fetchWishlist() {
    if (!authStore.thongTinNguoiDung?.maNguoiDung) return
    
    isLoading.value = true
    try {
      const data = await yeuThichService.getDanhSach(authStore.thongTinNguoiDung.maNguoiDung)
      items.value = data.map(item => mapToBook(item.sach))
    } catch (error) {
      console.error('Lỗi khi tải danh sách yêu thích:', error)
    } finally {
      isLoading.value = false
    }
  }

  /** Thêm/Xóa sách khỏi danh sách yêu thích (gọi API) */
  async function toggleWishlist(book: Book) {
    if (!authStore.daXacThuc || !authStore.thongTinNguoiDung?.maNguoiDung) {
      // Nếu chưa đăng nhập, chỉ xử lý local hoặc yêu cầu đăng nhập
      alert('Vui lòng đăng nhập để sử dụng tính năng này!')
      return
    }

    const maNguoiDung = authStore.thongTinNguoiDung.maNguoiDung
    const index = items.value.findIndex(item => item.id === book.id)

    try {
      if (index > -1) {
        // Đã có trong list -> Xóa
        await yeuThichService.xoa(maNguoiDung, book.id)
        items.value.splice(index, 1)
        return false
      } else {
        // Chưa có -> Thêm
        await yeuThichService.them(maNguoiDung, book.id)
        items.value.push(book)
        return true
      }
    } catch (error) {
      console.error('Lỗi khi cập nhật danh sách yêu thích:', error)
      return index > -1
    }
  }

  function isInWishlist(bookId: number) {
    return items.value.some(item => item.id === bookId)
  }

  function clearWishlist() {
    items.value = []
  }

  return {
    items,
    itemCount,
    isLoading,
    fetchWishlist,
    toggleWishlist,
    isInWishlist,
    clearWishlist
  }
})
