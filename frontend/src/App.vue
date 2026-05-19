<script setup lang="ts">
import { RouterView } from 'vue-router'
import ToastMessage from '@/components/admin/shared/ToastMessage.vue'
</script>

<template>
  <RouterView />
  <ToastMessage />
</template>

<style src="./assets/css/App.css"></style>
