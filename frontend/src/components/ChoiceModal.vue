<template>
  <q-dialog persistent>
    <q-card class="popup">
      <q-bar class="popup-bar"> </q-bar>
      <q-card-section class="popup-text">{{ modalContent }}</q-card-section>
      <div class="full-width row justify-evenly q-mb-lg">
        <q-btn class="primary" v-close-popup @click="$emit('confirm')">확인</q-btn>
        <q-btn class="secondary" v-close-popup @click="$emit('close')">취소</q-btn>
      </div>
    </q-card>
  </q-dialog>
</template>
<script>
export default {
  props: {
    modalContent: String
  },
  emits: ['confirm', 'close']
}
</script>
<style scoped>
.popup {
  background: #f3f1eb;
  width: 20%;
  min-width: 350px;
  max-width: 500px;
}
.popup-bar {
  background: #b3a286;
}
.popup-text {
  font-size: 150%;
  text-align: center;
}
</style>
