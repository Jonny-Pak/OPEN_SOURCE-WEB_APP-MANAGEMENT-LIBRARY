<!--
  ImportCuonSachExcelModal.vue — Import cuốn sách (bản sao vật lý) từ file Excel.
  Gửi file thẳng lên backend /api/v1/import/excel/cuon-sach.
  Cột: STT | Mã sách (ISBN hoặc ID) | Mã vạch | Vị trí kệ | Tình trạng vật lý | Ghi chú
-->
<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-container">
      <!-- Header -->
      <div class="modal-header">
        <h3>
          <font-awesome-icon icon="fa-solid fa-file-excel" />
          Import cuốn sách từ Excel
        </h3>
        <button class="btn-close" @click="$emit('close')">
          <font-awesome-icon icon="fa-solid fa-xmark" />
        </button>
      </div>

      <div class="modal-body">
        <!-- Bước 1: Tải template -->
        <div class="step-section">
          <p class="step-label">
            <span class="step-num">1</span> Tải file mẫu
          </p>
          <button class="btn-outline" @click="downloadTemplate">
            <font-awesome-icon icon="fa-solid fa-download" /> Tải template Excel
          </button>
          <p class="hint-text">
            Các cột: <strong>STT | Mã sách (ISBN hoặc ID) | Mã vạch | Vị trí kệ | Tình trạng vật lý | Ghi chú</strong>
          </p>
          <div class="info-box">
            <font-awesome-icon icon="fa-solid fa-circle-info" />
            <span>
              <strong>Mã sách</strong>: điền ISBN hoặc ID của đầu sách đã có trong hệ thống.
              <strong>Mã vạch</strong>: mã barcode vật lý dán lên cuốn sách, phải là duy nhất.
              <strong>Tình trạng</strong>: BINH_THUONG hoặc HONG_NANG.
            </span>
          </div>
        </div>

        <!-- Bước 2: Upload file -->
        <div class="step-section">
          <p class="step-label">
            <span class="step-num">2</span> Chọn file Excel
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
              :icon="selectedFile ? 'fa-solid fa-file-circle-check' : 'fa-solid fa-upload'"
              class="dropzone-icon"
              :class="{ 'icon-success': !!selectedFile }"
            />
            <p v-if="selectedFile"><strong>{{ selectedFile.name }}</strong></p>
            <p v-else>Kéo thả file vào đây hoặc <span class="link-text">click để chọn</span></p>
            <p class="hint-text">Hỗ trợ .xlsx (tối đa 10MB)</p>
          </div>
          <input ref="fileInput" type="file" accept=".xlsx" hidden @change="handleFileChange" />
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
                  <th>Mã sách</th>
                  <th>Mã vạch</th>
                  <th>Vị trí kệ</th>
                  <th>Tình trạng</th>
                  <th>Ghi chú</th>
                  <th>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(row, idx) in previewRows.slice(0, 10)"
                  :key="idx"
                  :class="row._error ? 'row-error' : 'row-valid'"
                >
                  <td>{{ idx + 1 }}</td>
                  <td><code>{{ row.maSach }}</code></td>
                  <td><code>{{ row.maVach }}</code></td>
                  <td>{{ row.viTriKe || '—' }}</td>
                  <td>{{ row.tinhTrang || '—' }}</td>
                  <td>{{ row.ghiChu || '—' }}</td>
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
            <p v-if="previewRows.length > 10" class="hint-text" style="padding: 0.5rem 0.75rem;">
              ... và {{ previewRows.length - 10 }} dòng nữa
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
          <p>✅ Thành công: <strong>{{ importResult.thanhCong }}</strong> / {{ importResult.tongSoDong }} cuốn sách</p>
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
          :disabled="!selectedFile || validCount === 0 || isImporting"
          @click="handleImport"
        >
          <font-awesome-icon v-if="isImporting" icon="fa-solid fa-spinner" class="fa-spin" />
          <font-awesome-icon v-else icon="fa-solid fa-file-import" />
          {{ isImporting ? 'Đang import...' : `Import ${validCount} cuốn sách` }}
        </button>
        <button v-else class="btn-primary" @click="handleDone">
          <font-awesome-icon icon="fa-solid fa-check" /> Xong
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import * as XLSX from 'xlsx'
import apiClient from '@/services/apiClient'

const emit = defineEmits<{ close: []; done: [] }>()

const isDragging = ref(false)
const fileInput = ref<HTMLInputElement>()
const selectedFile = ref<File | null>(null)
const isImporting = ref(false)
const importResult = ref<any>(null)

