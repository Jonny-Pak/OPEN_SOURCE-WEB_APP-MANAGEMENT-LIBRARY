<!--
  TaoPhieuMuonView.vue — Tạo phiếu mượn sách luồng 2 bước.
  Bước 1: Chọn độc giả + hạn trả. Bước 2: Quét mã vạch thêm sách.
-->
<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { muonSachService } from '@/services/muonSachService'
import { docGiaService } from '@/services/docGiaService'
import { useToast } from '@/composables/useToast'
import type { NguoiDung } from '@/types/nguoidung'

const router = useRouter()
const toast = useToast()

const buocHienTai = ref(1) // Keep for transition, though only 1 step now
const dangGui = ref(false)

// === THÔNG TIN ĐỘC GIẢ ===
const tuKhoaDocGia = ref('')
const danhSachTatCaDocGia = ref<NguoiDung[]>([])
const danhSachDocGia = ref<NguoiDung[]>([])
const docGiaDaChon = ref<NguoiDung | null>(null)
const hanTra = ref('')
const dangTimKiem = ref(false)
let boDem: ReturnType<typeof setTimeout> | null = null

async function timKiemDocGia() {
  if (boDem) clearTimeout(boDem)
  if (!tuKhoaDocGia.value.trim()) { danhSachDocGia.value = []; return }
  boDem = setTimeout(async () => {
    dangTimKiem.value = true
    try {
      if (danhSachTatCaDocGia.value.length === 0) {
        const response = await docGiaService.danhSach()
        danhSachTatCaDocGia.value = response.content
      }
      const kw = tuKhoaDocGia.value.trim().toLowerCase()
      danhSachDocGia.value = danhSachTatCaDocGia.value
        .filter((nd) => {
          const fullName = `${nd.hoDem} ${nd.ten}`.toLowerCase()
          return (
            fullName.includes(kw) ||
            nd.email.toLowerCase().includes(kw) ||
            nd.soDienThoai.toLowerCase().includes(kw) ||
            nd.maNguoiDung.toLowerCase().includes(kw)
          )
        })
        .slice(0, 5)
    } catch { /* im lặng */ } finally { dangTimKiem.value = false }
  }, 300)
}

function chonDocGia(nd: NguoiDung) {
  docGiaDaChon.value = nd
  tuKhoaDocGia.value = `${nd.hoDem} ${nd.ten}`
  danhSachDocGia.value = []
}

// === MÃ VẠCH SÁCH ===
const inputMaVach = ref('')
const danhSachDaQue = ref<string[]>([])

async function themMaVach() {
  const ma = inputMaVach.value.trim()
  if (!ma) return
  if (danhSachDaQue.value.includes(ma)) { toast.canhBao(`Mã '${ma}' đã được quét`); return }
  danhSachDaQue.value.push(ma)
  inputMaVach.value = ''
}

function xoaMaVach(ma: string) { danhSachDaQue.value = danhSachDaQue.value.filter(m => m !== ma) }

async function hoanTat() {
  if (!docGiaDaChon.value) return toast.canhBao('Vui lòng chọn độc giả')
  if (danhSachDaQue.value.length === 0) return toast.canhBao('Vui lòng quét ít nhất 1 cuốn sách')
  
  dangGui.value = true
  try {
    await muonSachService.taoCai({ 
      maNguoiDung: docGiaDaChon.value.maNguoiDung, 
      danhSachMaBarcodeVatLy: danhSachDaQue.value 
    })
    toast.thanhCong('Tạo phiếu mượn thành công!')
    router.push('/admin/muon-sach')
  } catch (err: any) { 
    toast.loi(err?.message || 'Không thể tạo phiếu mượn, kiểm tra lại điều kiện mượn') 
  } finally { 
    dangGui.value = false 
  }
}
</script>

