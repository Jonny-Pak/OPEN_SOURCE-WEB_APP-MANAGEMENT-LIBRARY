<!--
  ImportSachExcelModal.vue — Import đầu sách từ file Excel.
  Gửi file thẳng lên backend /api/v1/import/excel/sach.
  Template hỗ trợ: STT | ISBN | Tên sách | Tác giả | NXB | Thể loại | Năm XB
                   | Số trang | Lần tái bản | Giá tiền | Phạt/ngày | Mô tả
                   | Ảnh bìa trước | Ảnh bìa sau | Ảnh khác (URL, phân tách bằng |)
-->
<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-container">
      <!-- Header -->
      <div class="modal-header">
        <h3>
          <font-awesome-icon icon="fa-solid fa-file-excel" />
          Import đầu sách từ Excel
        </h3>
        <button class="btn-close" @click="$emit('close')">
          <font-awesome-icon icon="fa-solid fa-xmark" />
        </button>
      </div>

      <div class="modal-body">
        <!-- Chế độ import -->
        <div class="mode-switcher">
          <button
            class="mode-btn"
            :class="{ active: importMode === 'excel' }"
            @click="importMode = 'excel'"
          >
            <font-awesome-icon icon="fa-solid fa-file-excel" />
            Excel (URL ảnh)
          </button>
          <button
            class="mode-btn"
            :class="{ active: importMode === 'zip' }"
            @click="importMode = 'zip'"
          >
            <font-awesome-icon icon="fa-solid fa-file-zipper" />
            ZIP (ảnh local)
          </button>
        </div>

        <!-- Giới thiệu chế độ ZIP -->
        <div v-if="importMode === 'zip'" class="info-box info-zip">
          <font-awesome-icon icon="fa-solid fa-circle-info" />
          <span>
            <strong>Chế độ ZIP:</strong> Đóng gói file <code>sach.xlsx</code> + thư mục <code>images/</code>
            vào một file <code>.zip</code>. Cột ảnh trong Excel ghi <strong>tên file ảnh</strong>
            (vd: <code>bia_truoc.jpg</code>). Hệ thống sẽ tự lưu ảnh và hiển thị trên web.
          </span>
        </div>

        <!-- Bước 1: Tải template -->
        <div class="step-section">
          <p class="step-label">
            <span class="step-num">1</span> Tải file mẫu
          </p>
          <button class="btn-outline" @click="downloadTemplate">
            <font-awesome-icon icon="fa-solid fa-download" /> Tải template Excel
          </button>
          <p class="hint-text">
            Các cột: <strong>STT | ISBN | Tên sách | Tác giả | NXB | Thể loại | Năm XB |
            Số trang | Lần tái bản | Giá tiền | Phạt/ngày | Mô tả |
            Ảnh bìa trước (URL) | Ảnh bìa sau (URL) | Ảnh khác (URL, nhiều ảnh cách nhau |)</strong>
          </p>
          <div class="info-box">
            <font-awesome-icon icon="fa-solid fa-circle-info" />
            <span>
              <strong>Ảnh bìa trước</strong> sẽ được hiển thị làm ảnh đại diện trên trang chủ.
              Điền URL ảnh trực tiếp vào cột. Tác giả, NXB, Thể loại sẽ được tự động tạo nếu chưa tồn tại.
            </span>
          </div>
        </div>

        <!-- Bước 2: Upload file -->
        <div class="step-section">
          <p class="step-label">
            <span class="step-num">2</span>
            {{ importMode === 'zip' ? 'Chọn file ZIP' : 'Chọn file Excel' }}
          </p>
          <div
            class="dropzone"
            :class="{ dragover: isDragging, 'has-file': !!selectedFile }"
            @dragover.prevent="isDragging = true"
            @dragleave="isDragging = false"
            @drop.prevent="handleDrop"
            @click="fileInput?.click()"
          >
            <font-awesome-icon
              :icon="selectedFile ? 'fa-solid fa-file-circle-check' : (importMode === 'zip' ? 'fa-solid fa-file-zipper' : 'fa-solid fa-upload')"
              class="dropzone-icon"
              :class="{ 'icon-success': !!selectedFile }"
            />
            <p v-if="selectedFile"><strong>{{ selectedFile.name }}</strong></p>
            <p v-else>Kéo thả file vào đây hoặc <span class="link-text">click để chọn</span></p>
            <p class="hint-text">
              {{ importMode === 'zip' ? 'Hỗ trợ .zip (tối đa 50MB)' : 'Hỗ trợ .xlsx (tối đa 10MB)' }}
            </p>
          </div>
          <input
            ref="fileInput" type="file"
            :accept="importMode === 'zip' ? '.zip' : '.xlsx'"
            hidden @change="handleFileChange"
          />
        </div>

        <!-- Preview dữ liệu -->
        <div v-if="previewRows.length > 0" class="step-section">
          <p class="step-label">
            <span class="step-num">3</span> Xem trước dữ liệu
            <span class="badge-count">{{ previewRows.length }} dòng</span>
          </p>
          <div class="preview-table-wrapper">
            <table class="preview-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>ISBN</th>
                  <th>Tên sách</th>
                  <th>Tác giả</th>
                  <th>NXB</th>
                  <th>Thể loại</th>
                  <th>Năm XB</th>
                  <th>Ảnh bìa trước</th>
                  <th>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(row, idx) in previewRows.slice(0, 8)"
                  :key="idx"
                  :class="row._error ? 'row-error' : 'row-valid'"
                >
                  <td>{{ idx + 1 }}</td>
                  <td><code>{{ row.isbn || '—' }}</code></td>
                  <td>{{ row.tenSach }}</td>
                  <td>{{ row.tacGia || '—' }}</td>
                  <td>{{ row.nxb || '—' }}</td>
                  <td>{{ row.theLoai || '—' }}</td>
                  <td>{{ row.namXb || '—' }}</td>
                  <td>
                    <img
                      v-if="row.anhBiaTruoc"
                      :src="row.anhBiaTruoc"
                      class="preview-img"
                      alt="Bìa trước"
                      @error="(e: Event) => ((e.target as HTMLImageElement).style.display = 'none')"
                    />
                    <span v-else class="no-img">—</span>
                  </td>
                  <td>
                    <span v-if="row._error" class="badge-error">
                      <font-awesome-icon icon="fa-solid fa-triangle-exclamation" /> {{ row._error }}
                    </span>
                    <span v-else class="badge-success">
                      <font-awesome-icon icon="fa-solid fa-circle-check" /> Hợp lệ
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
            <p v-if="previewRows.length > 8" class="hint-text" style="padding: 0.5rem 0.75rem;">
              ... và {{ previewRows.length - 8 }} dòng nữa
            </p>
          </div>

          <div class="import-stats">
            <div class="stat-item stat-valid">
              <font-awesome-icon icon="fa-solid fa-circle-check" />
              <span>{{ validCount }} hợp lệ</span>
            </div>
            <div class="stat-item stat-error">
              <font-awesome-icon icon="fa-solid fa-circle-xmark" />
              <span>{{ errorCount }} lỗi (sẽ bỏ qua)</span>
            </div>
          </div>
        </div>

        <!-- Kết quả sau import -->
        <div v-if="importResult" class="result-section" :class="importResult.thatBai > 0 ? 'result-warn' : 'result-ok'">
          <h4>
            <font-awesome-icon :icon="importResult.thatBai === 0 ? 'fa-solid fa-circle-check' : 'fa-solid fa-triangle-exclamation'" />
            Kết quả import
          </h4>
          <p>✅ Thành công: <strong>{{ importResult.thanhCong }}</strong> / {{ importResult.tongSoDong }} đầu sách</p>
          <p v-if="importResult.thatBai > 0">❌ Thất bại: <strong>{{ importResult.thatBai }}</strong></p>
          <ul v-if="importResult.danhSachLoi?.length" class="error-list">
            <li v-for="(err, i) in importResult.danhSachLoi" :key="i">{{ err }}</li>
          </ul>
        </div>
      </div>

      <!-- Footer -->
      <div class="modal-footer">
        <button class="btn-secondary" @click="$emit('close')">
          {{ importResult ? 'Đóng' : 'Hủy' }}
        </button>
        <button
          v-if="!importResult"
          class="btn-primary"
          :disabled="!selectedFile || (importMode === 'excel' && validCount === 0) || isImporting"
          @click="handleImport"
        >
          <font-awesome-icon v-if="isImporting" icon="fa-solid fa-spinner" class="fa-spin" />
          <font-awesome-icon v-else icon="fa-solid fa-file-import" />
          <template v-if="isImporting">Đang import...</template>
          <template v-else-if="importMode === 'zip'">Import ZIP kèm ảnh</template>
          <template v-else>Import {{ validCount }} sách</template>
        </button>
        <button v-else class="btn-primary" @click="handleDone">
          <font-awesome-icon icon="fa-solid fa-check" /> Xong
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import * as XLSX from 'xlsx'
import apiClient from '@/services/apiClient'

