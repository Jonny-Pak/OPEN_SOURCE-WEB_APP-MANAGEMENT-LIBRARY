<!--
  SachFormView.vue — Form thêm/sửa đầu sách (dùng chung cho cả 2 mode).
  Hỗ trợ upload ảnh bìa với preview.
-->
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { sachService } from '@/services/sachService'
import { tacGiaService, nhaXuatBanService, theLoaiService } from '@/services/danhMucService'
import { useToast } from '@/composables/useToast'
import type { TacGia, NhaXuatBan, TheLoai } from '@/types/danhmuc'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const isEdit = computed(() => !!route.params.id)
const sachId = computed(() => Number(route.params.id))

// Dữ liệu form
const form = ref({
  tenSach: '', maIsbn: '', namXuatBan: new Date().getFullYear(), moTa: '',
  maNhaXuatBan: 0, maTacGias: [] as number[], maTheLoais: [] as number[],
  lanTaiBan: 1, soTrang: 100, giaTien: 100000, donGiaPhatTheoNgay: 5000,
  kichThuoc: '', dichGia: ''
})
const danhSachNXB = ref<NhaXuatBan[]>([])
const danhSachTacGia = ref<TacGia[]>([])
const danhSachTheLoai = ref<TheLoai[]>([])
const dangGui = ref(false)
const dangTai = ref(false)

// Upload ảnh bìa
interface ImageUpload {
  id?: number;         // Có id = ảnh đã tồn tại trên server
  previewUrl: string;  // URL hiển thị preview (full URL hoặc blob URL)
  file?: File;         // Có file = ảnh mới cần upload
  loaiHinhAnh: string;
}

// Lưu lại danh sách ID ảnh gốc (khi load form edit) để biết cái nào cần xóa
const anhGocIds = ref<Set<number>>(new Set())

const danhSachAnh = ref<ImageUpload[]>([])
const loaiHinhAnhOptions = [
  { value: 'BIA_TRUOC', label: 'Bìa trước' },
  { value: 'BIA_SAU', label: 'Bìa sau' },
  { value: 'MUC_LUC', label: 'Mục lục' },
  { value: 'KHAC', label: 'Khác' }
]

function chonFile(e: Event) {
  const files = (e.target as HTMLInputElement).files
  if (!files) return
  for (let i = 0; i < files.length; i++) {
    const file = files[i] as File
    danhSachAnh.value.push({
      previewUrl: URL.createObjectURL(file),
      file: file,
      loaiHinhAnh: danhSachAnh.value.length === 0 ? 'BIA_TRUOC' : 'KHAC'
    })
  }
  // Reset input để có thể chọn cùng file lần nữa
  ;(e.target as HTMLInputElement).value = ''
}

function xoaAnh(index: number) {
  danhSachAnh.value.splice(index, 1)
}

/**
 * Chiến lược đúng để upload/sync ảnh:
 * 1. Xác định ảnh nào đã bị xóa khỏi danh sách (so với danh sách gốc) → gọi API xóa từng ảnh đó
 * 2. Upload các ảnh mới (có File object)
 * 3. Cập nhật thuộc tính (loại, thứ tự) các ảnh giữ lại (có id, không có file)
 */
async function uploadTatCaAnh(id: number) {
  try {
    // Bước 1: Xác định và xóa các ảnh đã bị user remove
    const anhHienTaiIds = new Set(
      danhSachAnh.value.filter(a => a.id).map(a => a.id as number)
    )
    for (const originalId of anhGocIds.value) {
      if (!anhHienTaiIds.has(originalId)) {
        // Ảnh đã bị user xóa khỏi danh sách
        try {
          await sachService.xoaMotHinhAnh(originalId)
        } catch (e) {
          console.warn('Không thể xóa ảnh id:', originalId, e)
        }
      }
    }

    // Bước 2: Xử lý lần lượt từng ảnh trong danh sách hiện tại
    for (let i = 0; i < danhSachAnh.value.length; i++) {
      const anh = danhSachAnh.value[i]
      if (!anh) continue

      if (anh.file) {
        // Ảnh mới → upload file lên server rồi tạo record
        await sachService.uploadHinhAnh(id, anh.file, anh.loaiHinhAnh, i)
      } else if (anh.id) {
        // Ảnh cũ còn giữ lại → cập nhật loại và thứ tự nếu user thay đổi
        await sachService.capNhatHinhAnh(anh.id, anh.loaiHinhAnh, i)
      }
    }
  } catch (error: any) {
    console.error('Lỗi upload ảnh:', error)
    toast.canhBao('Upload ảnh thất bại, vui lòng kiểm tra lại kích thước ảnh.')
    throw error
  }
}

