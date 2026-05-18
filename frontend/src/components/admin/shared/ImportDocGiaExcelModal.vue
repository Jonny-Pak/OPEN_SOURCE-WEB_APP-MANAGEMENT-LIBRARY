<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-container">
      <div class="modal-header">
        <h3><font-awesome-icon icon="fa-solid fa-file-excel" /> Import độc giả từ Excel</h3>
        <button class="btn-close" @click="$emit('close')">
          <font-awesome-icon icon="fa-solid fa-xmark" />
        </button>
      </div>

      <div class="modal-body">
        <!-- Bước 1: Tải file mẫu -->
        <div class="step-section">
          <p class="step-label"><font-awesome-icon icon="fa-solid fa-1" /> Tải file mẫu</p>
          <button class="btn-outline" @click="downloadTemplate">
            <font-awesome-icon icon="fa-solid fa-download" /> Tải template Excel
          </button>
          <p class="hint-text">File mẫu gồm các cột: STT, Mã SV, Họ tên, Giới tính, Ngày sinh</p>
        </div>

        <!-- Bước 2: Upload file -->
        <div class="step-section">
          <p class="step-label"><font-awesome-icon icon="fa-solid fa-2" /> Upload file Excel</p>
          <div
            class="dropzone"
            :class="{ 'dragover': isDragging }"
            @dragover.prevent="isDragging = true"
            @dragleave="isDragging = false"
            @drop.prevent="handleDrop"
            @click="fileInput?.click()"
          >
            <font-awesome-icon icon="fa-solid fa-upload" class="dropzone-icon" />
            <p>Kéo thả file vào đây hoặc <span class="link-text">click để chọn</span></p>
            <p class="hint-text">Hỗ trợ .xlsx, .xls (tối đa 5MB)</p>
          </div>
          <input ref="fileInput" type="file" accept=".xlsx,.xls" hidden @change="handleFileChange" />
        </div>

        <!-- Preview dữ liệu -->
        <div v-if="previewData.length > 0" class="preview-section">
          <p class="step-label">
            <font-awesome-icon icon="fa-solid fa-3" /> Xem trước dữ liệu
            <span class="badge-count">{{ previewData.length }} dòng</span>
          </p>
          <div class="preview-table-wrapper">
            <table class="preview-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th v-for="col in columns" :key="col">{{ col }}</th>
                  <th>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(row, index) in previewData.slice(0, 10)"
                  :key="index"
                  :class="row._error ? 'row-error' : 'row-valid'"
                >
                  <td>{{ index + 1 }}</td>
                  <td v-for="col in columns" :key="col">{{ row[col] }}</td>
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
            <p v-if="previewData.length > 10" class="hint-text">
              ... và {{ previewData.length - 10 }} dòng nữa
            </p>
          </div>

          <!-- Thống kê -->
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
      </div>

      <div class="modal-footer">
        <button class="btn-secondary" @click="$emit('close')">Hủy</button>
        <button
          class="btn-primary"
          :disabled="validCount === 0 || isImporting"
          @click="handleImport"
        >
          <font-awesome-icon v-if="isImporting" icon="fa-solid fa-spinner" class="fa-spin" />
          <font-awesome-icon v-else icon="fa-solid fa-file-excel" />
          {{ isImporting ? 'Đang import...' : `Import ${validCount} độc giả` }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import * as XLSX from 'xlsx'

const emit = defineEmits<{
  close: []
  imported: [file: File]
}>()

const isDragging = ref(false)
const fileInput = ref<HTMLInputElement>()
const previewData = ref<Record<string, unknown>[]>([])
const selectedFile = ref<File | null>(null)
const isImporting = ref(false)

const columns = ['STT', 'Mã SV', 'Họ tên', 'Giới tính', 'Ngày sinh']

const validCount = computed(() => previewData.value.filter(r => !r._error).length)
const errorCount = computed(() => previewData.value.filter(r => r._error).length)

const downloadTemplate = () => {
  try {
    const ws = XLSX.utils.aoa_to_sheet([
      columns,
      [1, '1250080082', 'Đào Kỹ Vân Khoa', 'Nam', '04/05/2005'],
    ])
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, 'DanhSachDocGia')
    XLSX.writeFile(wb, 'template_import_doc_gia.xlsx')
  } catch (error) {
    console.error('Lỗi tải template:', error)
    alert('Lỗi tải template Excel')
  }
}

const parseFile = (file: File) => {
  selectedFile.value = file
  try {
    const reader = new FileReader()
    reader.onload = (e) => {
      try {
        const data = new Uint8Array(e.target!.result as ArrayBuffer)
        const workbook = XLSX.read(data, { type: 'array' })
        const sheetName = workbook.SheetNames[0]
        if (!sheetName) {
          alert('Không tìm thấy sheet nào trong file Excel')
          return
        }
        const sheet = workbook.Sheets[sheetName]
        if (!sheet) {
          alert('Không thể đọc dữ liệu từ sheet!')
          return
        }
        const rows = XLSX.utils.sheet_to_json(sheet, { header: columns, range: 1 }) as Record<string, any>[]

        // Lọc bỏ dòng trống hoàn toàn
        const filteredRows = rows.filter(row => {
          return Object.values(row).some(val => val !== null && val !== undefined && val.toString().trim() !== '')
        })

        // Validate preview data
        previewData.value = filteredRows.map(row => {
          let mssv = row['Mã SV'] ? row['Mã SV'].toString().trim() : ''
          // Xử lý định dạng Scientific Notation
          if (mssv.includes('e') || mssv.includes('E')) {
            const num = Number(mssv)
            if (!isNaN(num)) {
              mssv = Math.round(num).toString()
            }
          }

          if (!mssv) {
            return { ...row, _error: 'Thiếu mã số sinh viên' }
          }
          if (!/^\d+$/.test(mssv)) {
            return { ...row, _error: 'Mã SV phải là chuỗi số' }
          }

          const hoTen = row['Họ tên'] ? row['Họ tên'].toString().trim() : ''
          if (!hoTen) {
            return { ...row, _error: 'Thiếu họ tên độc giả' }
          }

          const gt = String(row['Giới tính'] || '').trim().toUpperCase()
          if (gt !== 'NAM' && gt !== 'NỮ' && gt !== 'NU') {
            return { ...row, _error: 'Giới tính phải là Nam hoặc Nữ' }
          }

          row['Mã SV'] = mssv
          row['Họ tên'] = hoTen
          return row
        })
      } catch (error) {
        console.error('Lỗi parse file:', error)
        alert('Lỗi đọc file Excel')
      }
    }
    reader.readAsArrayBuffer(file)
  } catch (error) {
    console.error('Lỗi:', error)
    alert('Lỗi xử lý file')
  }
}