const emit = defineEmits<{ close: []; done: [] }>()

// 'excel' = upload .xlsx (ảnh qua URL) | 'zip' = upload .zip (ảnh local)
const importMode = ref<'excel' | 'zip'>('excel')

const isDragging = ref(false)
const fileInput = ref<HTMLInputElement>()
const selectedFile = ref<File | null>(null)
const isImporting = ref(false)
const importResult = ref<any>(null)

// Reset khi đổi mode
watch(importMode, () => {
  selectedFile.value = null
  previewRows.value = []
  importResult.value = null
  if (fileInput.value) fileInput.value.value = ''
})

interface PreviewRow {
  isbn?: string
  tenSach: string
  tacGia?: string
  nxb?: string
  theLoai?: string
  namXb?: string
  anhBiaTruoc?: string
  _error?: string
}

const previewRows = ref<PreviewRow[]>([])

const validCount = computed(() => previewRows.value.filter(r => !r._error).length)
const errorCount = computed(() => previewRows.value.filter(r => r._error).length)

// Template columns (matching backend expectation)
const COLUMNS = [
  'STT', 'ISBN', 'Tên sách', 'Tác giả', 'NXB', 'Thể loại', 'Năm XB',
  'Số trang', 'Lần tái bản', 'Giá tiền', 'Phạt/ngày', 'Mô tả',
  'Ảnh bìa trước', 'Ảnh bìa sau', 'Ảnh khác'
]