<template>
  <div class="tao-phieu">
    <div class="dau-trang">
      <button class="nut-quay-lai" @click="router.push('/admin/muon-sach')">← Quay lại</button>
      <h2>Tạo phiếu mượn sách</h2>
    </div>

    <!-- ===== TẠO PHIẾU DUY NHẤT ===== -->
    <div class="the-buoc">
      <h3 class="tieu-de-buoc">Thông tin độc giả</h3>

      <div class="form-group">
        <label>Tìm và chọn độc giả *</label>
        <div class="tim-kiem-wrapper">
          <input
            v-model="tuKhoaDocGia"
            class="form-input"
            placeholder="Nhập tên, email hoặc SĐT để tìm..."
            @input="timKiemDocGia"
            autocomplete="off"
          />
          <!-- Kết quả gợi ý -->
          <div v-if="danhSachDocGia.length > 0" class="ket-qua-goi-y">
            <div
              v-for="nd in danhSachDocGia"
              :key="nd.maNguoiDung"
              class="goi-y-item"
              @click="chonDocGia(nd)"
            >
              <div class="goi-y-ten">{{ nd.hoDem }} {{ nd.ten }}</div>
              <div class="goi-y-email">{{ nd.email }} · {{ nd.soDienThoai }}</div>
            </div>
          </div>
        </div>
        <div v-if="docGiaDaChon" class="doc-gia-da-chon">
          ✅ Đã chọn: <strong>{{ docGiaDaChon.hoDem }} {{ docGiaDaChon.ten }}</strong> — {{ docGiaDaChon.email }}
        </div>
      </div>

      <hr class="duong-chia" />
      <h3 class="tieu-de-buoc mt-4">Quét mã vạch cuốn sách</h3>

      <div class="form-group">
        <label>Mã vạch cuốn sách (nhấn Enter để thêm)</label>
        <div class="input-quet">
          <input
            v-model="inputMaVach"
            class="form-input input-ma-vach"
            placeholder="Quét hoặc nhập mã vạch..."
            autofocus
            @keyup.enter="themMaVach"
          />
          <button class="nut-them-ma" @click="themMaVach">Thêm</button>
        </div>
      </div>

      <!-- Danh sách đã quét -->
      <div v-if="danhSachDaQue.length > 0" class="danh-sach-da-que">
        <div class="tieu-de-danh-sach">Đã quét {{ danhSachDaQue.length }} cuốn:</div>
        <div v-for="(ma, idx) in danhSachDaQue" :key="ma" class="item-ma-vach">
          <span class="stt">{{ idx + 1 }}</span>
          <code class="ma-code">{{ ma }}</code>
          <button class="nut-xoa-ma" @click="xoaMaVach(ma)">✕</button>
        </div>
      </div>
      <div v-else class="chua-co-sach">
        <span>📭 Chưa quét cuốn sách nào. Quét mã vạch để thêm vào phiếu.</span>
      </div>

      <div class="nhom-nut">
        <button class="nut-huy" @click="router.push('/admin/muon-sach')">Hủy bỏ</button>
        <button class="nut-chinh" :disabled="dangGui || danhSachDaQue.length === 0 || !docGiaDaChon" @click="hoanTat">
          {{ dangGui ? 'Đang xử lý...' : `✅ Tạo phiếu mượn (${danhSachDaQue.length} cuốn)` }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tao-phieu { animation:fadeInUp 0.4s ease; max-width:700px; }
.dau-trang { display:flex; align-items:center; gap:1rem; margin-bottom:1.5rem; }
.dau-trang h2 { font-size:1.2rem; font-weight:700; }
.nut-quay-lai { background:none; border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; padding:0.5rem 1rem; font-family:inherit; font-size:0.875rem; transition:all 0.2s; }
.nut-quay-lai:hover { background:rgba(255,255,255,0.06); color:var(--mau-chu); }

/* Card bước */
.the-buoc { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:16px; padding:2rem; }
.tieu-de-buoc { font-size:1rem; font-weight:700; color:var(--mau-chinh); margin-bottom:1.5rem; }
.mt-4 { margin-top: 1.5rem; }
.duong-chia { border:0; border-top:1px solid rgba(255,255,255,0.08); margin:1.5rem 0; }

/* Form */
.form-group { margin-bottom:1.25rem; display:flex; flex-direction:column; gap:0.375rem; }
.form-group label { font-size:0.825rem; font-weight:600; color:var(--mau-chu); }
.form-input { padding:0.75rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.9rem; outline:none; transition:border-color 0.2s; }
.form-input:focus { border-color:var(--mau-chinh); }

/* Tìm kiếm dropdown */
.tim-kiem-wrapper { position:relative; }
.ket-qua-goi-y { position:absolute; top:calc(100% + 4px); left:0; right:0; background:#1e1e35; border:1px solid rgba(255,255,255,0.12); border-radius:10px; z-index:50; overflow:hidden; box-shadow:0 8px 24px rgba(0,0,0,0.4); }
.goi-y-item { padding:0.75rem 1rem; cursor:pointer; transition:background 0.15s; }
.goi-y-item:hover { background:rgba(6,182,212,0.08); }
.goi-y-ten { font-size:0.875rem; font-weight:600; }
.goi-y-email { font-size:0.775rem; color:var(--mau-chu-mo); }
.doc-gia-da-chon { font-size:0.825rem; color:#51cf66; margin-top:0.375rem; padding:0.4rem 0.75rem; background:rgba(81,207,102,0.08); border:1px solid rgba(81,207,102,0.2); border-radius:6px; }

/* Thông tin phiếu */
.thong-tin-phieu { display:flex; gap:1.5rem; flex-wrap:wrap; margin-bottom:1.5rem; padding:0.875rem 1rem; background:rgba(6,182,212,0.06); border:1px solid rgba(6,182,212,0.12); border-radius:10px; font-size:0.85rem; color:var(--mau-chu-mo); }

/* Input quét mã vạch */
.input-quet { display:flex; gap:0.5rem; }
.input-ma-vach { flex:1; font-family:monospace; font-size:1rem; letter-spacing:0.05em; }
.nut-them-ma { padding:0.75rem 1.25rem; background:rgba(6,182,212,0.12); border:1px solid var(--mau-chinh); border-radius:8px; color:var(--mau-chinh); cursor:pointer; font-family:inherit; font-weight:600; white-space:nowrap; }

/* Danh sách đã quét */
.danh-sach-da-que { margin-top:1rem; }
.tieu-de-danh-sach { font-size:0.825rem; font-weight:600; color:var(--mau-chu-mo); margin-bottom:0.5rem; }
.item-ma-vach { display:flex; align-items:center; gap:0.75rem; padding:0.6rem 0.875rem; background:rgba(255,255,255,0.03); border:1px solid rgba(255,255,255,0.06); border-radius:8px; margin-bottom:0.375rem; }
.stt { width:24px; height:24px; border-radius:50%; background:rgba(6,182,212,0.08); display:flex; align-items:center; justify-content:center; font-size:0.75rem; font-weight:700; color:var(--mau-chinh); flex-shrink:0; }
.ma-code { flex:1; font-size:0.875rem; color:var(--mau-phu); }
.nut-xoa-ma { background:none; border:none; color:var(--mau-chu-rat-mo); cursor:pointer; font-size:1rem; transition:color 0.2s; }
.nut-xoa-ma:hover { color:var(--mau-loi); }
.chua-co-sach { padding:1.5rem; text-align:center; color:var(--mau-chu-mo); font-size:0.875rem; background:rgba(255,255,255,0.02); border-radius:10px; border:1px dashed rgba(255,255,255,0.08); }

/* Nút hành động */
.nhom-nut { display:flex; justify-content:flex-end; gap:0.75rem; margin-top:1.5rem; }
.nut-huy { padding:0.75rem 1.5rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; }
.nut-chinh { padding:0.75rem 1.75rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-size:0.9rem; font-weight:700; }
.nut-chinh:disabled { opacity:0.6; cursor:not-allowed; }
</style>
