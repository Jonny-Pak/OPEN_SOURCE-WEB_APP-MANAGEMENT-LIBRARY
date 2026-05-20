<!--
  TraSachView.vue — Trang trả sách: hiển thị danh sách phiếu mượn đang mượn,
  chọn phiếu để xem chi tiết và xử lý trả sách.
-->
<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { traSachService } from '@/services/traSachService'
import { muonSachService } from '@/services/muonSachService'
import { useToast } from '@/composables/useToast'
import type { PhieuMuon } from '@/types/muonsach'
import type { TinhTrangVatLy } from '@/types/sach'
import EmptyState from '@/components/admin/shared/EmptyState.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import StatusBadge from '@/components/admin/shared/StatusBadge.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()

// ===== DANH SÁCH PHIẾU MƯỢN ĐANG MƯỢN =====
const dangTaiDanhSach = ref(false)
const danhSachPhieu = ref<any[]>([])

// ===== PHIẾU ĐANG XỬ LÝ =====
const phieuDaTim = ref<PhieuMuon | null>(null)
const dangTimPhieu = ref(false)
const tinhTrangTra = ref<Record<number, TinhTrangVatLy | 'MAT'>>({})
const dangTra = ref(false)
const barcodeScan = ref('')

function formatNgay(s?: string) {
  if (!s) return '—'
  return new Date(s).toLocaleDateString('vi-VN')
}

function laQuaHan(hanTra?: string): boolean {
  if (!hanTra) return false
  return new Date(hanTra) < new Date()
}


const tuKhoaTim = ref('')

const danhSachPhieuLoc = computed(() => {
  const kw = tuKhoaTim.value.trim().toLowerCase()
  if (!kw) return danhSachPhieu.value
  return danhSachPhieu.value.filter((p: any) => 
    (p.tenDocGia || '').toLowerCase().includes(kw)
  )
})

/** Tải danh sách phiếu mượn đang mượn */
async function taiDanhSachPhieu() {
  dangTaiDanhSach.value = true
  try {
    const apiClient = (await import('@/services/apiClient')).default
    const response = await apiClient.get<any>('/api/v1/phieu-muon?page=0&size=50&sortBy=ngayMuon&direction=desc')
    // Chỉ lấy phiếu chưa hoàn tất (đang mượn)
    danhSachPhieu.value = (response.content || []).filter(
      (p: any) => p.trangThaiPhieu === 'CHUA_HOAN_TAT'
    )
  } catch { toast.loi('Không thể tải danh sách phiếu mượn') }
  finally { dangTaiDanhSach.value = false }
}

/** Chọn 1 phiếu mượn để xử lý trả sách */
async function chonPhieu(maPhieuMuon: string) {
  dangTimPhieu.value = true
  phieuDaTim.value = null
  tinhTrangTra.value = {}
  try {
    const phieu = await muonSachService.layChiTiet(maPhieuMuon)
    phieuDaTim.value = phieu
    const chiTietList = phieu?.chiTietList || []
    chiTietList.forEach(ct => {
      tinhTrangTra.value[ct.maChiTiet] = 'TOT'
    })
  } catch { toast.loi('Không tìm thấy phiếu mượn') }
  finally { dangTimPhieu.value = false }
}

/** Quay lại danh sách */
function quayLai() {
  phieuDaTim.value = null
}

