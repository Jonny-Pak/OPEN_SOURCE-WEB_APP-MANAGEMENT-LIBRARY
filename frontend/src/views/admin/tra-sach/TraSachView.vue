<!--
  TraSachView.vue — 2 tabs: Trả sách và Duyệt gia hạn.
-->
<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import { traSachService, giaHanService } from '@/services/traSachService'
import { muonSachService } from '@/services/muonSachService'
import { usePagination } from '@/composables/usePagination'
import { useToast } from '@/composables/useToast'
import type { PhieuMuon, TraSachChiTietItem } from '@/types/muonsach'
import type { LichSuGiaHan, TrangThaiGiaHan } from '@/types/giahan'
import type { TinhTrangVatLy } from '@/types/sach'
import ModalDialog from '@/components/admin/shared/ModalDialog.vue'
import Pagination from '@/components/admin/shared/Pagination.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import EmptyState from '@/components/admin/shared/EmptyState.vue'
import StatusBadge from '@/components/admin/shared/StatusBadge.vue'

const toast = useToast()
const tabHienTai = ref<'tra' | 'giahan'>('tra')

// ===== TAB 1: TRẢ SÁCH =====
const tuKhoaPhieu = ref('')
const phieuDaTim = ref<PhieuMuon | null>(null)
const dangTimPhieu = ref(false)
const tinhTrangTra = ref<Record<number, TinhTrangVatLy>>({})
const dangTra = ref(false)

function formatNgay(s: string) { return new Date(s).toLocaleDateString('vi-VN') }

function laQuaHan(hanTra: string): boolean {
  return new Date(hanTra) < new Date()
}

async function timPhieuMuon() {
  if (!tuKhoaPhieu.value.trim()) return
  dangTimPhieu.value = true
  phieuDaTim.value = null
  tinhTrangTra.value = {}
  try {
    const id = parseInt(tuKhoaPhieu.value)
    if (!isNaN(id)) {
      const phieu = await muonSachService.layChiTiet(id)
      phieuDaTim.value = phieu
      // Khởi tạo tình trạng trả mặc định là TOT
      phieu.chiTietList.forEach(ct => {
        tinhTrangTra.value[ct.maChiTiet] = 'TOT'
      })
    } else { toast.canhBao('Vui lòng nhập mã phiếu mượn (số)') }
  } catch { toast.loi('Không tìm thấy phiếu mượn') }
  finally { dangTimPhieu.value = false }
}

async function xacNhanTra() {
  if (!phieuDaTim.value) return
  const chiTietList: TraSachChiTietItem[] = phieuDaTim.value.chiTietList.map(ct => ({
    chiTietId: ct.maChiTiet,
    tinhTrangTraSach: tinhTrangTra.value[ct.maChiTiet] ?? 'TOT',
  }))
  dangTra.value = true
  try {
    await traSachService.traSach(phieuDaTim.value.maPhieuMuon, { chiTietList })
    toast.thanhCong('Trả sách thành công! Hệ thống sẽ tự sinh phiếu phạt nếu cần.')
    phieuDaTim.value = null
    tuKhoaPhieu.value = ''
  } catch { toast.loi('Trả sách thất bại') }
  finally { dangTra.value = false }
}

// ===== TAB 2: GIA HẠN =====
const phanTrang = usePagination()
const dangTaiGH = ref(false)
const danhSachGH = ref<LichSuGiaHan[]>([])
const filterGH = ref<TrangThaiGiaHan | ''>('CHO_DUYET')
const dangXuLyGH = ref(false)
const modalDuyetGH = ref<LichSuGiaHan | null>(null)
const hanTraMoi = ref('')
const modalTuChoiGH = ref<LichSuGiaHan | null>(null)
const lyDoTuChoi = ref('')

const GH_MAP: Record<TrangThaiGiaHan, { nhan: string; mau: 'vang' | 'xanh' | 'do' }> = {
  CHO_DUYET: { nhan: 'Chờ duyệt', mau: 'vang' },
  DA_DUYET: { nhan: 'Đã duyệt', mau: 'xanh' },
  TU_CHOI: { nhan: 'Từ chối', mau: 'do' },
}

