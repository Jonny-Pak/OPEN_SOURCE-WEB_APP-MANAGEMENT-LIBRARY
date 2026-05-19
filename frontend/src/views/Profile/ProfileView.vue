<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import apiClient from '@/services/apiClient'
import { datChoService } from '@/services/datChoService'
import { muonSachService } from '@/services/muonSachService'
import { useToast } from '@/composables/useToast'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const authStore = useAuthStore()
const router = useRouter()
const toast = useToast()

const activeTab = ref('overview')
const loading = ref(false)

const userRoleName = computed(() => {
  if (authStore.isAdmin) return 'Quản trị viên'
  if (authStore.isLibrarian) return 'Thủ thư'
  return 'Độc giả'
})

const user = ref({
  name: authStore.tenDayDu || 'Nguyễn Văn A',
  email: authStore.thongTinNguoiDung?.email || 'student.a@school.edu.vn',
  studentId: 'SV2024001',
  class: authStore.currentRole === 'DOC_GIA' ? 'Độc giả Thư viện' : 'Ban quản lý',
  avatar: authStore.thongTinNguoiDung?.avatar || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200',
  joinDate: '15/01/2024',
  borrowedCount: 0,
  returningSoon: 0,
  reservationCount: 0,
})

// Form fields for editing profile
const editableProfile = ref({
  hoDem: '',
  ten: '',
  soDienThoai: '',
  ngaySinh: '',
  gioiTinh: 'NAM',
  cccd: '',
  diaChi: '',
  avatar: ''
})

const avatarFileInput = ref<HTMLInputElement | null>(null)
const profileMessage = ref('')
const profileError = ref(false)
const updatingProfile = ref(false)

const tempAvatar = ref('')
const savingAvatar = ref(false)

const triggerAvatarUpload = () => {
  avatarFileInput.value?.click()
}

const onAvatarFileChange = (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (file.size > 2 * 1024 * 1024) {
    toast.error('Ảnh quá lớn (tối đa 2MB)')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    const url = ev.target?.result as string
    tempAvatar.value = url
    user.value.avatar = url
    toast.success('Đã chọn ảnh mới. Hãy nhấn "Lưu ảnh" ở dưới ảnh đại diện để hoàn tất.')
  }
  reader.readAsDataURL(file)
}

const handleSaveAvatar = async () => {
  if (!tempAvatar.value) return
  savingAvatar.value = true
  try {
    const res: any = await apiClient.put('/api/v1/nguoi-dung/me', {
      hoDem: editableProfile.value.hoDem || '',
      ten: editableProfile.value.ten || '',
      soDienThoai: editableProfile.value.soDienThoai || '',
      ngaySinh: editableProfile.value.ngaySinh || null,
      gioiTinh: editableProfile.value.gioiTinh || 'NAM',
      cccd: editableProfile.value.cccd || null,
      diaChi: editableProfile.value.diaChi || null,
      avatar: tempAvatar.value
    })
    if (res) {
      user.value.avatar = res.avatar
      editableProfile.value.avatar = res.avatar
      if (authStore.thongTinNguoiDung) {
        authStore.thongTinNguoiDung.avatar = res.avatar
        localStorage.setItem('userInfo', JSON.stringify(authStore.thongTinNguoiDung))
      }
      tempAvatar.value = ''
      toast.success('Cập nhật ảnh đại diện thành công!')
    }
  } catch (err: any) {
    toast.error(err.message || 'Có lỗi xảy ra khi cập nhật ảnh đại diện')
  } finally {
    savingAvatar.value = false
  }
}

const handleCancelAvatar = () => {
  tempAvatar.value = ''
  user.value.avatar = editableProfile.value.avatar || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200'
  toast.info('Đã hủy thay đổi ảnh đại diện')
}

