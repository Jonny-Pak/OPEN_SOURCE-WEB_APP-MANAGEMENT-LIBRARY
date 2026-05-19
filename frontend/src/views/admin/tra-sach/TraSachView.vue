<!--
  TraSachView.vue — 2 tabs: Trả sách và Duyệt gia hạn.
-->
<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { traSachService } from '@/services/traSachService'
import { muonSachService } from '@/services/muonSachService'
import { useToast } from '@/composables/useToast'
import type { PhieuMuon, TraSachChiTietItem } from '@/types/muonsach'

const route = useRoute()
const router = useRouter()
import type { TinhTrangVatLy } from '@/types/sach'
import EmptyState from '@/components/admin/shared/EmptyState.vue'

const toast = useToast()

// ===== TAB 1: TRẢ SÁCH =====
const tuKhoaPhieu = ref('')
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

async function timPhieuMuon() {
  const query = tuKhoaPhieu.value.trim()
  if (!query) return
  dangTimPhieu.value = true
  phieuDaTim.value = null
  tinhTrangTra.value = {}
  try {
    const phieu = await muonSachService.layChiTiet(query)
    phieuDaTim.value = phieu
    // Khởi tạo tình trạng trả mặc định là TOT
    const chiTietList = phieu?.chiTietList || []
    chiTietList.forEach(ct => {
      tinhTrangTra.value[ct.maChiTiet] = 'TOT'
    })
  } catch { toast.loi('Không tìm thấy phiếu mượn') }
  finally { dangTimPhieu.value = false }
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
    await timPhieuMuon() // Reload list
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
    tuKhoaPhieu.value = ''
  } catch { toast.loi('Trả sách thất bại') }
  finally { dangTra.value = false }
}

onMounted(async () => {
  const qPhieu = route.query.maPhieuMuon as string
  if (qPhieu) {
    tuKhoaPhieu.value = qPhieu
    await timPhieuMuon()
  }
})
</script>


<template>
  <div class="tra-sach">
    <div class="noi-dung-tab">
      <div class="tim-phieu">
        <div class="form-group">
          <label>Nhập mã phiếu mượn để tìm kiếm</label>
          <div class="input-ket-hop">
            <input v-model="tuKhoaPhieu" class="form-input" placeholder="Nhập mã phiếu mượn (UUID)..." @keyup.enter="timPhieuMuon" />
            <button class="nut-tim" :disabled="dangTimPhieu" @click="timPhieuMuon">
              <span v-if="dangTimPhieu">...</span>
              <span v-else><font-awesome-icon icon="fa-solid fa-magnifying-glass" /> Tìm</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Kết quả phiếu mượn -->
      <div v-if="phieuDaTim" class="ket-qua-phieu">
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
          <button class="nut-huy" @click="phieuDaTim = null">Hủy</button>
          <button class="nut-chinh" :disabled="dangTra" @click="xacNhanTraToanBo">
            <span v-if="dangTra">Đang xử lý...</span>
            <span v-else><font-awesome-icon icon="fa-solid fa-circle-check" /> Trả toàn bộ (Mặc định: Tốt)</span>
          </button>
        </div>
      </div>

    <div v-if="!phieuDaTim && !dangTimPhieu" class="trang-thai-trong">
        <EmptyState thong-diep="Nhập mã phiếu mượn để bắt đầu" mo-ta="Hệ thống sẽ hiển thị thông tin và danh sách sách cần trả" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.tra-sach { animation:fadeInUp 0.4s ease; }
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
.select-tinh-trang option { background:#1a1a2e; color:#ffffff; }


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