const danhSachGHLoc = computed(() => {
  if (!filterGH.value) return danhSachGH.value
  return danhSachGH.value.filter((item) => item.trangThai === filterGH.value)
})

const danhSachGHHienThi = computed(() => {
  const start = phanTrang.trangHienTai.value * phanTrang.kichThuocTrang.value
  const end = start + phanTrang.kichThuocTrang.value
  return danhSachGHLoc.value.slice(start, end)
})

async function taiGiaHan() {
  dangTaiGH.value = true
  try {
    danhSachGH.value = await giaHanService.danhSach()
  } catch { toast.loi('Không thể tải danh sách gia hạn') }
  finally { dangTaiGH.value = false }
}

async function duyetGiaHan() {
  if (!modalDuyetGH.value || !hanTraMoi.value) return toast.canhBao('Vui lòng chọn ngày hạn trả mới')
  dangXuLyGH.value = true
  try {
    await giaHanService.duyet(modalDuyetGH.value.maGiaHan, { hanTraMoi: hanTraMoi.value })
    toast.thanhCong('Đã duyệt gia hạn')
    modalDuyetGH.value = null
    taiGiaHan()
  } catch { toast.loi('Duyệt thất bại') } finally { dangXuLyGH.value = false }
}

async function tuChoiGiaHan() {
  if (!modalTuChoiGH.value || !lyDoTuChoi.value.trim()) return toast.canhBao('Vui lòng nhập lý do từ chối')
  dangXuLyGH.value = true
  try {
    await giaHanService.tuChoi(modalTuChoiGH.value.maGiaHan, { lyDo: lyDoTuChoi.value })
    toast.thanhCong('Đã từ chối gia hạn')
    modalTuChoiGH.value = null
    taiGiaHan()
  } catch { toast.loi('Từ chối thất bại') } finally { dangXuLyGH.value = false }
}

watch([filterGH, () => phanTrang.trangHienTai.value], () => {
  phanTrang.datLaiTrang()
})
watch(danhSachGHLoc, (val) => {
  phanTrang.capNhatTong(val.length)
}, { immediate: true })
watch(tabHienTai, (tab) => { if (tab === 'giahan') taiGiaHan() })
onMounted(() => { /* Tab 1 mặc định, không cần load */ })
</script>

