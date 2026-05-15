<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import { userService } from '../../services/userService'
import { muonSachService } from '../../services/muonSachService'
import type { NguoiDung, ChangePasswordRequest } from '@/types/nguoidung'
import type { PhieuMuon, ChiTietPhieuMuon } from '@/types/muonsach'

const activeTab = ref('overview')
const isLoading = ref(true)
const error = ref<string | null>(null)

const user = ref<NguoiDung | null>(null)
const allLoans = ref<PhieuMuon[]>([])

const profileForm = ref({
  hoDem: '',
  ten: '',
  soDienThoai: ''
})

const passwordForm = ref<ChangePasswordRequest>({
  matKhauCu: '',
  matKhauMoi: '',
  xacNhanMatKhau: ''
})

const currentLoans = computed(() => {
  const flattened: ChiTietPhieuMuon[] = []
  allLoans.value.forEach(phieu => {
    phieu.danhSachChiTiet.forEach(ct => {
      if (['DANG_MUON', 'QUA_HAN'].includes(ct.trangThaiChiTietPhieuMuon)) {
        flattened.push(ct)
      }
    })
  })
  return flattened
})

const loanHistory = computed(() => {
  const flattened: ChiTietPhieuMuon[] = []
  allLoans.value.forEach(phieu => {
    phieu.danhSachChiTiet.forEach(ct => {
      if (['DA_TRA', 'DA_TRA_TRE', 'MAT_SACH'].includes(ct.trangThaiChiTietPhieuMuon)) {
        flattened.push(ct)
      }
    })
  })
  return flattened
})

const fetchData = async () => {
  isLoading.value = true
  error.value = null
  try {
    const userData = await userService.getMyProfile()
    user.value = userData
    profileForm.value = {
      hoDem: userData.hoDem,
      ten: userData.ten,
      soDienThoai: userData.soDienThoai
    }
    
    const loansData = await muonSachService.getLichSuMuon(userData.maNguoiDung)
    allLoans.value = loansData
  } catch (err: any) {
    console.error('Failed to fetch profile data:', err)
    error.value = 'Không thể tải thông tin tài khoản. Vui lòng đăng nhập lại.'
  } finally {
    isLoading.value = false
  }
}

const handleUpdateProfile = async () => {
  try {
    await userService.updateProfile(profileForm.value)
    alert('Cập nhật thông tin thành công!')
    await fetchData()
  } catch (err: any) {
    alert('Cập nhật thất bại: ' + (err.message || 'Lỗi hệ thống'))
  }
}

const handleChangePassword = async () => {
  if (passwordForm.value.matKhauMoi !== passwordForm.value.xacNhanMatKhau) {
    alert('Mật khẩu xác nhận không khớp!')
    return
  }
  try {
    await userService.changePassword(passwordForm.value)
    alert('Đổi mật khẩu thành công!')
    passwordForm.value = { matKhauCu: '', matKhauMoi: '', xacNhanMatKhau: '' }
  } catch (err: any) {
    alert('Đổi mật khẩu thất bại: ' + (err.message || 'Lỗi hệ thống'))
  }
}

const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('vi-VN')
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    'DANG_MUON': 'Đang mượn',
    'QUA_HAN': 'Quá hạn',
    'DA_TRA': 'Đã trả',
    'DA_TRA_TRE': 'Trả trễ',
    'MAT_SACH': 'Mất sách',
    'DA_HUY': 'Đã hủy'
  }
  return labels[status] || status
}

import { useAuthStore } from '../../stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const handleLogout = () => {
  authStore.xoaXacThuc()
  router.push('/login')
}

onMounted(() => {
  fetchData()
})

</script>

