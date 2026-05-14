<script setup lang="ts">
import { useCartStore } from '../../stores/cart'
import { useWishlistStore } from '../../stores/wishlist'

const props = defineProps<{
  id: number
  title: string
  author: string
  category: string
  image: string
}>()

const cart = useCartStore()
const wishlist = useWishlistStore()

const addToCart = (e: Event) => {
  e.preventDefault()
  cart.addItem({
    id: props.id,
    title: props.title,
    author: props.author,
    image: props.image,
    category: props.category
  })
}

const toggleWishlist = (e: Event) => {
  e.preventDefault()
  wishlist.toggleWishlist({
    id: props.id,
    title: props.title,
    author: props.author,
    image: props.image,
    category: props.category
  })
}

</script>

<template>
  <RouterLink :to="`/book/${id}`" class="book-card">
    <div class="book-image">
      <img :src="image" :alt="title" />
      <div class="book-badge">{{ category }}</div>
      <div class="book-actions">
        <button class="action-btn">Xem chi tiết</button>
        <div class="action-group">
          <button @click="addToCart" class="action-btn icon-only" title="Thêm vào giỏ sách">
            <i class="fas fa-shopping-basket"></i>
          </button>
          <button 
            @click="toggleWishlist" 
            class="action-btn icon-only wishlist-btn" 
            :class="{ active: wishlist.isInWishlist(id) }"
            title="Thêm vào yêu thích"
          >
            <i :class="wishlist.isInWishlist(id) ? 'fas fa-heart' : 'far fa-heart'"></i>
          </button>
        </div>
      </div>
    </div>
    <div class="book-info">
      <h3>{{ title }}</h3>
      <p class="author">{{ author }}</p>
    </div>
  </RouterLink>
</template>

<style scoped src="../../assets/css/components/BookCard.css"></style>
