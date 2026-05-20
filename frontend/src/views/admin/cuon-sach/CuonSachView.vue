<!--
  CuonSachView.vue — Quản lý cuốn sách vật lý: thêm, sửa, xem mã vạch, in.
-->
<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { cuonSachService } from '@/services/cuonSachService'
import apiClient from '@/services/apiClient'
import { sachService } from '@/services/sachService'
import { useSearch } from '@/composables/useSearch'
import { usePagination } from '@/composables/usePagination'
import { useModal } from '@/composables/useModal'
import { useToast } from '@/composables/useToast'
import type { CuonSach, TinhTrangVatLy, TrangThaiCuonSach } from '@/types/sach'
import type { Sach } from '@/types/sach'
import ModalDialog from '@/components/admin/shared/ModalDialog.vue'
import ConfirmDialog from '@/components/admin/shared/ConfirmDialog.vue'
import Pagination from '@/components/admin/shared/Pagination.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import EmptyState from '@/components/admin/shared/EmptyState.vue'
import StatusBadge from '@/components/admin/shared/StatusBadge.vue'
import ImportCuonSachExcelModal from '@/components/admin/shared/ImportCuonSachExcelModal.vue'

const toast = useToast()
const { tuKhoaTimKiem, tuKhoaDebounced } = useSearch(300)
const phanTrang = usePagination()
const modalThem = useModal<CuonSach>()
const modalMaVach = useModal<CuonSach>()
const modalMaVachImage = ref<string | null>(null)

const dangTai = ref(false)
const danhSach = ref<CuonSach[]>([])
const danhSachSach = ref<Sach[]>([])
const filterTrangThai = ref<TrangThaiCuonSach | ''>('')
const xoaItem = ref<CuonSach | null>(null)
const dangXoa = ref(false)
const dangGui = ref(false)

const formThem = ref({ sachId: 0, viTriKe: '', tinhTrangVatLy: 'TOT' as TinhTrangVatLy })
const formSua = ref({ viTriKe: '', tinhTrangVatLy: 'TOT' as TinhTrangVatLy })
const itemDangSua = ref<CuonSach | null>(null)
const dangSua = ref(false)
const showImportCuonSach = ref(false)

const TRANG_THAI_LABEL: Record<TrangThaiCuonSach, { nhan: string; mau: 'xanh' | 'do' | 'vang' | 'xam' }> = {
  SAN_SANG: { nhan: 'Sẵn sàng', mau: 'xanh' },
  DANG_MUON: { nhan: 'Đang cho mượn', mau: 'vang' },
  CHO_MUON: { nhan: 'Đã được đặt chỗ', mau: 'xam' },
  BAO_MAT: { nhan: 'Đã báo mất', mau: 'do' },
}
const TINH_TRANG_LABEL: Record<TinhTrangVatLy, string> = { TOT: 'Tốt', HU_HONG: 'Hư hỏng', MAT: 'Mất' }

async function taiDanhSach() {
  dangTai.value = true
  try {
    const list = await cuonSachService.danhSach()
    
    // Lọc theo trạng thái
    let loc = [...list]
    if (filterTrangThai.value) {
      loc = loc.filter(item => item.trangThai === filterTrangThai.value)
    }
    
    // Lọc theo từ khóa
    if (tuKhoaDebounced.value.trim()) {
      const query = tuKhoaDebounced.value.toLowerCase().trim()
      loc = loc.filter(item => 
        item.maBarcodeVatLy.toLowerCase().includes(query) || 
        item.sach.tenSach.toLowerCase().includes(query)
      )
    }
    
    phanTrang.capNhatTong(loc.length)
    
    // Phân trang cục bộ (local pagination)
    const size = 10
    const batDau = phanTrang.trangHienTai.value * size
    const ketThuc = batDau + size
    danhSach.value = loc.slice(batDau, ketThuc)
  } catch { 
    toast.loi('Không thể tải danh sách cuốn sách') 
  } finally { 
    dangTai.value = false 
  }
}

async function taiDanhSachSach() {
  try { 
    const response = await sachService.danhSach(0, 1000)
    danhSachSach.value = response.content
  } catch { /* im lặng */ }
}

