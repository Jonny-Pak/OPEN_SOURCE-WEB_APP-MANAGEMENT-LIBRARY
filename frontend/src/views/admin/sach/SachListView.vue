<!--
  SachListView.vue — Danh sách đầu sách với filter, phân trang, xóa, import Excel.
-->
<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { sachService } from '@/services/sachService'
import { theLoaiService } from '@/services/danhMucService'
import { useSearch } from '@/composables/useSearch'
import { usePagination } from '@/composables/usePagination'
import { useToast } from '@/composables/useToast'
import { usePermission } from '@/composables/usePermission'
import type { Sach } from '@/types/sach'
import type { TheLoai } from '@/types/danhmuc'
import ConfirmDialog from '@/components/admin/shared/ConfirmDialog.vue'
import Pagination from '@/components/admin/shared/Pagination.vue'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import EmptyState from '@/components/admin/shared/EmptyState.vue'
import ImportSachExcelModal from '@/components/admin/shared/ImportSachExcelModal.vue'

const router = useRouter()
const toast = useToast()
const { can } = usePermission()
const { tuKhoaTimKiem, tuKhoaDebounced } = useSearch(300)
const phanTrang = usePagination()

const dangTai = ref(false)
const danhSach = ref<Sach[]>([])
const danhSachTheLoai = ref<TheLoai[]>([])
const filterTheLoai = ref<number | ''>('')
const xoaItem = ref<Sach | null>(null)
const dangXoa = ref(false)
const showImportModal = ref(false)

async function taiDanhSach() {
  dangTai.value = true
  try {
    const response = await sachService.danhSach(
      phanTrang.trangHienTai.value,
      10,
      tuKhoaDebounced.value
    )
    danhSach.value = response.content
    phanTrang.capNhatTong(response.totalElements)
  } catch { toast.loi('Không thể tải danh sách sách') }
  finally { dangTai.value = false }
}

async function xacNhanXoa() {
  if (!xoaItem.value) return
  dangXoa.value = true
  try {
    await sachService.xoa(xoaItem.value.maSach)
    toast.thanhCong('Đã xóa đầu sách')
    xoaItem.value = null
    taiDanhSach()
  } catch { toast.loi('Không thể xóa, sách có thể đang được mượn') }
  finally { dangXoa.value = false }
}

async function taiDuLieuDanhMuc() {
  try {
    danhSachTheLoai.value = await theLoaiService.danhSach()
  } catch { /* im lặng */ }
}

async function onImportDone() {
  toast.thanhCong('Import đầu sách hoàn tất!')
  showImportModal.value = false
  taiDanhSach()
}

watch([tuKhoaDebounced, filterTheLoai], () => { phanTrang.datLaiTrang(); taiDanhSach() })
watch(() => phanTrang.trangHienTai.value, taiDanhSach)
onMounted(() => { taiDanhSach(); taiDuLieuDanhMuc() })
</script>

<template>
  <div class="sach-list">
    <div class="thanh-cong-cu">
      <div class="vung-tim-kiem">
        <font-awesome-icon icon="fa-solid fa-magnifying-glass" class="icon-tim-kiem" />
        <input v-model="tuKhoaTimKiem" class="input-tk" placeholder="Tìm tên sách, ISBN..." />
      </div>
      <select v-model="filterTheLoai" class="select-filter">
        <option value="">Tất cả thể loại</option>
        <option v-for="tl in danhSachTheLoai" :key="tl.maTheLoai" :value="tl.maTheLoai">{{ tl.tenTheLoai }}</option>
      </select>
      <button class="nut-them" @click="router.push('/admin/sach/them-moi')">
        <font-awesome-icon icon="fa-solid fa-plus" /> Thêm đầu sách
      </button>
      <button
        v-if="can('sach:import-excel')"
        class="nut-them nut-import"
        @click="showImportModal = true"
      >
        <font-awesome-icon icon="fa-solid fa-file-excel" /> Import Excel
      </button>
    </div>

    <div class="bang-container">
      <SkeletonLoader v-if="dangTai" :rows="7" height="52px" />
      <template v-else>
        <EmptyState v-if="danhSach.length === 0" thong-diep="Không tìm thấy đầu sách nào" />
        <table v-else class="bang">
          <thead>
            <tr>
              <th>Ảnh bìa</th><th>Tên sách</th><th>ISBN</th>
              <th>Tác giả</th><th>NXB</th><th>Năm</th><th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in danhSach" :key="item.maSach">
              <td>
                <div class="anh-bia-nho">
                  <img
                    v-if="item.danhSachHinhAnh?.find(h => h.loaiHinhAnh === 'BIA_TRUOC') || item.danhSachHinhAnh?.[0]"
                    :src="(item.danhSachHinhAnh?.find(h => h.loaiHinhAnh === 'BIA_TRUOC') ?? item.danhSachHinhAnh?.[0])?.duongDan"
                    alt="Ảnh bìa trước"
                  />
                  <span v-else class="anh-placeholder"><font-awesome-icon icon="fa-solid fa-book" /></span>
                </div>
              </td>
              <td><span class="ten-sach">{{ item.tenSach }}</span></td>
              <td><code class="isbn">{{ item.maIsbn }}</code></td>
              <td>{{ item.danhSachTacGia?.map(t => `${t.hoDem} ${t.ten}`).join(', ') || '—' }}</td>
              <td>{{ item.nhaXuatBan?.tenNhaXuatBan || '—' }}</td>
              <td>{{ item.namXuatBan }}</td>
              <td>
                <div class="hanh-dong">
                  <button class="nut-hanh-dong nut-sua" @click="router.push(`/admin/sach/${item.maSach}/chinh-sua`)">
                    <font-awesome-icon icon="fa-solid fa-pen-to-square" /> Sửa
                  </button>
                  <button class="nut-hanh-dong nut-xoa" @click="xoaItem = item">
                    <font-awesome-icon icon="fa-solid fa-trash-can" /> Xóa
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <Pagination :trang-hien-tai="phanTrang.trangHienThiChoNguoiDung.value" :tong-trang="phanTrang.tongTrang.value" :tong-phan-tu="phanTrang.tongPhanTu.value" :kich-thuoc-trang="10" @doi-trang="phanTrang.denTrang" />
      </template>
    </div>

    <ConfirmDialog :dang-mo="xoaItem !== null" :thong-diep="`Xóa đầu sách '${xoaItem?.tenSach}'? Tất cả cuốn sách liên quan cũng sẽ bị xóa.`" :dang-xu-ly="dangXoa" @xac-nhan="xacNhanXoa" @huy="xoaItem = null" />

    <!-- Import Excel Modal -->
    <ImportSachExcelModal
      v-if="showImportModal"
      @close="showImportModal = false"
      @done="onImportDone"
    />
  </div>
