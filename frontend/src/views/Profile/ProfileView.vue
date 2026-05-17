<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import apiClient from '@/services/apiClient'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const authStore = useAuthStore()
const router = useRouter()

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
  returningSoon: 0
})

const handleLogout = () => {
  authStore.xoaXacThuc()
  router.push('/login')
}

const currentLoans = ref<any[]>([])
const history = ref<any[]>([])

const matKhauCu = ref('')
const matKhauMoi = ref('')
const xacNhanMatKhau = ref('')

const showMatKhauCu = ref(false)
const showMatKhauMoi = ref(false)
const showXacNhanMatKhau = ref(false)

const passMessage = ref('')
const passError = ref(false)

const loadProfileAndHistory = async () => {
  loading.value = true
  try {
    // 1. Fetch live user details
    const profile: any = await apiClient.get('/api/v1/nguoi-dung/me')
    if (profile) {
      user.value.name = `${profile.hoDem} ${profile.ten}`
      user.value.email = profile.email
      user.value.studentId = profile.maNguoiDung ? profile.maNguoiDung.substring(0, 8).toUpperCase() : 'SV2024001'
      if (profile.ngayTao) {
        user.value.joinDate = new Date(profile.ngayTao).toLocaleDateString('vi-VN')
      }
    }

    // 2. Fetch borrowing history
    const response: any = await apiClient.get('/api/v1/muon-sach/cua-toi')
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
                    ct.trangThaiChiTietPhieuMuon === 'DA_TRA_TRE' ? 'Đã trả trễ' : 'Khác',
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
      user.value.returningSoon = activeList.filter(l => l.status === 'Quá hạn').length
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
              <button class="edit-avatar"><font-awesome-icon icon="fa-solid fa-camera" /></button>
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
          
          <!-- Settings Tab -->
          <div v-if="activeTab === 'settings'" class="tab-content">
            <h1 class="tab-title">Cài đặt tài khoản</h1>
            
            <div class="section-card">
              <h3>Thông tin cá nhân</h3>
              <form class="settings-form" @submit.prevent>
                <div class="form-row">
                  <div class="form-group">
                    <label>Họ và tên</label>
                    <input type="text" :value="user.name" readonly />
                  </div>
                  <div class="form-group">
                    <label>Mã độc giả</label>
                    <input type="text" :value="user.studentId" readonly />
                  </div>
                </div>
                <div class="form-group">
                  <label>Email</label>
                  <input type="email" :value="user.email" readonly />
                </div>
                <div class="form-group">
                  <label>Lớp</label>
                  <input type="text" :value="user.class" readonly />
                </div>
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