async function luuThem() {
  if (!formThem.value.sachId) return toast.canhBao('Vui lòng chọn đầu sách')
  if (!formThem.value.viTriKe.trim()) return toast.canhBao('Vị trí kệ không được để trống')
  dangGui.value = true
  try {
    await cuonSachService.taoCai(formThem.value)
    toast.thanhCong('Thêm cuốn sách thành công')
    modalThem.dongModal()
    taiDanhSach()
  } catch { toast.loi('Thêm thất bại') } finally { dangGui.value = false }
}

function moSua(item: CuonSach) {
  itemDangSua.value = item
  formSua.value = { viTriKe: item.viTriKe, tinhTrangVatLy: item.tinhTrangVatLy }
  dangSua.value = true
}

async function luuSua() {
  if (!itemDangSua.value) return
  dangGui.value = true
  try {
    await cuonSachService.capNhat(itemDangSua.value.maCuonSach, formSua.value)
    toast.thanhCong('Cập nhật thành công')
    dangSua.value = false
    taiDanhSach()
  } catch { toast.loi('Cập nhật thất bại') } finally { dangGui.value = false }
}

async function xacNhanXoa() {
  if (!xoaItem.value) return
  if (xoaItem.value.trangThai !== 'SAN_SANG') { toast.canhBao('Chỉ xóa được cuốn sách đang sẵn sàng'); xoaItem.value = null; return }
  dangXoa.value = true
  try {
    await cuonSachService.xoa(xoaItem.value.maCuonSach)
    toast.thanhCong('Đã xóa')
    xoaItem.value = null
    taiDanhSach()
  } catch { toast.loi('Xóa thất bại') } finally { dangXoa.value = false }
}

function inMaVach() { window.print() }

async function openMaVach(item: CuonSach) {
  modalMaVach.moModalSua(item)
  modalMaVachImage.value = null
  try {
    const blob = await apiClient.get<Blob>(`/api/v1/cuon-sach/${item.maCuonSach}/ma-vach`, { responseType: 'blob' })
    modalMaVachImage.value = URL.createObjectURL(blob)
  } catch (e) {
    console.error('Lỗi tải mã vạch:', e)
    toast.loi('Không tải được mã vạch')
  }
}

// Cleanup object URL when modal closes
watch(() => modalMaVach.dangMo.value, (val) => {
  if (!val && modalMaVachImage.value) {
    URL.revokeObjectURL(modalMaVachImage.value)
    modalMaVachImage.value = null
  }
})

watch([filterTrangThai, tuKhoaDebounced], () => { phanTrang.datLaiTrang(); taiDanhSach() })
watch(() => phanTrang.trangHienTai.value, taiDanhSach)
onMounted(() => { taiDanhSach(); taiDanhSachSach() })
</script>

