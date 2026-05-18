<template>
  <div class="settings-view">
    <h2 class="tieu-de">⚙️ Cài đặt hệ thống</h2>

    <!-- QR Thanh toán -->
    <div class="section-card">
      <div class="card-head">
        <div class="card-icon">📱</div>
        <div>
          <h3>Mã QR Thanh toán chuyển khoản</h3>
          <p>Ảnh QR sẽ hiển thị khi độc giả/thủ thư chọn phương thức "Chuyển khoản".</p>
        </div>
      </div>

      <div class="qr-area">
        <div class="qr-preview" @click="triggerUpload">
          <img v-if="qrUrl" :src="qrUrl" alt="QR Thanh toán" class="qr-img" />
          <div v-else class="qr-placeholder">
            <span class="qr-icon">📷</span>
            <span>Click để chọn ảnh QR</span>
          </div>
        </div>

        <div class="qr-actions">
          <input ref="fileInput" type="file" accept="image/*" hidden @change="onFileChange" />
          <button class="nut-upload" @click="triggerUpload">
            📂 {{ qrUrl ? 'Thay ảnh QR' : 'Chọn ảnh QR' }}
          </button>
          <button v-if="qrUrl" class="nut-xoa-qr" @click="xoaQR">🗑️ Xóa QR</button>
          <p class="hint">Hỗ trợ JPG, PNG, WebP. Nên dùng ảnh vuông ≥ 300×300px.</p>
          <div v-if="qrUrl" class="qr-status">
            <span class="dot-xanh"></span> Đã cấu hình QR thanh toán
          </div>
        </div>
      </div>

      <div v-if="saved" class="alert-ok">✅ Đã lưu cài đặt thành công!</div>
    </div>

    <!-- Thông tin thư viện -->
    <div class="section-card">
      <div class="card-head">
        <div class="card-icon">🏛️</div>
        <div>
          <h3>Thông tin thư viện</h3>
          <p>Hiển thị trong email và giao diện người dùng.</p>
        </div>
      </div>
      <div class="form-grid">
        <div class="form-group">
          <label>Tên thư viện</label>
          <input v-model="settings.tenThuVien" class="form-input" placeholder="Thư viện Đại học Tài nguyên Môi trường TP.HCM" />
        </div>
        <div class="form-group">
          <label>Số điện thoại liên hệ</label>
          <input v-model="settings.sdtLienHe" class="form-input" placeholder="028 XXXX XXXX" />
        </div>
        <div class="form-group" style="grid-column: 1/-1;">
          <label>Địa chỉ</label>
          <input v-model="settings.diaChi" class="form-input" placeholder="236 Lê Văn Sỹ, Q.3, TP.HCM" />
        </div>
      </div>
      <button class="nut-luu" @click="luuCaiDat">💾 Lưu cài đặt</button>
    </div>

    <!-- Cấu hình mượn sách -->
    <div class="section-card">
      <div class="card-head">
        <div class="card-icon">📚</div>
        <div>
          <h3>Cấu hình quy tắc mượn sách</h3>
          <p>Các thông số mặc định dùng khi tạo phiếu mượn mới.</p>
        </div>
      </div>
      <div class="form-grid">
        <div class="form-group">
          <label>Số ngày mượn mặc định</label>
          <input v-model.number="settings.soNgayMuon" type="number" class="form-input" min="1" max="60" />
        </div>
        <div class="form-group">
          <label>Số lần gia hạn tối đa</label>
          <input v-model.number="settings.soLanGiaHan" type="number" class="form-input" min="0" max="5" />
        </div>
        <div class="form-group">
          <label>Số ngày gia hạn mỗi lần</label>
          <input v-model.number="settings.soNgayGiaHan" type="number" class="form-input" min="1" max="30" />
        </div>
        <div class="form-group">
          <label>Số sách mượn tối đa cùng lúc</label>
          <input v-model.number="settings.soSachToiDa" type="number" class="form-input" min="1" max="20" />
        </div>
      </div>
      <button class="nut-luu" @click="luuCaiDat">💾 Lưu cài đặt</button>
    </div>

    <!-- ===== MỨC PHẠT ===== -->
    <div class="section-card">
      <div class="card-head">
        <div class="card-icon">⚖️</div>
        <div>
          <h3>Cấu hình mức phạt</h3>
          <p>Các mức phạt áp dụng khi trả sách trễ, hư hỏng hoặc mất sách.</p>
        </div>
      </div>

      <div class="phat-tabs">
        <button
          v-for="tab in tabsPhat"
          :key="tab.key"
          class="phat-tab"
          :class="{ 'phat-tab--active': tabPhatHienTai === tab.key }"
          @click="tabPhatHienTai = tab.key"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- Theo ngày -->
      <div v-if="tabPhatHienTai === 'theo_ngay'" class="form-grid">
        <div class="form-group">
          <label>Mức phạt mặc định / ngày (VND)</label>
          <div class="input-prefix-wrapper">
            <span class="input-prefix">₫</span>
            <input
              v-model.number="settings.phat.mucPhatMacDinhTheoNgay"
              type="number"
              class="form-input form-input--prefix"
              min="0"
              step="1000"
              placeholder="5000"
            />
          </div>
          <span class="form-hint">Áp dụng khi cuốn sách không có đơn giá phạt riêng.</span>
        </div>
        <div class="form-group">
          <label>Phạt tối đa / cuốn (VND, 0 = không giới hạn)</label>
          <div class="input-prefix-wrapper">
            <span class="input-prefix">₫</span>
            <input
              v-model.number="settings.phat.mucPhatToiDaMoiCuon"
              type="number"
              class="form-input form-input--prefix"
              min="0"
              step="10000"
              placeholder="500000"
            />
          </div>
        </div>
      </div>

      <!-- Theo tình trạng -->
      <div v-if="tabPhatHienTai === 'theo_tinh_trang'" class="form-grid form-grid--3">
        <div class="form-group">
          <label>📗 Sách hư nhẹ (VND)</label>
          <div class="input-prefix-wrapper">
            <span class="input-prefix">₫</span>
            <input
              v-model.number="settings.phat.huNhe"
              type="number"
              class="form-input form-input--prefix"
              min="0"
              step="5000"
            />
          </div>
        </div>
        <div class="form-group">
          <label>📙 Sách hư vừa (VND)</label>
          <div class="input-prefix-wrapper">
            <span class="input-prefix">₫</span>
            <input
              v-model.number="settings.phat.huVua"
              type="number"
              class="form-input form-input--prefix"
              min="0"
              step="5000"
            />
          </div>
        </div>
        <div class="form-group">
          <label>📕 Sách hư nặng (VND)</label>
          <div class="input-prefix-wrapper">
            <span class="input-prefix">₫</span>
            <input
              v-model.number="settings.phat.huNang"
              type="number"
              class="form-input form-input--prefix"
              min="0"
              step="10000"
            />
          </div>
        </div>
      </div>

      <!-- Theo giá sách -->
      <div v-if="tabPhatHienTai === 'theo_gia_sach'" class="form-grid">
        <div class="form-group">
          <label>Hệ số phạt mất sách (x giá bìa)</label>
          <div class="input-prefix-wrapper">
            <span class="input-prefix">×</span>
            <input
              v-model.number="settings.phat.heSoMatSach"
              type="number"
              class="form-input form-input--prefix"
              min="1"
              max="5"
              step="0.1"
            />
          </div>
          <span class="form-hint">Ví dụ: 1.5 = phạt 150% giá bìa sách.</span>
        </div>
        <div class="form-group">
          <label>Phạt mất sách tối thiểu (VND)</label>
          <div class="input-prefix-wrapper">
            <span class="input-prefix">₫</span>
            <input
              v-model.number="settings.phat.matSachToiThieu"
              type="number"
              class="form-input form-input--prefix"
              min="0"
              step="10000"
            />
          </div>
        </div>
      </div>

      <!-- Preview tóm tắt -->
      <div class="phat-summary">
        <div class="summary-chip">
          ⏱ Phạt trễ: <b>{{ formatVND(settings.phat.mucPhatMacDinhTheoNgay) }}/ngày</b>
        </div>
        <div class="summary-chip">
          📕 Hư nặng: <b>{{ formatVND(settings.phat.huNang) }}</b>
        </div>
        <div class="summary-chip">
          ❌ Mất sách: <b>×{{ settings.phat.heSoMatSach }} giá bìa</b>
        </div>
      </div>

      <button class="nut-luu" @click="luuCaiDat">💾 Lưu cấu hình phạt</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const STORAGE_KEY_QR = 'library_qr_url'