function downloadTemplate() {
  const ws = XLSX.utils.aoa_to_sheet([
    COLUMNS,
    [
      1, '9786041186040', 'Lập trình Vue 3', 'Nguyễn Văn A', 'NXB Trẻ', 'Công nghệ',
      2024, 300, 1, 150000, 5000, 'Sách hướng dẫn Vue 3 từ cơ bản đến nâng cao',
      'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400',
      'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400',
      'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400|https://images.unsplash.com/photo-1507842217343-583bb7270b66?w=400'
    ]
  ])
  // Set column widths
  ws['!cols'] = COLUMNS.map((_, i) => ({ wch: i < 2 ? 8 : i < 12 ? 18 : 40 }))
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'DanhSachSach')
  XLSX.writeFile(wb, 'template_import_sach.xlsx')
}

function parseFile(file: File) {
  selectedFile.value = file
  importResult.value = null
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const data = new Uint8Array(e.target!.result as ArrayBuffer)
      const wb = XLSX.read(data, { type: 'array' })
      const firstSheetName = wb.SheetNames[0]
      if (!firstSheetName) throw new Error('File Excel rỗng')
      const sheet = wb.Sheets[firstSheetName]
      if (!sheet) throw new Error('Không thể tìm thấy sheet đầu tiên')
      const rows = XLSX.utils.sheet_to_json(sheet, { header: COLUMNS, range: 1 }) as Record<string, any>[]

      previewRows.value = rows
        .filter(row => Object.values(row).some(v => v !== null && v !== undefined && String(v).trim() !== ''))
        .map(row => {
          const tenSach = String(row['Tên sách'] || '').trim()
          if (!tenSach) return { tenSach: '—', _error: 'Thiếu tên sách' }
          return {
            isbn: String(row['ISBN'] || '').trim(),
            tenSach,
            tacGia: String(row['Tác giả'] || '').trim(),
            nxb: String(row['NXB'] || '').trim(),
            theLoai: String(row['Thể loại'] || '').trim(),
            namXb: String(row['Năm XB'] || '').trim(),
            anhBiaTruoc: String(row['Ảnh bìa trước'] || '').trim()
          }
        })
    } catch (err) {
      alert('Lỗi đọc file Excel')
    }
  }
  reader.readAsArrayBuffer(file)
}

function handleFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (file.size > 50 * 1024 * 1024) { alert('File quá lớn (tối đa 50MB)'); return }
  if (importMode.value === 'zip') {
    if (!file.name.toLowerCase().endsWith('.zip')) { alert('Vui lòng chọn file .zip'); return }
    selectedFile.value = file
    importResult.value = null
  } else {
    if (!file.name.toLowerCase().endsWith('.xlsx')) { alert('Vui lòng chọn file .xlsx'); return }
    parseFile(file)
  }
}

function handleDrop(e: DragEvent) {
  isDragging.value = false
  const file = e.dataTransfer?.files?.[0]
  if (!file) return
  if (file.size > 50 * 1024 * 1024) { alert('File quá lớn (tối đa 50MB)'); return }
  if (importMode.value === 'zip') {
    if (!file.name.toLowerCase().endsWith('.zip')) { alert('Vui lòng chọn file .zip'); return }
    selectedFile.value = file
    importResult.value = null
  } else {
    if (!file.name.toLowerCase().endsWith('.xlsx')) { alert('Vui lòng chọn file .xlsx'); return }
    parseFile(file)
  }
}

async function handleImport() {
  if (!selectedFile.value) return
  isImporting.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    const endpoint = importMode.value === 'zip'
      ? '/api/v1/import/excel/sach-zip'
      : '/api/v1/import/excel/sach'
    const result = await apiClient.upload<any>(endpoint, formData)
    importResult.value = result
  } catch (err: any) {
    alert('Lỗi import: ' + (err?.message || 'Vui lòng thử lại'))
  } finally {
    isImporting.value = false
  }
}

