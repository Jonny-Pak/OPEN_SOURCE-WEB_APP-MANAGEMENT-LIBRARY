<template>
  <div class="muon-sach-list">
    <div class="thanh-cong-cu">
      <div class="vung-tim-kiem">
        <font-awesome-icon icon="fa-solid fa-magnifying-glass" class="icon-tim-kiem" />
        <input v-model="tuKhoaTim" class="input-tk" placeholder="Tìm theo tên độc giả..." />
      </div>
      <select v-model="filterTrangThai" class="select-filter">
        <option value="">Tất cả trạng thái</option>
        <option value="CHUA_HOAN_TAT">Đang mượn</option>
        <option value="DA_HOAN_TAT">Đã hoàn tất</option>
        <option value="DA_HUY">Đã hủy</option>
      </select>
      <button class="nut-them" @click="router.push('/admin/muon-sach/tao-moi')">
        <font-awesome-icon icon="fa-solid fa-plus" /> Tạo phiếu mượn
      </button>
    </div>

    <div class="bang-container">
      <SkeletonLoader v-if="dangTai" :rows="7" height="52px" />
      <template v-else>
        <EmptyState v-if="danhSach.length === 0" thong-diep="Không có phiếu mượn nào" />
        <table v-else class="bang">
          <thead><tr>
            <th>Mã phiếu</th><th>Độc giả</th><th>Ngày mượn</th>
            <th>Số cuốn</th><th>Trạng thái</th><th>Hành động</th>
          </tr></thead>
          <tbody>
            <tr v-for="item in danhSachLoc" :key="item.maPhieuMuon">
              <td><code class="ma-phieu">#{{ String(item.maPhieuMuon).substring(0,8) }}...</code></td>
              <td>
                <div class="ten-nguoi">{{ item.tenDocGia || '' }}</div>
              </td>
              <td>{{ item.ngayMuon ? formatNgay(item.ngayMuon) : '—' }}</td>
              <td><span class="so-cuon">{{ item.danhSachChiTiet?.length || 0 }}</span></td>
              <td>
                <StatusBadge
                  :nhan-hien="TRANG_THAI_MAP[item.trangThaiPhieu]?.nhan || 'Không rõ'"
                  :loai="TRANG_THAI_MAP[item.trangThaiPhieu]?.mau || 'xam'"
                />
              </td>
              <td>
                <div class="hanh-dong-cell">
                  <button class="nut-chi-tiet" @click="moChiTiet(item)">
                    <font-awesome-icon icon="fa-solid fa-magnifying-glass" /> Chi tiết
                  </button>
                  <button
                    class="nut-tra-phat"
                    @click="router.push(`/admin/tra-sach?maPhieuMuon=${item.maPhieuMuon}`)"
                  >
                    <font-awesome-icon icon="fa-solid fa-file-invoice" /> Trả/Phạt
                  </button>
                </div>
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

    <!-- Modal Chi tiết Phiếu mượn -->
    <ModalDialog
      v-if="phieuChiTiet"
      :dang-mo="phieuChiTiet !== null"
      :tieu-de="`Chi tiết phiếu mượn #${String(phieuChiTiet.maPhieuMuon).substring(0,8)}...`"
      @dong="phieuChiTiet = null"
    >
      <div class="chi-tiet-modal">
        <div class="thong-tin-nguoi">
          <div class="info-row"><span>Độc giả:</span> <strong>{{ phieuChiTiet.tenDocGia }}</strong></div>
          <div class="info-row"><span>Ngày mượn:</span> {{ phieuChiTiet.ngayMuon ? formatNgay(phieuChiTiet.ngayMuon) : '—' }}</div>
          <div class="info-row">
            <span>Trạng thái:</span>
            <StatusBadge
              :nhan-hien="TRANG_THAI_MAP[phieuChiTiet.trangThaiPhieu]?.nhan || 'Không rõ'"
              :loai="TRANG_THAI_MAP[phieuChiTiet.trangThaiPhieu]?.mau || 'xam'"
            />
          </div>
        </div>

        <h4 class="tieu-de-ds">Danh sách sách ({{ phieuChiTiet.danhSachChiTiet?.length || 0 }} cuốn)</h4>
        <div class="ds-cuon-sach">
          <div
            v-for="ct in phieuChiTiet.danhSachChiTiet"
            :key="ct.maChiTietPhieuMuon"
            class="cuon-item"
            :class="{
              'cuon-item--tra': ct.trangThaiChiTietPhieuMuon === 'DA_TRA' || ct.trangThaiChiTietPhieuMuon === 'DA_TRA_TRE',
              'cuon-item--qua-han': ct.trangThaiChiTietPhieuMuon === 'QUA_HAN',
              'cuon-item--mat': ct.trangThaiChiTietPhieuMuon === 'MAT_SACH',
            }"
          >
            <div class="cuon-top">
              <span class="ten-sach">{{ ct.tenSach }}</span>
              <span class="badge-ct" :class="badgeCtClass(ct.trangThaiChiTietPhieuMuon)">
                {{ CHITIET_LABEL[ct.trangThaiChiTietPhieuMuon] || ct.trangThaiChiTietPhieuMuon }}
              </span>
            </div>
            <div class="cuon-bottom">
              <span class="label-mo">Mã vạch:</span> <code>{{ ct.maVach }}</code>
              <span class="sep">|</span>
              <span class="label-mo">Hạn trả:</span>
              <span :class="{ 'text-do': isQuaHan(ct.hanTraHienTai, ct.trangThaiChiTietPhieuMuon) }">
                {{ ct.hanTraHienTai ? formatNgay(ct.hanTraHienTai) : '—' }}
              </span>
              <template v-if="ct.ngayTraThucTe">
                <span class="sep">|</span>
                <span class="label-mo">Ngày trả:</span> {{ formatNgay(ct.ngayTraThucTe) }}
              </template>
              <template v-if="ct.soLanGiaHan">
                <span class="sep">|</span>
                <span class="label-mo">Gia hạn:</span> {{ ct.soLanGiaHan }} lần
              </template>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <button class="nut-dong" @click="phieuChiTiet = null">Đóng</button>
        <button
          class="nut-di-tra"
          @click="() => { router.push(`/admin/tra-sach?maPhieuMuon=${phieuChiTiet!.maPhieuMuon}`); phieuChiTiet = null }"
        >
          <font-awesome-icon icon="fa-solid fa-arrow-right-to-bracket" /> Đến trang trả sách
        </button>
      </template>
    </ModalDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { muonSachService } from '@/services/muonSachService'
