<!--
  PhatView.vue — Quản lý phiếu phạt: tạo thủ công và cập nhật thanh toán.
-->
<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { phatService } from '@/services/phatService'
import { usePagination } from '@/composables/usePagination'
import { useToast } from '@/composables/useToast'
import type { PhieuPhat, TrangThaiPhieuPhat, LyDoPhat, PhuongThucThanhToan } from '@/types/phat'

const route = useRoute()
import ModalDialog from '@/components/admin/shared/ModalDialog.vue'
import Pagination from '@/components/admin/shared/Pagination.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import EmptyState from '@/components/admin/shared/EmptyState.vue'
import StatusBadge from '@/components/admin/shared/StatusBadge.vue'

const toast = useToast()
const phanTrang = usePagination()

const dangTai = ref(false)
const danhSach = ref<PhieuPhat[]>([])
const filterTrangThai = ref<TrangThaiPhieuPhat | ''>('CHUA_THANH_TOAN')
const dangXuLy = ref(false)

// Modal tạo phiếu phạt
const modalTao = ref(false)
const formTao = ref({ maChiTietPhieuMuon: '', lyDo: 'TRA_TRE' as LyDoPhat, soTienPhat: 0 })
const soTienHienThi = ref('')

// Modal thanh toán
const modalThanhToan = ref<PhieuPhat | null>(null)
const phuongThucTT = ref<PhuongThucThanhToan>('TIEN_MAT')

const LY_DO_LABEL: Record<LyDoPhat, string> = { TRA_TRE: 'Trả trễ', HU_HONG: 'Hư hỏng', MAT_SACH: 'Mất sách' }

function formatTien(so: number): string { return new Intl.NumberFormat('vi-VN').format(so) + ' ₫' }
function formatNgay(s: string) { return new Date(s).toLocaleDateString('vi-VN') }

function onSoTienNhap(e: Event) {
  const raw = (e.target as HTMLInputElement).value.replace(/\D/g, '')
  formTao.value.soTienPhat = parseInt(raw) || 0
  soTienHienThi.value = raw ? parseInt(raw).toLocaleString('vi-VN') : ''
}

async function taiDanhSach() {
  dangTai.value = true
  try {
    const rs = await phatService.danhSach(phanTrang.trangHienTai.value, phanTrang.kichThuocTrang.value)
    danhSach.value = rs.content
    phanTrang.capNhatTong(rs.totalElements)
  } catch { toast.loi('Không thể tải danh sách phiếu phạt') }
  finally { dangTai.value = false }
}

async function taoPhieuPhat() {
  if (!formTao.value.maChiTietPhieuMuon.trim()) return toast.canhBao('Vui lòng nhập mã chi tiết phiếu mượn (UUID)')
  if (formTao.value.soTienPhat <= 0) return toast.canhBao('Số tiền phạt phải lớn hơn 0')
  dangXuLy.value = true
  try {
    const payload = {
      maChiTietPhieuMuon: formTao.value.maChiTietPhieuMuon,
      soTienPhat: formTao.value.soTienPhat,
      lyDoPhat: formTao.value.lyDo
    }
    await phatService.taoCai(payload as any)
    toast.thanhCong('Tạo phiếu phạt thành công')
    modalTao.value = false
    taiDanhSach()
  } catch (err: any) { toast.loi(err?.message || 'Tạo phiếu phạt thất bại (Kiểm tra lại UUID)') } finally { dangXuLy.value = false }
}

async function xacNhanThanhToan() {
  if (!modalThanhToan.value) return
  dangXuLy.value = true
  try {
    await phatService.thanhToan(String(modalThanhToan.value.maPhieuPhat), { phuongThucThanhToan: phuongThucTT.value })
    toast.thanhCong('Đã cập nhật thanh toán')
    modalThanhToan.value = null
    taiDanhSach()
  } catch { toast.loi('Thanh toán thất bại') } finally { dangXuLy.value = false }
}

watch(() => phanTrang.trangHienTai.value, taiDanhSach)
onMounted(() => {
  taiDanhSach()
  const maChiTiet = route.query.maChiTietPhieuMuon as string
  if (maChiTiet) {
    modalTao.value = true
    formTao.value = {
      maChiTietPhieuMuon: maChiTiet,
      lyDo: 'TRA_TRE',
      soTienPhat: 50000
    }
    soTienHienThi.value = '50.000'
  }
})
</script>

