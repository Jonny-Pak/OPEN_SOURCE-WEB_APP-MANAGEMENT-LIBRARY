<!--
  MuonSachListView.vue — Danh sách phiếu mượn sách.
-->
<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { muonSachService } from '@/services/muonSachService'
import { usePagination } from '@/composables/usePagination'
import { useToast } from '@/composables/useToast'
import type { PhieuMuon, TrangThaiPhieuMuon } from '@/types/muonsach'
import Pagination from '@/components/admin/shared/Pagination.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import EmptyState from '@/components/admin/shared/EmptyState.vue'
import StatusBadge from '@/components/admin/shared/StatusBadge.vue'

const router = useRouter()
const toast = useToast()
const phanTrang = usePagination()

const dangTai = ref(false)
const danhSach = ref<PhieuMuon[]>([])
const filterTrangThai = ref<TrangThaiPhieuMuon | ''>('')

const TRANG_THAI_MAP: Record<TrangThaiPhieuMuon, { nhan: string; mau: 'xanh-duong' | 'xanh' | 'do' }> = {
  DANG_MUON: { nhan: 'Đang mượn', mau: 'xanh-duong' },
  DA_TRA: { nhan: 'Đã trả', mau: 'xanh' },
  QUA_HAN: { nhan: 'Quá hạn', mau: 'do' },
}

function formatNgay(s: string) { return new Date(s).toLocaleDateString('vi-VN') }

async function taiDanhSach() {
  dangTai.value = true
  try {
    danhSach.value = await muonSachService.danhSach()
    phanTrang.capNhatTong(danhSach.value.length)
  } catch { toast.loi('Không thể tải danh sách phiếu mượn') }
  finally { dangTai.value = false }
}

watch([filterTrangThai, () => phanTrang.trangHienTai.value], taiDanhSach)
onMounted(taiDanhSach)
</script>

<template>
  <div class="muon-sach-list">
    <div class="thanh-cong-cu">
      <select v-model="filterTrangThai" class="select-filter">
        <option value="">Tất cả trạng thái</option>
        <option value="DANG_MUON">Đang mượn</option>
        <option value="DA_TRA">Đã trả</option>
        <option value="QUA_HAN">Quá hạn</option>
      </select>
      <button class="nut-them" @click="router.push('/admin/muon-sach/tao-moi')">+ Tạo phiếu mượn</button>
    </div>

    <div class="bang-container">
      <SkeletonLoader v-if="dangTai" :rows="7" height="52px" />
      <template v-else>
        <EmptyState v-if="danhSach.length === 0" thong-diep="Không có phiếu mượn nào" />
        <table v-else class="bang">
          <thead><tr><th>Mã phiếu</th><th>Độc giả</th><th>Ngày mượn</th><th>Hạn trả</th><th>Số cuốn</th><th>Trạng thái</th></tr></thead>
          <tbody>
            <tr v-for="item in danhSach" :key="item.maPhieuMuon">
              <td><code class="ma-phieu">#{{ item.maPhieuMuon }}</code></td>
              <td>
                <div class="ten-nguoi">{{ item.nguoiDung.hoDem }} {{ item.nguoiDung.ten }}</div>
                <div class="email-mo">{{ item.nguoiDung.email }}</div>
              </td>
              <td>{{ formatNgay(item.ngayMuon) }}</td>
              <td :class="{ 'qua-han': item.trangThai === 'QUA_HAN' }">{{ formatNgay(item.hanTra) }}</td>
              <td><span class="so-cuon">{{ item.soLuongCuon }}</span></td>
              <td><StatusBadge :nhan-hien="TRANG_THAI_MAP[item.trangThai].nhan" :loai="TRANG_THAI_MAP[item.trangThai].mau" /></td>
            </tr>
          </tbody>
        </table>
        <Pagination :trang-hien-tai="phanTrang.trangHienThiChoNguoiDung.value" :tong-trang="phanTrang.tongTrang.value" :tong-phan-tu="phanTrang.tongPhanTu.value" :kich-thuoc-trang="10" @doi-trang="phanTrang.denTrang" />
      </template>
    </div>
  </div>
</template>

<style scoped>
.muon-sach-list { animation:fadeInUp 0.4s ease; }
.thanh-cong-cu { display:flex; gap:0.75rem; margin-bottom:1rem; flex-wrap:wrap; }
.select-filter { padding:0.65rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; cursor:pointer; }
.select-filter option { background:#1a1a2e; }
.nut-them { padding:0.65rem 1.25rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-size:0.875rem; font-weight:600; white-space:nowrap; margin-left:auto; }
.bang-container { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; overflow:hidden; padding:1rem; }
.bang { width:100%; border-collapse:collapse; }
.bang th { padding:0.75rem 1rem; text-align:left; font-size:0.75rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--mau-chu-mo); border-bottom:1px solid rgba(255,255,255,0.08); }
.bang td { padding:0.875rem 1rem; font-size:0.875rem; border-bottom:1px solid rgba(255,255,255,0.04); }
.bang tr:last-child td { border-bottom:none; }
.bang tr:hover td { background:rgba(255,255,255,0.02); }
.ma-phieu { font-size:0.775rem; background:rgba(255,255,255,0.06); padding:0.2rem 0.5rem; border-radius:4px; }
.ten-nguoi { font-weight:600; }
.email-mo { font-size:0.775rem; color:var(--mau-chu-mo); }
.qua-han { color:#ff6b6b; font-weight:600; }
.so-cuon { display:inline-flex; align-items:center; justify-content:center; width:28px; height:28px; background:rgba(6,182,212,0.08); border-radius:50%; font-size:0.8rem; font-weight:700; color:var(--mau-chinh); }
</style>
