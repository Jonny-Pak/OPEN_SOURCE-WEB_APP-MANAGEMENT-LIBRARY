<script setup lang="ts">
import { useRouter } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'
import { useWishlistStore } from '../../stores/wishlist'

const wishlist = useWishlistStore()
const router = useRouter()

</script>

<template>
  <div class="favorites-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <header class="section-header">
          <h1>Sách <span class="text-accent">Yêu thích</span></h1>
          <p>Danh sách những cuốn sách bạn đã lưu để mượn sau</p>
        </header>

        <div v-if="wishlist.itemCount === 0" class="empty-state text-center">
          <div class="empty-icon">
            <i class="far fa-heart"></i>
          </div>
          <h2>Chưa có sách yêu thích</h2>
          <p>Hãy khám phá kho sách và nhấn vào biểu tượng trái tim để lưu lại những cuốn sách bạn thích.</p>
          <button @click="router.push('/books')" class="btn btn-primary">Khám phá ngay</button>
        </div>

        <div v-else class="grid grid-cols-4">
          <BookCard 
            v-for="book in wishlist.items" 
            :key="book.id"
            v-bind="book"
            :rating="5" 
          />
        </div>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/FavoritesView.css"></style>