<template>
  <div class="phat-view">
    <div class="thanh-cong-cu">
      <select v-model="filterTrangThai" class="select-filter">
        <option value="">Tất cả</option>
        <option value="CHUA_THANH_TOAN">Chưa thanh toán</option>
        <option value="DA_THANH_TOAN">Đã thanh toán</option>
      </select>
      <button class="nut-them" @click="() => { modalTao = true; formTao = { maChiTietPhieuMuon: '', lyDo: 'TRA_TRE', soTienPhat: 0 }; soTienHienThi = '' }">
        + Tạo phiếu phạt
      </button>
    </div>

    <div class="bang-container">
      <SkeletonLoader v-if="dangTai" :rows="6" height="52px" />
      <template v-else>
        <EmptyState v-if="danhSach.length === 0" thong-diep="Không có phiếu phạt nào" />
        <table v-else class="bang">
          <thead>
            <tr>
              <th>Độc giả</th><th>Mã phiếu mượn</th><th>Lý do</th>
              <th>Số tiền</th><th>Ngày tạo</th><th>Trạng thái</th><th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in danhSach" :key="item.maPhieuPhat">
              <td>
                <div class="ten-nguoi">{{ item.nguoiDung?.hoDem || '' }} {{ item.nguoiDung?.ten || '' }}</div>
                <div class="email-mo">{{ item.nguoiDung?.email || '' }}</div>
              </td>
              <td><code class="ma-phieu">#{{ item.maPhieuMuon }}</code></td>
              <td>{{ LY_DO_LABEL[item.lyDo] || item.lyDo || '—' }}</td>
              <td class="so-tien">{{ formatTien(item.soTienPhat) }}</td>
              <td>{{ formatNgay(item.ngayTao) }}</td>
              <td>
                <StatusBadge
                  :nhan-hien="item.trangThai === 'CHUA_THANH_TOAN' ? 'Chưa thanh toán' : 'Đã thanh toán'"
                  :loai="item.trangThai === 'CHUA_THANH_TOAN' ? 'do' : 'xanh'"
                />
              </td>
              <td>
                <button
                  v-if="item.trangThai === 'CHUA_THANH_TOAN'"
                  class="nut-thanh-toan"
                  @click="modalThanhToan = item; phuongThucTT = 'TIEN_MAT'"
                >
                  💳 Thanh toán
                </button>
                <span v-else class="text-mo">—</span>
              </td>
            </tr>
          </tbody>
        </table>
        <Pagination
          :trang-hien-tai="phanTrang.trangHienThiChoNguoiDung.value"
          :tong-trang="phanTrang.tongTrang.value"
          :tong-phan-tu="phanTrang.tongPhanTu.value"
          :kich-thuoc-trang="10"
          @doi-trang="phanTrang.denTrang"
        />
      </template>
    </div>

    <!-- Modal tạo phiếu phạt -->
    <ModalDialog :dang-mo="modalTao" tieu-de="Tạo phiếu phạt thủ công" @dong="modalTao = false">
      <div class="form-modal">
        <div class="form-group">
          <label>Mã chi tiết phiếu mượn (UUID) *</label>
          <input v-model="formTao.maChiTietPhieuMuon" type="text" class="form-input" placeholder="VD: 550e8400-e29b-41d4-a716-446655440000" />
        </div>
        <div class="form-group">
          <label>Lý do phạt *</label>
          <select v-model="formTao.lyDo" class="form-input">
            <option value="TRA_TRE">Trả trễ</option>
            <option value="HU_HONG">Hư hỏng sách</option>
            <option value="MAT_SACH">Mất sách</option>
          </select>
        </div>
        <div class="form-group">
          <label>Số tiền phạt (VND) *</label>
          <input
            :value="soTienHienThi"
            class="form-input"
            placeholder="VD: 50,000"
            @input="onSoTienNhap"
          />
          <span v-if="formTao.soTienPhat > 0" class="ghi-chu-tien">= {{ formatTien(formTao.soTienPhat) }}</span>
        </div>
      </div>
      <template #footer>
        <button class="nut-huy-modal" @click="modalTao = false">Hủy</button>
        <button class="nut-luu-modal" :disabled="dangXuLy" @click="taoPhieuPhat">
          {{ dangXuLy ? 'Đang tạo...' : 'Tạo phiếu phạt' }}
        </button>
      </template>
    </ModalDialog>

    <!-- Modal thanh toán -->
    <ModalDialog :dang-mo="modalThanhToan !== null" tieu-de="Xác nhận thanh toán" @dong="modalThanhToan = null">
      <div class="form-modal">
        <div class="thong-tin-phat">
          <div class="info-phat"><span>Độc giả:</span> <strong>{{ modalThanhToan?.nguoiDung?.hoDem || '' }} {{ modalThanhToan?.nguoiDung?.ten || '' }}</strong></div>
          <div class="info-phat"><span>Lý do:</span> <strong>{{ modalThanhToan ? LY_DO_LABEL[modalThanhToan.lyDo] : '' }}</strong></div>
          <div class="info-phat so-tien-lon"><span>Số tiền:</span> <strong>{{ modalThanhToan ? formatTien(modalThanhToan.soTienPhat) : '' }}</strong></div>
        </div>
        <div class="form-group">
          <label>Phương thức thanh toán *</label>
          <div class="chon-phuong-thuc">
            <label class="phuong-thuc-item" :class="{ active: phuongThucTT === 'TIEN_MAT' }">
              <input type="radio" v-model="phuongThucTT" value="TIEN_MAT" /> 💵 Tiền mặt
            </label>
            <label class="phuong-thuc-item" :class="{ active: phuongThucTT === 'CHUYEN_KHOAN' }">
              <input type="radio" v-model="phuongThucTT" value="CHUYEN_KHOAN" /> 🏦 Chuyển khoản
            </label>
          </div>
        </div>
      </div>
      <template #footer>
        <button class="nut-huy-modal" @click="modalThanhToan = null">Hủy</button>
        <button class="nut-luu-modal" :disabled="dangXuLy" @click="xacNhanThanhToan">
          {{ dangXuLy ? 'Đang xử lý...' : '✅ Xác nhận đã thanh toán' }}
        </button>
      </template>
    </ModalDialog>
  </div>
