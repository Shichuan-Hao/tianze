<template>
  <div class="login-bg" aria-hidden="true">
    <div class="glow glow-a"></div>
    <div class="glow glow-b"></div>
    <div class="glow glow-c"></div>
    <div class="glow glow-d"></div>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.login-bg {
  position: fixed;
  inset: 0;
  overflow: hidden;
  z-index: 1;
  pointer-events: none;
  background:
    radial-gradient(ellipse 80% 60% at 12% 18%, rgba(31, 169, 122, 0.12), transparent 55%),
    radial-gradient(ellipse 70% 50% at 88% 78%, rgba(184, 140, 90, 0.1), transparent 50%),
    linear-gradient(160deg, #f7faf8 0%, #f3f1eb 48%, #eef6f3 100%);
}

:global(body[arco-theme='dark']) .login-bg {
  background:
    radial-gradient(ellipse 80% 60% at 12% 18%, rgba(31, 169, 122, 0.14), transparent 55%),
    radial-gradient(ellipse 70% 50% at 88% 78%, rgba(184, 140, 90, 0.08), transparent 50%),
    linear-gradient(160deg, #141816 0%, #1a1c1b 48%, #151a18 100%);
}

.glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(2px);
  animation: drift 8s ease-in-out infinite;
}

.glow-a {
  left: 6%;
  top: 18%;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(11, 110, 110, 0.16) 0%, transparent 70%);
  animation-duration: 9s;
}

.glow-b {
  left: 18%;
  bottom: 8%;
  width: 280px;
  height: 280px;
  background: radial-gradient(circle, rgba(201, 162, 39, 0.12) 0%, transparent 70%);
  animation-duration: 11s;
  animation-delay: -2s;
}

.glow-c {
  right: 10%;
  top: 12%;
  width: 240px;
  height: 240px;
  background: radial-gradient(circle, rgba(31, 169, 122, 0.14) 0%, transparent 70%);
  animation-duration: 10s;
  animation-delay: -4s;
}

.glow-d {
  right: 4%;
  bottom: 16%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(184, 140, 90, 0.1) 0%, transparent 70%);
  animation-duration: 12s;
  animation-delay: -1s;
}

@keyframes drift {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }

  50% {
    transform: translate3d(0, 18px, 0) scale(1.06);
  }
}
</style>
