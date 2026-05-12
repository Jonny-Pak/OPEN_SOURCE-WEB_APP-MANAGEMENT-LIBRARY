<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import * as XLSX from 'xlsx'
import { docGiaService } from '@/services/docGiaService'
import { useSearch } from '@/composables/useSearch'
import { usePagination } from '@/composables/usePagination'
import { useModal } from '@/composables/useModal'
import { useToast } from '@/composables/useToast'
import type { NguoiDung, TaoNguoiDungExcelRequest, TrangThaiNguoiDung } from '@/types/nguoidung'
import ModalDialog from '@/components/admin/shared/ModalDialog.vue'
import ConfirmDialog from '@/components/admin/shared/ConfirmDialog.vue'
import Pagination from '@/components/admin/shared/Pagination.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import EmptyState from '@/components/admin/shared/EmptyState.vue'
import StatusBadge from '@/components/admin/shared/StatusBadge.vue'

interface ExcelPreviewRow {
  mssv: string
  hoTen: string
  lop: string
  khoa: string
  email: string
  valid: boolean
  error?: string
}

interface ImportResult {
  thanhCong: number
  thatBai: number
  loi: Array<{ email: string; message: string }>
}

const toast = useToast()
const { tuKhoaTimKiem, tuKhoaDebounced } = useSearch(250)
const phanTrang = usePagination(10)
const modal = useModal<NguoiDung>()

const dangTai = ref(false)
const danhSach = ref<NguoiDung[]>([])
const filterTrangThai = ref<'all' | 'chua_kich_hoat' | 'da_kich_hoat' | 'bi_khoa'>('all')

const xoaItem = ref<NguoiDung | null>(null)
const dangXoa = ref(false)
const dangGui = ref(false)
const thaoTacItem = ref<NguoiDung | null>(null)
const thaoTacLoai = ref<'kich-hoat' | 'khoa' | 'mo-khoa' | null>(null)
const dangXuLyTrangThai = ref(false)

const formThem = ref({ hoDem: '', ten: '', email: '', matKhau: '123', soDienThoai: '' })
const formSua = ref({ hoDem: '', ten: '', soDienThoai: '' })

const fileInputRef = ref<HTMLInputElement | null>(null)
const excelRows = ref<ExcelPreviewRow[]>([])
const fileName = ref('')
const dangImport = ref(false)
const ketQuaImport = ref<ImportResult | null>(null)

const danhSachLoc = computed(() => {
  const keyword = tuKhoaDebounced.value.trim().toLowerCase()
  return danhSach.value.filter((item) => {
    const trangThai = chuanHoaTrangThai(item.trangThai)
    const hopFilter = filterTrangThai.value === 'all' || filterTrangThai.value === trangThai
    if (!hopFilter) return false

    if (!keyword) return true
    const fullName = `${item.hoDem} ${item.ten}`.toLowerCase()
    return (
      fullName.includes(keyword) ||
      item.email.toLowerCase().includes(keyword) ||
      item.soDienThoai.toLowerCase().includes(keyword) ||
      item.maNguoiDung.toLowerCase().includes(keyword)
    )
  })
})

const danhSachTrang = computed(() => {
  const start = phanTrang.trangHienTai.value * phanTrang.kichThuocTrang.value
  const end = start + phanTrang.kichThuocTrang.value
  return danhSachLoc.value.slice(start, end)
})

const soBanGhiHopLe = computed(() => excelRows.value.filter((r) => r.valid).length)

function chuanHoaTrangThai(status: TrangThaiNguoiDung): 'chua_kich_hoat' | 'da_kich_hoat' | 'bi_khoa' {
  if (status === 'chua_kich_hoat' || status === 'CHUA_KICH_HOAT') return 'chua_kich_hoat'
  if (status === 'da_kich_hoat' || status === 'DA_KICH_HOAT' || status === 'HOAT_DONG') return 'da_kich_hoat'
  return 'bi_khoa'
}

function nhanTrangThai(status: TrangThaiNguoiDung): string {
  const val = chuanHoaTrangThai(status)
  if (val === 'chua_kich_hoat') return 'Chua kich hoat'
  if (val === 'da_kich_hoat') return 'Da kich hoat'
  return 'Bi khoa'
}

