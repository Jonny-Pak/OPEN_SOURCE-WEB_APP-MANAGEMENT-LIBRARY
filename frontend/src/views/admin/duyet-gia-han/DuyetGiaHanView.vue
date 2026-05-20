<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { giaHanService } from '@/services/traSachService'
import { usePagination } from '@/composables/usePagination'
import { useToast } from '@/composables/useToast'
import type { LichSuGiaHan, TrangThaiGiaHan } from '@/types/giahan'

import ModalDialog from '@/components/admin/shared/ModalDialog.vue'
import Pagination from '@/components/admin/shared/Pagination.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import EmptyState from '@/components/admin/shared/EmptyState.vue'
import StatusBadge from '@/components/admin/shared/StatusBadge.vue'

const toast = useToast()
const phanTrang = usePagination()

const dangTaiGH = ref(false)
const danhSachGH = ref<LichSuGiaHan[]>([])
const filterGH = ref<TrangThaiGiaHan | ''>('CHO_DUYET')
const dangXuLyGH = ref(false)
const modalDuyetGH = ref<LichSuGiaHan | null>(null)
const hanTraMoi = ref('')
const modalTuChoiGH = ref<LichSuGiaHan | null>(null)
const lyDoTuChoi = ref('')
const tuKhoaTim = ref('')

const GH_MAP: Record<TrangThaiGiaHan, { nhan: string; mau: 'vang' | 'xanh' | 'do' }> = {
  CHO_DUYET: { nhan: 'Chờ duyệt', mau: 'vang' },
  DA_DUYET: { nhan: 'Đã duyệt', mau: 'xanh' },
  TU_CHOI: { nhan: 'Từ chối', mau: 'do' },
}

function formatNgay(s: string) { return new Date(s).toLocaleDateString('vi-VN') }

function laQuaHan(hanTra: string): boolean {
  return new Date(hanTra) < new Date()
}

const danhSachGHLoc = computed(() => {
  let list = danhSachGH.value
  if (filterGH.value) list = list.filter((item) => item.trangThai === filterGH.value)
  const kw = tuKhoaTim.value.trim().toLowerCase()
  if (kw) {
    list = list.filter((item: any) => {
      const ten = `${item.nguoiDung?.hoDem || ''} ${item.nguoiDung?.ten || ''}`.toLowerCase()
      const sach = (item.tenSach || '').toLowerCase()
      return ten.includes(kw) || sach.includes(kw)
    })
  }
  return list
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
  } catch (err: any) { toast.loi(err?.message || 'Duyệt thất bại') } finally { dangXuLyGH.value = false }
}

async function tuChoiGiaHan() {
  if (!modalTuChoiGH.value || !lyDoTuChoi.value.trim()) return toast.canhBao('Vui lòng nhập lý do từ chối')
  dangXuLyGH.value = true
  try {
    await giaHanService.tuChoi(modalTuChoiGH.value.maGiaHan, { lyDo: lyDoTuChoi.value })
    toast.thanhCong('Đã từ chối gia hạn')
    modalTuChoiGH.value = null
    taiGiaHan()
  } catch (err: any) { toast.loi(err?.message || 'Từ chối thất bại') } finally { dangXuLyGH.value = false }
}

watch([filterGH, () => phanTrang.trangHienTai.value], () => {
  phanTrang.datLaiTrang()
})

watch(danhSachGHLoc, (val) => {
  phanTrang.capNhatTong(val.length)
}, { immediate: true })

onMounted(() => {
  taiGiaHan()
})
</script>

