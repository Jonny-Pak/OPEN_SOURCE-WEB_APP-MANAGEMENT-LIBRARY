<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { nhatKyService, type NhatKyHoatDong } from '@/services/nhatKyService'
import { useToast } from '@/composables/useToast'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'

const toast = useToast()
const dangTai = ref(false)
const keyword = ref('')
const vaiTro = ref('TAT_CA')
const trangHienTai = ref(0)
const tongSoTrang = ref(0)
const tongSoDong = ref(0)
const danhSachLog = ref<NhatKyHoatDong[]>([])

async function layDanhSachLog() {
  dangTai.value = true
  try {
    const res = await nhatKyService.getLogs(keyword.value, vaiTro.value, trangHienTai.value, 15)
    danhSachLog.value = res.content || []
    tongSoTrang.value = res.totalPages || 0
    tongSoDong.value = res.totalElements || 0
  } catch (err) {
    toast.loi('Không thể tải nhật ký hoạt động')
  } finally {
    dangTai.value = false
  }
}

function handleSearch() {
  trangHienTai.value = 0
  layDanhSachLog()
}

function handleRoleChange() {
  trangHienTai.value = 0
  layDanhSachLog()
}

function handlePageChange(p: number) {
  if (p < 0 || p >= tongSoTrang.value) return
  trangHienTai.value = p
  layDanhSachLog()
}

function formatNgay(dateStr: string) {
  if (!dateStr) return 'N/A'
  try {
    const d = new Date(dateStr)
    const padding = (n: number) => n.toString().padStart(2, '0')
    return `${padding(d.getDate())}/${padding(d.getMonth() + 1)}/${d.getFullYear()} ${padding(d.getHours())}:${padding(d.getMinutes())}:${padding(d.getSeconds())}`
  } catch {
    return dateStr
  }
}

function getRoleLabel(role: string) {
  switch (role) {
    case 'QUAN_TRI_VIEN': return 'Quản trị viên'
    case 'THU_THU': return 'Thủ thư'
    case 'DOC_GIA': return 'Độc giả'
    case 'KHACH': return 'Khách vãng lai'
    default: return role
  }
}

function getRoleClass(role: string) {
  switch (role) {
    case 'QUAN_TRI_VIEN': return 'badge--danger'
    case 'THU_THU': return 'badge--warning'
    case 'DOC_GIA': return 'badge--success'
    default: return 'badge--secondary'
  }
}

onMounted(() => {
  layDanhSachLog()
})
</script>

