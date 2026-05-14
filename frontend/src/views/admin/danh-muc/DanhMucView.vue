<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { tacGiaService, nhaXuatBanService, theLoaiService } from '@/services/danhMucService'
import { useSearch } from '@/composables/useSearch'
import { usePagination } from '@/composables/usePagination'
import { useModal } from '@/composables/useModal'
import { useToast } from '@/composables/useToast'
import type { TacGia, NhaXuatBan, TheLoai } from '@/types/danhmuc'
import ModalDialog from '@/components/admin/shared/ModalDialog.vue'
import ConfirmDialog from '@/components/admin/shared/ConfirmDialog.vue'
import Pagination from '@/components/admin/shared/Pagination.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import EmptyState from '@/components/admin/shared/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { tuKhoaTimKiem, tuKhoaDebounced } = useSearch(300)
const phanTrang = usePagination()

// Định nghĩa 4 Tab và map với URL
type TabId = 'tacGia' | 'nxb' | 'theLoai' | 'viTri'

const tabs = [
  { id: 'tacGia' as TabId, nhan: 'Tác giả', icon: 'fas fa-pen-nib', path: 'tac-gia' },
  { id: 'nxb' as TabId, nhan: 'Nhà xuất bản', icon: 'fas fa-building', path: 'nha-xuat-ban' },
  { id: 'theLoai' as TabId, nhan: 'Thể loại', icon: 'fas fa-tag', path: 'the-loai' },
  { id: 'viTri' as TabId, nhan: 'Vị trí kệ sách', icon: 'fas fa-map-pin', path: 'vi-tri' },
]

// Hàm xác định Tab hiện tại dựa trên URL
function layTabTuUrl(duongDan: string): TabId {
  if (duongDan.includes('nha-xuat-ban')) return 'nxb'
  if (duongDan.includes('the-loai')) return 'theLoai'
  if (duongDan.includes('vi-tri')) return 'viTri'
  return 'tacGia' // Mặc định
}

const tabHienTai = ref<TabId>(layTabTuUrl(route.path))

// Xử lý khi bấm chuyển Tab trên giao diện
function doiTab(tab: typeof tabs[0]) {
  // Đẩy URL mới lên thanh trình duyệt, watch route (bên dưới) sẽ tự cập nhật tabHienTai
  router.push(`/admin/danh-muc/${tab.path}`)
}

// ===== DỮ LIỆU =====
const dangTai = ref(false)
const danhSachTacGia = ref<TacGia[]>([])
const danhSachNXB = ref<NhaXuatBan[]>([])
const danhSachTheLoai = ref<TheLoai[]>([])
const danhSachViTri = ref<any[]>([]) // Dùng tạm any cho Vị Trí

// ===== MODAL THÊM/SỬA =====
const modalTacGia = useModal<TacGia>()
const modalNXB = useModal<NhaXuatBan>()
const modalTheLoai = useModal<TheLoai>()
const modalViTri = useModal<any>()

const formTacGia = ref({ tenTacGia: '', tieuSu: '' })
const formNXB = ref({ tenNXB: '', diaChi: '', website: '' })
const formTheLoai = ref({ tenTheLoai: '', moTa: '' })
const formViTri = ref({ tenViTri: '', moTa: '' })
const dangGui = ref(false)

// ===== XÓA =====
const xoaItem = ref<{ id: number; ten: string } | null>(null)
const dangXoa = ref(false)

async function taiDanhSach(): Promise<void> {
  dangTai.value = true
  try {
    if (tabHienTai.value === 'tacGia') {
      danhSachTacGia.value = await tacGiaService.danhSach()
      phanTrang.capNhatTong(danhSachTacGia.value.length)
    } else if (tabHienTai.value === 'nxb') {
      danhSachNXB.value = await nhaXuatBanService.danhSach()
      phanTrang.capNhatTong(danhSachNXB.value.length)
    } else if (tabHienTai.value === 'theLoai') {
      danhSachTheLoai.value = await theLoaiService.danhSach()
      phanTrang.capNhatTong(danhSachTheLoai.value.length)
    } else {
      // Mock data cho Vị trí kệ sách (vì chưa có service Backend)
      danhSachViTri.value = [
        { maViTri: 1, tenViTri: 'Kệ A - Tầng 1', moTa: 'Khu vực sách CNTT' },
        { maViTri: 2, tenViTri: 'Kệ B - Tầng 2', moTa: 'Khu vực Văn học' }
      ]
      phanTrang.capNhatTong(danhSachViTri.value.length)
    }
  } catch { toast.loi('Không thể tải dữ liệu') }
  finally { dangTai.value = false }
}

