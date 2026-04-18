<template>
  <span>{{ displayText }}<span class="typing-cursor">|</span></span>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";

interface Props {
  texts: string[];
  typeSpeed?: number;
  deleteSpeed?: number;
  pauseTime?: number;
}

const props = withDefaults(defineProps<Props>(), {
  typeSpeed: 100,
  deleteSpeed: 50,
  pauseTime: 2000,
});

const displayText = ref("");
const currentIndex = ref(0);
let timer: number | null = null;
let isTyping = true;
let isDeleting = false;
let charIndex = 0;

const type = () => {
  const currentText = props.texts[currentIndex.value];

  if (isTyping && !isDeleting) {
    // 打字中
    if (charIndex < currentText.length) {
      displayText.value = currentText.substring(0, charIndex + 1);
      charIndex++;
      timer = window.setTimeout(type, props.typeSpeed);
    } else {
      // 打字完成，等待后开始删除
      isTyping = false;
      timer = window.setTimeout(() => {
        isDeleting = true;
        type();
      }, props.pauseTime);
    }
  } else if (isDeleting) {
    // 删除中
    if (charIndex > 0) {
      charIndex--;
      displayText.value = currentText.substring(0, charIndex);
      timer = window.setTimeout(type, props.deleteSpeed);
    } else {
      // 删除完成，切换到下一条
      isDeleting = false;
      isTyping = true;
      currentIndex.value = (currentIndex.value + 1) % props.texts.length;
      timer = window.setTimeout(type, props.typeSpeed);
    }
  }
};

onMounted(() => {
  timer = window.setTimeout(type, props.typeSpeed);
});

onUnmounted(() => {
  if (timer) {
    clearTimeout(timer);
  }
});
</script>

<style scoped>
.typing-cursor {
  animation: blink 0.7s infinite;
}

@keyframes blink {
  0%,
  50% {
    opacity: 1;
  }
  51%,
  100% {
    opacity: 0;
  }
}
</style>