</template>

<style scoped>
.phat-view { animation:fadeInUp 0.4s ease; }
.thanh-cong-cu { display:flex; gap:0.75rem; margin-bottom:1rem; }
.select-filter { padding:0.65rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; cursor:pointer; }
.select-filter option { background:#1a1a2e; color:#ffffff; }
.nut-them { padding:0.65rem 1.25rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-size:0.875rem; font-weight:600; margin-left:auto; }
.bang-container { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; overflow:hidden; padding:1rem; }
.bang { width:100%; border-collapse:collapse; }
.bang th { padding:0.75rem 1rem; text-align:left; font-size:0.75rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--mau-chu-mo); border-bottom:1px solid rgba(255,255,255,0.08); }
.bang td { padding:0.875rem 1rem; font-size:0.875rem; border-bottom:1px solid rgba(255,255,255,0.04); vertical-align:middle; }
.bang tr:last-child td { border-bottom:none; }
.bang tr:hover td { background:rgba(255,255,255,0.02); }
.ten-nguoi { font-weight:600; }
.email-mo { font-size:0.775rem; color:var(--mau-chu-mo); }
.ma-phieu { font-size:0.775rem; background:rgba(255,255,255,0.06); padding:0.2rem 0.5rem; border-radius:4px; }
.so-tien { font-weight:700; color:#ffd43b; }
.nut-thanh-toan { padding:0.35rem 0.75rem; background:rgba(81,207,102,0.15); border:1px solid rgba(81,207,102,0.3); border-radius:6px; color:#51cf66; cursor:pointer; font-size:0.8rem; transition:all 0.2s; }
.nut-thanh-toan:hover { background:rgba(81,207,102,0.25); }
.text-mo { color:var(--mau-chu-rat-mo); }

/* Form modal */
.form-modal { display:flex; flex-direction:column; gap:1rem; }
.form-group { display:flex; flex-direction:column; gap:0.375rem; }
.form-group label { font-size:0.825rem; font-weight:600; color:var(--mau-chu); }
.form-input { padding:0.7rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.875rem; outline:none; }
.form-input:focus { border-color:var(--mau-chinh); }
.form-input option { background:#1a1a2e; color:#ffffff; }
.ghi-chu-tien { font-size:0.8rem; color:var(--mau-chinh); }

/* Thông tin phạt */
.thong-tin-phat { padding:1rem; background:rgba(255,212,59,0.06); border:1px solid rgba(255,212,59,0.2); border-radius:10px; margin-bottom:1rem; }
.info-phat { font-size:0.875rem; margin-bottom:0.375rem; }
.info-phat span { color:var(--mau-chu-mo); }
.so-tien-lon strong { color:#ffd43b; font-size:1.1rem; }

/* Chọn phương thức */
.chon-phuong-thuc { display:flex; gap:0.75rem; }
.phuong-thuc-item { display:flex; align-items:center; gap:0.5rem; cursor:pointer; padding:0.6rem 1rem; border-radius:8px; border:1px solid rgba(255,255,255,0.1); font-size:0.875rem; flex:1; justify-content:center; transition:all 0.2s; }
.phuong-thuc-item.active { border-color:var(--mau-chinh); background:rgba(6,182,212,0.08); color:var(--mau-chinh); }
.phuong-thuc-item input { display:none; }

.nut-huy-modal { padding:0.65rem 1.25rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; }
.nut-luu-modal { padding:0.65rem 1.5rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-weight:600; }
.nut-luu-modal:disabled { opacity:0.6; cursor:not-allowed; }
</style>