<template>
  <div class="nhat-ky-page">
    <div class="thanh-cong-cu">
      <div class="vung-tim-kiem">
        <font-awesome-icon icon="fa-solid fa-magnifying-glass" class="icon-tim-kiem" />
        <input 
          v-model="keyword"
          type="text" 
          class="input-tk"
          placeholder="Tìm tên người thực hiện, hành động, chi tiết..."
          @keyup.enter="handleSearch"
        />
      </div>
      <select v-model="vaiTro" @change="handleRoleChange" class="select-filter">
        <option value="TAT_CA">Tất cả vai trò</option>
        <option value="QUAN_TRI_VIEN">Quản trị viên</option>
        <option value="THU_THU">Thủ thư</option>
        <option value="DOC_GIA">Độc giả</option>
        <option value="KHACH">Khách vãng lai</option>
      </select>
      <button class="nut-lam-moi" @click="layDanhSachLog" :disabled="dangTai">
        <font-awesome-icon icon="fa-solid fa-rotate" :style="{ animation: dangTai ? 'spin 1s linear infinite' : 'none' }" />
        Làm mới
      </button>
    </div>

    <!-- Bảng hiển thị dữ liệu -->
    <div class="bang-container">
      <table class="bang">
        <thead>
          <tr>
            <th style="width: 180px;">Thời gian</th>
            <th style="width: 220px;">Người thực hiện</th>
            <th style="width: 150px;">Vai trò</th>
            <th style="width: 180px;">Hành động</th>
            <th>Nội dung chi tiết</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="dangTai">
            <td colspan="5" style="padding: 2rem;">
              <SkeletonLoader :rows="8" height="40px" />
            </td>
          </tr>
          <tr v-else-if="danhSachLog.length === 0">
            <td colspan="5" class="empty-state">
              <font-awesome-icon icon="fa-solid fa-inbox" class="empty-icon" />
              <p>Không có nhật ký hoạt động nào phù hợp với tìm kiếm.</p>
            </td>
          </tr>
          <tr v-else v-for="log in danhSachLog" :key="log.id" class="hang-log">
            <td class="cell-thoi-gian">
              <font-awesome-icon icon="fa-regular fa-clock" class="text-cyan mr-1" />
              {{ formatNgay(log.thoiGian) }}
            </td>
            <td class="cell-nguoi-dung">
              <span class="user-avatar-placeholder">
                {{ log.hoTen.charAt(0).toUpperCase() }}
              </span>
              <span class="user-name-text">{{ log.hoTen }}</span>
            </td>
            <td>
              <span class="badge" :class="getRoleClass(log.vaiTro)">
                {{ getRoleLabel(log.vaiTro) }}
              </span>
            </td>
            <td class="cell-hanh-dong">
              <span class="action-highlight">{{ log.hanhDong }}</span>
            </td>
            <td class="cell-chi-tiet">{{ log.chiTiet }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
    <div class="phan-trang" v-if="tongSoTrang > 1 && !dangTai">
      <button 
        class="nut-trang" 
        :disabled="trangHienTai === 0" 
        @click="handlePageChange(trangHienTai - 1)"
      >
        <font-awesome-icon icon="fa-solid fa-chevron-left" /> Trang trước
      </button>
      <span class="thong-tin-trang">Trang {{ trangHienTai + 1 }} trên {{ tongSoTrang }} (Tổng {{ tongSoDong }} dòng)</span>
      <button 
        class="nut-trang" 
        :disabled="trangHienTai === tongSoTrang - 1" 
        @click="handlePageChange(trangHienTai + 1)"
      >
        Trang sau <font-awesome-icon icon="fa-solid fa-chevron-right" />
      </button>
    </div>
  </div>
</template>

<style scoped>
.nhat-ky-page { animation: fadeInUp 0.4s ease; }

/* Toolbar */
.thanh-cong-cu { display: flex; gap: 0.75rem; margin-bottom: 1rem; flex-wrap: wrap; }
.vung-tim-kiem { position: relative; display: flex; align-items: center; flex: 1; min-width: 200px; }
.icon-tim-kiem { position: absolute; left: 1rem; color: var(--mau-chu-mo); pointer-events: none; }
.input-tk { width: 100%; padding: 0.65rem 1rem 0.65rem 2.5rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; color: var(--mau-chu); font-family: inherit; font-size: 0.875rem; outline: none; box-sizing: border-box; }
.input-tk:focus { border-color: var(--mau-chinh); box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.15); }
.select-filter { padding: 0.65rem 1rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; color: var(--mau-chu); font-family: inherit; cursor: pointer; font-size: 0.875rem; }
.select-filter option { background: #1a1a2e; color: #ffffff; }

.nut-lam-moi { display: flex; align-items: center; gap: 0.5rem; padding: 0.65rem 1rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; color: var(--mau-chu-mo); cursor: pointer; font-family: inherit; font-size: 0.875rem; transition: all 0.2s; white-space: nowrap; }
.nut-lam-moi:hover:not(:disabled) { background: rgba(6, 182, 212, 0.1); border-color: var(--mau-chinh); color: var(--mau-chinh); }

/* Table */
.bang-container { background: var(--glass-nen); border: 1px solid var(--glass-vien); border-radius: 12px; overflow-x: auto; padding: 1rem; }
.bang { width: 100%; border-collapse: collapse; text-align: left; font-size: 0.875rem; }
.bang th { padding: 0.75rem 1rem; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em; color: var(--mau-chu-mo); border-bottom: 1px solid rgba(255,255,255,0.08); font-weight: 600; }
.bang td { padding: 0.85rem 1rem; border-bottom: 1px solid rgba(255,255,255,0.04); color: var(--mau-chu); vertical-align: middle; }
.bang tr:last-child td { border-bottom: none; }
.bang tr:hover td { background: rgba(255,255,255,0.02); }

.cell-thoi-gian {
  font-family: monospace;
  font-size: 0.8rem;
  white-space: nowrap;
}

.cell-nguoi-dung {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.user-avatar-placeholder {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: var(--mau-chinh-nhe);
  color: var(--mau-chinh);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.75rem;
}

.user-name-text {
  font-weight: 500;
}

.action-highlight {
  font-weight: 600;
  color: var(--mau-chu);
}

.cell-chi-tiet {
  max-width: 400px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cell-chi-tiet:hover {
  white-space: normal;
  overflow: visible;
}

.badge {
  display: inline-block;
  padding: 0.25rem 0.6rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 600;
}

.badge--danger {
  background: rgba(239, 68, 68, 0.08);
  color: #dc2626;
  border: 1px solid rgba(239, 68, 68, 0.15);
}

.badge--warning {
  background: rgba(245, 158, 11, 0.08);
  color: #d97706;
  border: 1px solid rgba(245, 158, 11, 0.15);
}

.badge--success {
  background: rgba(16, 185, 129, 0.08);
  color: #059669;
  border: 1px solid rgba(16, 185, 129, 0.15);
}

.badge--secondary {
  background: rgba(107, 114, 128, 0.08);
  color: #4b5563;
  border: 1px solid rgba(107, 114, 128, 0.15);
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: var(--mau-chu-mo);
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.3;
}

.phan-trang {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
}

.nut-trang {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: var(--glass-nen);
  border: 1px solid var(--glass-vien);
  border-radius: 8px;
  color: var(--mau-chu);
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.2s;
}

.nut-trang:hover:not(:disabled) {
  border-color: var(--mau-chinh);
  color: var(--mau-chinh);
}

.nut-trang:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.thong-tin-trang {
  font-size: 0.825rem;
  color: var(--mau-chu-mo);
}

.mr-2 { margin-right: 0.5rem; }
.mr-1 { margin-right: 0.25rem; }
.text-cyan { color: var(--mau-chinh) !important; }

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