</template>

<style scoped>
.sach-list { animation: fadeInUp 0.4s ease; }
.thanh-cong-cu { display:flex; gap:0.75rem; margin-bottom:1rem; flex-wrap:wrap; }
.vung-tim-kiem { position: relative; display: flex; align-items: center; flex: 1; min-width: 200px; }
.icon-tim-kiem { position: absolute; left: 1rem; color: var(--mau-chu-mo); pointer-events: none; }
.input-tk { width: 100%; padding: 0.65rem 1rem 0.65rem 2.5rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; color: var(--mau-chu); font-family: inherit; font-size: 0.875rem; outline: none; box-sizing: border-box; }
.input-tk:focus { border-color: var(--mau-chinh); }
.select-filter { padding:0.65rem 1rem; background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1); border-radius:8px; color:var(--mau-chu); font-family:inherit; font-size:0.875rem; cursor:pointer; }
.select-filter option { background:#1a1a2e; color:#ffffff; }
.nut-them { padding:0.65rem 1.25rem; background:var(--color-primary); border:none; border-radius:8px; color:white; cursor:pointer; font-family:inherit; font-size:0.875rem; font-weight:600; white-space:nowrap; }
.nut-import { display: flex; align-items: center; gap: 0.5rem; background: #16a34a; }
.nut-import:hover { background: #15803d; }
.bang-container { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:12px; overflow:hidden; padding:1rem; }
.bang { width:100%; border-collapse:collapse; }
.bang th { padding:0.75rem 1rem; text-align:left; font-size:0.75rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--mau-chu-mo); border-bottom:1px solid rgba(255,255,255,0.08); }
.bang td { padding:0.8rem 1rem; font-size:0.875rem; border-bottom:1px solid rgba(255,255,255,0.04); vertical-align:middle; }
.bang tr:last-child td { border-bottom:none; }
.bang tr:hover td { background:rgba(255,255,255,0.02); }
.anh-bia-nho { width:40px; height:56px; border-radius:4px; overflow:hidden; background:rgba(255,255,255,0.05); display:flex; align-items:center; justify-content:center; }
.anh-bia-nho img { width:100%; height:100%; object-fit:cover; }
.anh-placeholder { font-size:1.25rem; }
.ten-sach { font-weight:600; }
.isbn { font-size:0.775rem; background:rgba(255,255,255,0.06); padding:0.15rem 0.4rem; border-radius:4px; }
.hanh-dong { display:flex; gap:0.5rem; }
.nut-hanh-dong { display: flex; align-items: center; gap: 0.35rem; padding:0.3rem 0.65rem; border-radius:6px; border:1px solid rgba(255,255,255,0.1); background:none; color:var(--mau-chu-mo); cursor:pointer; font-size:0.8rem; transition:all 0.2s; }
.nut-sua:hover { background:rgba(72,191,227,0.15); border-color:rgba(72,191,227,0.4); color:var(--mau-chu); }
.nut-xoa:hover { background:rgba(255,107,107,0.15); border-color:rgba(255,107,107,0.4); color:var(--mau-chu); }
</style>