const handleUpdateProfile = async () => {
  if (!editableProfile.value.hoDem.trim() || !editableProfile.value.ten.trim()) {
    profileMessage.value = 'Họ đệm và Tên không được để trống'
    profileError.value = true
    return
  }
  if (!editableProfile.value.soDienThoai.trim()) {
    profileMessage.value = 'Số điện thoại không được để trống'
    profileError.value = true
    return
  }
  if (!/^0\d{9}$/.test(editableProfile.value.soDienThoai.trim())) {
    profileMessage.value = 'Số điện thoại phải có đúng 10 chữ số và bắt đầu bằng số 0'
    profileError.value = true
    return
  }
  if (editableProfile.value.cccd && !/^\d{12}$/.test(editableProfile.value.cccd.trim())) {
    profileMessage.value = 'CCCD phải bao gồm đúng 12 chữ số'
    profileError.value = true
    return
  }

  updatingProfile.value = true
  profileMessage.value = ''
  profileError.value = false

  try {
    const res: any = await apiClient.put('/api/v1/nguoi-dung/me', {
      hoDem: editableProfile.value.hoDem.trim(),
      ten: editableProfile.value.ten.trim(),
      soDienThoai: editableProfile.value.soDienThoai.trim(),
      ngaySinh: editableProfile.value.ngaySinh ? editableProfile.value.ngaySinh : null,
      gioiTinh: editableProfile.value.gioiTinh,
      cccd: editableProfile.value.cccd ? editableProfile.value.cccd.trim() : null,
      diaChi: editableProfile.value.diaChi ? editableProfile.value.diaChi.trim() : null,
      avatar: editableProfile.value.avatar || null
    })

    if (res) {
      user.value.name = `${res.hoDem} ${res.ten}`
      if (res.avatar) {
        user.value.avatar = res.avatar
      }
      if (authStore.thongTinNguoiDung) {
        authStore.thongTinNguoiDung.avatar = res.avatar
        authStore.thongTinNguoiDung.hoDem = res.hoDem
        authStore.thongTinNguoiDung.ten = res.ten
        localStorage.setItem('userInfo', JSON.stringify(authStore.thongTinNguoiDung))
      }
    }

    profileMessage.value = 'Cập nhật thông tin cá nhân thành công!'
    profileError.value = false
    toast.success('Đã cập nhật thông tin cá nhân!')
  } catch (err: any) {
    profileMessage.value = err.message || 'Có lỗi xảy ra khi cập nhật thông tin'
    profileError.value = true
    toast.error(profileMessage.value)
  } finally {
    updatingProfile.value = false
  }
}

const handleLogout = () => {
  authStore.xoaXacThuc()
  router.push('/login')
}

const currentLoans = ref<any[]>([])
const history = ref<any[]>([])
const reservations = ref<any[]>([])

const matKhauCu = ref('')
const matKhauMoi = ref('')
const xacNhanMatKhau = ref('')

const showMatKhauCu = ref(false)
const showMatKhauMoi = ref(false)
const showXacNhanMatKhau = ref(false)

const passMessage = ref('')
const passError = ref(false)

// Map trạng thái đặt chỗ
const statusLabel = (status: string) => {
  switch (status) {
    case 'CHO_DUYET': case 'DANG_CHO': return 'Đang chờ'
    case 'DA_SAN_SANG': return 'Sẵn sàng nhận'
    case 'DA_MUON': case 'DA_NHAN_SACH': return 'Đã mượn'
    case 'DA_HUY': case 'HET_HAN': return 'Đã hủy / Hết hạn'
    default: return status
  }
}
const statusClass = (status: string) => {
  switch (status) {
    case 'CHO_DUYET': case 'DANG_CHO': return 'info'
    case 'DA_SAN_SANG': return 'success'
    case 'DA_MUON': case 'DA_NHAN_SACH': return 'purple'
    case 'DA_HUY': case 'HET_HAN': return 'warning'
    default: return 'info'
  }
}
const canCancelReservation = (status: string) =>
  status === 'CHO_DUYET' || status === 'DANG_CHO' || status === 'DA_SAN_SANG'

const cancellingId = ref<string | null>(null)
const cancelReservation = async (id: string | number) => {
  cancellingId.value = String(id)
  try {
    await datChoService.cancel(id)
    toast.success('Đã hủy đặt chỗ thành công!')
    await loadReservations()
  } catch (err: any) {
    toast.error(err?.message || 'Không thể hủy đặt chỗ lúc này.')
  } finally {
    cancellingId.value = null
  }
}

const extendingId = ref<string | null>(null)
const extensionRequests = ref<any[]>([])

const loadRenewalRequests = async () => {
  try {
    const allReqs: any[] = []
    for (const loan of currentLoans.value) {
      const histories: any = await apiClient.get(`/api/v1/phieu-muon/gia-han/${loan.id}`)
      if (histories && Array.isArray(histories)) {
        histories.forEach((h: any) => {
          allReqs.push({
            ...h,
            bookTitle: loan.title,
            loanId: loan.id
          })
        })
      }
    }
    extensionRequests.value = allReqs
  } catch (err) {
    console.error('Lỗi khi tải lịch sử gia hạn:', err)
  }
}

