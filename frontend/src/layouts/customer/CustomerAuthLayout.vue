<template>
  <div class="auth">
    <RouterView />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<style scoped>
.auth {
  min-height: 100vh;
  background: #f6f7fb;
}
</style>