const STORAGE_KEY_SETTINGS = 'library_settings'

const fileInput = ref<HTMLInputElement | null>(null)
const qrUrl = ref('')
const saved = ref(false)

const settings = ref({
  tenThuVien: 'Thư viện ĐH Tài nguyên Môi trường TP.HCM',
  sdtLienHe: '028 5445 2222',
  diaChi: '236 Lê Văn Sỹ, Phường 1, Quận 3, TP.HCM',
  soNgayMuon: 14,
  soLanGiaHan: 2,
  soNgayGiaHan: 7,
  soSachToiDa: 5,
  phat: {
    mucPhatMacDinhTheoNgay: 5000,
    mucPhatToiDaMoiCuon: 500000,
    huNhe: 20000,
    huVua: 50000,
    huNang: 100000,
    heSoMatSach: 1.5,
    matSachToiThieu: 50000,
  }
})

// Tab phạt
const tabsPhat = [
  { key: 'theo_ngay', label: '📅 Theo ngày trễ' },
  { key: 'theo_tinh_trang', label: '📋 Theo tình trạng' },
  { key: 'theo_gia_sach', label: '💰 Theo giá sách' },
]
const tabPhatHienTai = ref('theo_ngay')

onMounted(() => {
  const saved_qr = localStorage.getItem(STORAGE_KEY_QR)
  if (saved_qr) qrUrl.value = saved_qr

  const saved_settings = localStorage.getItem(STORAGE_KEY_SETTINGS)
  if (saved_settings) {
    try {
      const parsed = JSON.parse(saved_settings)
      Object.assign(settings.value, parsed)
      // Merge phat separately to avoid missing keys
      if (parsed.phat) {
        Object.assign(settings.value.phat, parsed.phat)
      }
    } catch {}
  }
})

