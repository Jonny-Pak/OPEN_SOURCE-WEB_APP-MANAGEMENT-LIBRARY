<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '../../components/Navbar/Navbar.vue'
import Footer from '../../components/Footer/Footer.vue'
import BookCard from '../../components/BookCard/BookCard.vue'

const route = useRoute()
const pubId = route.params.id

// Mock publisher data
const publisher = ref({
  id: Number(pubId),
  name: 'Nhà xuất bản Trẻ',
  logo: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=600',
  description: 'Nhà xuất bản Trẻ là một doanh nghiệp nhà nước tại Việt Nam, chuyên xuất bản sách cho thanh thiếu niên. Được thành lập vào ngày 24 tháng 3 năm 1981, NXB Trẻ đã khẳng định vị thế của mình qua nhiều thập kỷ với các dòng sách văn học, kỹ năng, kinh tế và dịch thuật chất lượng cao.',
  address: '161B Lý Chính Thắng, Quận 3, TP. Hồ Chí Minh',
  website: 'www.nxbtre.com.vn',
  email: 'info@nxbtre.com.vn',
  founded: '1981',
  books: [
    {
      id: 1,
      title: 'Đắc Nhân Tâm',
      author: 'Dale Carnegie',
      category: 'Kỹ năng sống',
      image: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400'
    },
    {
      id: 9,
      title: 'Harry Potter và Hòn đá Phù thủy',
      author: 'J.K. Rowling',
      category: 'Văn học',
      image: 'https://images.unsplash.com/photo-1541963463532-d68292c34b19?auto=format&fit=crop&q=80&w=400'
    },
    {
      id: 10,
      title: 'Suối Nguồn',
      author: 'Ayn Rand',
      category: 'Văn học',
      image: 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&q=80&w=400'
    }
  ]
})

</script>

<template>
  <div class="pub-detail-page">
    <Navbar />
    
    <main class="main-content">
      <div class="container">
        <section class="pub-profile">
          <div class="pub-visual">
            <div class="logo-box">
              <img :src="publisher.logo" :alt="publisher.name" />
            </div>
            <div class="pub-quick-info">
              <div class="info-row">
                <i class="fas fa-calendar-check"></i>
                <span>Thành lập: {{ publisher.founded }}</span>
              </div>
              <div class="info-row">
                <i class="fas fa-globe"></i>
                <a :href="'https://' + publisher.website" target="_blank">{{ publisher.website }}</a>
              </div>
            </div>
          </div>

          <div class="pub-content">
            <nav class="breadcrumb">
              <RouterLink to="/publishers">Nhà xuất bản</RouterLink>
              <span class="separator">/</span>
              <span class="current">{{ publisher.name }}</span>
            </nav>
            <h1>{{ publisher.name }}</h1>
            
            <div class="pub-description">
              <h3>Về chúng tôi</h3>
              <p>{{ publisher.description }}</p>
            </div>

            <div class="pub-contact-grid">
              <div class="contact-item">
                <i class="fas fa-map-marker-alt"></i>
                <div>
                  <label>Địa chỉ</label>
                  <p>{{ publisher.address }}</p>
                </div>
              </div>
              <div class="contact-item">
                <i class="fas fa-envelope"></i>
                <div>
                  <label>Email</label>
                  <p>{{ publisher.email }}</p>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="pub-books">
          <div class="section-header">
            <h2>Sách từ {{ publisher.name }}</h2>
            <p>Khám phá kho tri thức được phát hành bởi {{ publisher.name }}</p>
          </div>

          <div class="grid grid-cols-4">
            <BookCard 
              v-for="book in publisher.books" 
              :key="book.id"
              v-bind="book"
              :rating="5"
            />
          </div>
        </section>
      </div>
    </main>
    
    <Footer />
  </div>
</template>

<style scoped src="../../assets/css/views/PublisherDetailView.css"></style>