const handleFileChange = (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (file) {
    if (file.size > 5 * 1024 * 1024) {
      alert('File quá lớn (tối đa 5MB)')
      return
    }
    parseFile(file)
  }
}

const handleDrop = (e: DragEvent) => {
  isDragging.value = false
  const file = e.dataTransfer?.files?.[0]
  if (file) {
    if (file.size > 5 * 1024 * 1024) {
      alert('File quá lớn (tối đa 5MB)')
      return
    }
    parseFile(file)
  }
}

const handleImport = async () => {
  if (!selectedFile.value) return
  isImporting.value = true
  try {
    const validRows = previewData.value.filter(r => !r._error)
    if (validRows.length === 0) {
      alert('Không có độc giả nào hợp lệ để import!')
      isImporting.value = false
      return
    }

    // Build file Excel sạch từ validRows
    const sheetData = [
      columns, // Header
      ...validRows.map((row, index) => [
        index + 1,
        row['Mã SV'],
        row['Họ tên'],
        row['Giới tính'],
        row['Ngày sinh'] || ''
      ])
    ]

    const ws = XLSX.utils.aoa_to_sheet(sheetData)
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, 'DanhSachDocGia')
    
    // Ghi workbook thành ArrayBuffer
    const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
    const cleanFile = new File([wbout], selectedFile.value.name, {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })

    emit('imported', cleanFile)
    emit('close')
  } catch (error) {
    console.error('Lỗi import:', error)
    alert('Lỗi import dữ liệu')
  } finally {
    isImporting.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background: #ffffff;
  border-radius: 12px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.25rem;
  color: #94a3b8;
  cursor: pointer;
  transition: color 0.2s;
}

.btn-close:hover {
  color: #0f172a;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.step-section {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.step-label {
  margin: 0;
  font-size: 0.9375rem;
  font-weight: 600;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.badge-count {
  display: inline-flex;
  align-items: center;
  padding: 0.25rem 0.75rem;
  background: #dcfce7;
  color: #16a34a;
  border-radius: 20px;
  font-size: 0.8125rem;
  font-weight: 600;
  margin-left: auto;
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1rem;
  background: #ffffff;
  border: 2px solid #0284c7;
  color: #0284c7;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  width: fit-content;
}

.btn-outline:hover {
  background: #e0f2fe;
}

.hint-text {
  margin: 0;
  font-size: 0.8125rem;
  color: #94a3b8;
}

.dropzone {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 2rem;
  background: #f8fafc;
  border: 2px dashed #cbd5e1;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.dropzone:hover {
  background: #f1f5f9;
  border-color: #0284c7;
}

.dropzone.dragover {
  background: #e0f2fe;
  border-color: #0284c7;
}

.dropzone p {
  margin: 0;
  font-size: 0.875rem;
  color: #475569;
}

.dropzone p:first-of-type {
  font-weight: 500;
}

.link-text {
  color: #0284c7;
  font-weight: 600;
  cursor: pointer;
}

.dropzone-icon {
  font-size: 2rem;
  color: #94a3b8;
}

.preview-section {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.preview-table-wrapper {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow-x: auto;
}

.preview-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.8125rem;
}

.preview-table th {
  background: #f8fafc;
  padding: 0.625rem;
  text-align: left;
  font-weight: 600;
  color: #0f172a;
  border-bottom: 2px solid #e5e7eb;
}

.preview-table td {
  padding: 0.625rem;
  border-bottom: 1px solid #e5e7eb;
  color: #475569;
}

.preview-table tr.row-error td {
  background: #fee2e2;
}

.preview-table tr.row-valid td {
  background: #ffffff;
}

.badge-error,
.badge-success {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.75rem;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

.badge-error {
  background: #fee2e2;
  color: #dc2626;
}

.badge-success {
  background: #dcfce7;
  color: #16a34a;
}

.import-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.875rem 1rem;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
}

.stat-valid {
  background: #dcfce7;
  color: #16a34a;
}

.stat-error {
  background: #fee2e2;
  color: #dc2626;
}

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1.5rem;
  border-top: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.btn-secondary {
  padding: 0.625rem 1rem;
  background: #ffffff;
  border: 1px solid #d1d5db;
  color: #0f172a;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1.25rem;
  background: #1e40af;
  border: none;
  color: #ffffff;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background: #1d4ed8;
}

.btn-primary:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
  opacity: 0.6;
}

.fa-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@media (max-width: 640px) {
  .modal-container {
    width: 95%;
    max-height: 95vh;
  }

  .modal-body {
    padding: 1rem;
    gap: 1rem;
  }

  .import-stats {
    grid-template-columns: 1fr;
  }
}
</style>
