/**
 * router/index.ts — Cấu hình Vue Router cho toàn bộ ứng dụng.
 * [DEV MODE] Auth guard bị vô hiệu hóa — cho phép truy cập tất cả route admin trực tiếp.
 */
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    { path: '/', redirect: '/admin/dashboard' },
    {
      path: '/admin',
      component: () => import('@/components/admin/layout/AdminLayout.vue'),
      children: [
        {
          path: '',
          redirect: '/admin/dashboard',
        },
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/admin/DashboardView.vue'),
        },
        // ===== SỬA LỖI 404 CÁC TRANG DANH MỤC Ở ĐÂY =====
        {
          path: 'danh-muc',
          children: [
            {
              path: '',
              name: 'danh-muc',
              component: () => import('@/views/admin/danh-muc/DanhMucView.vue')
            },
            // Map tạm các đường dẫn con về chung 1 trang DanhMucView
            { path: 'tac-gia', component: () => import('@/views/admin/danh-muc/DanhMucView.vue') },
            { path: 'nha-xuat-ban', component: () => import('@/views/admin/danh-muc/DanhMucView.vue') },
            { path: 'the-loai', component: () => import('@/views/admin/danh-muc/DanhMucView.vue') },
            { path: 'vi-tri', component: () => import('@/views/admin/danh-muc/DanhMucView.vue') },
          ]
        },
        // ===== SỬA LỖI 404 NHÂN SỰ & CÀI ĐẶT (TRỎ TẠM VỀ DASHBOARD) =====
        {
          path: 'nhan-su',
          name: 'nhan-su',
          component: () => import('@/views/admin/DashboardView.vue'), // Sửa lại đường dẫn import khi có file thật
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('@/views/admin/DashboardView.vue'), // Sửa lại đường dẫn import khi có file thật
        },
        // =========================================================
        {
          path: 'sach',
          name: 'admin-sach-list',
          component: () => import('@/views/admin/sach/SachListView.vue'),
        },
        {
          path: 'sach/them-moi',
          name: 'admin-sach-them',
          component: () => import('@/views/admin/sach/SachFormView.vue'),
        },
        {
          path: 'sach/:id/chinh-sua',
          name: 'admin-sach-sua',
          component: () => import('@/views/admin/sach/SachFormView.vue'),
        },
        {
          path: 'cuon-sach',
          name: 'cuon-sach',
          component: () => import('@/views/admin/cuon-sach/CuonSachView.vue'),
        },
        {
          path: 'doc-gia',
          name: 'doc-gia',
          component: () => import('@/views/admin/doc-gia/DocGiaView.vue'),
        },
        {
          path: 'users',
          redirect: '/admin/doc-gia',
        },
        {
          path: 'dat-cho',
          name: 'admin-dat-cho',
          component: () => import('@/views/admin/dat-cho/DatChoView.vue'),
        },
        {
          path: 'muon-sach',
          name: 'muon-sach',
          component: () => import('@/views/admin/muon-sach/MuonSachListView.vue'),
        },
        {
          path: 'muon-sach/tao-moi',
          name: 'tao-phieu-muon',
          component: () => import('@/views/admin/muon-sach/TaoPhieuMuonView.vue'),
        },
        {
          path: 'tra-sach',
          name: 'tra-sach',
          component: () => import('@/views/admin/tra-sach/TraSachView.vue'),
        },
        {
          path: 'phat',
          name: 'phat',
          component: () => import('@/views/admin/phat/PhatView.vue'),
        },
      ],
    },

    // =========================================================
    // ⚠️ TRANG LỖI — Truy cập sau khi đăng nhập
    // =========================================================
    {
      path: '/403',
      name: 'forbidden',
      component: () => import('@/views/KhongCoQuyenView.vue'),
    },
    {
      path: '/404',
      name: 'not-found-page',
      component: () => import('@/views/KhongTimThayView.vue'),
    },

    // =========================================================
    // 404 — catch-all → luôn về trang đăng nhập
    // =========================================================
    { path: '/:pathMatch(.*)*', name: 'not-found', redirect: '/404' },
  ],
})

// ===== NAVIGATION GUARDS =====
// [DEV MODE] Bỏ qua auth — cho phép truy cập tất cả route
router.beforeEach((to) => {
  // Redirect root → /admin/dashboard
  if (to.path === '/' || to.path === '') {
    return { path: '/admin/dashboard' }
  }
})

export default router