async function traTheoMaVach() {
  const ma = barcodeScan.value.trim()
  if (!ma || !phieuDaTim.value) return
  
  const ct = phieuDaTim.value.chiTietList?.find(x => x.maBarcodeVatLy === ma)
  if (!ct) {
    toast.canhBao('Mã vạch không thuộc phiếu mượn này!')
    return
  }

  const tinhTrang = tinhTrangTra.value[ct.maChiTiet] || 'TOT'
  dangTra.value = true
  try {
    if (tinhTrang === 'MAT') {
      await traSachService.baoMatSach(String(ct.maChiTiet))
      toast.thanhCong('Đã báo mất sách thành công')
    } else {
      await traSachService.traCuonSach({ maChiTietPhieuMuon: String(ct.maChiTiet), tinhTrangVatLyKhiTra: tinhTrang })
      toast.thanhCong('Đã trả cuốn sách: ' + ma)
    }
    barcodeScan.value = ''
    await chonPhieu(String(phieuDaTim.value.maPhieuMuon))
  } catch (err: any) {
    toast.loi(err?.message || 'Trả sách thất bại')
  } finally {
    dangTra.value = false
  }
}

async function xacNhanTraToanBo() {
  if (!phieuDaTim.value) return
  dangTra.value = true
  try {
    await traSachService.traToanBo({ maPhieuMuon: String(phieuDaTim.value.maPhieuMuon), ghiChu: 'Trả toàn bộ, trạng thái mặc định: Tốt' })
    toast.thanhCong('Trả toàn bộ sách thành công!')
    phieuDaTim.value = null
    await taiDanhSachPhieu() // Reload danh sách
  } catch { toast.loi('Trả sách thất bại') }
  finally { dangTra.value = false }
}

onMounted(async () => {
  // Nếu đến từ link Trả/Phạt, tự chọn phiếu luôn
  const qPhieu = route.query.maPhieuMuon as string
  if (qPhieu) {
    await chonPhieu(qPhieu)
  }
  // Luôn tải danh sách phiếu nền
  await taiDanhSachPhieu()
})
</script>