interface PreviewRow {
  maSach: string
  maVach: string
  viTriKe?: string
  tinhTrang?: string
  ghiChu?: string
  _error?: string
}

const previewRows = ref<PreviewRow[]>([])
const validCount = computed(() => previewRows.value.filter(r => !r._error).length)
const errorCount = computed(() => previewRows.value.filter(r => r._error).length)

const COLUMNS = ['STT', 'Mã sách', 'Mã vạch', 'Vị trí kệ', 'Tình trạng', 'Ghi chú']

function downloadTemplate() {
  const ws = XLSX.utils.aoa_to_sheet([
    COLUMNS,
    [1, '9786041186040', 'LIB-2024-0001', 'A1-K01', 'BINH_THUONG', ''],
    [2, '9786041186040', 'LIB-2024-0002', 'A1-K02', 'BINH_THUONG', 'Bản mới nhất'],
    [3, '1234567890',    'LIB-2024-0003', 'B2-K05', 'HONG_NANG',   'Bìa bị rách nhẹ'],
  ])
  ws['!cols'] = [{ wch: 6 }, { wch: 18 }, { wch: 18 }, { wch: 12 }, { wch: 16 }, { wch: 25 }]
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'DanhSachCuonSach')
  XLSX.writeFile(wb, 'template_import_cuon_sach.xlsx')
}