<template>
  <div class="tra-sach">
    <!-- Tabs -->
    <div class="tabs">
      <button class="tab" :class="{ 'tab--active': tabHienTai === 'tra' }" @click="tabHienTai = 'tra'">📥 Trả sách</button>
      <button class="tab" :class="{ 'tab--active': tabHienTai === 'giahan' }" @click="tabHienTai = 'giahan'">🔄 Duyệt gia hạn</button>
    </div>

    <!-- ===== TAB TRẢ SÁCH ===== -->
    <div v-if="tabHienTai === 'tra'" class="noi-dung-tab">
      <div class="tim-phieu">
        <div class="form-group">
          <label>Nhập mã phiếu mượn để tìm kiếm</label>
          <div class="input-ket-hop">
            <input v-model="tuKhoaPhieu" class="form-input" placeholder="VD: 42" @keyup.enter="timPhieuMuon" />
            <button class="nut-tim" :disabled="dangTimPhieu" @click="timPhieuMuon">
              {{ dangTimPhieu ? '...' : '🔍 Tìm' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Kết quả phiếu mượn -->
      <div v-if="phieuDaTim" class="ket-qua-phieu">
        <!-- Thông tin độc giả -->
        <div class="thong-tin-nguoi-muon">
          <div class="info-item"><span class="label">Độc giả:</span> <strong>{{ phieuDaTim.nguoiDung.hoDem }} {{ phieuDaTim.nguoiDung.ten }}</strong></div>
          <div class="info-item"><span class="label">Email:</span> {{ phieuDaTim.nguoiDung.email }}</div>
          <div class="info-item"><span class="label">Ngày mượn:</span> {{ formatNgay(phieuDaTim.ngayMuon) }}</div>
          <div class="info-item"><span class="label">Hạn trả:</span>
            <span :class="{ 'text-do': laQuaHan(phieuDaTim.hanTra) }">{{ formatNgay(phieuDaTim.hanTra) }}</span>
          </div>
        </div>

        <!-- Danh sách sách đang mượn -->
        <h4 class="tieu-de-danh-sach">Danh sách sách cần trả ({{ phieuDaTim.chiTietList.length }} cuốn)</h4>
        <div class="danh-sach-tra">
          <div v-for="ct in phieuDaTim.chiTietList" :key="ct.maChiTiet" class="item-tra" :class="{ 'item-tra--qua-han': laQuaHan(ct.hanTra) }">
            <div class="item-tra-info">
              <div class="ten-sach-tra">{{ ct.tenSach }}</div>
              <div class="ma-vach-tra">
                <code>{{ ct.maBarcodeVatLy }}</code>
                <span v-if="laQuaHan(ct.hanTra)" class="tag-qua-han">⚠️ Quá hạn ({{ formatNgay(ct.hanTra) }})</span>
              </div>
            </div>
            <div class="item-tra-tinh-trang">
              <label style="font-size:0.8rem;color:var(--mau-chu-mo)">Tình trạng trả:</label>
              <select v-model="tinhTrangTra[ct.maChiTiet]" class="select-tinh-trang">
                <option value="TOT">Tốt</option>
                <option value="HU_HONG">Hư hỏng</option>
                <option value="MAT">Mất</option>
              </select>
            </div>
          </div>
        </div>

        <div class="nhom-nut">
          <button class="nut-huy" @click="phieuDaTim = null">Hủy</button>
          <button class="nut-chinh" :disabled="dangTra" @click="xacNhanTra">
            {{ dangTra ? 'Đang xử lý...' : '✅ Xác nhận trả sách' }}
          </button>
        </div>
      </div>

      <div v-else-if="!dangTimPhieu" class="trang-thai-trong">
        <EmptyState thong-diep="Nhập mã phiếu mượn để bắt đầu" mo-ta="Hệ thống sẽ hiển thị thông tin và danh sách sách cần trả" />
      </div>
    </div>

    <!-- ===== TAB GIA HẠN ===== -->
    <div v-else class="noi-dung-tab">
      <div class="thanh-filter">
        <select v-model="filterGH" class="select-filter">
          <option value="">Tất cả</option>
          <option value="CHO_DUYET">Chờ duyệt</option>
          <option value="DA_DUYET">Đã duyệt</option>
          <option value="TU_CHOI">Từ chối</option>
        </select>
      </div>

      <div class="bang-container">
        <SkeletonLoader v-if="dangTaiGH" :rows="5" height="52px" />
        <template v-else>
          <EmptyState v-if="danhSachGH.length === 0" thong-diep="Không có yêu cầu gia hạn nào" />
          <table v-else class="bang">
            <thead>
              <tr><th>Độc giả</th><th>Sách</th><th>Hạn hiện tại</th><th>Xin gia hạn đến</th><th>Trạng thái</th><th>Hành động</th></tr>
            </thead>
            <tbody>
              <tr v-for="item in danhSachGHHienThi" :key="item.maGiaHan">
                <td>{{ item.nguoiDung.hoDem }} {{ item.nguoiDung.ten }}</td>
                <td><div style="font-size:0.85rem">{{ item.tenSach }}</div><code style="font-size:0.75rem;color:var(--mau-chu-mo)">{{ item.maBarcodeVatLy }}</code></td>
                <td :class="{ 'text-do': laQuaHan(item.hanTraHienTai) }">{{ formatNgay(item.hanTraHienTai) }}</td>
                <td>{{ formatNgay(item.hanTraXinGiaHan) }}</td>
                <td><StatusBadge :nhan-hien="GH_MAP[item.trangThai].nhan" :loai="GH_MAP[item.trangThai].mau" /></td>
                <td>
                  <div v-if="item.trangThai === 'CHO_DUYET'" class="hanh-dong">
                    <button class="nut-duyet" @click="modalDuyetGH = item; hanTraMoi = ''">✅ Duyệt</button>
                    <button class="nut-tuchoi" @click="modalTuChoiGH = item; lyDoTuChoi = ''">❌ Từ chối</button>
                  </div>
                  <span v-else class="text-mo">—</span>
                </td>
              </tr>
            </tbody>
          </table>
          <Pagination :trang-hien-tai="phanTrang.trangHienThiChoNguoiDung.value" :tong-trang="phanTrang.tongTrang.value" :tong-phan-tu="phanTrang.tongPhanTu.value" :kich-thuoc-trang="phanTrang.kichThuocTrang.value" @doi-trang="phanTrang.denTrang" />
        </template>
      </div>
    </div>

    <!-- Modal duyệt gia hạn -->
    <ModalDialog :dang-mo="modalDuyetGH !== null" tieu-de="Duyệt gia hạn" @dong="modalDuyetGH = null">
      <div class="form-group">
        <p style="color:var(--mau-chu-mo);font-size:0.875rem;margin-bottom:1rem">Duyệt gia hạn cho sách <strong>{{ modalDuyetGH?.tenSach }}</strong></p>
        <label>Ngày hạn trả mới *</label>
        <input v-model="hanTraMoi" type="date" class="form-input" :min="modalDuyetGH?.hanTraXinGiaHan" />
      </div>
      <template #footer>
        <button class="nut-huy-modal" @click="modalDuyetGH = null">Hủy</button>
        <button class="nut-luu-modal" :disabled="dangXuLyGH" @click="duyetGiaHan">{{ dangXuLyGH ? 'Đang xử lý...' : 'Xác nhận duyệt' }}</button>
      </template>
    </ModalDialog>

    <!-- Modal từ chối -->
    <ModalDialog :dang-mo="modalTuChoiGH !== null" tieu-de="Từ chối gia hạn" @dong="modalTuChoiGH = null">
      <div class="form-group">
        <p style="color:var(--mau-chu-mo);font-size:0.875rem;margin-bottom:1rem">Từ chối gia hạn sách <strong>{{ modalTuChoiGH?.tenSach }}</strong></p>
        <label>Lý do từ chối *</label>
        <textarea v-model="lyDoTuChoi" class="form-input" style="min-height:80px;resize:vertical" placeholder="Nhập lý do..."></textarea>
      </div>
      <template #footer>
        <button class="nut-huy-modal" @click="modalTuChoiGH = null">Hủy</button>
        <button class="nut-luu-modal nut-do" :disabled="dangXuLyGH" @click="tuChoiGiaHan">{{ dangXuLyGH ? 'Đang xử lý...' : 'Từ chối' }}</button>
      </template>
    </ModalDialog>
  </div>
</template>

<style scoped>
.tra-sach { animation:fadeInUp 0.4s ease; }
.tabs { display:flex; gap:0.5rem; margin-bottom:1.25rem; border-bottom:1px solid rgba(255,255,255,0.08); }
.tab { padding:0.65rem 1.25rem; background:none; border:none; border-bottom:2px solid transparent; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; font-size:0.9rem; transition:all 0.2s; }
.tab:hover { color:var(--mau-chu); }
.tab--active { color:var(--mau-chinh); border-bottom-color:var(--mau-chinh); font-weight:600; }
.noi-dung-tab { animation:fadeInUp 0.3s ease; }

/* Tìm phiếu */
.tim-phieu { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; padding:1.5rem; margin-bottom:1.25rem; }
.form-group { display:flex; flex-direction:column; gap:0.375rem; }
.form-group label { font-size:0.825rem; font-weight:600; color:var(--mau-chu); }
.input-ket-hop { display:flex; gap:0.5rem; }
.form-input { padding:0.75rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.9rem; outline:none; flex:1; }
.form-input:focus { border-color:var(--mau-chinh); }
.nut-tim { padding:0.75rem 1.25rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-weight:600; white-space:nowrap; }
.nut-tim:disabled { opacity:0.6; cursor:not-allowed; }

/* Kết quả phiếu */
.ket-qua-phieu { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; padding:1.5rem; }
.thong-tin-nguoi-muon { display:grid; grid-template-columns:1fr 1fr; gap:0.75rem 1.5rem; margin-bottom:1.5rem; padding:1rem; background:rgba(6,182,212,0.06); border-radius:10px; }
.info-item { font-size:0.875rem; }
.label { color:var(--mau-chu-mo); margin-right:0.25rem; }
.text-do { color:#ff6b6b; font-weight:600; }
.tieu-de-danh-sach { font-size:0.9rem; font-weight:700; color:var(--mau-chinh); margin-bottom:0.75rem; }
.danh-sach-tra { display:flex; flex-direction:column; gap:0.5rem; margin-bottom:1.5rem; }
.item-tra { display:flex; align-items:center; justify-content:space-between; gap:1rem; padding:0.875rem 1rem; background:rgba(255,255,255,0.03); border:1px solid rgba(255,255,255,0.06); border-radius:10px; }
.item-tra--qua-han { border-color:rgba(255,107,107,0.3); background:rgba(255,107,107,0.04); }
.ten-sach-tra { font-size:0.875rem; font-weight:600; }
.ma-vach-tra { display:flex; align-items:center; gap:0.5rem; font-size:0.775rem; margin-top:0.2rem; }
.tag-qua-han { color:#ff6b6b; font-size:0.75rem; }
.item-tra-tinh-trang { display:flex; flex-direction:column; gap:0.25rem; flex-shrink:0; }
.select-tinh-trang { padding:0.4rem 0.75rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:6px; color:var(--mau-chu); font-family:inherit; cursor:pointer; }
.select-tinh-trang option { background:#1a1a2e; }

/* Tab gia hạn */
.thanh-filter { margin-bottom:1rem; }
.select-filter { padding:0.65rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; cursor:pointer; }
.select-filter option { background:#1a1a2e; }
.bang-container { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; overflow:hidden; padding:1rem; }
.bang { width:100%; border-collapse:collapse; }
.bang th { padding:0.75rem 1rem; text-align:left; font-size:0.75rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--mau-chu-mo); border-bottom:1px solid rgba(255,255,255,0.08); }
.bang td { padding:0.875rem 1rem; font-size:0.875rem; border-bottom:1px solid rgba(255,255,255,0.04); vertical-align:middle; }
.bang tr:last-child td { border-bottom:none; }
.bang tr:hover td { background:rgba(255,255,255,0.02); }
.hanh-dong { display:flex; gap:0.4rem; }
.nut-duyet { padding:0.35rem 0.75rem; background:rgba(81,207,102,0.15); border:1px solid rgba(81,207,102,0.3); border-radius:6px; color:#51cf66; cursor:pointer; font-size:0.8rem; }
.nut-tuchoi { padding:0.35rem 0.75rem; background:rgba(255,107,107,0.15); border:1px solid rgba(255,107,107,0.3); border-radius:6px; color:#ff6b6b; cursor:pointer; font-size:0.8rem; }
.text-mo { color:var(--mau-chu-rat-mo); }

/* Nút chung */
.nhom-nut { display:flex; justify-content:flex-end; gap:0.75rem; }
.nut-huy { padding:0.75rem 1.5rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; }
.nut-chinh { padding:0.75rem 1.75rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-size:0.9rem; font-weight:700; }
.nut-chinh:disabled { opacity:0.6; cursor:not-allowed; }
.nut-huy-modal { padding:0.65rem 1.25rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; }
.nut-luu-modal { padding:0.65rem 1.5rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-weight:600; }
.nut-do { background:#ef4444 !important; }
.nut-luu-modal:disabled { opacity:0.6; cursor:not-allowed; }
</style>