<template>
  <div class="tra-sach">
    <!-- ===== BƯỚC 1: DANH SÁCH PHIẾU MƯỢN ĐANG MƯỢN ===== -->
    <div v-if="!phieuDaTim && !dangTimPhieu" class="noi-dung-tab">
      <div class="thanh-cong-cu">
        <div class="vung-tim-kiem">
          <font-awesome-icon icon="fa-solid fa-magnifying-glass" class="icon-tim-kiem" />
          <input v-model="tuKhoaTim" class="input-tk" placeholder="Tìm theo tên độc giả..." />
        </div>
      </div>
      <div class="bang-container">
        <SkeletonLoader v-if="dangTaiDanhSach" :rows="5" height="52px" />
        <template v-else>
          <EmptyState v-if="danhSachPhieu.length === 0" thong-diep="Không có phiếu mượn nào đang mượn" mo-ta="Tất cả sách đã được trả hoặc chưa có phiếu mượn" />
          <template v-else>
            <p class="huong-dan">Chọn phiếu mượn cần xử lý trả sách:</p>
            <table class="bang">
              <thead>
                <tr>
                  <th>Độc giả</th>
                  <th>Ngày mượn</th>
                  <th>Số cuốn</th>
                  <th>Trạng thái</th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in danhSachPhieuLoc" :key="item.maPhieuMuon" class="dong-chon" @click="chonPhieu(item.maPhieuMuon)">
                  <td>
                    <div class="ten-nguoi">{{ item.tenDocGia || '—' }}</div>
                  </td>
                  <td>{{ item.ngayMuon ? formatNgay(item.ngayMuon) : '—' }}</td>
                  <td><span class="so-cuon">{{ item.danhSachChiTiet?.length || 0 }}</span></td>
                  <td>
                    <StatusBadge nhan-hien="Đang mượn" loai="xanh-duong" />
                  </td>
                  <td>
                    <button class="nut-chon-tra" @click.stop="chonPhieu(item.maPhieuMuon)">
                      <font-awesome-icon icon="fa-solid fa-arrow-right-to-bracket" /> Chọn để trả
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </template>
        </template>
      </div>
    </div>

    <!-- Loading khi đang tìm phiếu -->
    <div v-if="dangTimPhieu" class="dang-tai-phieu">
      <SkeletonLoader :rows="4" height="52px" />
    </div>

    <!-- ===== BƯỚC 2: XỬ LÝ TRẢ SÁCH ===== -->
    <div v-if="phieuDaTim" class="noi-dung-tab">
      <!-- Nút quay lại -->
      <button class="nut-quay-lai" @click="quayLai">
        <font-awesome-icon icon="fa-solid fa-arrow-left" /> Quay lại danh sách
      </button>

      <div class="ket-qua-phieu">
        <!-- Thông tin độc giả -->
        <div class="thong-tin-nguoi-muon">
          <div class="info-item"><span class="label">Độc giả:</span> <strong>{{ phieuDaTim.nguoiDung?.hoDem || '' }} {{ phieuDaTim.nguoiDung?.ten || '' }}</strong></div>
          <div class="info-item"><span class="label">Email:</span> {{ phieuDaTim.nguoiDung?.email || 'N/A' }}</div>
          <div class="info-item"><span class="label">Ngày mượn:</span> {{ formatNgay(phieuDaTim.ngayMuon) }}</div>
          <div class="info-item"><span class="label">Hạn trả:</span>
            <span :class="{ 'text-do': laQuaHan(phieuDaTim.hanTra) }">{{ formatNgay(phieuDaTim.hanTra) }}</span>
          </div>
        </div>

        <!-- Danh sách sách đang mượn -->
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom: 0.75rem;">
          <h4 class="tieu-de-danh-sach" style="margin-bottom:0">Danh sách sách cần trả ({{ (phieuDaTim?.chiTietList || []).length }} cuốn)</h4>
          <div class="input-ket-hop" style="width: 250px;">
            <input v-model="barcodeScan" class="form-input" style="padding:0.4rem 0.8rem;font-size:0.85rem;" placeholder="Quét mã vạch trả lẻ..." @keyup.enter="traTheoMaVach" />
          </div>
        </div>
        <div class="danh-sach-tra">
          <div v-for="ct in (phieuDaTim?.chiTietList || [])" :key="ct.maChiTiet" class="item-tra" :class="{ 'item-tra--qua-han': laQuaHan(ct.hanTra) }">
            <div class="item-tra-info">
              <div class="ten-sach-tra">{{ ct.tenSach }}</div>
              <div class="ma-vach-tra">
                <code>{{ ct.maBarcodeVatLy }}</code>
                <span v-if="laQuaHan(ct.hanTra)" class="tag-qua-han">
                  <font-awesome-icon icon="fa-solid fa-triangle-exclamation" /> Quá hạn ({{ formatNgay(ct.hanTra) }})
                </span>
              </div>
            </div>
            <div class="item-tra-tinh-trang" style="align-items: flex-end;">
              <label style="font-size:0.8rem;color:var(--mau-chu-mo)">Tình trạng trả:</label>
              <select v-model="tinhTrangTra[ct.maChiTiet]" class="select-tinh-trang">
                <option value="TOT">Tốt</option>
                <option value="HU_HONG">Hư hỏng</option>
                <option value="MAT">Mất</option>
              </select>
              <button 
                v-if="laQuaHan(ct.hanTra)" 
                class="nut-phat-qua-han"
                @click="router.push(`/admin/phat?maChiTietPhieuMuon=${ct.maChiTiet}`)"
                style="margin-top: 5px; padding: 0.25rem 0.5rem; background: rgba(239, 68, 68, 0.15); border: 1px solid rgba(239, 68, 68, 0.3); border-radius: 6px; color: #ef4444; cursor: pointer; font-size: 0.75rem; font-weight: 600; display: inline-flex; align-items: center; gap: 3px;"
              >
                <font-awesome-icon icon="fa-solid fa-triangle-exclamation" /> Phạt quá hạn
              </button>
            </div>
          </div>
        </div>

        <div class="nhom-nut">
          <button class="nut-huy" @click="quayLai">Quay lại</button>
          <button class="nut-chinh" :disabled="dangTra" @click="xacNhanTraToanBo">
            <span v-if="dangTra">Đang xử lý...</span>
            <span v-else><font-awesome-icon icon="fa-solid fa-circle-check" /> Trả toàn bộ (Mặc định: Tốt)</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tra-sach { animation:fadeInUp 0.4s ease; padding-top: 1rem; }
