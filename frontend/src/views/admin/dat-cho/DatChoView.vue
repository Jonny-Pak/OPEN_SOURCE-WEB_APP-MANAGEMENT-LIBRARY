<!--
  DatChoView.vue — Quản lý đặt chỗ mượn sách: duyệt hoặc hủy.
-->
<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { datChoService } from '@/services/datChoService'
import { usePagination } from '@/composables/usePagination'
import { useToast } from '@/composables/useToast'
import type { DatCho, TrangThaiDatCho } from '@/types/datcho'
import ModalDialog from '@/components/admin/shared/ModalDialog.vue'
import Pagination from '@/components/admin/shared/Pagination.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import EmptyState from '@/components/admin/shared/EmptyState.vue'
import StatusBadge from '@/components/admin/shared/StatusBadge.vue'

const toast = useToast()
const phanTrang = usePagination()

const dangTai = ref(false)
const danhSach = ref<DatCho[]>([])
const filterTrangThai = ref<TrangThaiDatCho | ''>('CHO_DUYET')
const dangXuLy = ref(false)

// Modal hủy
const huyItem = ref<DatCho | null>(null)
const lyDoHuy = ref('')

const TRANG_THAI_MAP: Record<TrangThaiDatCho, { nhan: string; mau: 'vang' | 'xanh' | 'do' | 'xam' }> = {
  CHO_DUYET: { nhan: 'Chờ duyệt', mau: 'vang' },
  DA_SAN_SANG: { nhan: 'Đã sẵn sàng', mau: 'xanh' },
  DA_HUY: { nhan: 'Đã hủy', mau: 'do' },
  DA_MUON: { nhan: 'Đã mượn', mau: 'xam' },
}

function formatNgay(s: string) { return new Date(s).toLocaleDateString('vi-VN') }

async function taiDanhSach() {
  dangTai.value = true
  try {
    danhSach.value = await datChoService.danhSach()
    phanTrang.capNhatTong(danhSach.value.length)
  } catch { toast.loi('Không thể tải danh sách đặt chỗ') }
  finally { dangTai.value = false }
}

async function duyet(item: DatCho) {
  dangXuLy.value = true
  try {
    await datChoService.duyet(item.maDatCho)
    toast.thanhCong('Đã duyệt đặt chỗ')
    taiDanhSach()
  } catch { toast.loi('Duyệt thất bại') } finally { dangXuLy.value = false }
}

async function xacNhanHuy() {
  if (!huyItem.value || !lyDoHuy.value.trim()) return toast.canhBao('Vui lòng nhập lý do hủy')
  dangXuLy.value = true
  try {
    await datChoService.huy(huyItem.value.maDatCho, { lyDo: lyDoHuy.value })
    toast.thanhCong('Đã hủy đặt chỗ')
    huyItem.value = null
    lyDoHuy.value = ''
    taiDanhSach()
  } catch { toast.loi('Hủy thất bại') } finally { dangXuLy.value = false }
}

watch([filterTrangThai, () => phanTrang.trangHienTai.value], () => { phanTrang.datLaiTrang(); taiDanhSach() })
onMounted(taiDanhSach)
</script>

