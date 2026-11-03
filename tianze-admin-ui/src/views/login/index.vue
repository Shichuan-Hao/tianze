<template>
  <div v-if="isDesktop" class="login pc">
    <div class="login-stage">
      <div class="login-stage__orb login-stage__orb--a"></div>
      <div class="login-stage__orb login-stage__orb--b"></div>
      <div class="login-stage__orb login-stage__orb--c"></div>
    </div>

    <h3 class="login-logo">
      <img :src="displayLogo" alt="logo" />
      <span>天泽</span>
    </h3>

    <div class="login-main">
      <section class="login-promo">
        <div class="login-promo__text">
          <h1 class="login-promo__title">履道坦坦 泽被万物</h1>
          <p class="login-promo__subtitle">面向企业场景的多租户中后台管理系统</p>
        </div>
        <div class="login-promo__cards">
          <article
            v-for="(card, index) in brandCards"
            :key="card.title"
            class="glass-card"
            :style="{ '--delay': `${index * 60}ms` }"
          >
            <div class="glass-card__icon" v-html="card.icon"></div>
            <div class="glass-card__body">
              <h3 class="glass-card__title">{{ card.title }}</h3>
              <p class="glass-card__desc">{{ card.desc }}</p>
            </div>
          </article>
        </div>
      </section>

      <section class="login-panel">
        <header class="login-panel__header">
          <div v-if="!isEmailLogin" class="login-panel__welcome">欢迎登录</div>
          <h3 v-else class="login-panel__title">邮箱登录</h3>
          <p class="login-panel__hint">{{ loginHint }}</p>
        </header>
        <EmailLogin v-if="isEmailLogin" />
        <a-tabs v-else v-model:active-key="activeTab" class="login-panel__form">
          <a-tab-pane key="1" title="账号登录">
            <component :is="AccountLogin" v-if="activeTab === '1'" />
          </a-tab-pane>
          <a-tab-pane key="2" title="手机号登录">
            <component :is="PhoneLogin" v-if="activeTab === '2'" />
          </a-tab-pane>
        </a-tabs>
        <div class="login-panel__oauth">
          <a-divider orientation="center">其他登录方式</a-divider>
          <div class="list">
            <button
              v-if="isEmailLogin"
              type="button"
              class="mode item"
              @click="toggleLoginMode"
            >
              <icon-user /> 账号/手机号登录
            </button>
            <button
              v-else
              type="button"
              class="mode item"
              @click="toggleLoginMode"
            >
              <icon-email /> 邮箱登录
            </button>
            <a class="item social" title="使用 Gitee 账号登录" @click="onOauth('gitee')">
              <GiSvgIcon name="gitee" :size="20" />
            </a>
            <a class="item social" title="使用 GitHub 账号登录" @click="onOauth('github')">
              <GiSvgIcon name="github" :size="20" />
            </a>
            <a class="item social" title="使用微信账号登录" @click="onOauth('wechat_open')">
              <GiSvgIcon name="wechat" :size="20" />
            </a>
          </div>
        </div>
      </section>
    </div>

    <div class="footer">
      <div class="beian">
        <div class="below text">{{ appStore.getCopyright() }}{{ appStore.getForRecord() ? ` · ${appStore.getForRecord()}` : '' }}</div>
      </div>
    </div>

    <GiThemeBtn class="theme-btn" />
  </div>

  <div v-else class="login h5">
    <div class="login-logo">
      <img :src="displayLogo" alt="logo" />
      <div class="login-logo__text">
        <span class="login-logo__title">天泽</span>
        <span class="login-logo__slogan">多租户中后台管理系统</span>
      </div>
    </div>
    <a-row align="stretch" class="login-box">
      <a-col :xs="24">
        <div class="login-right">
          <div v-if="!isEmailLogin" class="login-right__welcome">欢迎登录</div>
          <h3 v-if="isEmailLogin" class="login-right__title">邮箱登录</h3>
          <EmailLogin v-if="isEmailLogin" />
          <a-tabs v-else v-model:active-key="activeTab" class="login-right__form">
            <a-tab-pane key="1" title="账号登录">
              <component :is="AccountLogin" v-if="activeTab === '1'" />
            </a-tab-pane>
            <a-tab-pane key="2" title="手机号登录">
              <component :is="PhoneLogin" v-if="activeTab === '2'" />
            </a-tab-pane>
          </a-tabs>
        </div>
      </a-col>
    </a-row>
    <div class="login-right__oauth">
      <a-divider orientation="center">其他登录方式</a-divider>
      <div class="list">
        <div v-if="isEmailLogin" class="mode item" @click="toggleLoginMode"><icon-user /> 账号/手机号登录</div>
        <div v-else class="mode item" @click="toggleLoginMode"><icon-email /> 邮箱登录</div>
        <a class="item" title="使用 Gitee 账号登录" @click="onOauth('gitee')">
          <GiSvgIcon name="gitee" :size="24" />
        </a>
        <a class="item" title="使用 GitHub 账号登录" @click="onOauth('github')">
          <GiSvgIcon name="github" :size="24" />
        </a>
        <a class="item" title="使用微信账号登录" @click="onOauth('wechat_open')">
          <GiSvgIcon name="wechat" :size="24" />
        </a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import AccountLogin from './components/account/index.vue'