.noi-dung-tab { animation:fadeInUp 0.3s ease; }

.thanh-cong-cu { display:flex; gap:0.75rem; margin-bottom:1rem; flex-wrap:wrap; }
.vung-tim-kiem { position:relative; display:flex; align-items:center; max-width:400px; flex:1; }
.icon-tim-kiem { position:absolute; left:1rem; color:var(--mau-chu-mo); pointer-events:none; }
.input-tk { width:100%; padding:0.65rem 1rem 0.65rem 2.5rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.875rem; outline:none; box-sizing:border-box; }
.input-tk:focus { border-color:var(--mau-chinh); box-shadow:0 0 0 3px rgba(6,182,212,0.15); }

/* Hướng dẫn */
.huong-dan { font-size:0.875rem; color:var(--mau-chu-mo); margin-bottom:1rem; }

/* Bảng danh sách phiếu */
.bang-container { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; overflow:hidden; padding:1rem; }
.bang { width:100%; border-collapse:collapse; }
.bang th { padding:0.75rem 1rem; text-align:left; font-size:0.75rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--mau-chu-mo); border-bottom:1px solid rgba(255,255,255,0.08); }
.bang td { padding:0.875rem 1rem; font-size:0.875rem; border-bottom:1px solid rgba(255,255,255,0.04); vertical-align:middle; }
.bang tr:last-child td { border-bottom:none; }
.dong-chon { cursor:pointer; transition:background 0.15s; }
.dong-chon:hover td { background:rgba(6,182,212,0.06); }
.ten-nguoi { font-weight:600; }
.so-cuon { display:inline-flex; align-items:center; justify-content:center; width:28px; height:28px; background:rgba(6,182,212,0.08); border-radius:50%; font-size:0.8rem; font-weight:700; color:var(--mau-chinh); }
.nut-chon-tra { padding:0.4rem 0.85rem; background:rgba(6,182,212,0.12); border:1px solid rgba(6,182,212,0.25); border-radius:6px; color:var(--mau-chinh); cursor:pointer; font-size:0.8rem; font-weight:600; font-family:inherit; transition:all 0.2s; }
.nut-chon-tra:hover { background:rgba(6,182,212,0.2); border-color:rgba(6,182,212,0.4); }

/* Loading */
.dang-tai-phieu { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; padding:1.5rem; }

/* Nút quay lại */
.nut-quay-lai { padding:0.5rem 1rem; background:none; border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; font-size:0.85rem; margin-bottom:1rem; transition:all 0.2s; display:inline-flex; align-items:center; gap:0.4rem; }
.nut-quay-lai:hover { background:rgba(6,182,212,0.06); color:var(--mau-chinh); border-color:rgba(6,182,212,0.3); }

/* Form input */
.form-group { display:flex; flex-direction:column; gap:0.375rem; }
.form-group label { font-size:0.825rem; font-weight:600; color:var(--mau-chu); }
.input-ket-hop { display:flex; gap:0.5rem; }
.form-input { padding:0.75rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.9rem; outline:none; flex:1; }
.form-input:focus { border-color:var(--mau-chinh); }

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
.select-tinh-trang option { background:#1a1a2e; color:#ffffff; }

/* Nút chung */
.nhom-nut { display:flex; justify-content:flex-end; gap:0.75rem; }
.nut-huy { padding:0.75rem 1.5rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; }
.nut-chinh { padding:0.75rem 1.75rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-size:0.9rem; font-weight:700; }
.nut-chinh:disabled { opacity:0.6; cursor:not-allowed; }
</style>
