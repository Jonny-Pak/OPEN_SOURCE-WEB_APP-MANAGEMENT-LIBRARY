<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { doiMatKhau } from '@/services/authService'
import { kiemTraMatKhau } from '@/composables/useForm'

const router = useRouter()
const authStore = useAuthStore()

const matKhauCu = ref('')
const matKhauMoi = ref('')
const xacNhan = ref('')
const dangGui = ref(false)
const showMatKhauCu = ref(false)
const showMatKhauMoi = ref(false)
const showXacNhan = ref(false)
const thongBao = ref<{ loai: 'ok' | 'loi'; noi: string } | null>(null)

async function xuLyDoiMatKhau(): Promise<void> {
  thongBao.value = null

  if (!matKhauCu.value || !matKhauMoi.value || !xacNhan.value) {
    thongBao.value = { loai: 'loi', noi: 'Vui long dien day du thong tin.' }
    return
  }

  if (!kiemTraMatKhau(matKhauMoi.value)) {
    thongBao.value = { loai: 'loi', noi: 'Mat khau moi phai co it nhat 6 ky tu.' }
    return
  }

  if (matKhauMoi.value !== xacNhan.value) {
    thongBao.value = { loai: 'loi', noi: 'Mat khau xac nhan khong khop.' }
    return
  }

  dangGui.value = true
  try {
    await doiMatKhau(matKhauCu.value, matKhauMoi.value)
    authStore.daDoiMatKhauBatBuoc()
    thongBao.value = { loai: 'ok', noi: 'Doi mat khau thanh cong.' }
    await router.push('/admin/dashboard')
  } catch (e: unknown) {
    const err = e as { message?: string }
    thongBao.value = { loai: 'loi', noi: err.message || 'Doi mat khau that bai.' }
  } finally {
    dangGui.value = false
  }
}
</script>

<template>
  <div class="doi-mat-khau-page">
    <div class="card-form">
      <h1 class="card-title">Doi mat khau lan dau</h1>
      <p class="card-desc">Ban can doi mat khau mac dinh truoc khi su dung he thong.</p>

      <div v-if="thongBao" class="alert" :class="thongBao.loai === 'ok' ? 'alert--ok' : 'alert--loi'">
        {{ thongBao.noi }}
      </div>

      <form class="form-grid" @submit.prevent="xuLyDoiMatKhau">
        <div class="field">
          <label class="label">Mat khau hien tai</label>
          <div style="position: relative; display: flex; align-items: center; width: 100%;">
            <input 
              v-model="matKhauCu" 
              :type="showMatKhauCu ? 'text' : 'password'" 
              class="input" 
              placeholder="Nhap mat khau hien tai" 
              style="width: 100%; padding-right: 40px;"
            />
            <button 
              type="button" 
              @click="showMatKhauCu = !showMatKhauCu" 
              style="position: absolute; right: 12px; background: none; border: none; color: #9ca3af; cursor: pointer; display: flex; align-items: center;"
            >
              <font-awesome-icon :icon="showMatKhauCu ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'" />
            </button>
          </div>
        </div>
        <div class="field">
          <label class="label">Mat khau moi</label>
          <div style="position: relative; display: flex; align-items: center; width: 100%;">
            <input 
              v-model="matKhauMoi" 
              :type="showMatKhauMoi ? 'text' : 'password'" 
              class="input" 
              placeholder="Toi thieu 6 ky tu" 
              style="width: 100%; padding-right: 40px;"
            />
            <button 
              type="button" 
              @click="showMatKhauMoi = !showMatKhauMoi" 
              style="position: absolute; right: 12px; background: none; border: none; color: #9ca3af; cursor: pointer; display: flex; align-items: center;"
            >
              <font-awesome-icon :icon="showMatKhauMoi ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'" />
            </button>
          </div>
        </div>
        <div class="field">
          <label class="label">Xac nhan mat khau moi</label>
          <div style="position: relative; display: flex; align-items: center; width: 100%;">
            <input 
              v-model="xacNhan" 
              :type="showXacNhan ? 'text' : 'password'" 
              class="input" 
              placeholder="Nhap lai mat khau moi" 
              style="width: 100%; padding-right: 40px;"
            />
            <button 
              type="button" 
              @click="showXacNhan = !showXacNhan" 
              style="position: absolute; right: 12px; background: none; border: none; color: #9ca3af; cursor: pointer; display: flex; align-items: center;"
            >
              <font-awesome-icon :icon="showXacNhan ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'" />
            </button>
          </div>
        </div>

        <button type="submit" class="btn-submit" :disabled="dangGui">
          {{ dangGui ? 'Dang cap nhat...' : 'Cap nhat mat khau' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.doi-mat-khau-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: var(--color-background);
}
.card-form {
  width: 100%;
  max-width: 460px;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 1.75rem;
  background: var(--color-background);
}
.card-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-text);
}
.card-desc {
  margin-top: 0.4rem;
  margin-bottom: 1rem;
  color: #6b7280;
  font-size: 0.92rem;
}
.alert {
  padding: 0.75rem;
  border-radius: 10px;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}
.alert--ok {
  background: rgba(6, 182, 212, 0.1);
  color: var(--color-primary);
  border: 1px solid rgba(6, 182, 212, 0.3);
}
.alert--loi {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.3);
}
.form-grid {
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}
.field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}
.label {
  color: var(--color-text);
  font-size: 0.88rem;
  font-weight: 600;
}
.input {
  border: 1px solid #d1d5db;
  border-radius: 10px;
  padding: 0.75rem 0.9rem;
  font-size: 0.92rem;
  outline: none;
}
.input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.16);
}
.btn-submit {
  margin-top: 0.5rem;
  border: none;
  border-radius: 10px;
  background: var(--color-primary);
  color: #fff;
  padding: 0.8rem;
  font-weight: 700;
  cursor: pointer;
}
.btn-submit:hover:not(:disabled) {
  background: var(--color-primary-hover);
}
.btn-submit:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}
</style>