<template>
  <div class="cuon-sach">
    <div class="thanh-cong-cu">
      <div class="vung-tim-kiem">
        <font-awesome-icon icon="fa-solid fa-magnifying-glass" class="icon-tim-kiem" />
        <input v-model="tuKhoaTimKiem" class="input-tk" placeholder="Tìm mã vạch, tên sách..." />
      </div>
      <select v-model="filterTrangThai" class="select-filter">
        <option value="">Tất cả trạng thái</option>
        <option value="SAN_SANG">Sẵn sàng</option>
        <option value="DANG_MUON">Đang cho mượn</option>
        <option value="CHO_MUON">Đã được đặt chỗ</option>
        <option value="BAO_MAT">Đã báo mất</option>
      </select>
      <button class="nut-them" @click="modalThem.moModalThem()">
        <font-awesome-icon icon="fa-solid fa-plus" /> Thêm cuốn sách
      </button>
      <button class="nut-them nut-import" @click="showImportCuonSach = true">
        <font-awesome-icon icon="fa-solid fa-file-excel" /> Import Excel
      </button>
    </div>

    <div class="bang-container">
      <SkeletonLoader v-if="dangTai" :rows="7" height="48px" />
      <template v-else>
        <EmptyState v-if="danhSach.length === 0" thong-diep="Không tìm thấy cuốn sách nào" />
        <table v-else class="bang">
          <thead><tr><th>Mã vạch</th><th>Tên sách</th><th>Vị trí kệ</th><th>Tình trạng</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
          <tbody>
            <tr v-for="item in danhSach" :key="item.maCuonSach">
              <td><code class="ma-vach">{{ item.maBarcodeVatLy }}</code></td>
              <td>{{ item.sach.tenSach }}</td>
              <td>{{ item.viTriKe }}</td>
              <td>{{ TINH_TRANG_LABEL[item.tinhTrangVatLy] }}</td>
              <td>
                <StatusBadge :nhan-hien="TRANG_THAI_LABEL[item.trangThai].nhan" :loai="TRANG_THAI_LABEL[item.trangThai].mau" />
              </td>
              <td>
                <div class="hanh-dong">
                  <button class="nut-hd" @click="openMaVach(item)" title="Xem mã vạch">
                    <font-awesome-icon icon="fa-solid fa-barcode" />
                  </button>
                  <button class="nut-hd" @click="moSua(item)" title="Sửa">
                    <font-awesome-icon icon="fa-solid fa-pen-to-square" />
                  </button>
                  <button class="nut-hd nut-xoa-btn" @click="xoaItem = item" title="Xóa" :disabled="item.trangThai !== 'SAN_SANG'">
                    <font-awesome-icon icon="fa-solid fa-trash-can" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <Pagination :trang-hien-tai="phanTrang.trangHienThiChoNguoiDung.value" :tong-trang="phanTrang.tongTrang.value" :tong-phan-tu="phanTrang.tongPhanTu.value" :kich-thuoc-trang="10" @doi-trang="phanTrang.denTrang" />
      </template>
    </div>

    <!-- Modal thêm cuốn sách -->
    <ModalDialog :dang-mo="modalThem.dangMo.value" tieu-de="Thêm cuốn sách mới" @dong="modalThem.dongModal()">
      <div class="form-modal">
        <div class="form-group"><label>Đầu sách *</label>
          <select v-model.number="formThem.sachId" class="form-input">
            <option value="0" disabled>-- Chọn đầu sách --</option>
            <option v-for="s in danhSachSach" :key="s.maSach" :value="s.maSach">{{ s.tenSach }}</option>
          </select>
        </div>
        <div class="form-group"><label>Vị trí kệ *</label><input v-model="formThem.viTriKe" class="form-input" placeholder="VD: A1-01" /></div>
        <div class="form-group"><label>Tình trạng vật lý *</label>
          <select v-model="formThem.tinhTrangVatLy" class="form-input">
            <option value="TOT">Tốt</option><option value="HU_HONG">Hư hỏng</option><option value="MAT">Mất</option>
          </select>
        </div>
      </div>
      <template #footer>
        <button class="nut-huy" @click="modalThem.dongModal()">Hủy</button>
        <button class="nut-luu" :disabled="dangGui" @click="luuThem">{{ dangGui ? 'Đang lưu...' : 'Thêm' }}</button>
      </template>
    </ModalDialog>

    <!-- Modal sửa -->
    <ModalDialog :dang-mo="dangSua" tieu-de="Cập nhật cuốn sách" @dong="dangSua = false">
      <div class="form-modal">
        <div class="form-group"><label>Vị trí kệ</label><input v-model="formSua.viTriKe" class="form-input" /></div>
        <div class="form-group"><label>Tình trạng vật lý</label>
          <select v-model="formSua.tinhTrangVatLy" class="form-input">
            <option value="TOT">Tốt</option><option value="HU_HONG">Hư hỏng</option><option value="MAT">Mất</option>
          </select>
        </div>
      </div>
      <template #footer>
        <button class="nut-huy" @click="dangSua = false">Hủy</button>
        <button class="nut-luu" :disabled="dangGui" @click="luuSua">{{ dangGui ? 'Đang lưu...' : 'Lưu' }}</button>
      </template>
    </ModalDialog>

    <!-- Modal mã vạch -->
    <ModalDialog :dang-mo="modalMaVach.dangMo.value" tieu-de="Mã vạch cuốn sách" @dong="modalMaVach.dongModal()">
      <div class="ma-vach-container" id="in-ma-vach">
        <p style="text-align:center;margin-bottom:1rem;color:var(--mau-chu-mo)">{{ modalMaVach.itemDangSua.value?.sach.tenSach }}</p>
        <div class="ma-vach-hinh">
          <img v-if="modalMaVach.itemDangSua.value" :src="modalMaVachImage || undefined" alt="Mã vạch" style="max-width:100%" />
        </div>
        <p style="text-align:center;font-family:monospace;margin-top:0.75rem">{{ modalMaVach.itemDangSua.value?.maBarcodeVatLy }}</p>
      </div>
      <template #footer>
        <button class="nut-huy" @click="modalMaVach.dongModal()">Đóng</button>
        <button class="nut-luu" @click="inMaVach">
          <font-awesome-icon icon="fa-solid fa-print" /> In mã vạch
        </button>
      </template>
    </ModalDialog>

    <ConfirmDialog :dang-mo="xoaItem !== null" :thong-diep="`Xóa cuốn sách mã '${xoaItem?.maBarcodeVatLy}'?`" :dang-xu-ly="dangXoa" @xac-nhan="xacNhanXoa" @huy="xoaItem = null" />

    <!-- Import cuốn sách Excel -->
    <ImportCuonSachExcelModal
      v-if="showImportCuonSach"
      @close="showImportCuonSach = false"
      @done="() => { showImportCuonSach = false; taiDanhSach() }"
    />
  </div>