function loaiBadgeTrangThai(status: TrangThaiNguoiDung): 'xam' | 'xanh-duong' | 'do' {
  const val = chuanHoaTrangThai(status)
  if (val === 'chua_kich_hoat') return 'xam'
  if (val === 'da_kich_hoat') return 'xanh-duong'
  return 'do'
}

async function taiDanhSach() {
  dangTai.value = true
  try {
    danhSach.value = await docGiaService.danhSach()
  } catch {
    toast.loi('Khong the tai danh sach doc gia')
  } finally {
    dangTai.value = false
  }
}

function moSua(item: NguoiDung) {
  modal.moModalSua(item)
  formSua.value = { hoDem: item.hoDem, ten: item.ten, soDienThoai: item.soDienThoai }
}

async function luuThem() {
  const f = formThem.value
  if (!f.hoDem.trim() || !f.ten.trim() || !f.email.trim() || !f.soDienThoai.trim()) {
    toast.canhBao('Vui long dien du cac truong bat buoc')
    return
  }

  dangGui.value = true
  try {
    await docGiaService.taoCai({
      ...f,
      vaiTro: 'DOC_GIA',
      trangThai: 'chua_kich_hoat',
      isDefaultPassword: true,
      matKhau: f.matKhau || '123',
    })
    toast.thanhCong('Them doc gia thanh cong')
    modal.dongModal()
    await taiDanhSach()
  } catch {
    toast.loi('Them doc gia that bai')
  } finally {
    dangGui.value = false
  }
}

async function luuSua() {
  if (!modal.itemDangSua.value) return
  dangGui.value = true
  try {
    await docGiaService.capNhat(modal.itemDangSua.value.maNguoiDung, formSua.value)
    toast.thanhCong('Cap nhat thanh cong')
    modal.dongModal()
    await taiDanhSach()
  } catch {
    toast.loi('Cap nhat that bai')
  } finally {
    dangGui.value = false
  }
}

function moXacNhanTrangThai(item: NguoiDung) {
  const status = chuanHoaTrangThai(item.trangThai)
  if (status === 'chua_kich_hoat') {
    thaoTacLoai.value = 'kich-hoat'
  } else if (status === 'da_kich_hoat') {
    thaoTacLoai.value = 'khoa'
  } else {
    thaoTacLoai.value = 'mo-khoa'
  }
  thaoTacItem.value = item
}

async function xuLyTrangThaiTaiKhoan() {
  if (!thaoTacItem.value || !thaoTacLoai.value) return

  dangXuLyTrangThai.value = true
  try {
    if (thaoTacLoai.value === 'kich-hoat') {
      await docGiaService.kichHoat(thaoTacItem.value.maNguoiDung)
      toast.thanhCong('Da kich hoat tai khoan')
    } else if (thaoTacLoai.value === 'khoa') {
      await docGiaService.khoa(thaoTacItem.value.maNguoiDung)
      toast.thanhCong('Da khoa tai khoan')
    } else {
      await docGiaService.moKhoa(thaoTacItem.value.maNguoiDung)
      toast.thanhCong('Da mo khoa tai khoan')
    }

    thaoTacItem.value = null
    thaoTacLoai.value = null
    await taiDanhSach()
  } catch {
    toast.loi('Khong the cap nhat trang thai tai khoan')
  } finally {
    dangXuLyTrangThai.value = false
  }
}

async function xacNhanXoa() {
  if (!xoaItem.value) return
  dangXoa.value = true
  try {
    await docGiaService.xoa(xoaItem.value.maNguoiDung)
    toast.thanhCong('Da xoa doc gia')
    xoaItem.value = null
    await taiDanhSach()
  } catch {
    toast.loi('Khong the xoa doc gia')
  } finally {
    dangXoa.value = false
  }
}

function kyTuDau(nd: NguoiDung) {
  return `${nd.hoDem.charAt(0)}${nd.ten.charAt(0)}`.toUpperCase()
}