import PhoneLogin from './components/phone/index.vue'
import EmailLogin from './components/email/index.vue'
import { socialAuth } from '@/apis/auth'
import { useAppStore } from '@/stores'
import { useTenantStore } from '@/stores/modules/tenant'
import { useDevice } from '@/hooks'
import { getTenantIdByDomain, getTenantStatus } from '@/apis'

defineOptions({ name: 'Login' })

const appStore = useAppStore()
const tenantStore = useTenantStore()

const { isDesktop } = useDevice()
const logo = computed(() => appStore.getLogo())
const displayLogo = computed(() => {
  const value = logo.value?.trim()
  if (!value || value === '/logo.svg' || value === '/logo.png') {
    return '/logo.png?v=deer5'
  }
  return value
})

const isEmailLogin = ref(false)
const activeTab = ref('1')

const loginHint = computed(() => {
  if (isEmailLogin.value) {
    return '使用邮箱验证码快速登录'
  }
  return '输入用户名和密码即可登录'
})

const brandCards = [
  {
    title: '统一身份登录',
    desc: '账号、手机号、邮箱与第三方登录',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="3.2"/><path d="M5 19c1.6-3.2 4-4.8 7-4.8s5.4 1.6 7 4.8"/></svg>`,
  },
  {
    title: '细粒度权限',
    desc: '菜单、按钮、数据权限分级可控',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"><rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V8a4 4 0 0 1 8 0v3"/></svg>`,
  },
  {
    title: '操作可追溯',
    desc: '登录轨迹与操作日志完整可查',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"><path d="M5 5h14v14H5z"/><path d="M8 9h8M8 12h6M8 15h4"/></svg>`,
  },
  {
    title: '系统可配置',
    desc: '字典、参数、租户与基础模块开箱可用',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M12 3v2.2M12 18.8V21M4.9 4.9l1.6 1.6M17.5 17.5l1.6 1.6M3 12h2.2M18.8 12H21M4.9 19.1l1.6-1.6M17.5 6.5l1.6-1.6"/></svg>`,
  },
]

const toggleLoginMode = () => {
  isEmailLogin.value = !isEmailLogin.value
}

const onOauth = async (source: string) => {
  const { data } = await socialAuth(source)
  window.location.href = data.authorizeUrl
}

const onGetTenant = async () => {
  const { data } = await getTenantStatus()
  tenantStore.setTenantEnable(data)
  if (!data) {
    tenantStore.resetTenantId()
    return
  }
  const domain = window.location.hostname
  const { data: tenantId } = await getTenantIdByDomain(domain)
  // 未绑定域名时清空残留租户头，交给后端按账号解析租户
  tenantStore.setTenantId(tenantId || undefined)
}
onMounted(() => {
  onGetTenant()
})
</script>