async function taiDuLieu() {
  dangTai.value = true
  try {
    const [nxb, tg, tl] = await Promise.all([
      nhaXuatBanService.danhSach(),
      tacGiaService.danhSach(),
      theLoaiService.danhSach(),
    ])
    danhSachNXB.value = nxb
    danhSachTacGia.value = tg
    danhSachTheLoai.value = tl

    if (isEdit.value) {
      const sach = await sachService.layMotCuon(sachId.value)
      // Chú ý: Backend chưa trả về lanTaiBan, soTrang, giaTien trong SachResponse?
      // Ta lấy tạm mặc định nếu thiếu.
      form.value = {
        tenSach: sach.tenSach,
        maIsbn: sach.maIsbn || '',
        namXuatBan: sach.namXuatBan,
        moTa: sach.moTa ?? '',
        maNhaXuatBan: sach.nhaXuatBan?.maNhaXuatBan || 0,
        maTacGias: sach.danhSachTacGia?.map(t => t.maTacGia) || [],
        maTheLoais: sach.danhSachTheLoai?.map(t => t.maTheLoai) || [],
        lanTaiBan: sach.lanTaiBan || 1,
        soTrang: sach.soTrang || 100,
        giaTien: sach.giaTien || 100000,
        donGiaPhatTheoNgay: sach.donGiaPhatTheoNgay || 5000,
        kichThuoc: (sach as any).kichThuoc || '',
        dichGia: (sach as any).dichGia || ''
      }
      if (sach.danhSachHinhAnh && sach.danhSachHinhAnh.length > 0) {
        danhSachAnh.value = sach.danhSachHinhAnh.map((ha: any) => ({
          id: ha.maHinhAnh,
          // duongDan đã được formatSachHinhAnh chuyển sang full URL
          previewUrl: ha.duongDan,
          loaiHinhAnh: typeof ha.loaiHinhAnh === 'string' ? ha.loaiHinhAnh : String(ha.loaiHinhAnh)
        }))
        // Lưu lại IDs gốc để phát hiện ảnh nào bị xóa
        anhGocIds.value = new Set(sach.danhSachHinhAnh.map((ha: any) => ha.maHinhAnh as number))
      }
    }
  } catch { toast.loi('Không thể tải dữ liệu') }
  finally { dangTai.value = false }
}

function toggleId(arr: number[], id: number) {
  const idx = arr.indexOf(id)
  if (idx === -1) arr.push(id)
  else arr.splice(idx, 1)
}

