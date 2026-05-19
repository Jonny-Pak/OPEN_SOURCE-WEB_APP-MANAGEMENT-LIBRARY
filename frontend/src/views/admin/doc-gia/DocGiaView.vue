<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
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
import ImportDocGiaExcelModal from '@/components/admin/shared/ImportDocGiaExcelModal.vue'
import { useAuthStore } from '@/stores/auth'

interface ImportResult {
  thanhCong: number
  thatBai: number
  loi: Array<{ email: string; message: string }>
}

const authStore = useAuthStore()
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

const formThem = ref({ hoDem: '', ten: '', email: '', soDienThoai: '' })
const formSua = ref({ hoDem: '', ten: '', soDienThoai: '' })

// ===== Admin đổi mật khẩu =====
const doiMatKhauItem = ref<NguoiDung | null>(null)
const matKhauMoi = ref('')
const dangDoiMatKhau = ref(false)

const showImportModal = ref(false)
const fileInputRef = ref<HTMLInputElement | null>(null)
const fileToImport = ref<File | null>(null)
const fileName = ref('')
const dangImport = ref(false)
const ketQuaImport = ref<ImportResult | null>(null)

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
    const response = await docGiaService.danhSach(
      phanTrang.trangHienTai.value,
      phanTrang.kichThuocTrang.value,
      tuKhoaDebounced.value,
      filterTrangThai.value
    )
    danhSach.value = response.content
    phanTrang.capNhatTong(response.totalElements)
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
  if (!f.hoDem.trim() || !f.ten.trim() || !f.email.trim()) {
    toast.canhBao('Vui lòng điền đủ họ tên và email')
    return
  }
  if (!/^[\w.-]+@[\w.-]+\.[a-zA-Z]{2,}$/.test(f.email)) {
    toast.canhBao('Email không đúng định dạng')
    return
  }
  if (f.soDienThoai && !/^0\d{9}$/.test(f.soDienThoai)) {
    toast.canhBao('Số điện thoại phải có 10 chữ số và bắt đầu bằng 0')
    return
  }

  dangGui.value = true
  try {
    await docGiaService.taoCai({
      hoDem: f.hoDem.trim(),
      ten: f.ten.trim(),
      email: f.email.trim().toLowerCase(),
      soDienThoai: f.soDienThoai.trim() || undefined,
      vaiTro: 'DOC_GIA',
      trangThai: 'CHUA_KICH_HOAT',
    } as any)
    toast.thanhCong('Thêm độc giả thành công (mật khẩu mặc định: Password@123)')
    modal.dongModal()
    await taiDanhSach()
  } catch (err: any) {
    toast.loi(err?.message || 'Thêm độc giả thất bại')
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

async function handleExcelImport(file: File) {
  dangImport.value = true
  try {
    const res = await docGiaService.importExcel(file)
    ketQuaImport.value = res
    await taiDanhSach()
    toast.thanhCong(`Import hoàn tất! Thành công: ${res.thanhCong}, Thất bại: ${res.thatBai}`)
  } catch (err: any) {
    toast.loi(err?.message || 'Có lỗi xảy ra khi import Excel')
  } finally {
    dangImport.value = false
  }
}

async function luuDoiMatKhau() {
  if (!doiMatKhauItem.value) return
  if (!matKhauMoi.value || matKhauMoi.value.length < 6) {
    toast.canhBao('Mật khẩu phải có ít nhất 6 ký tự')
    return
  }
  dangDoiMatKhau.value = true
  try {
    await docGiaService.adminDoiMatKhau(doiMatKhauItem.value.maNguoiDung, matKhauMoi.value)
    toast.thanhCong('Đổi mật khẩu thành công!')
    doiMatKhauItem.value = null
    matKhauMoi.value = ''
  } catch (err: any) {
    toast.loi(err?.message || 'Đổi mật khẩu thất bại')
  } finally {
    dangDoiMatKhau.value = false
  }
}


watch([tuKhoaDebounced, filterTrangThai], () => {
  phanTrang.datLaiTrang()
  taiDanhSach()
})

watch(() => phanTrang.trangHienTai.value, taiDanhSach)

onMounted(taiDanhSach)
</script>

<template>
  <div class="doc-gia">
    <div class="thanh-cong-cu">
      <input v-model="tuKhoaTimKiem" class="input-tk" placeholder="Tìm theo tên, email, số điện thoại..." />
      <select v-model="filterTrangThai" class="select-filter">
        <option value="all">Tất cả trạng thái</option>
        <option value="chua_kich_hoat">Chưa kích hoạt</option>
        <option value="da_kich_hoat">Đã kích hoạt</option>
        <option value="bi_khoa">Bị khóa</option>
      </select>
      <button class="nut-them nut-import" @click="showImportModal = true">
        <font-awesome-icon icon="fa-solid fa-file-excel" /> Import Excel
      </button>
      <button
        class="nut-them"
        @click="() => {
          formThem = { hoDem: '', ten: '', email: '', soDienThoai: '' }
          modal.moModalThem()
        }"
      >
        <font-awesome-icon icon="fa-solid fa-user-plus" /> Thêm độc giả
      </button>
    </div>
    
    <!-- Kết quả import Excel -->
    <div v-if="ketQuaImport" class="ket-qua-import" style="margin-bottom: 1rem;">
      <div class="import-head">
        <h4 style="margin: 0; font-size: 1rem; font-weight: 600; display: flex; align-items: center; gap: 0.5rem;">
          <font-awesome-icon icon="fa-solid fa-file-excel" /> Kết quả import Excel
        </h4>
        <button class="nut-huy" style="padding: 0.25rem 0.75rem; font-size: 0.8rem; border-radius: 6px;" @click="ketQuaImport = null">Đóng</button>
      </div>
      <div style="display: flex; gap: 1.5rem; font-size: 0.9rem; margin-top: 0.75rem; margin-bottom: 0.75rem;">
        <span style="color: #16a34a; font-weight: 600;">
          <font-awesome-icon icon="fa-solid fa-circle-check" /> Thành công: {{ ketQuaImport.thanhCong }}
        </span>
        <span style="color: #dc2626; font-weight: 600;">
          <font-awesome-icon icon="fa-solid fa-circle-xmark" /> Thất bại: {{ ketQuaImport.thatBai }}
        </span>
      </div>
      <div v-if="ketQuaImport.loi.length > 0" class="loi-import-list">
        <div v-for="(err, idx) in ketQuaImport.loi" :key="idx" style="margin-top: 0.35rem; display: flex; gap: 0.25rem;">
          <span>•</span>
          <div>
            <strong style="color: #374151;">{{ err.email }}</strong>: <span style="color: #dc2626;">{{ err.message }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="bang-container">
      <SkeletonLoader v-if="dangTai" :rows="7" height="56px" />
      <template v-else>
        <EmptyState v-if="danhSach.length === 0" thong-diep="Khong tim thay doc gia nao" />
        <table v-else class="bang">
          <thead>
            <tr>
              <th>Họ tên</th>
              <th>Email</th>
              <th>Số điện thoại</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in danhSach" :key="item.maNguoiDung">
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
                  <button class="nut-hd" title="Sửa thông tin" @click="moSua(item)">
                    <font-awesome-icon icon="fa-solid fa-pen" />
                  </button>
                  <button class="nut-hd" :title="chuanHoaTrangThai(item.trangThai) === 'chua_kich_hoat' ? 'Kích hoạt' : chuanHoaTrangThai(item.trangThai) === 'da_kich_hoat' ? 'Khóa tài khoản' : 'Mở khóa'" @click="moXacNhanTrangThai(item)">
                    <font-awesome-icon :icon="chuanHoaTrangThai(item.trangThai) === 'chua_kich_hoat' ? 'fa-solid fa-circle-check' : chuanHoaTrangThai(item.trangThai) === 'da_kich_hoat' ? 'fa-solid fa-lock' : 'fa-solid fa-lock-open'" />
                  </button>
                  <button v-if="authStore.isAdmin" class="nut-hd nut-mat-khau" title="Đổi mật khẩu" @click="() => { doiMatKhauItem = item; matKhauMoi = '' }">
                    <font-awesome-icon icon="fa-solid fa-key" />
                  </button>
                  <button v-if="authStore.isAdmin" class="nut-hd nut-xoa-btn" title="Xóa" @click="xoaItem = item">
                    <font-awesome-icon icon="fa-solid fa-trash" />
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
          :kich-thuoc-trang="phanTrang.kichThuocTrang.value"
          @doi-trang="phanTrang.denTrang"
        />
      </template>
    </div>

    <ModalDialog :dang-mo="modal.dangMo.value && modal.dangThem() && !fileToImport" tieu-de="Thêm độc giả mới" @dong="modal.dongModal()">
      <div class="form-modal">
        <div class="hang-doi">
          <div class="form-group">
            <label>Họ đệm *</label>
            <input v-model="formThem.hoDem" class="form-input" placeholder="Nguyễn Văn" />
          </div>
          <div class="form-group">
            <label>Tên *</label>
            <input v-model="formThem.ten" class="form-input" placeholder="A" />
          </div>
        </div>
        <div class="form-group">
          <label>Email *</label>
          <input v-model="formThem.email" type="email" class="form-input" placeholder="ten@example.com" />
        </div>
        <div class="form-group">
          <label>Số điện thoại</label>
          <input v-model="formThem.soDienThoai" class="form-input" placeholder="09xxxxxxxx" />
        </div>
        <div class="form-group">
          <small style="color: #6b7280; font-size: 0.82rem;">Mật khẩu mặc định: <strong>Password@123</strong> (yêu cầu đổi khi đăng nhập lần đầu)</small>
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

    <!-- Modal Admin đổi mật khẩu -->
    <ModalDialog :dang-mo="doiMatKhauItem !== null" tieu-de="Đổi mật khẩu (Admin)" @dong="doiMatKhauItem = null">
      <div class="form-modal">
        <p style="font-size:0.9rem;color:#374151;margin:0">
          Đổi mật khẩu cho <b>{{ doiMatKhauItem?.hoDem }} {{ doiMatKhauItem?.ten }}</b>
        </p>
        <div class="form-group">
          <label>Mật khẩu mới *</label>
          <input v-model="matKhauMoi" type="password" class="form-input" placeholder="Tối thiểu 6 ký tự" />
        </div>
      </div>
      <template #footer>
        <button class="nut-huy" @click="doiMatKhauItem = null">Hủy</button>
        <button class="nut-luu" :disabled="dangDoiMatKhau" @click="luuDoiMatKhau">
          {{ dangDoiMatKhau ? 'Đang xử lý...' : 'Đổi mật khẩu' }}
        </button>
      </template>
    </ModalDialog>

    <!-- Import Excel Modal -->
    <ImportDocGiaExcelModal
      v-if="showImportModal"
      @close="showImportModal = false"
      @imported="handleExcelImport"
    />

    <ConfirmDialog
      :dang-mo="thaoTacItem !== null"
      :tieu-de="thaoTacLoai === 'kich-hoat' ? 'Kích hoạt tài khoản' : thaoTacLoai === 'khoa' ? 'Khóa tài khoản' : 'Mở khóa tài khoản'"
      :thong-diep="`Bạn có chắc muốn thực hiện thao tác với tài khoản '${thaoTacItem?.hoDem} ${thaoTacItem?.ten}'?`"
      loai="canh-bao"
      :dang-xu-ly="dangXuLyTrangThai"
      @xac-nhan="xuLyTrangThaiTaiKhoan"
      @huy="() => { thaoTacItem = null; thaoTacLoai = null }"
    />

    <ConfirmDialog
      :dang-mo="xoaItem !== null"
      :thong-diep="`Xóa tài khoản '${xoaItem?.hoDem} ${xoaItem?.ten}'?`"
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
.nut-import {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #16a34a;
  color: #fff;
}
.nut-import:hover {
  background: #15803d;
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
.nut-mat-khau { color: #7c3aed; }
.nut-mat-khau:hover { border-color: #7c3aed; color: #7c3aed; background: rgba(124,58,237,0.06); }

@media (max-width: 800px) {
  .hang-doi { grid-template-columns: 1fr; }
}
</style>