function tachHoTen(hoTen: string): { hoDem: string; ten: string } {
  const cleaned = hoTen.trim().replace(/\s+/g, ' ')
  if (!cleaned) return { hoDem: '', ten: '' }
  const parts = cleaned.split(' ')
  const ten = parts.pop() || ''
  return { hoDem: parts.join(' '), ten }
}

function moNhapExcel() {
  fileInputRef.value?.click()
}

function taoEmailTuMssv(mssv: string): string {
  return `${mssv.toLowerCase()}@sv.hcmunre.edu.vn`
}

function mapRow(raw: Record<string, unknown>): ExcelPreviewRow {
  const mssv = String(raw.MSSV ?? raw.mssv ?? raw['Ma SV'] ?? raw['ma_sv'] ?? '').trim()
  const hoTen = String(raw['Ho ten'] ?? raw['Họ tên'] ?? raw.hoTen ?? raw.hoten ?? '').trim()
  const lop = String(raw.Lop ?? raw['Lớp'] ?? raw.lop ?? '').trim()
  const khoa = String(raw.Khoa ?? raw.khoa ?? '').trim()

  if (!mssv) {
    return { mssv: '', hoTen, lop, khoa, email: '', valid: false, error: 'Thieu MSSV' }
  }
  if (!hoTen) {
    return { mssv, hoTen: '', lop, khoa, email: taoEmailTuMssv(mssv), valid: false, error: 'Thieu ho ten' }
  }

  return { mssv, hoTen, lop, khoa, email: taoEmailTuMssv(mssv), valid: true }
}

function xuLyFileExcel(e: Event) {
  ketQuaImport.value = null
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  fileName.value = file.name
  const reader = new FileReader()
  reader.onload = (ev) => {
    try {
      const data = ev.target?.result
      if (!(data instanceof ArrayBuffer)) {
        toast.loi('Khong doc duoc file Excel')
        return
      }

      const workbook = XLSX.read(data, { type: 'array' })
      const firstSheetName = workbook.SheetNames[0]
      if (!firstSheetName) {
        toast.canhBao('File Excel khong co sheet du lieu')
        return
      }
      const firstSheet = workbook.Sheets[firstSheetName]
      if (!firstSheet) {
        toast.canhBao('Khong doc duoc sheet dau tien trong file Excel')
        return
      }
      const json = XLSX.utils.sheet_to_json<Record<string, unknown>>(firstSheet, { defval: '' })
      excelRows.value = json.map(mapRow)
      if (excelRows.value.length === 0) {
        toast.canhBao('File Excel khong co du lieu')
        return
      }
      modal.moModalThem()
    } catch {
      toast.loi('File Excel khong hop le')
    }
  }
  reader.readAsArrayBuffer(file)
}

function dongModalImport() {
  excelRows.value = []
  fileName.value = ''
  ketQuaImport.value = null
  modal.dongModal()
}

function toPayload(rows: ExcelPreviewRow[]): TaoNguoiDungExcelRequest[] {
  return rows
    .filter((r) => r.valid)
    .map((r) => {
      const tach = tachHoTen(r.hoTen)
      return {
        mssv: r.mssv,
        hoDem: tach.hoDem,
        ten: tach.ten,
        lop: r.lop,
        khoa: r.khoa,
        email: r.email,
      }
    })
}

async function importExcel() {
  const payload = toPayload(excelRows.value)
  if (payload.length === 0) {
    toast.canhBao('Khong co dong hop le de import')
    return
  }

  dangImport.value = true
  try {
    try {
      ketQuaImport.value = await docGiaService.taoHangLoat(payload)
    } catch {
      const failures: Array<{ email: string; message: string }> = []
      let success = 0
      for (const item of payload) {
        const matKhau = '123'
        try {
          await docGiaService.taoCai({
            hoDem: item.hoDem,
            ten: item.ten,
            email: item.email,
            soDienThoai: '',
            matKhau,
            vaiTro: 'DOC_GIA',
            trangThai: 'chua_kich_hoat',
            isDefaultPassword: true,
          })
          success += 1
        } catch {
          failures.push({ email: item.email, message: 'Email trung hoac du lieu khong hop le' })
        }
      }
      ketQuaImport.value = { thanhCong: success, thatBai: failures.length, loi: failures }
    }

    await taiDanhSach()
    toast.thanhCong('Import doc gia hoan tat')
  } finally {
    dangImport.value = false
  }
}