function triggerUpload() {
  fileInput.value?.click()
}

function onFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    alert('Ảnh quá lớn (tối đa 5MB)')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    const url = ev.target?.result as string
    qrUrl.value = url
    localStorage.setItem(STORAGE_KEY_QR, url)
    saved.value = true
    setTimeout(() => { saved.value = false }, 3000)
  }
  reader.readAsDataURL(file)
}

function xoaQR() {
  qrUrl.value = ''
  localStorage.removeItem(STORAGE_KEY_QR)
}

function luuCaiDat() {
  localStorage.setItem(STORAGE_KEY_SETTINGS, JSON.stringify(settings.value))
  saved.value = true
  setTimeout(() => { saved.value = false }, 3000)
}

function formatVND(val: number) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}
</script>

<style scoped>
.settings-view { animation: fadeInUp 0.4s ease; display: flex; flex-direction: column; gap: 1.5rem; }
.tieu-de { font-size: 1.4rem; font-weight: 700; color: var(--mau-chu); margin-bottom: 0; }
.section-card { background: var(--glass-nen); border: 1px solid var(--glass-vien); border-radius: 14px; padding: 1.5rem; display: flex; flex-direction: column; gap: 1.25rem; }
.card-head { display: flex; align-items: flex-start; gap: 1rem; }
.card-icon { font-size: 2rem; line-height: 1; flex-shrink: 0; }
.card-head h3 { margin: 0; font-size: 1rem; font-weight: 700; color: var(--mau-chu); }
.card-head p { margin: 0.25rem 0 0; font-size: 0.825rem; color: var(--mau-chu-mo); }