import { usePagination } from '@/composables/usePagination'
import { useToast } from '@/composables/useToast'
import Pagination from '@/components/admin/shared/Pagination.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import EmptyState from '@/components/admin/shared/EmptyState.vue'
import StatusBadge from '@/components/admin/shared/StatusBadge.vue'
import ModalDialog from '@/components/admin/shared/ModalDialog.vue'

const router = useRouter()
const toast = useToast()
const phanTrang = usePagination()

const dangTai = ref(false)
const danhSach = ref<any[]>([])
const filterTrangThai = ref('')
const phieuChiTiet = ref<any | null>(null)
const tuKhoaTim = ref('')

import { computed } from 'vue'
const danhSachLoc = computed(() => {
  const kw = tuKhoaTim.value.trim().toLowerCase()
  if (!kw) return danhSach.value
  return danhSach.value.filter((item: any) =>
    (item.tenDocGia || '').toLowerCase().includes(kw)
  )
})

const TRANG_THAI_MAP: Record<string, { nhan: string; mau: 'xanh-duong' | 'xanh' | 'do' | 'xam' | 'vang' }> = {
  CHUA_HOAN_TAT: { nhan: 'Đang mượn', mau: 'xanh-duong' },
  DA_HOAN_TAT:   { nhan: 'Đã hoàn tất', mau: 'xanh' },
  DA_HUY:        { nhan: 'Đã hủy', mau: 'xam' },
}

const CHITIET_LABEL: Record<string, string> = {
  DANG_MUON:  'Đang mượn',
  QUA_HAN:    'Quá hạn',
  DA_TRA:     'Đã trả',
  DA_TRA_TRE: 'Trả trễ',
  MAT_SACH:   'Mất sách',
  DA_HUY:     'Đã hủy',
}

function formatNgay(s: string) {
  return new Date(s).toLocaleDateString('vi-VN')
}

function isQuaHan(hanTra: string, trangThai: string): boolean {
  if (trangThai === 'DA_TRA' || trangThai === 'DA_TRA_TRE') return false
  return new Date(hanTra) < new Date()
}

function badgeCtClass(tt: string): string {
  if (tt === 'DA_TRA') return 'badge-xanh'
  if (tt === 'DA_TRA_TRE') return 'badge-vang'
  if (tt === 'QUA_HAN' || tt === 'MAT_SACH') return 'badge-do'
  if (tt === 'DANG_MUON') return 'badge-xanh-duong'
  return 'badge-xam'
}

async function taiDanhSach() {
  dangTai.value = true
  try {
    const params = new URLSearchParams()
    params.append('page', phanTrang.trangHienTai.value.toString())
    params.append('size', '10')
    if (filterTrangThai.value) params.append('trangThaiPhieu', filterTrangThai.value)
    const response = await (await import('@/services/apiClient')).default.get<any>(`/api/v1/phieu-muon?${params}`)
    danhSach.value = response.content || []
    phanTrang.capNhatTong(response.totalElements || 0)
  } catch { toast.loi('Không thể tải danh sách phiếu mượn') }
  finally { dangTai.value = false }
}

function moChiTiet(item: any) {
  phieuChiTiet.value = item
}

watch([filterTrangThai, () => phanTrang.trangHienTai.value], taiDanhSach)
onMounted(taiDanhSach)
</script>