async function luuSach() {
  if (!form.value.tenSach.trim()) return toast.canhBao('Tên sách không được để trống')
  if (!form.value.maIsbn.trim()) return toast.canhBao('ISBN không được để trống')

  // Normalize ISBN by removing all non-alphanumeric characters and converting to uppercase
  const normalizedIsbn = form.value.maIsbn.replace(/[^0-9xX]/g, '').toUpperCase()
  if (!/^(\d{9}[0-9X]|\d{13})$/.test(normalizedIsbn)) {
    return toast.canhBao('Mã ISBN phải là ISBN-10 (10 ký tự) hoặc ISBN-13 (13 chữ số)')
  }
  form.value.maIsbn = normalizedIsbn

  if (!form.value.maNhaXuatBan) return toast.canhBao('Vui lòng chọn nhà xuất bản')
  if (form.value.maTacGias.length === 0) return toast.canhBao('Vui lòng chọn ít nhất 1 tác giả')
  if (form.value.maTheLoais.length === 0) return toast.canhBao('Vui lòng chọn ít nhất 1 thể loại')

  dangGui.value = true
  let savedId = sachId.value
  try {
    if (isEdit.value) {
      await sachService.capNhat(savedId, form.value)
      toast.thanhCong('Cập nhật đầu sách thành công')
    } else {
      const sach = await sachService.taoCai(form.value)
      savedId = sach.maSach
      toast.thanhCong('Thêm đầu sách thành công')
    }
    // Upload / sync ảnh (chỉ gọi khi có ảnh mới hoặc ảnh đã bị xóa)
    const coAnhMoi = danhSachAnh.value.some(a => !!a.file)
    const coAnhBiXoa = [...anhGocIds.value].some(
      id => !danhSachAnh.value.find(a => a.id === id)
    )
    if (coAnhMoi || coAnhBiXoa || danhSachAnh.value.some(a => a.id)) {
      await uploadTatCaAnh(savedId)
    }
    router.push('/admin/sach')
  } catch (err: any) {
    if (err && err.details) {
      const detailsText = Object.values(err.details).join(', ')
      toast.loi(`Lỗi: ${detailsText}`)
    } else if (err?.message) {
      toast.loi(err.message)
    } else {
      toast.loi('Lưu thất bại, vui lòng kiểm tra lại')
    }
    // Nếu tạo sách mới thành công nhưng upload ảnh lỗi → chuyển sang chế độ sửa
    // để user không cần tạo lại sách khi thử lại
    if (!isEdit.value && savedId) {
      router.push(`/admin/sach/${savedId}/chinh-sua`)
    }
  } finally {
    dangGui.value = false
  }
}

onMounted(taiDuLieu)
</script>

