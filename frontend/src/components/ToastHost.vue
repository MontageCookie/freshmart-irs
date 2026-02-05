<script setup lang="ts">
import { removeToast, useToastState } from '@/ui/toast'

const toastState = useToastState()
</script>

<template>
  <div class="toast-host" aria-live="polite" aria-atomic="true">
    <div v-for="t in toastState.items" :key="t.id" class="toast" :data-type="t.type">
      <div class="toast__content">
        <div class="toast__message">{{ t.message }}</div>
      </div>
      <button class="toast__close" type="button" @click="removeToast(t.id)">×</button>
    </div>
  </div>
</template>

<style scoped>
.toast-host {
  position: fixed;
  top: 12px;
  right: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 9999;
  width: min(420px, calc(100vw - 24px));
}

.toast {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
  padding: 12px 12px;
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: #fff;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12);
}

.toast[data-type='error'] {
  border-color: rgba(214, 48, 49, 0.35);
}

.toast__message {
  font-size: 14px;
  line-height: 1.4;
  white-space: pre-wrap;
  word-break: break-word;
}

.toast__close {
  border: none;
  background: transparent;
  color: rgba(0, 0, 0, 0.45);
  font-size: 20px;
  cursor: pointer;
  padding: 0 6px;
}

.toast__close:hover {
  color: rgba(0, 0, 0, 0.75);
}
</style>