function handleDone() {
  emit('done')
  emit('close')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.55);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000; backdrop-filter: blur(4px);
}
.modal-container {
  background: #fff; border-radius: 14px;
  width: 92%; max-width: 900px; max-height: 92vh;
  display: flex; flex-direction: column;
  box-shadow: 0 25px 50px rgba(0,0,0,0.2);
}
.modal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}
.modal-header h3 {
  margin: 0; font-size: 1.15rem; font-weight: 700;
  color: #0f172a; display: flex; align-items: center; gap: 0.6rem;
}
.btn-close {
  background: none; border: none; font-size: 1.1rem;
  color: #94a3b8; cursor: pointer; transition: color 0.2s;
}
.btn-close:hover { color: #0f172a; }
.modal-body {
  flex: 1; overflow-y: auto; padding: 1.5rem;
  display: flex; flex-direction: column; gap: 1.5rem;
}
.step-section { display: flex; flex-direction: column; gap: 0.65rem; }
.step-label {
  margin: 0; font-size: 0.9375rem; font-weight: 700; color: #0f172a;
  display: flex; align-items: center; gap: 0.5rem;
}
.step-num {
  width: 22px; height: 22px; border-radius: 50%;
  background: #1e40af; color: #fff; font-size: 0.75rem; font-weight: 700;
  display: inline-flex; align-items: center; justify-content: center;
}
.badge-count {
  margin-left: auto; padding: 0.2rem 0.6rem;
  background: #dcfce7; color: #16a34a;
  border-radius: 20px; font-size: 0.8rem; font-weight: 600;
}
.btn-outline {
  display: inline-flex; align-items: center; gap: 0.4rem;
  padding: 0.55rem 1rem; background: #fff;
  border: 2px solid #0284c7; color: #0284c7;
  border-radius: 8px; font-size: 0.875rem; font-weight: 600;
  cursor: pointer; transition: all 0.2s; width: fit-content;
}
.btn-outline:hover { background: #e0f2fe; }
.hint-text { margin: 0; font-size: 0.8rem; color: #94a3b8; }
.info-box {
  display: flex; gap: 0.5rem; padding: 0.75rem 1rem;
  background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 8px;
  font-size: 0.8125rem; color: #1e40af; align-items: flex-start;
}
.info-box svg { margin-top: 2px; flex-shrink: 0; }
.dropzone {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 0.5rem; padding: 1.75rem;
  background: #f8fafc; border: 2px dashed #cbd5e1;
  border-radius: 10px; cursor: pointer; transition: all 0.2s;
}
.dropzone:hover, .dropzone.dragover { background: #eff6ff; border-color: #0284c7; }
.dropzone.has-file { background: #f0fdf4; border-color: #16a34a; border-style: solid; }
.dropzone p { margin: 0; font-size: 0.875rem; color: #475569; }
.dropzone-icon { font-size: 2rem; color: #94a3b8; }
.icon-success { color: #16a34a !important; }
.link-text { color: #0284c7; font-weight: 600; }
.preview-table-wrapper {
  border: 1px solid #e5e7eb; border-radius: 8px; overflow-x: auto;
}
.preview-table { width: 100%; border-collapse: collapse; font-size: 0.8rem; }
.preview-table th {
  background: #f8fafc; padding: 0.5rem 0.625rem;
  text-align: left; font-weight: 700; color: #0f172a;
  border-bottom: 2px solid #e5e7eb; white-space: nowrap;
}
.preview-table td {
  padding: 0.5rem 0.625rem; border-bottom: 1px solid #e5e7eb; color: #475569;
  max-width: 160px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.preview-table tr.row-error td { background: #fff1f2; }
.preview-table tr.row-valid td { background: #fff; }
.preview-img { width: 36px; height: 50px; object-fit: cover; border-radius: 4px; }
.no-img { color: #cbd5e1; }
.badge-error, .badge-success {
  display: inline-flex; align-items: center; gap: 0.3rem;
  font-size: 0.72rem; font-weight: 600; padding: 0.2rem 0.45rem; border-radius: 4px;
  white-space: nowrap;
}
.badge-error { background: #fee2e2; color: #dc2626; }
.badge-success { background: #dcfce7; color: #16a34a; }
.import-stats { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }
.stat-item {
  display: flex; align-items: center; gap: 0.5rem;
  padding: 0.75rem 1rem; border-radius: 8px;
  font-size: 0.875rem; font-weight: 600;
}
.stat-valid { background: #dcfce7; color: #16a34a; }
.stat-error { background: #fee2e2; color: #dc2626; }
.result-section {
  padding: 1rem 1.25rem; border-radius: 10px;
}
.result-ok { background: #f0fdf4; border: 1px solid #bbf7d0; }
.result-warn { background: #fffbeb; border: 1px solid #fde68a; }
.result-section h4 { margin: 0 0 0.75rem; display: flex; align-items: center; gap: 0.5rem; font-size: 1rem; }
.result-section p { margin: 0.25rem 0; font-size: 0.875rem; }
.error-list { margin: 0.5rem 0 0; padding-left: 1.25rem; font-size: 0.8125rem; color: #b45309; }
.error-list li { margin-bottom: 0.25rem; }
.modal-footer {
  display: flex; align-items: center; justify-content: flex-end;
  gap: 0.75rem; padding: 1.25rem 1.5rem;
  border-top: 1px solid #e5e7eb; flex-shrink: 0;
}
.btn-secondary {
  padding: 0.6rem 1rem; background: #fff;
  border: 1px solid #d1d5db; color: #374151;
  border-radius: 8px; font-size: 0.875rem; font-weight: 600;
  cursor: pointer; transition: all 0.2s;
}
.btn-secondary:hover { background: #f1f5f9; }
.btn-primary {
  display: flex; align-items: center; gap: 0.5rem;
  padding: 0.6rem 1.25rem; background: #1e40af;
  border: none; color: #fff; border-radius: 8px;
  font-size: 0.875rem; font-weight: 600; cursor: pointer; transition: all 0.2s;
}
.btn-primary:hover:not(:disabled) { background: #1d4ed8; }
.btn-primary:disabled { background: #cbd5e1; cursor: not-allowed; }
.fa-spin { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
@media (max-width: 640px) {
  .modal-container { width: 97%; max-height: 97vh; }
  .modal-body { padding: 1rem; gap: 1rem; }
  .import-stats { grid-template-columns: 1fr; }
  .mode-switcher { flex-direction: column; }
}
.mode-switcher {
  display: flex; gap: 0.5rem;
  background: #f1f5f9; border-radius: 10px; padding: 0.35rem;
}
.mode-btn {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: 0.45rem;
  padding: 0.55rem 1rem; border: none; border-radius: 7px;
  font-size: 0.875rem; font-weight: 600; cursor: pointer;
  background: transparent; color: #64748b; transition: all 0.2s;
}
.mode-btn:hover { background: #e2e8f0; color: #0f172a; }
.mode-btn.active { background: #fff; color: #1e40af; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
.info-zip { background: #fefce8; border-color: #fde68a; color: #92400e; }
.info-zip code { background: rgba(0,0,0,0.07); padding: 0.1rem 0.3rem; border-radius: 3px; font-size: 0.85em; }
</style>