const hasPendingRequest = (loanId: string) => {
  return extensionRequests.value.some(
    r => r.maChiTietPhieuMuon === loanId && r.trangThai === 'CHO_DUYET'
  )
}

const getSoLanGiaHanDaDuyet = (loanId: string) => {
  return extensionRequests.value.filter(
    r => r.maChiTietPhieuMuon === loanId && r.trangThai === 'DA_DUYET'
  ).length
}

const extendLoan = async (id: string) => {
  extendingId.value = id
  try {
    await muonSachService.extendBorrowDocGia(id)
    toast.success('Đã gửi yêu cầu gia hạn sách thành công! Vui lòng chờ Thủ thư duyệt.')
    await loadProfileAndHistory()
  } catch (err: any) {
    toast.error(err?.message || 'Không thể gia hạn lúc này.')
  } finally {
    extendingId.value = null
  }
}

const loadReservations = async () => {
  try {
    const list = await datChoService.getMy()
    reservations.value = list
    user.value.reservationCount = list.filter(
      (r: any) => r.trangThai === 'CHO_DUYET' || r.trangThai === 'DA_SAN_SANG'
    ).length
  } catch (err) {
    console.error('Lỗi khi tải đặt chỗ:', err)
  }
}

const loadProfileAndHistory = async () => {
  loading.value = true
  try {
    // 1. Fetch live user details
    const profile: any = await apiClient.get('/api/v1/nguoi-dung/me')
    if (profile) {
      user.value.name = `${profile.hoDem} ${profile.ten}`
      user.value.email = profile.email
      user.value.studentId = profile.maNguoiDung ? profile.maNguoiDung.substring(0, 8).toUpperCase() : 'SV2024001'
      if (profile.avatar) {
        user.value.avatar = profile.avatar
      }
      if (profile.ngayTao) {
        user.value.joinDate = new Date(profile.ngayTao).toLocaleDateString('vi-VN')
      }

      // Populate form fields for editing
      editableProfile.value.hoDem = profile.hoDem || ''
      editableProfile.value.ten = profile.ten || ''
      editableProfile.value.soDienThoai = profile.soDienThoai || ''
      editableProfile.value.ngaySinh = profile.ngaySinh || ''
      editableProfile.value.gioiTinh = profile.gioiTinh || 'NAM'
      editableProfile.value.cccd = profile.cccd || ''
      editableProfile.value.diaChi = profile.diaChi || ''
      editableProfile.value.avatar = profile.avatar || ''
    }

    // 2. Fetch borrowing history via correct endpoint
    const response: any = await apiClient.get('/api/v1/phieu-muon/cua-toi?page=0&size=50')
    if (response && response.content) {
      const activeList: any[] = []
      const returnedList: any[] = []

      response.content.forEach((phieu: any) => {
        const details = phieu.danhSachChiTiet || []
        details.forEach((ct: any) => {
          const loanItem = {
            id: ct.maChiTietPhieuMuon,
            title: ct.tenSach,
            author: 'N/A',
            borrowDate: phieu.ngayMuon ? new Date(phieu.ngayMuon).toLocaleDateString('vi-VN') : '',
            dueDate: ct.hanTraHienTai ? new Date(ct.hanTraHienTai).toLocaleDateString('vi-VN') : '',
            returnDate: ct.ngayTraThucTe ? new Date(ct.ngayTraThucTe).toLocaleDateString('vi-VN') : '',
            status: ct.trangThaiChiTietPhieuMuon === 'DANG_MUON' ? 'Đang mượn' :
                    ct.trangThaiChiTietPhieuMuon === 'QUA_HAN' ? 'Quá hạn' :
                    ct.trangThaiChiTietPhieuMuon === 'DA_TRA' ? 'Đã trả' :
                    ct.trangThaiChiTietPhieuMuon === 'DA_TRA_TRE' ? 'Đã trả trễ' :
                    ct.trangThaiChiTietPhieuMuon === 'MAT_SACH' ? 'Mất sách' : 'Khác',
            rawStatus: ct.trangThaiChiTietPhieuMuon,
            image: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=100'
          }

          if (ct.trangThaiChiTietPhieuMuon === 'DANG_MUON' || ct.trangThaiChiTietPhieuMuon === 'QUA_HAN') {
            activeList.push(loanItem)
          } else {
            returnedList.push(loanItem)
          }
        })
      })

      currentLoans.value = activeList
      history.value = returnedList
      user.value.borrowedCount = activeList.length + returnedList.length
      user.value.returningSoon = activeList.filter(l => l.rawStatus === 'QUA_HAN').length

      // Load renewal requests immediately for active loans
      if (authStore.currentRole === 'DOC_GIA' && activeList.length > 0) {
        await loadRenewalRequests()
      }
    }

    // 3. Fetch reservations
    if (authStore.currentRole === 'DOC_GIA') {
      await loadReservations()
    }
  } catch (error) {
    console.error('Lỗi khi tải thông tin:', error)
  } finally {
    loading.value = false
  }
}

