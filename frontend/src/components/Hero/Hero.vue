<script setup lang="ts">
import { ref } from 'vue'

const searchQuery = ref('')

</script>

<template>
  <section class="hero">
    <div class="hero-overlay"></div>
    <div class="container hero-content">
      <div class="hero-text">
        <h1>Khám phá tri thức tại <span class="text-accent">LibManage</span></h1>
        <p>Hàng ngàn cuốn sách, tài liệu và tài nguyên học tập đang chờ đón bạn. Bắt đầu hành trình tìm kiếm ngay hôm nay.</p>
        
        <div class="search-container">
          <div class="search-box">
            <span class="search-icon"><i class="fas fa-search"></i></span>
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="Tìm kiếm sách, tác giả hoặc chủ đề..." 
            />
            <button class="btn btn-primary">Tìm kiếm</button>
          </div>
          <div class="search-tags">
            <span>Phổ biến:</span>
            <a href="#">Kỹ năng sống</a>
            <a href="#">Công nghệ</a>
            <a href="#">Văn học</a>
            <a href="#">Kinh tế</a>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped src="../../assets/css/components/Hero.css"></style>