function moSuaTacGia(item: TacGia) { modalTacGia.moModalSua(item); formTacGia.value = { tenTacGia: item.tenTacGia, tieuSu: item.tieuSu ?? '' } }
function moSuaNXB(item: NhaXuatBan) { modalNXB.moModalSua(item); formNXB.value = { tenNXB: item.tenNXB, diaChi: item.diaChi ?? '', website: item.website ?? '' } }
function moSuaTheLoai(item: TheLoai) { modalTheLoai.moModalSua(item); formTheLoai.value = { tenTheLoai: item.tenTheLoai, moTa: item.moTa ?? '' } }
function moSuaViTri(item: any) { modalViTri.moModalSua(item); formViTri.value = { tenViTri: item.tenViTri, moTa: item.moTa ?? '' } }

function moThemMoi() {
  if (tabHienTai.value === 'tacGia') { formTacGia.value = { tenTacGia: '', tieuSu: '' }; modalTacGia.moModalThem() }
  else if (tabHienTai.value === 'nxb') { formNXB.value = { tenNXB: '', diaChi: '', website: '' }; modalNXB.moModalThem() }
  else if (tabHienTai.value === 'theLoai') { formTheLoai.value = { tenTheLoai: '', moTa: '' }; modalTheLoai.moModalThem() }
  else { formViTri.value = { tenViTri: '', moTa: '' }; modalViTri.moModalThem() }
}

// ... [Giữ nguyên logic luuTacGia, luuNXB, luuTheLoai như cũ]
async function luuTacGia() {
  if (!formTacGia.value.tenTacGia.trim()) return toast.canhBao('Tên tác giả không được để trống')
  dangGui.value = true
  try {
    if (modalTacGia.dangThem()) { await tacGiaService.taoCai(formTacGia.value); toast.thanhCong('Thêm tác giả thành công') }
    else { await tacGiaService.capNhat(modalTacGia.itemDangSua.value!.maTacGia, formTacGia.value); toast.thanhCong('Cập nhật thành công') }
    modalTacGia.dongModal(); taiDanhSach()
  } catch { toast.loi('Thao tác thất bại') } finally { dangGui.value = false }
}

async function luuNXB() {
  if (!formNXB.value.tenNXB.trim()) return toast.canhBao('Tên NXB không được để trống')
  dangGui.value = true
  try {
    if (modalNXB.dangThem()) { await nhaXuatBanService.taoCai(formNXB.value); toast.thanhCong('Thêm NXB thành công') }
    else { await nhaXuatBanService.capNhat(modalNXB.itemDangSua.value!.maNXB, formNXB.value); toast.thanhCong('Cập nhật thành công') }
    modalNXB.dongModal(); taiDanhSach()
  } catch { toast.loi('Thao tác thất bại') } finally { dangGui.value = false }
}

async function luuTheLoai() {
  if (!formTheLoai.value.tenTheLoai.trim()) return toast.canhBao('Tên thể loại không được để trống')
  dangGui.value = true
  try {
    if (modalTheLoai.dangThem()) { await theLoaiService.taoCai(formTheLoai.value); toast.thanhCong('Thêm thể loại thành công') }
    else { await theLoaiService.capNhat(modalTheLoai.itemDangSua.value!.maTheLoai, formTheLoai.value); toast.thanhCong('Cập nhật thành công') }
    modalTheLoai.dongModal(); taiDanhSach()
  } catch { toast.loi('Thao tác thất bại') } finally { dangGui.value = false }
}

// Logic lưu giả lập cho Vị Trí
async function luuViTri() {
  if (!formViTri.value.tenViTri.trim()) return toast.canhBao('Tên vị trí không được để trống')
  dangGui.value = true
  setTimeout(() => {
    toast.thanhCong('Lưu vị trí thành công (Mock)')
    modalViTri.dongModal()
    dangGui.value = false
  }, 500)
}

async function xacNhanXoa() {
  if (!xoaItem.value) return
  dangXoa.value = true
  try {
    if (tabHienTai.value === 'tacGia') await tacGiaService.xoa(xoaItem.value.id)
    else if (tabHienTai.value === 'nxb') await nhaXuatBanService.xoa(xoaItem.value.id)
    else if (tabHienTai.value === 'theLoai') await theLoaiService.xoa(xoaItem.value.id)
    else { /* Mock xóa vị trí */ await new Promise(r => setTimeout(r, 500)) }
    
    toast.thanhCong('Đã xóa thành công')
    xoaItem.value = null
    taiDanhSach()
  } catch { toast.loi('Không thể xóa, có thể đang được sử dụng') }
  finally { dangXoa.value = false }
}