watch([tuKhoaDebounced, filterTrangThai], () => {
  phanTrang.datLaiTrang()
})

watch(danhSachLoc, (val) => {
  phanTrang.capNhatTong(val.length)
}, { immediate: true })

onMounted(taiDanhSach)
</script>

<template>
  <div class="doc-gia">
    <div class="thanh-cong-cu">
      <input v-model="tuKhoaTimKiem" class="input-tk" placeholder="Tim theo ten, email, so dien thoai..." />
      <select v-model="filterTrangThai" class="select-filter">
        <option value="all">Tat ca trang thai</option>
        <option value="chua_kich_hoat">Chua kich hoat</option>
        <option value="da_kich_hoat">Da kich hoat</option>
        <option value="bi_khoa">Bi khoa</option>
      </select>
      <button class="nut-phu" @click="moNhapExcel">Nhap tu Excel</button>
      <button
        class="nut-them"
        @click="() => {
          formThem = { hoDem: '', ten: '', email: '', matKhau: '123', soDienThoai: '' }
          modal.moModalThem()
        }"
      >
        + Them doc gia
      </button>
      <input
        ref="fileInputRef"
        type="file"
        accept=".xlsx,.xls"
        class="an-input-file"
        @change="xuLyFileExcel"
      />
    </div>

    <div class="bang-container">
      <SkeletonLoader v-if="dangTai" :rows="7" height="56px" />
      <template v-else>
        <EmptyState v-if="danhSachLoc.length === 0" thong-diep="Khong tim thay doc gia nao" />
        <table v-else class="bang">
          <thead>
            <tr>
              <th>Ho ten</th>
              <th>Email</th>
              <th>So dien thoai</th>
              <th>Trang thai</th>
              <th>Hanh dong</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in danhSachTrang" :key="item.maNguoiDung">
              <td>
                <div class="ten-nguoi-dung">
                  <div class="avatar">{{ kyTuDau(item) }}</div>
                  <div>
                    <div class="ho-ten">{{ item.hoDem }} {{ item.ten }}</div>
                    <div class="ma-nd">{{ item.maNguoiDung }}</div>
                  </div>
                </div>
              </td>
              <td>{{ item.email }}</td>
              <td>{{ item.soDienThoai || '--' }}</td>
              <td>
                <StatusBadge :nhan-hien="nhanTrangThai(item.trangThai)" :loai="loaiBadgeTrangThai(item.trangThai)" />
              </td>
              <td>
                <div class="hanh-dong">
                  <button class="nut-hd" @click="moSua(item)">Sua</button>
                  <button class="nut-hd" @click="moXacNhanTrangThai(item)">
                    {{ chuanHoaTrangThai(item.trangThai) === 'chua_kich_hoat' ? 'Kich hoat' : chuanHoaTrangThai(item.trangThai) === 'da_kich_hoat' ? 'Khoa' : 'Mo khoa' }}
                  </button>
                  <button class="nut-hd nut-xoa-btn" @click="xoaItem = item">Xoa</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <Pagination
          :trang-hien-tai="phanTrang.trangHienThiChoNguoiDung.value"
          :tong-trang="phanTrang.tongTrang.value"
          :tong-phan-tu="phanTrang.tongPhanTu.value"
          :kich-thuoc-trang="phanTrang.kichThuocTrang.value"
          @doi-trang="phanTrang.denTrang"
        />
      </template>
    </div>

    <ModalDialog :dang-mo="modal.dangMo.value && modal.dangThem() && excelRows.length === 0" tieu-de="Them doc gia moi" @dong="modal.dongModal()">
      <div class="form-modal">
        <div class="hang-doi">
          <div class="form-group">
            <label>Ho dem *</label>
            <input v-model="formThem.hoDem" class="form-input" placeholder="Nguyen Van" />
          </div>
          <div class="form-group">
            <label>Ten *</label>
            <input v-model="formThem.ten" class="form-input" placeholder="A" />
          </div>
        </div>
        <div class="form-group">
          <label>Email *</label>
          <input v-model="formThem.email" type="email" class="form-input" placeholder="mssv@sv.hcmunre.edu.vn" />
        </div>
        <div class="form-group">
          <label>Mat khau mac dinh *</label>
          <input v-model="formThem.matKhau" class="form-input" placeholder="123" />
        </div>
        <div class="form-group">
          <label>So dien thoai</label>
          <input v-model="formThem.soDienThoai" class="form-input" placeholder="09xxxxxxxx" />
        </div>
      </div>
      <template #footer>
        <button class="nut-huy" @click="modal.dongModal()">Huy</button>
        <button class="nut-luu" :disabled="dangGui" @click="luuThem">{{ dangGui ? 'Dang luu...' : 'Them' }}</button>
      </template>
    </ModalDialog>

    <ModalDialog :dang-mo="modal.dangMo.value && !modal.dangThem()" tieu-de="Sua thong tin doc gia" @dong="modal.dongModal()">
      <div class="form-modal">
        <div class="hang-doi">
          <div class="form-group"><label>Ho dem</label><input v-model="formSua.hoDem" class="form-input" /></div>
          <div class="form-group"><label>Ten</label><input v-model="formSua.ten" class="form-input" /></div>
        </div>
        <div class="form-group"><label>So dien thoai</label><input v-model="formSua.soDienThoai" class="form-input" /></div>
      </div>
      <template #footer>
        <button class="nut-huy" @click="modal.dongModal()">Huy</button>
        <button class="nut-luu" :disabled="dangGui" @click="luuSua">{{ dangGui ? 'Dang luu...' : 'Luu' }}</button>
      </template>
    </ModalDialog>

    <ModalDialog
      :dang-mo="modal.dangMo.value && modal.dangThem() && excelRows.length > 0"
      tieu-de="Preview import Excel"
      chieu-rong="980px"
      @dong="dongModalImport"
    >
      <div class="import-head">
        <div>
          <strong>File:</strong> {{ fileName }}
        </div>
        <div>
          Hop le: <strong>{{ soBanGhiHopLe }}</strong> / {{ excelRows.length }}
        </div>
      </div>

      <div class="preview-table-wrap">
        <table class="bang bang-preview">
          <thead>
            <tr>
              <th>MSSV</th>
              <th>Ho ten</th>
              <th>Lop</th>
              <th>Khoa</th>
              <th>Email tao</th>
              <th>Trang thai</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, idx) in excelRows" :key="`${row.mssv}-${idx}`">
              <td>{{ row.mssv || '--' }}</td>
              <td>{{ row.hoTen || '--' }}</td>
              <td>{{ row.lop || '--' }}</td>
              <td>{{ row.khoa || '--' }}</td>
              <td>{{ row.email || '--' }}</td>
              <td>
                <StatusBadge :nhan-hien="row.valid ? 'Hop le' : row.error || 'Loi'" :loai="row.valid ? 'xanh-duong' : 'do'" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="ketQuaImport" class="ket-qua-import">
        <div>Thanh cong: <strong>{{ ketQuaImport.thanhCong }}</strong></div>
        <div>That bai: <strong>{{ ketQuaImport.thatBai }}</strong></div>
        <div v-if="ketQuaImport.loi.length > 0" class="loi-import-list">
          <div v-for="(err, index) in ketQuaImport.loi" :key="`${err.email}-${index}`">{{ err.email }} - {{ err.message }}</div>
        </div>
      </div>

      <template #footer>
        <button class="nut-huy" @click="dongModalImport">Dong</button>
        <button class="nut-luu" :disabled="dangImport || soBanGhiHopLe === 0" @click="importExcel">
          {{ dangImport ? 'Dang import...' : 'Xac nhan import' }}
        </button>
      </template>
    </ModalDialog>

    <ConfirmDialog
      :dang-mo="thaoTacItem !== null"
      :tieu-de="thaoTacLoai === 'kich-hoat' ? 'Kich hoat tai khoan' : thaoTacLoai === 'khoa' ? 'Khoa tai khoan' : 'Mo khoa tai khoan'"
      :thong-diep="`Ban co chac muon thuc hien thao tac voi tai khoan '${thaoTacItem?.hoDem} ${thaoTacItem?.ten}'?`"
      loai="canh-bao"
      :dang-xu-ly="dangXuLyTrangThai"
      @xac-nhan="xuLyTrangThaiTaiKhoan"
      @huy="() => { thaoTacItem = null; thaoTacLoai = null }"
    />

    <ConfirmDialog
      :dang-mo="xoaItem !== null"
      :thong-diep="`Xoa tai khoan '${xoaItem?.hoDem} ${xoaItem?.ten}'?`"
      :dang-xu-ly="dangXoa"
      @xac-nhan="xacNhanXoa"
      @huy="xoaItem = null"
    />
  </div>