<template>
  <div class="profile-page">
    <Navbar />
    
    <main class="main-content">
      <div v-if="isLoading" class="loading-overlay">
        <div class="spinner"></div>
        <p>Đang tải thông tin cá nhân...</p>
      </div>

      <div v-else-if="error" class="error-container">
        <i class="fas fa-exclamation-triangle"></i>
        <p>{{ error }}</p>
        <button @click="fetchData" class="btn btn-primary">Thử lại</button>
      </div>

      <div v-else-if="user" class="container profile-grid">
        <!-- Sidebar Navigation -->
        <aside class="profile-sidebar">
          <div class="user-card">
            <div class="avatar-wrapper">
              <img :src="'https://ui-avatars.com/api/?name=' + user.hoDem + '+' + user.ten + '&background=random'" :alt="user.ten" />
              <button class="edit-avatar"><i class="fas fa-camera"></i></button>
            </div>
            <div class="user-meta">
              <h2>{{ user.hoDem }} {{ user.ten }}</h2>
              <p class="student-id">{{ user.email }}</p>
              <span class="badge">{{ user.vaiTro }}</span>
            </div>
          </div>
          
          <nav class="side-nav">
            <button 
              :class="{ active: activeTab === 'overview' }" 
              @click="activeTab = 'overview'"
            >
              <span class="icon"><i class="fas fa-chart-pie"></i></span> Tổng quan
            </button>
            <button 
              :class="{ active: activeTab === 'books' }" 
              @click="activeTab = 'books'"
            >
              <span class="icon"><i class="fas fa-book"></i></span> Sách của tôi
            </button>
            <button 
              :class="{ active: activeTab === 'settings' }" 
              @click="activeTab = 'settings'"
            >
              <span class="icon"><i class="fas fa-user-cog"></i></span> Cài đặt tài khoản
            </button>
            <button class="logout-btn" @click="handleLogout">
              <span class="icon"><i class="fas fa-sign-out-alt"></i></span> Đăng xuất
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
                <div class="stat-icon yellow"><i class="fas fa-book-open"></i></div>
                <div class="stat-info">
                  <span class="stat-value">{{ loanHistory.length + currentLoans.length }}</span>
                  <span class="stat-label">Tổng sách đã mượn</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon cyan"><i class="fas fa-clock"></i></div>
                <div class="stat-info">
                  <span class="stat-value">{{ currentLoans.filter(l => l.trangThaiChiTietPhieuMuon === 'QUA_HAN').length }}</span>
                  <span class="stat-label">Sách quá hạn</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon purple"><i class="fas fa-calendar-alt"></i></div>
                <div class="stat-info">
                  <span class="stat-value">{{ user.trangThai }}</span>
                  <span class="stat-label">Trạng thái tài khoản</span>
                </div>
              </div>
            </div>
            
            <div class="section-card">
              <div class="card-header">
                <h3>Đang mượn gần đây</h3>
                <button @click="activeTab = 'books'" class="link-btn">Xem tất cả</button>
              </div>
              <div class="loans-list">
                <div v-if="currentLoans.length === 0" class="empty-state">
                  <p>Bạn không có sách nào đang mượn.</p>
                </div>
                <div v-for="loan in currentLoans.slice(0, 3)" :key="loan.maChiTietPhieuMuon" class="loan-item">
                  <img :src="loan.anhBiaUrl || 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=100'" :alt="loan.tenSach" />
                  <div class="loan-details">
                    <h4>{{ loan.tenSach }}</h4>
                    <p>Mã vạch: {{ loan.maVach }}</p>
                  </div>
                  <div class="loan-dates">
                    <span class="date-label">Hạn trả:</span>
                    <span class="date-value">{{ formatDateTime(loan.hanTraHienTai) }}</span>
                  </div>
                  <span class="status-tag" :class="loan.trangThaiChiTietPhieuMuon === 'QUA_HAN' ? 'warning' : 'info'">
                    {{ getStatusLabel(loan.trangThaiChiTietPhieuMuon) }}
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
                <p>Không có dữ liệu.</p>
              </div>
              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>Tên sách</th>
                    <th>Hạn trả</th>
                    <th>Số lần gia hạn</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="loan in currentLoans" :key="loan.maChiTietPhieuMuon">
                    <td>
                      <div class="book-cell">
                        <strong>{{ loan.tenSach }}</strong>
                        <span>Mã vạch: {{ loan.maVach }}</span>
                      </div>
                    </td>
                    <td>{{ formatDateTime(loan.hanTraHienTai) }}</td>
                    <td>{{ loan.soLanGiaHan }}</td>
                    <td>
                      <span class="status-tag" :class="loan.trangThaiChiTietPhieuMuon === 'QUA_HAN' ? 'warning' : 'info'">
                        {{ getStatusLabel(loan.trangThaiChiTietPhieuMuon) }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div class="section-card mt-xl">
              <h3>Lịch sử mượn trả</h3>
              <div v-if="loanHistory.length === 0" class="empty-state">
                <p>Chưa có lịch sử mượn trả.</p>
              </div>
              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>Tên sách</th>
                    <th>Hạn trả</th>
                    <th>Ngày trả thực tế</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in loanHistory" :key="item.maChiTietPhieuMuon">
                    <td><strong>{{ item.tenSach }}</strong></td>
                    <td>{{ formatDateTime(item.hanTraHienTai) }}</td>
                    <td>{{ formatDateTime(item.ngayTraThucTe) }}</td>
                    <td>
                      <span class="status-tag" :class="item.trangThaiChiTietPhieuMuon === 'DA_TRA' ? 'success' : 'danger'">
                        {{ getStatusLabel(item.trangThaiChiTietPhieuMuon) }}
                      </span>
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
              <form @submit.prevent="handleUpdateProfile" class="settings-form">
                <div class="form-row">
                  <div class="form-group">
                    <label>Họ đệm</label>
                    <input type="text" v-model="profileForm.hoDem" required />
                  </div>
                  <div class="form-group">
                    <label>Tên</label>
                    <input type="text" v-model="profileForm.ten" required />
                  </div>
                </div>
                <div class="form-group">
                  <label>Email (Không thể thay đổi)</label>
                  <input type="email" :value="user.email" readonly disabled />
                </div>
                <div class="form-group">
                  <label>Số điện thoại</label>
                  <input type="text" v-model="profileForm.soDienThoai" required />
                </div>
                <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
              </form>
            </div>
            
            <div class="section-card mt-xl">
              <h3>Đổi mật khẩu</h3>
              <form @submit.prevent="handleChangePassword" class="settings-form">
                <div class="form-group">
                  <label>Mật khẩu hiện tại</label>
                  <input type="password" v-model="passwordForm.matKhauCu" placeholder="********" required />
                </div>
                <div class="form-group">
                  <label>Mật khẩu mới</label>
                  <input type="password" v-model="passwordForm.matKhauMoi" placeholder="Nhập mật khẩu mới" required />
                </div>
                <div class="form-group">
                  <label>Xác nhận mật khẩu mới</label>
                  <input type="password" v-model="passwordForm.xacNhanMatKhau" placeholder="Nhập lại mật khẩu mới" required />
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
