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
    },
    {
      path: '/favorites',
      name: 'favorites',
      component: FavoritesView,
    },
    {
      path: '/reservation/:id',
      name: 'reservation-detail',
      component: ReservationDetailView,
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
    },

    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
  ],
})

export default router
