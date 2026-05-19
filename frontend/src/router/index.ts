/**
 * router/index.ts — Cấu hình Vue Router cho toàn bộ ứng dụng.
 * [DEV MODE] Auth guard bị vô hiệu hóa — cho phép truy cập tất cả route admin trực tiếp.
 */
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/Home/HomeView.vue'

import LoginView from '../views/Login/LoginView.vue'
import BookDetailView from '../views/BookDetail/BookDetailView.vue'
import BookListView from '../views/BookList/BookListView.vue'
import ProfileView from '../views/Profile/ProfileView.vue'
import AboutView from '../views/About/AboutView.vue'
import GuideView from '../views/Guide/GuideView.vue'
import RulesView from '../views/Rules/RulesView.vue'
import FaqView from '../views/Faq/FaqView.vue'
import ContactView from '../views/Contact/ContactView.vue'

import CartView from '../views/Cart/CartView.vue'
import FavoritesView from '../views/Favorites/FavoritesView.vue'
import ReservationDetailView from '../views/ReservationDetail/ReservationDetailView.vue'
import AuthorsListView from '../views/AuthorsList/AuthorsListView.vue'
import AuthorDetailView from '../views/AuthorDetail/AuthorDetailView.vue'
import PublishersListView from '../views/PublishersList/PublishersListView.vue'
import PublisherDetailView from '../views/PublisherDetail/PublisherDetailView.vue'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView,
    },
    {
      path: '/guide',
      name: 'guide',
      component: GuideView,
    },
    {
      path: '/rules',
      name: 'rules',
      component: RulesView,
    },
    {
      path: '/faq',
      name: 'faq',
      component: FaqView,
    },
    {
      path: '/contact',
      name: 'contact',
      component: ContactView,
    },
    {
      path: '/books',
      name: 'books',
      component: BookListView,
    },
    {
      path: '/book/:id',
      name: 'book-detail',
      component: BookDetailView,
    },
    {
      path: '/borrow/cart',
      name: 'borrow-cart',
      component: CartView,
      meta: { requiresAuth: true }
    },
    {
      path: '/favorites',
      name: 'favorites',
      component: FavoritesView,
      meta: { requiresAuth: true }
    },
    {
      path: '/reservation/:id',
      name: 'reservation-detail',
      component: ReservationDetailView,
      meta: { requiresAuth: true }
    },
    {
      path: '/authors',
      name: 'authors',
      component: AuthorsListView,
    },
    {
      path: '/author/:id',
      name: 'author-detail',
      component: AuthorDetailView,
    },
    {
      path: '/publishers',
      name: 'publishers',
      component: PublishersListView,
    },
    {
      path: '/publisher/:id',
      name: 'publisher-detail',
      component: PublisherDetailView,
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },

    // ===== ADMIN ROUTES =====
    {
      path: '/admin',
      component: () => import('@/components/admin/layout/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
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
        {
          path: 'danh-muc',
          children: [
            {
              path: '',
              name: 'danh-muc',
              component: () => import('@/views/admin/danh-muc/DanhMucView.vue')
            },
            { path: 'tac-gia', component: () => import('@/views/admin/danh-muc/DanhMucView.vue') },
            { path: 'nha-xuat-ban', component: () => import('@/views/admin/danh-muc/DanhMucView.vue') },
            { path: 'the-loai', component: () => import('@/views/admin/danh-muc/DanhMucView.vue') },
            { path: 'vi-tri', component: () => import('@/views/admin/danh-muc/DanhMucView.vue') },
          ]
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('@/views/admin/SettingsView.vue'),
        },
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
          path: 'duyet-gia-han',
          name: 'duyet-gia-han',
          component: () => import('@/views/admin/duyet-gia-han/DuyetGiaHanView.vue'),
        },
        {
          path: 'phat',
          name: 'phat',
          component: () => import('@/views/admin/phat/PhatView.vue'),
        },
      ],
    },

    // ===== ERROR PAGES =====
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
    { path: '/:pathMatch(.*)*', name: 'not-found', redirect: '/404' },
  ],
})

import { useAuthStore } from '@/stores/auth'

// ===== NAVIGATION GUARDS =====
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // Bắt buộc đăng nhập nếu route yêu cầu requiresAuth
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!authStore.daXacThuc) {
      return next({
        name: 'login',
        query: { redirect: to.fullPath }
      })
    }
  }

  // Yêu cầu quyền Admin/Librarian đối với route admin
  if (to.matched.some(record => record.meta.requiresAdmin)) {
    if (authStore.currentRole === 'DOC_GIA') {
      return next({ path: '/403' })
    }
  }

  // Đã đăng nhập nhưng cố tình vào /login
  if (authStore.daXacThuc && to.name === 'login') {
    if (authStore.isAdmin || authStore.isLibrarian) {
      return next({ path: '/admin/dashboard' })
    } else {
      return next({ path: '/' })
    }
  }

  next()
})

export default router