<template>
  <div class="sach-form">
    <div class="dau-trang">
      <button class="nut-quay-lai" @click="router.push('/admin/sach')">← Quay lại</button>
      <h2>{{ isEdit ? 'Chỉnh sửa đầu sách' : 'Thêm đầu sách mới' }}</h2>
    </div>

    <div v-if="dangTai" style="padding:2rem"><span>Đang tải dữ liệu...</span></div>

    <div v-else class="khung-form">
      <div class="luoi-form">
        <!-- Cột trái: thông tin chính -->
        <div class="cot-chinh">
          <div class="the-nhom">
            <h3 class="tieu-de-nhom">Thông tin cơ bản</h3>
            <div class="form-group">
              <label>Tên sách *</label>
              <input v-model="form.tenSach" class="form-input" placeholder="Nhập tên sách" />
            </div>
            <div class="hang-doi">
              <div class="form-group">
                <label>ISBN *</label>
                <input v-model="form.maIsbn" class="form-input" placeholder="978-..." />
              </div>
              <div class="form-group">
                <label>Năm xuất bản *</label>
                <input v-model.number="form.namXuatBan" type="number" class="form-input" :min="1900" :max="2099" />
              </div>
            </div>
            <div class="form-group">
              <label>Mô tả</label>
              <textarea v-model="form.moTa" class="form-input form-textarea" placeholder="Mô tả nội dung sách..."></textarea>
            </div>
            <div class="form-group">
              <label>Nhà xuất bản *</label>
              <select v-model.number="form.maNhaXuatBan" class="form-input form-select">
                <option value="0" disabled>-- Chọn NXB --</option>
                <option v-for="nxb in danhSachNXB" :key="nxb.maNhaXuatBan" :value="nxb.maNhaXuatBan">{{ nxb.tenNhaXuatBan }}</option>
              </select>
            </div>
          </div>

          <!-- Thông tin xuất bản & Giá sách -->
          <div class="the-nhom">
            <h3 class="tieu-de-nhom">Thông tin xuất bản & Giá sách</h3>
            <div class="hang-doi">
              <div class="form-group">
                <label>Lần tái bản *</label>
                <input v-model.number="form.lanTaiBan" type="number" class="form-input" :min="1" />
              </div>
              <div class="form-group">
                <label>Số trang *</label>
                <input v-model.number="form.soTrang" type="number" class="form-input" :min="1" />
              </div>
            </div>
            <div class="hang-doi">
              <div class="form-group">
                <label>Giá tiền sách (VNĐ) *</label>
                <input v-model.number="form.giaTien" type="number" class="form-input" :min="0" />
              </div>
              <div class="form-group">
                <label>Phạt quá hạn/ngày (VNĐ) *</label>
                <input v-model.number="form.donGiaPhatTheoNgay" type="number" class="form-input" :min="0" />
              </div>
            </div>
            <div class="hang-doi">
              <div class="form-group">
                <label>Kích thước</label>
                <input v-model="form.kichThuoc" class="form-input" placeholder="Ví dụ: 13x20 cm" />
              </div>
              <div class="form-group">
                <label>Dịch giả</label>
                <input v-model="form.dichGia" class="form-input" placeholder="Nhập tên dịch giả nếu có" />
              </div>
            </div>
          </div>

          <!-- Tác giả -->
          <div class="the-nhom">
            <h3 class="tieu-de-nhom">Tác giả * (chọn nhiều)</h3>
            <div class="chon-nhieu">
              <label v-for="tg in danhSachTacGia" :key="tg.maTacGia" class="nhan-checkbox">
                <input type="checkbox" :value="tg.maTacGia" :checked="form.maTacGias.includes(tg.maTacGia)" @change="toggleId(form.maTacGias, tg.maTacGia)" />
                <span>{{ tg.hoDem }} {{ tg.ten }}</span>
              </label>
            </div>
          </div>

          <!-- Thể loại -->
          <div class="the-nhom">
            <h3 class="tieu-de-nhom">Thể loại (chọn nhiều)</h3>
            <div class="chon-nhieu">
              <label v-for="tl in danhSachTheLoai" :key="tl.maTheLoai" class="nhan-checkbox">
                <input type="checkbox" :value="tl.maTheLoai" :checked="form.maTheLoais.includes(tl.maTheLoai)" @change="toggleId(form.maTheLoais, tl.maTheLoai)" />
                <span>{{ tl.tenTheLoai }}</span>
              </label>
            </div>
          </div>
        </div>

        <!-- Cột phải: ảnh bìa -->
        <div class="cot-phu">
          <div class="the-nhom">
            <h3 class="tieu-de-nhom">Hình ảnh sách</h3>
            <div class="vung-upload">
              <div class="danh-sach-anh">
                <div v-for="(anh, index) in danhSachAnh" :key="index" class="item-anh">
                  <div class="preview-anh-nho">
                    <img :src="anh.previewUrl" />
                    <button class="nut-xoa-anh" @click.stop="xoaAnh(index)">X</button>
                  </div>
                  <select v-model="anh.loaiHinhAnh" class="form-input form-select" style="padding: 0.3rem; font-size: 0.75rem;">
                    <option v-for="opt in loaiHinhAnhOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
                  </select>
                </div>
              </div>
              
              <label class="nut-chon-anh">
                <input type="file" accept="image/*" class="input-file" multiple @change="chonFile" />
                📁 Chọn hình ảnh
              </label>
              <p class="ghi-chu-anh">Định dạng: JPG, PNG, WebP. Có thể chọn nhiều ảnh.</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Nút hành động -->
      <div class="nhom-nut">
        <button class="nut-huy" @click="router.push('/admin/sach')">Hủy bỏ</button>
        <button class="nut-luu" :disabled="dangGui" @click="luuSach">
          <span v-if="dangGui">Đang lưu...</span>
          <span v-else>{{ isEdit ? '💾 Lưu thay đổi' : '✅ Thêm đầu sách' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.sach-form { animation: fadeInUp 0.4s ease; }
.dau-trang { display:flex; align-items:center; gap:1rem; margin-bottom:1.5rem; }
.dau-trang h2 { font-size:1.2rem; font-weight:700; }
.nut-quay-lai { background:none; border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; padding:0.5rem 1rem; font-family:inherit; font-size:0.875rem; transition:all 0.2s; }
.nut-quay-lai:hover { color:var(--mau-chu); background:rgba(255,255,255,0.06); }
.khung-form { display:flex; flex-direction:column; gap:1.25rem; }
.luoi-form { display:grid; grid-template-columns:1fr 280px; gap:1.25rem; }
.cot-chinh,.cot-phu { display:flex; flex-direction:column; gap:1.25rem; }
.the-nhom { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; padding:1.25rem; }
.tieu-de-nhom { font-size:0.875rem; font-weight:700; color:var(--mau-chinh); margin-bottom:1rem; text-transform:uppercase; letter-spacing:0.05em; }
.form-group { display:flex; flex-direction:column; gap:0.375rem; margin-bottom:0.875rem; }
.form-group:last-child { margin-bottom:0; }
.form-group label { font-size:0.8rem; font-weight:600; color:var(--mau-chu); }
.form-input { padding:0.7rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.875rem; outline:none; transition:border-color 0.2s; }
.form-input:focus { border-color:var(--mau-chinh); }
.form-textarea { resize:vertical; min-height:100px; }
.form-select { cursor:pointer; }
.form-select option { background:#1a1a2e; color:#ffffff; }
.hang-doi { display:grid; grid-template-columns:1fr 1fr; gap:0.75rem; }
.chon-nhieu { display:flex; flex-wrap:wrap; gap:0.5rem; }
.nhan-checkbox { display:flex; align-items:center; gap:0.4rem; cursor:pointer; padding:0.3rem 0.65rem; border-radius:20px; border:1px solid rgba(255,255,255,0.08); font-size:0.8rem; transition:all 0.2s; }
.nhan-checkbox:hover { border-color:var(--mau-chinh); background:rgba(6,182,212,0.08); }
.nhan-checkbox input { accent-color:var(--mau-chinh); }
/* Upload ảnh bìa */
.vung-upload { display:flex; flex-direction:column; gap:0.75rem; align-items:stretch; }
.danh-sach-anh { display: grid; grid-template-columns: repeat(2, 1fr); gap: 0.75rem; margin-bottom: 1rem; }
.item-anh { display: flex; flex-direction: column; gap: 0.4rem; }
.preview-anh-nho { position: relative; width:100%; aspect-ratio: 3/4; border-radius:8px; overflow:hidden; background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); display:flex; align-items:center; justify-content:center; }
.preview-anh-nho img { width:100%; height:100%; object-fit:cover; }
.nut-xoa-anh { position: absolute; top: 4px; right: 4px; background: rgba(255,0,0,0.7); color: white; border: none; border-radius: 50%; width: 20px; height: 20px; font-size: 10px; cursor: pointer; display: flex; align-items: center; justify-content: center; z-index: 10; }
.placeholder-anh { text-align:center; font-size:2rem; color:var(--mau-chu-rat-mo); line-height:2; }
.placeholder-anh span { font-size:0.8rem; display:block; }
.nut-chon-anh { cursor:pointer; padding:0.6rem 1rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; font-size:0.8rem; color:var(--mau-chu); transition:all 0.2s; text-align:center; }
.nut-chon-anh:hover { background:rgba(6,182,212,0.12); border-color:var(--mau-chinh); }
.input-file { display:none; }
.ghi-chu-anh { font-size:0.75rem; color:var(--mau-chu-rat-mo); text-align:center; }
/* Nút hành động */
.nhom-nut { display:flex; justify-content:flex-end; gap:0.75rem; }
.nut-huy { padding:0.75rem 1.5rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; font-size:0.9rem; }
.nut-luu { padding:0.75rem 1.75rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-size:0.9rem; font-weight:600; }
.nut-luu:disabled { opacity:0.6; cursor:not-allowed; }
@media (max-width:768px) { .luoi-form { grid-template-columns:1fr; } }
</style>