<template>
  <div class="dat-cho">
    <div class="thanh-cong-cu">
      <select v-model="filterTrangThai" class="select-filter">
        <option value="">Tất cả</option>
        <option value="CHO_DUYET">Chờ duyệt</option>
        <option value="DA_SAN_SANG">Đã sẵn sàng</option>
        <option value="DA_HUY">Đã hủy</option>
      </select>
    </div>

    <div class="bang-container">
      <SkeletonLoader v-if="dangTai" :rows="6" height="52px" />
      <template v-else>
        <EmptyState v-if="danhSach.length === 0" thong-diep="Không có đặt chỗ nào" />
        <table v-else class="bang">
          <thead><tr><th>Độc giả</th><th>Sách đặt</th><th>Ngày đặt</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
          <tbody>
            <tr v-for="item in danhSach" :key="item.maDatCho">
              <td>
                <div class="ten-nguoi">{{ item.nguoiDung?.hoDem || '' }} {{ item.nguoiDung?.ten || '' }}</div>
                <div class="email-mo">{{ item.nguoiDung?.email || 'N/A' }}</div>
              </td>
              <td>{{ item.sach?.tenSach || 'N/A' }}</td>
              <td>{{ item.ngayDatCho ? formatNgay(item.ngayDatCho) : '' }}</td>
              <td><StatusBadge v-if="item.trangThai && TRANG_THAI_MAP[item.trangThai]" :nhan-hien="TRANG_THAI_MAP[item.trangThai].nhan" :loai="TRANG_THAI_MAP[item.trangThai].mau" /></td>
              <td>
                <div v-if="item.trangThai === 'CHO_DUYET'" class="hanh-dong">
                  <button class="nut-duyet" :disabled="dangXuLy" @click="duyet(item)">
                    <font-awesome-icon icon="fa-solid fa-circle-check" /> Duyệt
                  </button>
                  <button class="nut-huy-btn" :disabled="dangXuLy" @click="huyItem = item; lyDoHuy = ''">
                    <font-awesome-icon icon="fa-solid fa-circle-xmark" /> Hủy
                  </button>
                </div>
                <span v-else class="khong-hanh-dong">—</span>
              </td>
            </tr>
          </tbody>
        </table>
        <Pagination :trang-hien-tai="phanTrang.trangHienThiChoNguoiDung.value" :tong-trang="phanTrang.tongTrang.value" :tong-phan-tu="phanTrang.tongPhanTu.value" :kich-thuoc-trang="10" @doi-trang="phanTrang.denTrang" />
      </template>
    </div>

    <!-- Modal hủy -->
    <ModalDialog :dang-mo="huyItem !== null" tieu-de="Hủy đặt chỗ" @dong="huyItem = null">
      <div class="form-modal">
        <p style="color:var(--mau-chu-mo);font-size:0.875rem;margin-bottom:0.75rem">Hủy đặt chỗ của <strong>{{ huyItem?.nguoiDung?.hoDem || '' }} {{ huyItem?.nguoiDung?.ten || '' }}</strong> cho sách <strong>{{ huyItem?.sach?.tenSach || 'N/A' }}</strong>.</p>
        <div class="form-group">
          <label>Lý do hủy *</label>
          <textarea v-model="lyDoHuy" class="form-input form-textarea" placeholder="Nhập lý do hủy..."></textarea>
        </div>
      </div>
      <template #footer>
        <button class="nut-huy" @click="huyItem = null">Quay lại</button>
        <button class="nut-luu nut-do" :disabled="dangXuLy" @click="xacNhanHuy">{{ dangXuLy ? 'Đang xử lý...' : 'Xác nhận hủy' }}</button>
      </template>
    </ModalDialog>
  </div>
</template>

<style scoped>
.dat-cho { animation:fadeInUp 0.4s ease; }
.thanh-cong-cu { margin-bottom:1rem; }
.select-filter { padding:0.65rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; cursor:pointer; }
.select-filter option { background:#1a1a2e; color:#ffffff; }
.bang-container { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; overflow:hidden; padding:1rem; }
.bang { width:100%; border-collapse:collapse; }
.bang th { padding:0.75rem 1rem; text-align:left; font-size:0.75rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--mau-chu-mo); border-bottom:1px solid rgba(255,255,255,0.08); }
.bang td { padding:0.875rem 1rem; font-size:0.875rem; border-bottom:1px solid rgba(255,255,255,0.04); vertical-align:middle; }
.bang tr:last-child td { border-bottom:none; }
.bang tr:hover td { background:rgba(255,255,255,0.02); }
.ten-nguoi { font-weight:600; }
.email-mo { font-size:0.775rem; color:var(--mau-chu-mo); }
.hanh-dong { display:flex; gap:0.5rem; }
.nut-duyet { padding:0.35rem 0.8rem; background:rgba(81,207,102,0.15); border:1px solid rgba(81,207,102,0.3); border-radius:6px; color:#51cf66; cursor:pointer; font-size:0.8rem; transition:all 0.2s; }
.nut-duyet:hover:not(:disabled) { background:rgba(81,207,102,0.25); }
.nut-huy-btn { padding:0.35rem 0.8rem; background:rgba(255,107,107,0.15); border:1px solid rgba(255,107,107,0.3); border-radius:6px; color:#ff6b6b; cursor:pointer; font-size:0.8rem; transition:all 0.2s; }
.nut-duyet:disabled,.nut-huy-btn:disabled { opacity:0.5; cursor:not-allowed; }
.khong-hanh-dong { color:var(--mau-chu-rat-mo); }
.form-modal { display:flex; flex-direction:column; gap:0.875rem; }
.form-group { display:flex; flex-direction:column; gap:0.375rem; }
.form-group label { font-size:0.8rem; font-weight:600; color:var(--mau-chu); }
.form-input { padding:0.7rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.875rem; outline:none; }
.form-input:focus { border-color:var(--mau-chinh); }
.form-textarea { resize:vertical; min-height:80px; }
.nut-huy { padding:0.65rem 1.25rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; }
.nut-luu { padding:0.65rem 1.5rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-weight:600; }
.nut-do { background:#ef4444 !important; }
.nut-luu:disabled { opacity:0.6; cursor:not-allowed; }
</style>