<style scoped lang="scss">
$tz-teal: #0b6e6e;
$tz-jade: #1fa97a;
$tz-ink: #123a38;

@media screen and (max-width: 570px) {
  .pc {
    display: none !important;
  }

  .login {
    position: relative;
    height: 100%;
    display: flex;
    flex-direction: column;
    background: linear-gradient(160deg, #eef8f5 0%, #f7faf8 100%);

    &-logo {
      width: 100%;
      min-height: 88px;
      padding: 20px 24px 12px;
      display: flex;
      align-items: center;
      gap: 12px;
      box-sizing: border-box;

      img {
        width: 42px;
        height: 42px;
        object-fit: contain;
      }

      &__text {
        display: flex;
        flex-direction: column;
        gap: 2px;
      }

      &__title {
        font-weight: 700;
        font-size: 20px;
        color: $tz-ink;
      }

      &__slogan {
        font-size: 12px;
        color: rgba(18, 58, 56, 0.65);
      }
    }

    &-box {
      width: 100%;
    }
  }

  .login-right {
    padding: 20px 24px 0;

    :deep(.arco-btn-primary) {
      background-color: $tz-teal;
      border-color: $tz-teal;
    }

    &__welcome {
      font-size: 22px;
      font-weight: 600;
      margin-bottom: 8px;
      color: $tz-ink;
    }

    &__title {
      font-size: 20px;
      margin-bottom: 16px;
    }

    &__form {
      :deep(.arco-tabs-tab-active),
      :deep(.arco-tabs-tab-title:hover) {
        color: $tz-teal;
      }

      :deep(.arco-tabs-nav-ink) {
        background-color: $tz-jade;
      }

      :deep(.arco-tabs-nav::before),
      :deep(.arco-tabs-tab-title:before) {
        display: none;
      }
    }

    &__oauth {
      position: fixed;
      left: 0;
      bottom: 0;
      width: 100%;
      padding-bottom: 20px;

      .list {
        display: flex;
        justify-content: center;
        align-items: center;

        .item {
          margin-right: 14px;
        }

        .mode {
          display: flex;
          align-items: center;
          height: 32px;
          padding: 0 10px;
          border: 1px solid var(--color-border-3);
          border-radius: 32px;
          font-size: 12px;
          cursor: pointer;
        }
      }
    }
  }

  .theme-btn {
    position: fixed;
    top: 18px;
    right: 18px;
    z-index: 20;
  }

  .footer {
    display: none;
  }
}

@media screen and (min-width: 571px) {
  .h5 {
    display: none !important;
  }

  .login.pc {
    position: relative;
    min-height: 100vh;
    height: 100%;
    overflow: hidden;
  }

  .login-stage {
    position: absolute;
    inset: 0;
    z-index: 1;
    background:
      radial-gradient(ellipse 70% 55% at 16% 18%, rgba(31, 169, 122, 0.18), transparent 58%),
      radial-gradient(ellipse 55% 45% at 78% 82%, rgba(184, 140, 90, 0.12), transparent 55%),
      linear-gradient(145deg, #0a5f5f 0%, #0d7568 36%, #1fa97a 70%, #2bb889 100%);

    &__orb {
      position: absolute;
      border-radius: 50%;
      pointer-events: none;
      animation: orb-drift 9s ease-in-out infinite;

      &--a {
        width: 360px;
        height: 360px;
        left: -90px;
        bottom: -70px;
        background: rgba(255, 255, 255, 0.1);
      }

      &--b {
        width: 240px;
        height: 240px;
        left: 34%;
        top: -50px;
        background: rgba(201, 162, 39, 0.14);
        animation-duration: 11s;
      }

      &--c {
        width: 280px;
        height: 280px;
        right: 26%;
        bottom: 6%;
        background: rgba(11, 110, 110, 0.16);
        animation-duration: 10s;
        animation-delay: -3s;
      }
    }
  }

  .login-logo {
    position: fixed;
    top: 28px;
    left: 40px;
    z-index: 20;
    display: flex;
    align-items: center;
    margin: 0;
    color: #fff;
    font-size: 20px;
    font-weight: 700;
    line-height: 1;
    text-shadow: 0 1px 8px rgba(0, 0, 0, 0.16);

    img {
      width: 36px;
      height: 36px;
      margin-right: 10px;
      object-fit: contain;
      filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.18));
    }
  }

  .login-main {
    position: relative;
    z-index: 5;
    height: 100%;
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: clamp(24px, 4vw, 72px);
    padding: 64px clamp(36px, 5vw, 80px) 96px;
    box-sizing: border-box;
    transform: translateY(-3vh);
  }

  /* 左侧：文案 + 四张玻璃拟态卡片 */
  .login-promo {
    flex: 1 1 auto;
    min-width: 0;
    max-width: 720px;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    gap: 36px;
    color: #fff;

    &__text {
      max-width: 640px;
    }

    &__title {
      margin: 0;
      font-size: clamp(34px, 3.8vw, 48px);
      font-weight: 700;
      line-height: 1.25;
      letter-spacing: 0.16em;
      white-space: nowrap;
    }

    &__subtitle {
      margin: 18px 0 0;
      font-size: 16px;
      line-height: 1.7;
      letter-spacing: 0.04em;
      opacity: 0.9;
    }

    &__cards {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 14px;
      width: min(480px, 88%);
      margin-left: 28px;
    }
  }

  .glass-card {
    position: relative;
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 14px 14px 14px 12px;
    overflow: hidden;
    border-radius: 14px;
    background:
      linear-gradient(155deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.06) 55%, rgba(255, 255, 255, 0.03) 100%);
    border: 1px solid rgba(255, 255, 255, 0.22);
    box-shadow:
      0 10px 24px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.35);
    backdrop-filter: blur(16px) saturate(1.2);
    -webkit-backdrop-filter: blur(16px) saturate(1.2);
    animation: glass-in 0.55s ease both;
    animation-delay: var(--delay, 0ms);
    transition:
      transform 0.28s ease,
      border-color 0.28s ease,
      box-shadow 0.28s ease,
      background 0.28s ease;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 12px;
      bottom: 12px;
      width: 2px;
      border-radius: 2px;
      background: rgba(127, 212, 192, 0.85);
      box-shadow: 0 0 10px rgba(31, 169, 122, 0.35);
    }

    &::after {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: inherit;
      pointer-events: none;
      background: radial-gradient(120px 60px at 12% 0%, rgba(127, 212, 192, 0.18), transparent 70%);
      opacity: 0.7;
    }

    &:hover {
      transform: translateY(-3px);
      border-color: rgba(255, 255, 255, 0.4);
      background:
        linear-gradient(155deg, rgba(255, 255, 255, 0.28) 0%, rgba(255, 255, 255, 0.1) 55%, rgba(255, 255, 255, 0.05) 100%);
      box-shadow:
        0 14px 30px rgba(0, 0, 0, 0.16),
        0 0 0 1px rgba(127, 212, 192, 0.22),
        inset 0 1px 0 rgba(255, 255, 255, 0.4);
    }

    &__icon {
      position: relative;
      z-index: 1;
      flex: 0 0 auto;
      width: 34px;
      height: 34px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 10px;
      color: #fff;
      background: linear-gradient(160deg, rgba(127, 212, 192, 0.35), rgba(255, 255, 255, 0.08));
      border: 1px solid rgba(127, 212, 192, 0.4);
      box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.28);

      :deep(svg) {
        width: 16px;
        height: 16px;
      }
    }

    &__body {
      position: relative;
      z-index: 1;
      min-width: 0;
      padding-top: 1px;
    }

    &__title {
      margin: 0;
      font-size: 14px;
      font-weight: 600;
      letter-spacing: 0.03em;
      line-height: 1.3;
    }

    &__desc {
      margin: 5px 0 0;
      font-size: 12px;
      line-height: 1.45;
      letter-spacing: 0.01em;
      color: rgba(255, 255, 255, 0.78);
    }
  }

  @keyframes glass-in {
    from {
      opacity: 0;
      transform: translateY(10px);
    }

    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .login-panel {
    position: relative;
    flex: 0 0 auto;
    width: min(420px, 38vw);
    min-width: 360px;
    min-height: 540px;
    display: flex;
    flex-direction: column;
    padding: 32px 30px 22px;
    box-sizing: border-box;
    border-radius: 20px;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 252, 250, 0.96) 100%);
    box-shadow:
      0 24px 56px rgba(6, 52, 48, 0.22),
      0 2px 10px rgba(0, 0, 0, 0.04),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(14px);
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      right: 0;
      top: 0;
      height: 3px;
      background: linear-gradient(90deg, $tz-teal 0%, $tz-jade 55%, #c9a227 100%);
    }

    :deep(.arco-input-wrapper),
    :deep(.arco-input-password),
    :deep(.arco-select-view-single) {
      height: 42px;
      border-radius: 10px;
      background: rgba(11, 110, 110, 0.03);
      border-color: rgba(11, 110, 110, 0.12);
      transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
    }

    :deep(.arco-input-wrapper:hover),
    :deep(.arco-input-password:hover) {
      background: rgba(11, 110, 110, 0.045);
      border-color: rgba(31, 169, 122, 0.45);
    }

    :deep(.arco-input-wrapper.arco-input-focus),
    :deep(.arco-input-password.arco-input-focus),
    :deep(.arco-input-wrapper:focus-within) {
      background: #fff;
      border-color: $tz-jade;
      box-shadow: 0 0 0 3px rgba(31, 169, 122, 0.14);
    }

    :deep(.arco-checkbox-checked .arco-checkbox-icon) {
      background-color: $tz-teal;
      border-color: $tz-teal;
    }

    :deep(.arco-link) {
      color: $tz-teal;
    }

    :deep(.arco-link:hover) {
      color: $tz-jade;
    }

    :deep(.arco-btn-primary) {
      height: 42px;
      border-radius: 10px;
      font-weight: 600;
      letter-spacing: 0.04em;
      background: linear-gradient(135deg, $tz-teal 0%, $tz-jade 100%);
      border: none;
      box-shadow: 0 8px 18px rgba(11, 110, 110, 0.28);
    }

    :deep(.arco-btn-primary:hover),
    :deep(.arco-btn-primary:focus) {
      background: linear-gradient(135deg, #0c7a74 0%, #24b985 100%);
      box-shadow: 0 10px 22px rgba(11, 110, 110, 0.34);
    }

    &__header {
      margin-bottom: 8px;
    }

    &__welcome,
    &__title {
      margin: 0;
      color: $tz-ink;
      font-weight: 700;
      font-size: 24px;
      line-height: 1.3;
      letter-spacing: 0.02em;
    }

    &__hint {
      margin: 8px 0 0;
      color: rgba(18, 58, 56, 0.55);
      font-size: 13px;
      line-height: 1.5;
    }

    &__form {
      :deep(.arco-tabs-nav-tab) {
        display: flex;
        justify-content: flex-start;
        gap: 4px;
      }

      :deep(.arco-tabs-tab) {
        margin: 0 18px 0 0;
        color: rgba(18, 58, 56, 0.55);
      }

      :deep(.arco-tabs-tab-title) {
        font-size: 15px;
        font-weight: 500;
      }

      :deep(.arco-tabs-content) {
        margin-top: 14px;
      }

      :deep(.arco-tabs-tab-active),
      :deep(.arco-tabs-tab-title:hover) {
        color: $tz-teal;
      }

      :deep(.arco-tabs-nav-ink) {
        height: 3px;
        border-radius: 3px;
        background: linear-gradient(90deg, $tz-teal, $tz-jade);
      }

      :deep(.arco-tabs-nav::before),
      :deep(.arco-tabs-tab-title:before) {
        display: none;
      }

      :deep(.arco-form-item) {
        margin-bottom: 18px;
      }
    }

    &__oauth {
      margin-top: auto;
      padding-top: 4px;

      :deep(.arco-divider-text) {
        color: rgba(18, 58, 56, 0.4);
        font-size: 12px;
        padding: 0 12px;
      }

      :deep(.arco-divider-horizontal) {
        margin: 12px 0 16px;
        border-bottom-color: rgba(11, 110, 110, 0.1);
      }

      .list {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 12px;
      }

      .item {
        margin: 0;
      }

      .mode {
        appearance: none;
        color: rgba(18, 58, 56, 0.72);
        font-size: 12px;
        padding: 0 12px;
        border: 1px solid rgba(11, 110, 110, 0.16);
        border-radius: 999px;
        display: inline-flex;
        align-items: center;
        height: 34px;
        cursor: pointer;
        background: rgba(11, 110, 110, 0.03);
        transition: all 0.2s ease;
        font-family: inherit;
        line-height: 1;
      }

      .mode svg {
        font-size: 15px;
        margin-right: 6px;
      }

      .mode:hover {
        background: rgba(11, 110, 110, 0.08);
        border-color: rgba(31, 169, 122, 0.45);
        color: $tz-teal;
      }

      .social {
        width: 34px;
        height: 34px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        border: 1px solid rgba(11, 110, 110, 0.14);
        background: rgba(11, 110, 110, 0.03);
        color: rgba(18, 58, 56, 0.75);
        transition: all 0.2s ease;
        cursor: pointer;
      }

      .social:hover {
        transform: translateY(-2px);
        border-color: rgba(31, 169, 122, 0.45);
        background: rgba(31, 169, 122, 0.1);
        color: $tz-teal;
        box-shadow: 0 6px 14px rgba(11, 110, 110, 0.12);
      }
    }
  }

  :global(body[arco-theme='dark']) .login-panel {
    background: linear-gradient(180deg, rgba(30, 34, 34, 0.96) 0%, rgba(24, 28, 28, 0.94) 100%);
    border-color: rgba(255, 255, 255, 0.08);

    .login-panel__welcome,
    .login-panel__title {
      color: var(--color-text-1);
    }

    .login-panel__hint {
      color: var(--color-text-3);
    }
  }

  .theme-btn {
    position: fixed;
    top: 24px;
    right: 28px;
    z-index: 30;
  }

  .footer {
    position: absolute;
    left: 50%;
    bottom: 16px;
    transform: translateX(-50%);
    z-index: 20;
    color: rgba(255, 255, 255, 0.78);
    text-shadow: 0 1px 6px rgba(0, 0, 0, 0.2);

    .beian .text {
      font-size: 12px;
      line-height: 20px;
      text-align: center;
    }

    .beian .below {
      display: flex;
      align-items: center;
    }
  }
}

@media screen and (min-width: 571px) and (max-width: 980px) {
  .login-main {
    padding: 56px 28px 72px;
    gap: 20px;
    transform: translateY(-2vh);
  }

  .login-promo {
    &__title {
      font-size: 28px;
      letter-spacing: 0.1em;
    }

    &__cards {
      width: min(400px, 90%);
      margin-left: 16px;
      gap: 12px;
    }
  }

  .glass-card {
    padding: 12px;
    gap: 10px;

    &__icon {
      width: 30px;
      height: 30px;
    }

    &__title {
      font-size: 13px;
    }

    &__desc {
      font-size: 11px;
    }
  }

  .login-panel {
    width: min(380px, 44vw);
    min-width: 320px;
    padding: 28px 22px 16px;
  }
}

@keyframes orb-drift {
  0%,
  100% {
    transform: translate3d(0, 0, 0);
  }

  50% {
    transform: translate3d(10px, -14px, 0);
  }
}
</style>
