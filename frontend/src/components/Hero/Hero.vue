<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchQuery = ref('')

const heroTitle = ref('Khám phá tri thức tại LibManage')
const heroSubtitle = ref('Hàng ngàn cuốn sách, tài liệu và tài nguyên học tập đang chờ đón bạn. Bắt đầu hành trình tìm kiếm ngay hôm nay.')
const heroImages = ref<string[]>(['/hero-bg.png'])
const heroPopularGenres = ref<string[]>(['Kỹ năng sống', 'Công nghệ', 'Văn học', 'Kinh tế'])

const currentImageIndex = ref(0)
let slideInterval: any = null

onMounted(() => {
  const saved_settings = localStorage.getItem('library_settings')
  if (saved_settings) {
    try {
      const parsed = JSON.parse(saved_settings)
      if (parsed.heroTitle) heroTitle.value = parsed.heroTitle
      if (parsed.heroSubtitle) heroSubtitle.value = parsed.heroSubtitle
      if (parsed.heroImages && parsed.heroImages.length > 0) {
        heroImages.value = parsed.heroImages
      }
      if (parsed.heroPopularGenres && parsed.heroPopularGenres.length > 0) {
        const valid = parsed.heroPopularGenres.filter((g: string) => !!g)
        if (valid.length > 0) {
          heroPopularGenres.value = valid
        }
      }
    } catch {}
  }

  if (heroImages.value.length > 1) {
    slideInterval = setInterval(() => {
      currentImageIndex.value = (currentImageIndex.value + 1) % heroImages.value.length
    }, 5000)
  }
})

onUnmounted(() => {
  if (slideInterval) {
    clearInterval(slideInterval)
  }
})

const handleSearch = () => {
  router.push({ name: 'books', query: { search: searchQuery.value } })
}

const selectGenre = (genre: string) => {
  router.push({ name: 'books', query: { search: genre } })
}
</script>

<template>
  <section class="hero">
    <!-- Sliding background images -->
    <div 
      v-for="(img, idx) in heroImages" 
      :key="idx"
      class="hero-bg-slide"
      :class="{ 'hero-bg-slide--active': currentImageIndex === idx }"
      :style="{ backgroundImage: `url(${img})` }"
    ></div>

    <div class="hero-overlay"></div>
    <div class="container hero-content">
      <div class="hero-text">
        <h1 v-html="heroTitle"></h1>
        <p>{{ heroSubtitle }}</p>
        
        <div class="search-container">
          <form @submit.prevent="handleSearch" class="search-box">
            <span class="search-icon"><font-awesome-icon icon="fa-solid fa-magnifying-glass" /></span>
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="Tìm kiếm sách, tác giả hoặc chủ đề..." 
            />
            <button type="submit" class="btn btn-primary">Tìm kiếm</button>
          </form>
          <div class="search-tags">
            <span>Phổ biến:</span>
            <a 
              v-for="genre in heroPopularGenres" 
              :key="genre" 
              href="#" 
              @click.prevent="selectGenre(genre)"
            >
              {{ genre }}
            </a>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped src="../../assets/css/components/Hero.css"></style>

<style scoped>
.hero-bg-slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  opacity: 0;
  transition: opacity 1.2s ease-in-out;
  z-index: 1;
}
.hero-bg-slide--active {
  opacity: 1;
}
.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.85) 0%, rgba(15, 23, 42, 0.45) 100%);
  z-index: 2;
}
.hero-content {
  position: relative;
  z-index: 5;
  width: 100%;
}
</style>