<template>
  <div class="duyet-gia-han">
    <div class="noi-dung-tab">
      <div class="thanh-filter">
        <div class="vung-tim-kiem">
          <font-awesome-icon icon="fa-solid fa-magnifying-glass" class="icon-tim-kiem" />
          <input v-model="tuKhoaTim" class="input-tk" placeholder="Tìm theo tên độc giả, sách..." />
        </div>
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
                <td>{{ item.nguoiDung?.hoDem || '' }} {{ item.nguoiDung?.ten || '' }}</td>
                <td><div style="font-size:0.85rem">{{ item.tenSach }}</div><code style="font-size:0.75rem;color:var(--mau-chu-mo)">{{ item.maBarcodeVatLy }}</code></td>
                <td :class="{ 'text-do': laQuaHan(item.hanTraHienTai) }">{{ formatNgay(item.hanTraHienTai) }}</td>
                <td>{{ formatNgay(item.hanTraXinGiaHan) }}</td>
                <td><StatusBadge :nhan-hien="GH_MAP[item.trangThai].nhan" :loai="GH_MAP[item.trangThai].mau" /></td>
                <td>
                  <div v-if="item.trangThai === 'CHO_DUYET'" class="hanh-dong">
                    <button class="nut-duyet" @click="modalDuyetGH = item; hanTraMoi = item.hanTraXinGiaHan ? item.hanTraXinGiaHan.substring(0, 10) : ''">
                      <font-awesome-icon icon="fa-solid fa-circle-check" /> Duyệt
                    </button>
                    <button class="nut-tuchoi" @click="modalTuChoiGH = item; lyDoTuChoi = ''">
                      <font-awesome-icon icon="fa-solid fa-circle-xmark" /> Từ chối
                    </button>
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
        <label>Ngày hạn trả mới đề xuất</label>
        <input v-model="hanTraMoi" type="date" class="form-input" disabled />
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
.duyet-gia-han { animation:fadeInUp 0.4s ease; padding-top: 1rem; }
.noi-dung-tab { animation:fadeInUp 0.3s ease; }

.thanh-filter { margin-bottom:1rem; display:flex; gap:0.75rem; flex-wrap:wrap; }
.vung-tim-kiem { position:relative; display:flex; align-items:center; flex:1; min-width:200px; }
.icon-tim-kiem { position:absolute; left:1rem; color:var(--mau-chu-mo); pointer-events:none; }
.input-tk { width:100%; padding:0.65rem 1rem 0.65rem 2.5rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.875rem; outline:none; box-sizing:border-box; }
.input-tk:focus { border-color:var(--mau-chinh); box-shadow:0 0 0 3px rgba(6,182,212,0.15); }
.select-filter { padding:0.65rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; cursor:pointer; }
.select-filter option { background:#1a1a2e; color:#ffffff; }

.bang-container { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; overflow:hidden; padding:1rem; }
.bang { width:100%; border-collapse:collapse; }
.bang th { padding:0.75rem 1rem; text-align:left; font-size:0.75rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--mau-chu-mo); border-bottom:1px solid rgba(255,255,255,0.08); }
.bang td { padding:0.875rem 1rem; font-size:0.875rem; border-bottom:1px solid rgba(255,255,255,0.04); vertical-align:middle; }
.bang tr:last-child td { border-bottom:none; }
.bang tr:hover td { background:rgba(255,255,255,0.02); }

.text-do { color:#ff6b6b; font-weight:600; }
.hanh-dong { display:flex; gap:0.4rem; }
.nut-duyet { padding:0.35rem 0.75rem; background:rgba(81,207,102,0.15); border:1px solid rgba(81,207,102,0.3); border-radius:6px; color:#51cf66; cursor:pointer; font-size:0.8rem; }
.nut-tuchoi { padding:0.35rem 0.75rem; background:rgba(255,107,107,0.15); border:1px solid rgba(255,107,107,0.3); border-radius:6px; color:#ff6b6b; cursor:pointer; font-size:0.8rem; }
.text-mo { color:var(--mau-chu-rat-mo); }

/* form & modal styles */
.form-group { display:flex; flex-direction:column; gap:0.375rem; }
.form-group label { font-size:0.825rem; font-weight:600; color:var(--mau-chu); }
.form-input { padding:0.75rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.9rem; outline:none; width: 100%; box-sizing: border-box; }
.form-input:focus { border-color:var(--mau-chinh); }

.nut-huy-modal { padding:0.65rem 1.25rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; }
.nut-luu-modal { padding:0.65rem 1.5rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-weight:600; }
.nut-do { background:#ef4444 !important; }
.nut-luu-modal:disabled { opacity:0.6; cursor:not-allowed; }
</style>