</template>

<style scoped>
.cuon-sach { animation:fadeInUp 0.4s ease; }
.thanh-cong-cu { display:flex; gap:0.75rem; margin-bottom:1rem; flex-wrap:wrap; }
.vung-tim-kiem { position: relative; display: flex; align-items: center; flex: 1; min-width: 200px; }
.icon-tim-kiem { position: absolute; left: 1rem; color: var(--mau-chu-mo); pointer-events: none; }
.input-tk { width: 100%; padding: 0.65rem 1rem 0.65rem 2.5rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; color: var(--mau-chu); font-family: inherit; font-size: 0.875rem; outline: none; box-sizing: border-box; }
.input-tk:focus { border-color: var(--mau-chinh); }
.select-filter { padding:0.65rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; cursor:pointer; }
.select-filter option { background:#1a1a2e; color:#ffffff; }
.nut-them { padding:0.65rem 1.25rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-size:0.875rem; font-weight:600; white-space:nowrap; }
.nut-import { display: flex; align-items: center; gap: 0.5rem; background: #16a34a; }
.nut-import:hover { background: #15803d; }
.bang-container { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; overflow:hidden; padding:1rem; }
.bang { width:100%; border-collapse:collapse; }
.bang th { padding:0.75rem 1rem; text-align:left; font-size:0.75rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--mau-chu-mo); border-bottom:1px solid rgba(255,255,255,0.08); }
.bang td { padding:0.8rem 1rem; font-size:0.875rem; border-bottom:1px solid rgba(255,255,255,0.04); }
.bang tr:last-child td { border-bottom:none; }
.bang tr:hover td { background:rgba(255,255,255,0.02); }
.ma-vach { font-size:0.775rem; background:rgba(255,255,255,0.06); padding:0.2rem 0.5rem; border-radius:4px; }
.hanh-dong { display:flex; gap:0.4rem; }
.nut-hd { background:none; border:1px solid rgba(255,255,255,0.1); border-radius:6px; padding:0.3rem 0.6rem; cursor:pointer; font-size:0.9rem; transition:all 0.2s; }
.nut-hd:hover:not(:disabled) { background:rgba(6,182,212,0.08); border-color:var(--color-primary); }
.nut-hd:disabled { opacity:0.3; cursor:not-allowed; }
.nut-xoa-btn:hover:not(:disabled) { background:rgba(255,107,107,0.15) !important; border-color:rgba(255,107,107,0.4) !important; }
.form-modal { display:flex; flex-direction:column; gap:1rem; }
.form-group { display:flex; flex-direction:column; gap:0.375rem; }
.form-group label { font-size:0.8rem; font-weight:600; color:var(--mau-chu); }
.form-input { padding:0.7rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.875rem; outline:none; }
.form-input:focus { border-color:var(--mau-chinh); }
.nut-huy { padding:0.65rem 1.25rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; }
.nut-luu { padding:0.65rem 1.5rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-weight:600; }
.ma-vach-container { text-align:center; padding:1rem; }
.ma-vach-hinh { background:white; padding:1rem; border-radius:8px; display:inline-block; }
@media print {
  body > *:not(#in-ma-vach) { display:none !important; }
  #in-ma-vach { display:block !important; }
}
</style>