function parseFile(file: File) {
  selectedFile.value = file
  importResult.value = null
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const data = new Uint8Array(e.target!.result as ArrayBuffer)
      const wb = XLSX.read(data, { type: 'array' })
      const sheet = wb.Sheets[wb.SheetNames[0]]
      const rows = XLSX.utils.sheet_to_json(sheet, { header: COLUMNS, range: 1 }) as Record<string, any>[]

      previewRows.value = rows
        .filter(row => Object.values(row).some(v => v !== null && v !== undefined && String(v).trim() !== ''))
        .map(row => {
          const maSach = String(row['Mã sách'] || '').trim()
          const maVach = String(row['Mã vạch'] || '').trim()
          if (!maSach) return { maSach: '—', maVach, _error: 'Thiếu mã sách' }
          if (!maVach) return { maSach, maVach: '—', _error: 'Thiếu mã vạch' }
          return {
            maSach,
            maVach,
            viTriKe: String(row['Vị trí kệ'] || '').trim(),
            tinhTrang: String(row['Tình trạng'] || 'BINH_THUONG').trim(),
            ghiChu: String(row['Ghi chú'] || '').trim()
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
  if (file.size > 10 * 1024 * 1024) { alert('File quá lớn (tối đa 10MB)'); return }
  parseFile(file)
}

function handleDrop(e: DragEvent) {
  isDragging.value = false
  const file = e.dataTransfer?.files?.[0]
  if (!file) return
  if (file.size > 10 * 1024 * 1024) { alert('File quá lớn (tối đa 10MB)'); return }
  parseFile(file)
}

async function handleImport() {
  if (!selectedFile.value) return
  isImporting.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    const result = await apiClient.upload<any>('/api/v1/import/excel/cuon-sach', formData)
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
  position: fixed; inset: 0; background: rgba(0,0,0,0.55);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000; backdrop-filter: blur(4px);
}
.modal-container {
  background: #fff; border-radius: 14px;
  width: 92%; max-width: 820px; max-height: 92vh;
  display: flex; flex-direction: column;
  box-shadow: 0 25px 50px rgba(0,0,0,0.2);
}
.modal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 1.25rem 1.5rem; border-bottom: 1px solid #e5e7eb;
}
.modal-header h3 {
  margin: 0; font-size: 1.1rem; font-weight: 700;
  color: #0f172a; display: flex; align-items: center; gap: 0.6rem;
}
.btn-close { background: none; border: none; font-size: 1.1rem; color: #94a3b8; cursor: pointer; }
.btn-close:hover { color: #0f172a; }
.modal-body {
  flex: 1; overflow-y: auto; padding: 1.5rem;
  display: flex; flex-direction: column; gap: 1.5rem;
}
.step-section { display: flex; flex-direction: column; gap: 0.65rem; }
.step-label {
  margin: 0; font-size: 0.9rem; font-weight: 700; color: #0f172a;
  display: flex; align-items: center; gap: 0.5rem;
}
.step-num {
  width: 22px; height: 22px; border-radius: 50%;
  background: #16a34a; color: #fff; font-size: 0.75rem; font-weight: 700;
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
  border: 2px solid #16a34a; color: #16a34a;
  border-radius: 8px; font-size: 0.875rem; font-weight: 600;
  cursor: pointer; transition: all 0.2s; width: fit-content;
}
.btn-outline:hover { background: #f0fdf4; }
.hint-text { margin: 0; font-size: 0.8rem; color: #94a3b8; }
.info-box {
  display: flex; gap: 0.5rem; padding: 0.75rem 1rem;
  background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 8px;
  font-size: 0.8rem; color: #166534; align-items: flex-start;
}
.info-box svg { margin-top: 2px; flex-shrink: 0; }
.dropzone {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 0.5rem; padding: 1.75rem;
  background: #f8fafc; border: 2px dashed #cbd5e1;
  border-radius: 10px; cursor: pointer; transition: all 0.2s;
}
.dropzone:hover, .dropzone.dragover { background: #f0fdf4; border-color: #16a34a; }
.dropzone.has-file { background: #f0fdf4; border-color: #16a34a; border-style: solid; }
.dropzone p { margin: 0; font-size: 0.875rem; color: #475569; }
.dropzone-icon { font-size: 2rem; color: #94a3b8; }
.icon-success { color: #16a34a !important; }
.link-text { color: #16a34a; font-weight: 600; }
.preview-table-wrapper { border: 1px solid #e5e7eb; border-radius: 8px; overflow-x: auto; }
.preview-table { width: 100%; border-collapse: collapse; font-size: 0.8rem; }
.preview-table th {
  background: #f8fafc; padding: 0.5rem 0.625rem;
  text-align: left; font-weight: 700; color: #0f172a;
  border-bottom: 2px solid #e5e7eb; white-space: nowrap;
}
.preview-table td { padding: 0.5rem 0.625rem; border-bottom: 1px solid #e5e7eb; color: #475569; }
.preview-table tr.row-error td { background: #fff1f2; }
.preview-table tr.row-valid td { background: #fff; }
.badge-error, .badge-success {
  display: inline-flex; align-items: center; gap: 0.3rem;
  font-size: 0.72rem; font-weight: 600; padding: 0.2rem 0.45rem; border-radius: 4px; white-space: nowrap;
}
.badge-error { background: #fee2e2; color: #dc2626; }
.badge-success { background: #dcfce7; color: #16a34a; }
.import-stats { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }
.stat-item {
  display: flex; align-items: center; gap: 0.5rem;
  padding: 0.75rem 1rem; border-radius: 8px; font-size: 0.875rem; font-weight: 600;
}
.stat-valid { background: #dcfce7; color: #16a34a; }
.stat-error { background: #fee2e2; color: #dc2626; }
.result-section { padding: 1rem 1.25rem; border-radius: 10px; }
.result-ok { background: #f0fdf4; border: 1px solid #bbf7d0; }
.result-warn { background: #fffbeb; border: 1px solid #fde68a; }
.result-section h4 { margin: 0 0 0.75rem; display: flex; align-items: center; gap: 0.5rem; font-size: 1rem; }
.result-section p { margin: 0.25rem 0; font-size: 0.875rem; }
.error-list { margin: 0.5rem 0 0; padding-left: 1.25rem; font-size: 0.8rem; color: #b45309; }
.error-list li { margin-bottom: 0.25rem; }
.modal-footer {
  display: flex; align-items: center; justify-content: flex-end;
  gap: 0.75rem; padding: 1.25rem 1.5rem;
  border-top: 1px solid #e5e7eb; flex-shrink: 0;
}
.btn-secondary {
  padding: 0.6rem 1rem; background: #fff; border: 1px solid #d1d5db;
  color: #374151; border-radius: 8px; font-size: 0.875rem; font-weight: 600; cursor: pointer;
}
.btn-secondary:hover { background: #f1f5f9; }
.btn-primary {
  display: flex; align-items: center; gap: 0.5rem;
  padding: 0.6rem 1.25rem; background: #16a34a;
  border: none; color: #fff; border-radius: 8px;
  font-size: 0.875rem; font-weight: 600; cursor: pointer;
}
.btn-primary:hover:not(:disabled) { background: #15803d; }
.btn-primary:disabled { background: #cbd5e1; cursor: not-allowed; }
.fa-spin { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
@media (max-width: 640px) {
  .modal-container { width: 97%; }
  .modal-body { padding: 1rem; gap: 1rem; }
  .import-stats { grid-template-columns: 1fr; }
}
</style>