</template>

<style scoped>
.doc-gia { animation: fadeInUp 0.3s ease; }
.thanh-cong-cu { display: flex; gap: 0.75rem; margin-bottom: 1rem; flex-wrap: wrap; }
.input-tk,
.select-filter,
.form-input {
  padding: 0.65rem 0.85rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  color: var(--color-text);
  background: #fff;
  outline: none;
}
.input-tk { min-width: 260px; flex: 1; }
.input-tk:focus,
.select-filter:focus,
.form-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.15);
}
.nut-them,
.nut-luu,
.nut-phu {
  padding: 0.65rem 1rem;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 600;
}
.nut-them,
.nut-luu { background: var(--color-primary); color: #fff; }
.nut-them:hover,
.nut-luu:hover { background: var(--color-primary-hover); }
.nut-huy {
  padding: 0.65rem 1rem;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #fff;
  color: #374151;
  cursor: pointer;
}
.nut-phu {
  border: 1px solid var(--color-primary);
  color: var(--color-primary);
  background: rgba(6, 182, 212, 0.06);
}
.bang-container {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  padding: 1rem;
}
.bang { width: 100%; border-collapse: collapse; }
.bang th {
  text-align: left;
  color: #6b7280;
  font-size: 0.76rem;
  text-transform: uppercase;
  padding: 0.75rem;
  border-bottom: 1px solid #e5e7eb;
}
.bang td {
  font-size: 0.9rem;
  color: var(--color-text);
  padding: 0.8rem 0.75rem;
  border-bottom: 1px solid #f3f4f6;
}
.bang tr:last-child td { border-bottom: none; }
.ten-nguoi-dung { display: flex; gap: 0.75rem; align-items: center; }
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  color: #fff;
  font-size: 0.82rem;
  font-weight: 700;
  background: var(--color-primary);
}
.ho-ten { font-weight: 600; }
.ma-nd { color: #6b7280; font-size: 0.76rem; }
.hanh-dong { display: flex; gap: 0.35rem; }
.nut-hd {
  border: 1px solid #d1d5db;
  background: #fff;
  color: #374151;
  border-radius: 8px;
  padding: 0.35rem 0.6rem;
  cursor: pointer;
}
.nut-hd:hover { border-color: var(--color-primary); color: var(--color-primary); }
.nut-xoa-btn:hover { border-color: #ef4444; color: #ef4444; }
.form-modal { display: flex; flex-direction: column; gap: 0.8rem; }
.hang-doi { display: grid; grid-template-columns: 1fr 1fr; gap: 0.7rem; }
.form-group { display: flex; flex-direction: column; gap: 0.35rem; }
.form-group label { color: #374151; font-size: 0.86rem; font-weight: 600; }
.import-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  color: #374151;
}
.preview-table-wrap {
  max-height: 340px;
  overflow: auto;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}
.bang-preview th,
.bang-preview td { font-size: 0.84rem; }
.ket-qua-import {
  margin-top: 0.9rem;
  padding: 0.8rem;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #f9fafb;
  color: #374151;
}
.loi-import-list {
  margin-top: 0.6rem;
  max-height: 120px;
  overflow: auto;
  font-size: 0.84rem;
  color: #dc2626;
}
.an-input-file { display: none; }
@media (max-width: 800px) {
  .hang-doi { grid-template-columns: 1fr; }
}
</style>