/* QR */
.qr-area { display: flex; gap: 1.5rem; align-items: flex-start; flex-wrap: wrap; }
.qr-preview {
  width: 180px; height: 180px; border: 2px dashed rgba(6,182,212,0.4); border-radius: 12px;
  display: flex; align-items: center; justify-content: center; cursor: pointer;
  background: rgba(6,182,212,0.04); overflow: hidden; flex-shrink: 0; transition: border-color 0.2s;
}
.qr-preview:hover { border-color: var(--mau-chinh); }
.qr-img { width: 100%; height: 100%; object-fit: contain; }
.qr-placeholder { display: flex; flex-direction: column; align-items: center; gap: 0.5rem; color: var(--mau-chu-mo); font-size: 0.825rem; }
.qr-icon { font-size: 2.5rem; }
.qr-actions { display: flex; flex-direction: column; gap: 0.75rem; justify-content: center; }
.nut-upload { padding: 0.65rem 1.25rem; background: var(--color-primary); border: none; border-radius: 8px; color: white; cursor: pointer; font-family: inherit; font-size: 0.875rem; font-weight: 600; }
.nut-xoa-qr { padding: 0.65rem 1.25rem; background: rgba(239,68,68,0.1); border: 1px solid rgba(239,68,68,0.3); border-radius: 8px; color: #ef4444; cursor: pointer; font-family: inherit; font-size: 0.875rem; }
.hint { font-size: 0.8rem; color: var(--mau-chu-mo); margin: 0; }
.qr-status { display: flex; align-items: center; gap: 0.4rem; font-size: 0.825rem; color: #51cf66; }
.dot-xanh { width: 8px; height: 8px; border-radius: 50%; background: #51cf66; animation: pulse 2s infinite; }
@keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.4} }
.alert-ok { padding: 0.75rem 1rem; background: rgba(81,207,102,0.1); border: 1px solid rgba(81,207,102,0.3); border-radius: 8px; color: #51cf66; font-size: 0.875rem; font-weight: 600; }

/* Form */
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.form-grid--3 { grid-template-columns: 1fr 1fr 1fr; }
.form-group { display: flex; flex-direction: column; gap: 0.375rem; }
.form-group label { font-size: 0.825rem; font-weight: 600; color: var(--mau-chu); }
.form-input { padding: 0.7rem 1rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; color: var(--mau-chu); font-family: inherit; font-size: 0.875rem; outline: none; }
.form-input:focus { border-color: var(--mau-chinh); }
.form-hint { font-size: 0.75rem; color: var(--mau-chu-mo); margin-top: 0.2rem; }

/* Input prefix */
.input-prefix-wrapper { position: relative; display: flex; }
.input-prefix {
  position: absolute;
  left: 0.875rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--mau-chu-mo);
  pointer-events: none;
  user-select: none;
}
.form-input--prefix { padding-left: 2rem; }

.nut-luu { align-self: flex-start; padding: 0.65rem 1.5rem; background: var(--color-primary); border: none; border-radius: 8px; color: white; cursor: pointer; font-family: inherit; font-weight: 600; }

/* Phạt tabs */
.phat-tabs { display: flex; gap: 0.5rem; flex-wrap: wrap; }
.phat-tab {
  padding: 0.5rem 1rem;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  color: var(--mau-chu-mo);
  cursor: pointer;
  font-family: inherit;
  font-size: 0.825rem;
  font-weight: 500;
  transition: all 0.2s;
}
.phat-tab:hover { background: rgba(6,182,212,0.08); color: var(--mau-chu); }
.phat-tab--active {
  background: rgba(6,182,212,0.15);
  border-color: rgba(6,182,212,0.4);
  color: #0891b2;
  font-weight: 700;
}

/* Summary chips */
.phat-summary {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  padding: 0.75rem;
  background: rgba(6,182,212,0.05);
  border: 1px solid rgba(6,182,212,0.15);
  border-radius: 10px;
}
.summary-chip {
  font-size: 0.8rem;
  color: var(--mau-chu);
  padding: 0.35rem 0.75rem;
  background: rgba(6,182,212,0.08);
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 0.35rem;
}
.summary-chip b { color: #0891b2; }

@media (max-width: 600px) {
  .form-grid { grid-template-columns: 1fr; }
  .form-grid--3 { grid-template-columns: 1fr; }
}
</style>