const handleUpdatePassword = async () => {
  if (!matKhauCu.value || !matKhauMoi.value || !xacNhanMatKhau.value) {
    passMessage.value = 'Vui lòng điền đầy đủ thông tin'
    passError.value = true
    return
  }
  if (matKhauMoi.value !== xacNhanMatKhau.value) {
    passMessage.value = 'Mật khẩu mới và xác nhận mật khẩu không khớp'
    passError.value = true
    return
  }

  try {
    await apiClient.put('/api/v1/nguoi-dung/me/password', {
      matKhauCu: matKhauCu.value,
      matKhauMoi: matKhauMoi.value,
      xacNhanMatKhau: xacNhanMatKhau.value
    })
    passMessage.value = 'Đổi mật khẩu thành công!'
    passError.value = false
    matKhauCu.value = ''
    matKhauMoi.value = ''
    xacNhanMatKhau.value = ''
  } catch (err: any) {
    passMessage.value = err.message || 'Mật khẩu cũ không chính xác hoặc mật khẩu mới không đúng định dạng'
    passError.value = true
  }
}

onMounted(() => {
  loadProfileAndHistory()
})

</script>


<template>
  <div class="profile-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container profile-grid">
        <!-- Sidebar Navigation -->
        <aside class="profile-sidebar">
          <div class="user-card">
            <div class="avatar-wrapper">
              <img :src="user.avatar" :alt="user.name" />
              <button class="edit-avatar" type="button" @click="triggerAvatarUpload">
                <font-awesome-icon icon="fa-solid fa-camera" />
              </button>
              <input 
                type="file" 
                ref="avatarFileInput" 
                style="display: none;" 
                accept="image/*" 
                @change="onAvatarFileChange" 
              />
            </div>
            
            <!-- Quick save/cancel buttons for avatar -->
            <div v-if="tempAvatar" class="avatar-actions" style="margin-top: 10px; display: flex; gap: 8px; justify-content: center; width: 100%; z-index: 10;">
              <button 
                type="button" 
                class="btn btn-sm" 
                :disabled="savingAvatar" 
                @click="handleSaveAvatar"
                style="font-size: 0.8rem; padding: 6px 12px; display: flex; align-items: center; gap: 4px; background: #10b981; color: white; border: none; border-radius: 6px; cursor: pointer; transition: all 0.2s;"
              >
                <font-awesome-icon v-if="savingAvatar" icon="fa-solid fa-spinner" class="fa-spin" />
                Lưu ảnh
              </button>
              <button 
                type="button" 
                class="btn btn-sm" 
                :disabled="savingAvatar" 
                @click="handleCancelAvatar"
                style="font-size: 0.8rem; padding: 6px 12px; background: #6b7280; color: white; border: none; border-radius: 6px; cursor: pointer; transition: all 0.2s;"
              >
                Hủy
              </button>
            </div>

            <div class="user-meta">
              <h2>{{ user.name }}</h2>
              <p class="student-id">{{ user.studentId }}</p>
              <span class="badge">{{ userRoleName }}</span>
            </div>
          </div>
          
          <nav class="side-nav">
            <button 
              :class="{ active: activeTab === 'overview' }" 
              @click="activeTab = 'overview'"
            >
              <span class="icon"><font-awesome-icon icon="fa-solid fa-chart-line" /></span> Tổng quan
            </button>
            <button 
              v-if="authStore.currentRole === 'DOC_GIA'"
              :class="{ active: activeTab === 'books' }" 
              @click="activeTab = 'books'"
            >
              <span class="icon"><font-awesome-icon icon="fa-solid fa-book" /></span> Sách của tôi
            </button>
            <button 
              v-if="authStore.currentRole === 'DOC_GIA'"
              :class="{ active: activeTab === 'renewals' }" 
              @click="activeTab = 'renewals'"
            >
              <span class="icon"><font-awesome-icon icon="fa-solid fa-calendar-plus" /></span> Gia hạn sách
            </button>
            <button 
              v-if="authStore.currentRole === 'DOC_GIA'"
              :class="{ active: activeTab === 'reservations' }" 
              @click="activeTab = 'reservations'"
            >
              <span class="icon"><font-awesome-icon icon="fa-solid fa-bookmark" /></span>
              Đặt chỗ
              <span v-if="user.reservationCount > 0" class="badge-count">{{ user.reservationCount }}</span>
            </button>
            <button 
              :class="{ active: activeTab === 'settings' }" 
              @click="activeTab = 'settings'"
            >
              <span class="icon"><font-awesome-icon icon="fa-solid fa-id-card" /></span> Cài đặt tài khoản
            </button>
            <button @click="handleLogout" class="logout-btn">
              <span class="icon"><font-awesome-icon icon="fa-solid fa-right-from-bracket" /></span> Đăng xuất
            </button>
          </nav>
        </aside>

        
        <!-- Main Content Area -->
        <section class="profile-main">
          <!-- Overview Tab -->
          <div v-if="activeTab === 'overview'" class="tab-content">
            <h1 class="tab-title">Tổng quan tài khoản</h1>
            
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-icon yellow"><font-awesome-icon icon="fa-solid fa-book-open" /></div>
                <div class="stat-info">
                  <span class="stat-value">{{ user.borrowedCount }}</span>
                  <span class="stat-label">Tổng sách đã mượn</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon cyan"><font-awesome-icon icon="fa-solid fa-clock" /></div>
                <div class="stat-info">
                  <span class="stat-value">{{ user.returningSoon }}</span>
                  <span class="stat-label">Sách đang quá hạn</span>
                </div>
              </div>
              <div v-if="authStore.currentRole === 'DOC_GIA'" class="stat-card" style="cursor:pointer" @click="activeTab = 'reservations'">
                <div class="stat-icon orange"><font-awesome-icon icon="fa-solid fa-bookmark" /></div>
                <div class="stat-info">
                  <span class="stat-value">{{ user.reservationCount }}</span>
                  <span class="stat-label">Đặt chỗ đang chờ</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon purple"><font-awesome-icon icon="fa-solid fa-calendar-days" /></div>
                <div class="stat-info">
                  <span class="stat-value">{{ user.joinDate }}</span>
                  <span class="stat-label">Ngày tham gia</span>
                </div>
              </div>
            </div>
            
            <div class="section-card">
              <div class="card-header">
                <h3>Đang mượn gần đây</h3>
                <button v-if="authStore.currentRole === 'DOC_GIA'" @click="activeTab = 'books'" class="link-btn">Xem tất cả</button>
              </div>
              <div class="loans-list">
                <div v-if="currentLoans.length === 0" class="empty-state">
                  <p>Bạn không có cuốn sách nào đang mượn.</p>
                </div>
                <div v-for="loan in currentLoans" :key="loan.id" class="loan-item">
                  <img :src="loan.image" :alt="loan.title" />
                  <div class="loan-details">
                    <h4>{{ loan.title }}</h4>
                    <p>{{ loan.author }}</p>
                  </div>
                  <div class="loan-dates">
                    <span class="date-label">Hạn trả:</span>
                    <span class="date-value">{{ loan.dueDate }}</span>
                  </div>
                  <span class="status-tag" :class="loan.status === 'Quá hạn' ? 'warning' : 'info'">
                    {{ loan.status }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- My Books Tab -->
          <div v-if="activeTab === 'books'" class="tab-content">
            <h1 class="tab-title">Sách của tôi</h1>
            
            <div class="section-card">
              <h3>Sách đang mượn</h3>
              <div v-if="currentLoans.length === 0" class="empty-state">
                <p>Bạn không có cuốn sách nào đang mượn.</p>
              </div>
              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>Tên sách</th>
                    <th>Ngày mượn</th>
                    <th>Hạn trả</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="loan in currentLoans" :key="loan.id">
                    <td>
                      <div class="book-cell">
                        <strong>{{ loan.title }}</strong>
                        <span>{{ loan.author }}</span>
                      </div>
                    </td>
                    <td>{{ loan.borrowDate }}</td>
                    <td>{{ loan.dueDate }}</td>
                    <td>
                      <span class="status-tag" :class="loan.status === 'Quá hạn' ? 'warning' : 'info'">
                        {{ loan.status }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div class="section-card mt-xl">
              <h3>Lịch sử mượn trả</h3>
              <div v-if="history.length === 0" class="empty-state">
                <p>Không có lịch sử mượn trả.</p>
              </div>
              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>Tên sách</th>
                    <th>Ngày mượn</th>
                    <th>Ngày trả</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in history" :key="item.id">
                    <td><strong>{{ item.title }}</strong></td>
                    <td>{{ item.borrowDate }}</td>
                    <td>{{ item.returnDate }}</td>
                    <td><span class="status-tag success">{{ item.status }}</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <!-- Renewals Tab -->
          <div v-if="activeTab === 'renewals'" class="tab-content">
            <h1 class="tab-title">Gia hạn sách đang mượn</h1>

            <div class="alert-box info-box" style="margin-bottom: 20px; padding: 1rem; border-radius: 12px; background: rgba(6,182,212,0.06); border-left: 4px solid #06b6d4;">
              <div class="info-content" style="color: #0f766e; font-size: 0.9rem;">
                <strong style="display:block; margin-bottom: 0.5rem; font-size: 0.95rem;"><font-awesome-icon icon="fa-solid fa-lightbulb" style="color:#0891b2" /> Quy định gia hạn sách trực tuyến:</strong>
                <ul style="margin-left: 1.25rem; list-style-type: disc; line-height: 1.5;">
                  <li>Mỗi cuốn sách chỉ được gia hạn tối đa <strong>2 lần</strong>.</li>
                  <li>Sách đã quá hạn trả hoặc đã có độc giả khác đặt chỗ sẽ không thể gia hạn trực tuyến.</li>
                  <li>Yêu cầu gia hạn sẽ chuyển sang trạng thái <strong>Chờ duyệt</strong> và cần được Thủ thư phê duyệt.</li>
                  <li>Thời gian gia hạn tự động lấy theo cấu hình hệ thống (mặc định lấy từ Cài đặt hệ thống).</li>
                </ul>
              </div>
            </div>

            <div class="section-card">
              <h3>Danh sách sách đang mượn</h3>
              <div v-if="currentLoans.length === 0" class="empty-state">
                <p>Bạn không có cuốn sách nào đang mượn.</p>
              </div>
              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>Tên sách</th>
                    <th>Hạn trả hiện tại</th>
                    <th>Số lần gia hạn</th>
                    <th>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="loan in currentLoans" :key="loan.id">
                    <td>
                      <div class="book-cell">
                        <strong>{{ loan.title }}</strong>
                        <span>{{ loan.author }}</span>
                      </div>
                    </td>
                    <td>{{ loan.dueDate }}</td>
                    <td>
                      <span class="badge" style="background: rgba(6,182,212,0.1); color: #0891b2; font-weight: bold; padding: 0.25rem 0.5rem; border-radius: 6px; font-size: 0.8rem;">
                        {{ getSoLanGiaHanDaDuyet(loan.id) }} / 2 lần
                      </span>
                    </td>
                    <td>
                      <span v-if="hasPendingRequest(loan.id)" class="status-tag warning">
                        🕒 Chờ duyệt
                      </span>
                      <button 
                        v-else-if="getSoLanGiaHanDaDuyet(loan.id) < 2 && loan.rawStatus === 'DANG_MUON'" 
                        class="btn-sm btn-primary"
                        :disabled="extendingId === loan.id"
                        @click="extendLoan(loan.id)"
                      >
                        <font-awesome-icon :icon="extendingId === loan.id ? 'fa-solid fa-spinner' : 'fa-solid fa-calendar-plus'" :class="{ 'fa-spin': extendingId === loan.id }" />
                        {{ extendingId === loan.id ? 'Đang xử lý...' : 'Yêu cầu gia hạn' }}
                      </button>
                      <span v-else class="text-muted" style="font-size:0.8rem">Không khả dụng</span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="section-card mt-xl" style="margin-top: 2rem;">
              <h3>Lịch sử yêu cầu gia hạn sách</h3>
              <div v-if="extensionRequests.length === 0" class="empty-state">
                <p>Không có lịch sử yêu cầu gia hạn.</p>
              </div>
              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>Tên sách</th>
                    <th>Hạn cũ</th>
                    <th>Hạn mới đề xuất</th>
                    <th>Ngày yêu cầu</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="req in extensionRequests" :key="req.maLichSuGiaHan">
                    <td><strong>{{ req.bookTitle }}</strong></td>
                    <td>{{ req.hanTraCu ? new Date(req.hanTraCu).toLocaleDateString('vi-VN') : '—' }}</td>
                    <td>{{ req.hanTraMoi ? new Date(req.hanTraMoi).toLocaleDateString('vi-VN') : '—' }}</td>
                    <td>{{ req.ngayTao ? new Date(req.ngayTao).toLocaleDateString('vi-VN') : '—' }}</td>
                    <td>
                      <span class="status-tag" :class="req.trangThai === 'CHO_DUYET' ? 'warning' : req.trangThai === 'DA_DUYET' ? 'success' : 'danger'">
                        {{ req.trangThai === 'CHO_DUYET' ? 'Chờ duyệt' : req.trangThai === 'DA_DUYET' ? 'Đã duyệt' : 'Từ chối' }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <!-- Reservations Tab -->
          <div v-if="activeTab === 'reservations'" class="tab-content">
            <h1 class="tab-title">Lịch sử đặt chỗ</h1>

            <div class="section-card">
              <div class="card-header">
                <h3>Danh sách đặt chỗ của tôi</h3>
                <button @click="loadReservations" class="link-btn">
                  <font-awesome-icon icon="fa-solid fa-rotate" /> Làm mới
                </button>
              </div>

              <div v-if="reservations.length === 0" class="empty-state">
                <font-awesome-icon icon="fa-solid fa-bookmark" style="font-size:2rem; margin-bottom:12px; opacity:0.4" />
                <p>Bạn chưa có lượt đặt chỗ nào.</p>
                <button @click="$router.push('/books')" class="btn btn-primary" style="margin-top:12px">
                  Tìm kiếm sách
                </button>
              </div>

              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>Tên sách</th>
                    <th>Thời gian đặt</th>
                    <th>Hạn nhận sách</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="res in reservations" :key="res.maDatCho">
                    <td>
                      <div class="book-cell">
                        <strong>{{ res.sach?.tenSach || 'Đầu sách' }}</strong>
                        <span style="font-size:0.78rem; color:var(--text-muted)">ISBN: {{ res.sach?.maIsbn || 'N/A' }}</span>
                      </div>
                    </td>
                    <td>{{ res.ngayDatCho ? new Date(res.ngayDatCho).toLocaleString('vi-VN') : '—' }}</td>
                    <td>
                      <span :style="{ color: canCancelReservation(res.trangThai) && res.ngayHetHan && new Date(res.ngayHetHan) < new Date() ? 'var(--color-warning)' : '' }">
                        {{ res.ngayHetHan ? new Date(res.ngayHetHan).toLocaleString('vi-VN') : '—' }}
                      </span>
                    </td>
                    <td>
                      <span class="status-tag" :class="statusClass(res.trangThai)">
                        {{ statusLabel(res.trangThai) }}
                      </span>
                    </td>
                    <td>
                      <button
                        v-if="canCancelReservation(res.trangThai)"
                        class="btn-sm btn-danger"
                        :disabled="cancellingId === String(res.maDatCho)"
                        @click="cancelReservation(res.maDatCho)"
                      >
                        <font-awesome-icon :icon="cancellingId === String(res.maDatCho) ? 'fa-solid fa-spinner' : 'fa-solid fa-xmark'" :class="{ 'fa-spin': cancellingId === String(res.maDatCho) }" />
                        {{ cancellingId === String(res.maDatCho) ? 'Đang hủy...' : 'Hủy' }}
                      </button>
                      <span v-else class="text-muted" style="font-size:0.8rem">—</span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <!-- Settings Tab -->
          <div v-if="activeTab === 'settings'" class="tab-content">

            <h1 class="tab-title">Cài đặt tài khoản</h1>
            
            <div class="section-card">
              <h3>Thông tin cá nhân</h3>
              <form class="settings-form" @submit.prevent="handleUpdateProfile">
                <div v-if="profileMessage" class="alert-box" :class="{ error: profileError, success: !profileError }">
                  {{ profileMessage }}
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label>Họ đệm <span style="color: red;">*</span></label>
                    <input type="text" v-model="editableProfile.hoDem" required placeholder="Nhập họ đệm" />
                  </div>
                  <div class="form-group">
                    <label>Tên <span style="color: red;">*</span></label>
                    <input type="text" v-model="editableProfile.ten" required placeholder="Nhập tên" />
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label>Số điện thoại <span style="color: red;">*</span></label>
                    <input type="text" v-model="editableProfile.soDienThoai" required placeholder="Ví dụ: 0912345678" />
                  </div>
                  <div class="form-group">
                    <label>Số CCCD / Định danh (12 chữ số)</label>
                    <input type="text" v-model="editableProfile.cccd" placeholder="Nhập 12 số định danh" />
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label>Ngày sinh</label>
                    <input type="date" v-model="editableProfile.ngaySinh" />
                  </div>
                  <div class="form-group">
                    <label>Giới tính</label>
                    <select v-model="editableProfile.gioiTinh" style="padding: 0.75rem 1rem; border: 1px solid var(--border); border-radius: var(--radius); background: white;">
                      <option value="NAM">Nam</option>
                      <option value="NU">Nữ</option>
                    </select>
                  </div>
                </div>

                <div class="form-group">
                  <label>Địa chỉ</label>
                  <input type="text" v-model="editableProfile.diaChi" placeholder="Nhập địa chỉ cư trú của bạn" />
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label>Email (Không thể thay đổi)</label>
                    <input type="email" :value="user.email" readonly />
                  </div>
                  <div class="form-group">
                    <label>Mã độc giả / Nhân viên (Không thể thay đổi)</label>
                    <input type="text" :value="user.studentId" readonly />
                  </div>
                </div>

                <div class="form-group">
                  <label>Lớp / Ban ngành (Không thể thay đổi)</label>
                  <input type="text" :value="user.class" readonly />
                </div>

                <button type="submit" class="btn btn-outline" style="align-self: flex-start;" :disabled="updatingProfile">
                  <font-awesome-icon v-if="updatingProfile" icon="fa-solid fa-spinner" class="fa-spin" style="margin-right: 0.5rem;" />
                  {{ updatingProfile ? 'Đang cập nhật...' : 'Cập nhật thông tin' }}
                </button>
              </form>
            </div>
            
            <div class="section-card mt-xl">
              <h3>Đổi mật khẩu</h3>
              <form class="settings-form" @submit.prevent="handleUpdatePassword">
                <div v-if="passMessage" class="alert-box" :class="{ error: passError, success: !passError }">
                  {{ passMessage }}
                </div>

                <div class="form-group">
                  <label>Mật khẩu hiện tại</label>
                  <div style="position: relative; display: flex; align-items: center; width: 100%;">
                    <input 
                      :type="showMatKhauCu ? 'text' : 'password'" 
                      v-model="matKhauCu" 
                      placeholder="********" 
                      required 
                      style="width: 100%; padding-right: 40px;" 
                    />
                    <button 
                      type="button" 
                      @click="showMatKhauCu = !showMatKhauCu" 
                      style="position: absolute; right: 12px; background: none; border: none; color: var(--text-muted); cursor: pointer; display: flex; align-items: center;"
                    >
                      <font-awesome-icon :icon="showMatKhauCu ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'" />
                    </button>
                  </div>
                </div>
                <div class="form-group">
                  <label>Mật khẩu mới</label>
                  <div style="position: relative; display: flex; align-items: center; width: 100%;">
                    <input 
                      :type="showMatKhauMoi ? 'text' : 'password'" 
                      v-model="matKhauMoi" 
                      placeholder="Nhập mật khẩu mới" 
                      required 
                      style="width: 100%; padding-right: 40px;" 
                    />
                    <button 
                      type="button" 
                      @click="showMatKhauMoi = !showMatKhauMoi" 
                      style="position: absolute; right: 12px; background: none; border: none; color: var(--text-muted); cursor: pointer; display: flex; align-items: center;"
                    >
                      <font-awesome-icon :icon="showMatKhauMoi ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'" />
                    </button>
                  </div>
                </div>
                <div class="form-group">
                  <label>Xác nhận mật khẩu mới</label>
                  <div style="position: relative; display: flex; align-items: center; width: 100%;">
                    <input 
                      :type="showXacNhanMatKhau ? 'text' : 'password'" 
                      v-model="xacNhanMatKhau" 
                      placeholder="Nhập lại mật khẩu mới" 
                      required 
                      style="width: 100%; padding-right: 40px;" 
                    />
                    <button 
                      type="button" 
                      @click="showXacNhanMatKhau = !showXacNhanMatKhau" 
                      style="position: absolute; right: 12px; background: none; border: none; color: var(--text-muted); cursor: pointer; display: flex; align-items: center;"
                    >
                      <font-awesome-icon :icon="showXacNhanMatKhau ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'" />
                    </button>
                  </div>
                </div>
                <button type="submit" class="btn btn-outline">Cập nhật mật khẩu</button>
              </form>
            </div>
          </div>
        </section>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/ProfileView.css"></style>