<style scoped>
.muon-sach-list { animation: fadeInUp 0.4s ease; }
.thanh-cong-cu { display: flex; gap: 0.75rem; margin-bottom: 1rem; flex-wrap: wrap; }
.vung-tim-kiem { position: relative; display: flex; align-items: center; flex: 1; min-width: 200px; }
.icon-tim-kiem { position: absolute; left: 1rem; color: var(--mau-chu-mo); pointer-events: none; }
.input-tk { width: 100%; padding: 0.65rem 1rem 0.65rem 2.5rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; color: var(--mau-chu); font-family: inherit; font-size: 0.875rem; outline: none; box-sizing: border-box; }
.input-tk:focus { border-color: var(--mau-chinh); box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.15); }
.select-filter { padding: 0.65rem 1rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; color: var(--mau-chu); font-family: inherit; cursor: pointer; }
.select-filter option { background: #1a1a2e; color: #ffffff; }
.nut-them { padding: 0.65rem 1.25rem; background: var(--color-primary); border: none; border-radius: 8px; color: white; cursor: pointer; font-family: inherit; font-size: 0.875rem; font-weight: 600; white-space: nowrap; margin-left: auto; }
.bang-container { background: var(--glass-nen); border: 1px solid var(--glass-vien); border-radius: 12px; overflow: hidden; padding: 1rem; }
.bang { width: 100%; border-collapse: collapse; }
.bang th { padding: 0.75rem 1rem; text-align: left; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em; color: var(--mau-chu-mo); border-bottom: 1px solid rgba(255,255,255,0.08); }
.bang td { padding: 0.875rem 1rem; font-size: 0.875rem; border-bottom: 1px solid rgba(255,255,255,0.04); vertical-align: middle; }
.bang tr:last-child td { border-bottom: none; }
.bang tr:hover td { background: rgba(255,255,255,0.02); }
.ma-phieu { font-size: 0.775rem; background: rgba(255,255,255,0.06); padding: 0.2rem 0.5rem; border-radius: 4px; }
.ten-nguoi { font-weight: 600; }
.so-cuon { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: rgba(6,182,212,0.08); border-radius: 50%; font-size: 0.8rem; font-weight: 700; color: var(--mau-chinh); }
.hanh-dong-cell { display: flex; gap: 0.4rem; flex-wrap: wrap; }
.nut-chi-tiet { padding: 0.35rem 0.65rem; background: rgba(139,92,246,0.12); border: 1px solid rgba(139,92,246,0.25); border-radius: 6px; color: #a78bfa; cursor: pointer; font-size: 0.8rem; font-weight: 600; }
.nut-tra-phat { padding: 0.35rem 0.65rem; background: rgba(6,182,212,0.12); border: 1px solid rgba(6,182,212,0.25); border-radius: 6px; color: var(--mau-chinh); cursor: pointer; font-size: 0.8rem; font-weight: 600; }

/* Modal chi tiết */
.chi-tiet-modal { display: flex; flex-direction: column; gap: 1rem; }
.thong-tin-nguoi { background: rgba(6,182,212,0.06); border: 1px solid rgba(6,182,212,0.15); border-radius: 10px; padding: 1rem; display: flex; flex-direction: column; gap: 0.5rem; }
.info-row { display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem; }
.info-row span:first-child { color: var(--mau-chu-mo); min-width: 90px; }
.tieu-de-ds { font-size: 0.9rem; font-weight: 700; color: var(--mau-chinh); margin: 0; }
.ds-cuon-sach { display: flex; flex-direction: column; gap: 0.5rem; max-height: 340px; overflow-y: auto; }
.cuon-item { padding: 0.875rem; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; }
.cuon-item--tra { border-color: rgba(81,207,102,0.2); }
.cuon-item--qua-han { border-color: rgba(255,107,107,0.3); background: rgba(255,107,107,0.04); }
.cuon-item--mat { border-color: rgba(239,68,68,0.4); background: rgba(239,68,68,0.06); }
.cuon-top { display: flex; align-items: center; justify-content: space-between; gap: 0.5rem; margin-bottom: 0.4rem; }
.ten-sach { font-weight: 600; font-size: 0.875rem; }
.cuon-bottom { display: flex; align-items: center; gap: 0.5rem; font-size: 0.775rem; flex-wrap: wrap; }
.label-mo { color: var(--mau-chu-mo); }
.sep { color: rgba(255,255,255,0.15); }
.text-do { color: #ff6b6b; font-weight: 600; }
.badge-ct { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.725rem; font-weight: 700; white-space: nowrap; }
.badge-xanh { background: rgba(81,207,102,0.15); color: #51cf66; }
.badge-vang { background: rgba(255,212,59,0.15); color: #ffd43b; }
.badge-do { background: rgba(255,107,107,0.15); color: #ff6b6b; }
.badge-xanh-duong { background: rgba(6,182,212,0.15); color: #06b6d4; }
.badge-xam { background: rgba(156,163,175,0.15); color: #9ca3af; }
.nut-dong { padding: 0.65rem 1.25rem; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; color: var(--mau-chu-mo); cursor: pointer; font-family: inherit; }
.nut-di-tra { padding: 0.65rem 1.5rem; background: var(--color-primary); border: none; border-radius: 8px; color: white; cursor: pointer; font-family: inherit; font-weight: 600; }
</style>