// -------------------------------------------------------------
// ĐỒNG BỘ URL VÀ TAB
// Lắng nghe sự thay đổi của route, nếu URL đổi thì update Tab
watch(() => route.path, (newPath) => {
  const newTab = layTabTuUrl(newPath)
  if (tabHienTai.value !== newTab) {
    tabHienTai.value = newTab
    tuKhoaTimKiem.value = '' // Reset thanh tìm kiếm khi chuyển tab
  }
})

// Lắng nghe khi tab hoặc từ khóa thay đổi thì load lại data
watch([tabHienTai, tuKhoaDebounced], () => { 
  phanTrang.datLaiTrang(); 
  taiDanhSach() 
})
// Lắng nghe phân trang
watch(() => phanTrang.trangHienTai.value, taiDanhSach)

onMounted(taiDanhSach)
</script>

<template>
  <div class="danh-muc">
    <div class="tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.id" 
        class="tab" 
        :class="{ 'tab--active': tabHienTai === tab.id }" 
        @click="doiTab(tab)"
      >
        <i :class="tab.icon"></i> {{ tab.nhan }}
      </button>
    </div>

    <div class="thanh-cong-cu">
      <input v-model="tuKhoaTimKiem" class="input-tim-kiem" placeholder="🔍 Tìm kiếm..." />
      <button class="nut-them" @click="moThemMoi">+ Thêm mới</button>
    </div>

    <div class="bang-container">
      <SkeletonLoader v-if="dangTai" :rows="6" height="44px" />
      <template v-else>
        
        <template v-if="tabHienTai === 'tacGia'">
          <EmptyState v-if="danhSachTacGia.length === 0" thong-diep="Chưa có tác giả nào" />
          <table v-else class="bang">
            <thead><tr><th>#</th><th>Tên tác giả</th><th>Tiểu sử</th><th>Hành động</th></tr></thead>
            <tbody>
              <tr v-for="(item, i) in danhSachTacGia" :key="item.maTacGia">
                <td>{{ i + 1 }}</td><td>{{ item.tenTacGia }}</td>
                <td class="o-mo-ta">{{ item.tieuSu ?? '—' }}</td>
                <td><div class="hanh-dong">
                  <button class="nut-sua" @click="moSuaTacGia(item)"><i class="fas fa-pen-to-square"></i></button>
                  <button class="nut-xoa" @click="xoaItem = { id: item.maTacGia, ten: item.tenTacGia }"><i class="fas fa-trash"></i></button>
                </div></td>
              </tr>
            </tbody>
          </table>
        </template>

        <template v-else-if="tabHienTai === 'nxb'">
          <EmptyState v-if="danhSachNXB.length === 0" thong-diep="Chưa có nhà xuất bản nào" />
          <table v-else class="bang">
            <thead><tr><th>#</th><th>Tên NXB</th><th>Địa chỉ</th><th>Website</th><th>Hành động</th></tr></thead>
            <tbody>
              <tr v-for="(item, i) in danhSachNXB" :key="item.maNXB">
                <td>{{ i + 1 }}</td><td>{{ item.tenNXB }}</td>
                <td>{{ item.diaChi ?? '—' }}</td><td>{{ item.website ?? '—' }}</td>
                <td><div class="hanh-dong">
                  <button class="nut-sua" @click="moSuaNXB(item)"><i class="fas fa-pen-to-square"></i></button>
                  <button class="nut-xoa" @click="xoaItem = { id: item.maNXB, ten: item.tenNXB }"><i class="fas fa-trash"></i></button>
                </div></td>
              </tr>
            </tbody>
          </table>
        </template>

        <template v-else-if="tabHienTai === 'theLoai'">
          <EmptyState v-if="danhSachTheLoai.length === 0" thong-diep="Chưa có thể loại nào" />
          <table v-else class="bang">
            <thead><tr><th>#</th><th>Tên thể loại</th><th>Mô tả</th><th>Hành động</th></tr></thead>
            <tbody>
              <tr v-for="(item, i) in danhSachTheLoai" :key="item.maTheLoai">
                <td>{{ i + 1 }}</td><td>{{ item.tenTheLoai }}</td>
                <td class="o-mo-ta">{{ item.moTa ?? '—' }}</td>
                <td><div class="hanh-dong">
                  <button class="nut-sua" @click="moSuaTheLoai(item)"><i class="fas fa-pen-to-square"></i></button>
                  <button class="nut-xoa" @click="xoaItem = { id: item.maTheLoai, ten: item.tenTheLoai }"><i class="fas fa-trash"></i></button>
                </div></td>
              </tr>
            </tbody>
          </table>
        </template>

        <template v-else>
          <EmptyState v-if="danhSachViTri.length === 0" thong-diep="Chưa có vị trí kệ sách nào" />
          <table v-else class="bang">
            <thead><tr><th>#</th><th>Tên kệ / Vị trí</th><th>Mô tả / Khu vực</th><th>Hành động</th></tr></thead>
            <tbody>
              <tr v-for="(item, i) in danhSachViTri" :key="item.maViTri">
                <td>{{ i + 1 }}</td><td><strong>{{ item.tenViTri }}</strong></td>
                <td class="o-mo-ta">{{ item.moTa ?? '—' }}</td>
                <td><div class="hanh-dong">
                  <button class="nut-sua" @click="moSuaViTri(item)"><i class="fas fa-pen-to-square"></i></button>
                  <button class="nut-xoa" @click="xoaItem = { id: item.maViTri, ten: item.tenViTri }"><i class="fas fa-trash"></i></button>
                </div></td>
              </tr>
            </tbody>
          </table>
        </template>

        <Pagination :trang-hien-tai="phanTrang.trangHienThiChoNguoiDung.value" :tong-trang="phanTrang.tongTrang.value" :tong-phan-tu="phanTrang.tongPhanTu.value" :kich-thuoc-trang="phanTrang.kichThuocTrang.value" @doi-trang="phanTrang.denTrang" />
      </template>
    </div>

    <ModalDialog :dang-mo="modalTacGia.dangMo.value" :tieu-de="modalTacGia.dangThem() ? 'Thêm tác giả' : 'Sửa tác giả'" @dong="modalTacGia.dongModal()">
      <div class="form-modal">
        <div class="form-group"><label>Tên tác giả *</label><input v-model="formTacGia.tenTacGia" class="form-input" placeholder="Nhập tên tác giả" /></div>
        <div class="form-group"><label>Tiểu sử</label><textarea v-model="formTacGia.tieuSu" class="form-input form-textarea" placeholder="Nhập tiểu sử..."></textarea></div>
      </div>
      <template #footer>
        <button class="nut-huy" @click="modalTacGia.dongModal()">Hủy</button>
        <button class="nut-luu" :disabled="dangGui" @click="luuTacGia">{{ dangGui ? 'Đang lưu...' : 'Lưu' }}</button>
      </template>
    </ModalDialog>

    <ModalDialog :dang-mo="modalNXB.dangMo.value" :tieu-de="modalNXB.dangThem() ? 'Thêm nhà xuất bản' : 'Sửa nhà xuất bản'" @dong="modalNXB.dongModal()">
      <div class="form-modal">
        <div class="form-group"><label>Tên NXB *</label><input v-model="formNXB.tenNXB" class="form-input" placeholder="Nhập tên NXB" /></div>
        <div class="form-group"><label>Địa chỉ</label><input v-model="formNXB.diaChi" class="form-input" placeholder="Địa chỉ..." /></div>
        <div class="form-group"><label>Website</label><input v-model="formNXB.website" class="form-input" placeholder="https://..." /></div>
      </div>
      <template #footer>
        <button class="nut-huy" @click="modalNXB.dongModal()">Hủy</button>
        <button class="nut-luu" :disabled="dangGui" @click="luuNXB">{{ dangGui ? 'Đang lưu...' : 'Lưu' }}</button>
      </template>
    </ModalDialog>

    <ModalDialog :dang-mo="modalTheLoai.dangMo.value" :tieu-de="modalTheLoai.dangThem() ? 'Thêm thể loại' : 'Sửa thể loại'" @dong="modalTheLoai.dongModal()">
      <div class="form-modal">
        <div class="form-group"><label>Tên thể loại *</label><input v-model="formTheLoai.tenTheLoai" class="form-input" placeholder="Nhập tên thể loại" /></div>
        <div class="form-group"><label>Mô tả</label><textarea v-model="formTheLoai.moTa" class="form-input form-textarea" placeholder="Mô tả..."></textarea></div>
      </div>
      <template #footer>
        <button class="nut-huy" @click="modalTheLoai.dongModal()">Hủy</button>
        <button class="nut-luu" :disabled="dangGui" @click="luuTheLoai">{{ dangGui ? 'Đang lưu...' : 'Lưu' }}</button>
      </template>
    </ModalDialog>

    <ModalDialog :dang-mo="modalViTri.dangMo.value" :tieu-de="modalViTri.dangThem() ? 'Thêm vị trí kệ sách' : 'Sửa vị trí kệ sách'" @dong="modalViTri.dongModal()">
      <div class="form-modal">
        <div class="form-group"><label>Tên Kệ / Vị trí *</label><input v-model="formViTri.tenViTri" class="form-input" placeholder="VD: Kệ A - Tầng 1" /></div>
        <div class="form-group"><label>Khu vực / Mô tả</label><textarea v-model="formViTri.moTa" class="form-input form-textarea" placeholder="Mô tả khu vực..."></textarea></div>
      </div>
      <template #footer>
        <button class="nut-huy" @click="modalViTri.dongModal()">Hủy</button>
        <button class="nut-luu" :disabled="dangGui" @click="luuViTri">{{ dangGui ? 'Đang lưu...' : 'Lưu' }}</button>
      </template>
    </ModalDialog>

    <ConfirmDialog :dang-mo="xoaItem !== null" :thong-diep="`Bạn có chắc muốn xóa '${xoaItem?.ten}'? Hành động này không thể hoàn tác.`" :dang-xu-ly="dangXoa" @xac-nhan="xacNhanXoa" @huy="xoaItem = null" />
  </div>
