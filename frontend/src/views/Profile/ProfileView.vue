<script setup lang="ts">
import { ref } from 'vue'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'

const activeTab = ref('overview')

const user = ref({
  name: 'Nguyễn Văn A',
  email: 'student.a@school.edu.vn',
  studentId: 'SV2024001',
  class: 'Kỹ thuật Phần mềm 01',
  avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200',
  joinDate: '15/01/2024',
  borrowedCount: 12,
  returningSoon: 2
})

const currentLoans = [
  {
    id: 1,
    title: 'Đắc Nhân Tâm',
    author: 'Dale Carnegie',
    borrowDate: '01/05/2024',
    dueDate: '15/05/2024',
    status: 'Đang mượn',
    image: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=100'
  },
  {
    id: 3,
    title: 'Clean Code',
    author: 'Robert C. Martin',
    borrowDate: '05/05/2024',
    dueDate: '19/05/2024',
    status: 'Sắp hết hạn',
    image: 'https://images.unsplash.com/photo-1516116216624-53e697fedbea?auto=format&fit=crop&q=80&w=100'
  }
]

const history = [
  {
    id: 2,
    title: 'Nhà Giả Kim',
    author: 'Paulo Coelho',
    borrowDate: '10/04/2024',
    returnDate: '24/04/2024',
    status: 'Đã trả'
  },
  {
    id: 4,
    title: 'Sapiens',
    author: 'Yuval Noah Harari',
    borrowDate: '01/03/2024',
    returnDate: '15/03/2024',
    status: 'Đã trả'
  }
]

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
              <button class="edit-avatar"><i class="fas fa-camera"></i></button>
            </div>
            <div class="user-meta">
              <h2>{{ user.name }}</h2>
              <p class="student-id">{{ user.studentId }}</p>
              <span class="badge">Học sinh</span>
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
            <button class="logout-btn">
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
                  <span class="stat-value">{{ user.borrowedCount }}</span>
                  <span class="stat-label">Tổng sách đã mượn</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon cyan"><i class="fas fa-clock"></i></div>
                <div class="stat-info">
                  <span class="stat-value">{{ user.returningSoon }}</span>
                  <span class="stat-label">Sách sắp hết hạn</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon purple"><i class="fas fa-calendar-alt"></i></div>
                <div class="stat-info">
                  <span class="stat-value">{{ user.joinDate }}</span>
                  <span class="stat-label">Ngày tham gia</span>
                </div>
              </div>
            </div>
            
            <div class="section-card">
              <div class="card-header">
                <h3>Đang mượn gần đây</h3>
                <button @click="activeTab = 'books'" class="link-btn">Xem tất cả</button>
              </div>
              <div class="loans-list">
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
                  <span class="status-tag" :class="loan.status === 'Sắp hết hạn' ? 'warning' : 'info'">
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
              <table class="data-table">
                <thead>
                  <tr>
                    <th>Tên sách</th>
                    <th>Ngày mượn</th>
                    <th>Hạn trả</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
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
                      <span class="status-tag" :class="loan.status === 'Sắp hết hạn' ? 'warning' : 'info'">
                        {{ loan.status }}
                      </span>
                    </td>
                    <td>
                      <button class="btn btn-outline btn-sm">Gia hạn</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div class="section-card mt-xl">
              <h3>Lịch sử mượn trả</h3>
              <table class="data-table">
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
              <form class="settings-form">
                <div class="form-row">
                  <div class="form-group">
                    <label>Họ và tên</label>
                    <input type="text" :value="user.name" readonly />
                  </div>
                  <div class="form-group">
                    <label>Mã số học sinh</label>
                    <input type="text" :value="user.studentId" readonly />
                  </div>
                </div>
                <div class="form-group">
                  <label>Email</label>
                  <input type="email" :value="user.email" />
                </div>
                <div class="form-group">
                  <label>Lớp</label>
                  <input type="text" :value="user.class" readonly />
                </div>
                <button type="button" class="btn btn-primary">Lưu thay đổi</button>
              </form>
            </div>
            
            <div class="section-card mt-xl">
              <h3>Đổi mật khẩu</h3>
              <form class="settings-form">
                <div class="form-group">
                  <label>Mật khẩu hiện tại</label>
                  <input type="password" placeholder="********" />
                </div>
                <div class="form-group">
                  <label>Mật khẩu mới</label>
                  <input type="password" placeholder="Nhập mật khẩu mới" />
                </div>
                <div class="form-group">
                  <label>Xác nhận mật khẩu mới</label>
                  <input type="password" placeholder="Nhập lại mật khẩu mới" />
                </div>
                <button type="button" class="btn btn-outline">Cập nhật mật khẩu</button>
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
