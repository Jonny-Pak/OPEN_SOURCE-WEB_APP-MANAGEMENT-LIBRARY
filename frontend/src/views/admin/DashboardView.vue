<!--
  DashboardView.vue — Trang tổng quan với 4 KPI card và count-up animation.
-->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
// import { layThongKe, type ThongKeDashboard } from '@/services/dashboardService'
import SkeletonLoader from '@/components/admin/shared/SkeletonLoader.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const dangTai = ref(false)
// const thongKe = ref<ThongKeDashboard | null>(null)
const hienThi = ref({ soBanDangMuon: 0, soSachQuaHan: 0, tongTienPhat: 0, tongNguoiDung: 0 })

function demLen(key: keyof typeof hienThi.value, soMucTieu: number): void {
  const buoc = 60
  let i = 0
  const iv = setInterval(() => {
    i++
    const t = 1 - Math.pow(1 - i / buoc, 3)
    hienThi.value[key] = Math.round(soMucTieu * t)
    if (i >= buoc) clearInterval(iv)
  }, 1200 / buoc)
}

async function taiThongKe(): Promise<void> {
  dangTai.value = true
  try {
    // TODO: Implement dashboard endpoint on backend at /api/v1/admin/dashboard/thong-ke
    toast.loi('Tính năng thống kê chưa được triển khai trên máy chủ')
    // thongKe.value = await layThongKe()
    // demLen('soBanDangMuon', thongKe.value.soBanDangMuon)
    // demLen('soSachQuaHan', thongKe.value.soSachQuaHan)
    // demLen('tongTienPhat', thongKe.value.tongTienPhat)
    // demLen('tongNguoiDung', thongKe.value.tongNguoiDung)
  } catch { toast.loi('Không thể tải dữ liệu thống kê') }
  finally { dangTai.value = false }
}

function formatTien(so: number): string {
  return new Intl.NumberFormat('vi-VN').format(so) + ' ₫'
}

// onMounted(taiThongKe)
</script>

<template>
  <div class="dashboard">
    <div class="dau-trang">
      <div>
        <h2 class="tieu-de">Tổng quan hệ thống</h2>
        <p class="mo-ta">Theo dõi hoạt động thư viện theo thời gian thực</p>
      </div>
      <button class="nut-lam-moi" @click="taiThongKe" :disabled="dangTai">
        <i class="fas fa-rotate" :style="{ animation: dangTai ? 'spin 1s linear infinite' : 'none' }"></i>
        Làm mới
      </button>
    </div>

    <div class="luoi-kpi">
      <template v-if="dangTai">
        <div v-for="i in 4" :key="i" class="kpi-skeleton"><SkeletonLoader :rows="3" height="18px" /></div>
      </template>
      <template v-else>
        <div class="kpi-card kpi-card--xanh-duong">
          <i class="kpi-icon fas fa-book"></i>
          <div>
            <div class="kpi-so">{{ hienThi.soBanDangMuon.toLocaleString('vi-VN') }}</div>
            <div class="kpi-nhan">Sách đang mượn</div>
          </div>
        </div>
        <div class="kpi-card" :class="hienThi.soSachQuaHan > 0 ? 'kpi-card--do' : 'kpi-card--xam'">
          <i class="kpi-icon fas fa-exclamation-triangle"></i>
          <div>
            <div class="kpi-so">{{ hienThi.soSachQuaHan.toLocaleString('vi-VN') }}</div>
            <div class="kpi-nhan">Sách quá hạn</div>
          </div>
        </div>
        <div class="kpi-card kpi-card--vang">
          <i class="kpi-icon fas fa-money-bill"></i>
          <div>
            <div class="kpi-so" style="font-size:1.3rem">{{ formatTien(hienThi.tongTienPhat) }}</div>
            <div class="kpi-nhan">Tổng tiền phạt</div>
          </div>
        </div>
        <div class="kpi-card kpi-card--xanh">
          <i class="kpi-icon fas fa-users"></i>
          <div>
            <div class="kpi-so">{{ hienThi.tongNguoiDung.toLocaleString('vi-VN') }}</div>
            <div class="kpi-nhan">Tổng người dùng</div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.dashboard { animation: fadeInUp 0.5s ease; }
.dau-trang { display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:1.5rem; flex-wrap:wrap; gap:1rem; }
.tieu-de { font-size:1.3rem; font-weight:700; margin-bottom:0.25rem; }
.mo-ta { color:var(--mau-chu-mo); font-size:0.875rem; }
.nut-lam-moi { display:flex; align-items:center; gap:0.5rem; padding:0.6rem 1rem; background:#fff; border:1px solid #d1d5db; border-radius:8px; color:var(--mau-chu); cursor:pointer; font-family:inherit; font-size:0.875rem; transition:all 0.2s; }
.nut-lam-moi:hover:not(:disabled) { background:rgba(6,182,212,0.1); border-color:var(--mau-chinh); }
.nut-lam-moi:disabled { opacity:0.6; cursor:not-allowed; }
.luoi-kpi { display:grid; grid-template-columns:repeat(auto-fit,minmax(220px,1fr)); gap:1rem; }
.kpi-skeleton { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:16px; padding:1.5rem; min-height:100px; }
.kpi-card { background:var(--glass-nen); border:1px solid var(--glass-vien); border-radius:16px; padding:1.5rem; display:flex; align-items:center; gap:1rem; backdrop-filter:var(--glass-blur); transition:all 0.3s; animation:fadeInUp 0.5s ease; }
.kpi-card:hover { transform:translateY(-3px); box-shadow:0 12px 28px rgba(15,23,42,0.14); }
.kpi-icon { font-size:2.5rem; flex-shrink:0; color: var(--color-icon-muted); }

/* Icon colors per KPI */
.kpi-card--xanh-duong .kpi-icon { color: var(--color-primary); }
.kpi-card--do .kpi-icon { color: var(--color-danger); }
.kpi-card--vang .kpi-icon { color: var(--color-warning); }
.kpi-card--xanh .kpi-icon { color: var(--color-success); }
.kpi-so { font-size:1.75rem; font-weight:800; line-height:1.1; }
.kpi-nhan { font-size:0.8rem; color:var(--mau-chu-mo); margin-top:0.2rem; }
.kpi-card--xanh-duong .kpi-so { color: var(--color-primary); }
.kpi-card--do { border-color:rgba(255,107,107,0.4); }
.kpi-card--do .kpi-so { color:#ff6b6b; }
.kpi-card--vang .kpi-so { color:#ffd43b; }
.kpi-card--xanh .kpi-so { color:#51cf66; }
.kpi-card--xam .kpi-so { color:var(--mau-chu-mo); }
</style>