</template>

<style scoped>
/* TOÀN BỘ CSS ĐƯỢC GIỮ NGUYÊN NHƯ FILE GỐC CỦA BẠN */
.danh-muc { animation: fadeInUp 0.4s ease; }
.tabs { display:flex; gap:0.5rem; margin-bottom:1.25rem; border-bottom:1px solid rgba(255,255,255,0.08); padding-bottom:0; }
.tab { padding:0.65rem 1.25rem; background:none; border:none; border-bottom:2px solid transparent; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; font-size:0.9rem; transition:all 0.2s; }
.tab:hover { color:var(--mau-chu); }
.tab--active { color:var(--mau-chinh); border-bottom-color:var(--mau-chinh); font-weight:600; }
.thanh-cong-cu { display:flex; gap:0.75rem; margin-bottom:1rem; }
.input-tim-kiem { flex:1; padding:0.65rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.875rem; outline:none; }
.input-tim-kiem:focus { border-color:var(--mau-chinh); }
.nut-them { padding:0.65rem 1.25rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-size:0.875rem; font-weight:600; white-space:nowrap; }
.bang-container { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; overflow:hidden; padding:1rem; }
.bang { width:100%; border-collapse:collapse; }
.bang th { padding:0.75rem 1rem; text-align:left; font-size:0.775rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--mau-chu-mo); border-bottom:1px solid rgba(255,255,255,0.08); }
.bang td { padding:0.875rem 1rem; font-size:0.875rem; border-bottom:1px solid rgba(255,255,255,0.04); }
.bang tr:last-child td { border-bottom:none; }
.bang tr:hover td { background:rgba(255,255,255,0.03); }
.o-mo-ta { max-width:300px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; color:var(--mau-chu-mo); font-size:0.825rem; }
.hanh-dong { display:flex; gap:0.5rem; }
.nut-sua,.nut-xoa { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; background:none; border:1px solid rgba(255,255,255,0.1); border-radius:6px; cursor:pointer; font-size:0.9rem; transition:all 0.2s; }
.nut-sua:hover { background:rgba(72,191,227,0.15); border-color:rgba(72,191,227,0.4); }
.nut-xoa:hover { background:rgba(255,107,107,0.15); border-color:rgba(255,107,107,0.4); }
.form-modal { display:flex; flex-direction:column; gap:1rem; }
.form-group { display:flex; flex-direction:column; gap:0.375rem; }
.form-group label { font-size:0.825rem; font-weight:600; color:var(--mau-chu); }
.form-input { padding:0.7rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.875rem; outline:none; }
.form-input:focus { border-color:var(--mau-chinh); }
.form-textarea { resize:vertical; min-height:80px; }
.nut-huy { padding:0.65rem 1.25rem; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu-mo); cursor:pointer; font-family:inherit; }
.nut-luu { padding:0.65rem 1.5rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-weight:600; }
.nut-luu:disabled { opacity:0.6; cursor:not-allowed; }
</style>