<template>
  <a-form
    ref="formRef"
    :model="form"
    :rules="rules"
    :label-col-style="{ display: 'none' }"
    :wrapper-col-style="{ flex: 1 }"
    size="large"
    @submit="handleLogin"
  >
    <a-form-item field="username" hide-label>
      <a-input v-model="form.username" placeholder="请输入用户名" allow-clear />
    </a-form-item>
    <a-form-item field="password" hide-label>
      <a-input-password v-model="form.password" placeholder="请输入密码" />
    </a-form-item>
    <a-form-item v-if="isCaptchaEnabled" field="captcha" hide-label>
      <a-input v-model="form.captcha" placeholder="请输入验证码" :max-length="4" allow-clear style="flex: 1 1" />
      <div class="captcha-container" @click="getCaptcha">
        <img :src="captchaImgBase64" alt="验证码" class="captcha" />
        <div v-if="form.expired" class="overlay">
          <p>已过期，请刷新</p>
        </div>
      </div>
    </a-form-item>
    <a-form-item>
      <a-row justify="space-between" align="center" class="w-full">
        <a-checkbox v-model="loginConfig.rememberMe">记住我</a-checkbox>
        <a-link>忘记密码</a-link>
      </a-row>
    </a-form-item>
    <a-form-item>
      <a-space direction="vertical" fill class="w-full">
        <a-button class="btn" type="primary" :loading="loading" html-type="submit" size="large" long>立即登录</a-button>
      </a-space>
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import { type FormInstance, Message } from '@arco-design/web-vue'
import { useStorage } from '@vueuse/core'
import { getImageCaptcha } from '@/apis/common'
import { useTabsStore, useUserStore } from '@/stores'
import { encryptByRsa } from '@/utils/encrypt'

const loginConfig = useStorage('login-config', {
  rememberMe: true,
  username: 'admin', // 演示默认值
  password: 'admin123', // 演示默认值
  // username: debug ? 'admin' : '', // 演示默认值
  // password: debug ? 'admin123' : '', // 演示默认值
})
// 是否启用验证码
const isCaptchaEnabled = ref(true)
// 验证码图片
const captchaImgBase64 = ref()
const formRef = ref<FormInstance>()
const form = reactive({
  username: loginConfig.value.username,
  password: loginConfig.value.password,
  captcha: '',
  uuid: '',
  expired: false,
})
// 校验规则部分
const rules: FormInstance['rules'] = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
  captcha: [{ required: isCaptchaEnabled.value, message: '请输入验证码' }],
}

// 验证码过期定时器
let timer
const startTimer = (expireTime: number, curTime = Date.now()) => {
  if (timer) {
    clearTimeout(timer)
  }
  const remainingTime = expireTime - curTime
  if (remainingTime <= 0) {
    form.expired = true
    return
  }
  timer = setTimeout(() => {
    form.expired = true
  }, remainingTime)
}
// 组件销毁时清理定时器
onBeforeUnmount(() => {
  if (timer) {
    clearTimeout(timer)
  }
})

// 获取验证码
const getCaptcha = () => {
  getImageCaptcha().then((res) => {
    const { uuid, img, expireTime, isEnabled } = res.data
    isCaptchaEnabled.value = isEnabled
    captchaImgBase64.value = img
    form.uuid = uuid
    form.expired = false
    startTimer(expireTime, Number(res.timestamp))
  })
}

const userStore = useUserStore()
const tabsStore = useTabsStore()
const router = useRouter()
const loading = ref(false)
// 登录
const handleLogin = async () => {
  try {
    const isInvalid = await formRef.value?.validate()
    if (isInvalid) return
    loading.value = true

    await userStore.accountLogin({
      username: form.username,
      password: encryptByRsa(form.password) || '',
      captcha: form.captcha,
      uuid: form.uuid,
    })
    tabsStore.reset()
    const { redirect, ...othersQuery } = router.currentRoute.value.query
    const { rememberMe } = loginConfig.value
    loginConfig.value.username = rememberMe ? form.username : ''

    // 如果有重定向参数，解码并直接跳转到完整路径
    if (redirect) {
      const redirectPath = decodeURIComponent(redirect as string)
      await router.push(redirectPath)
    } else {
      await router.push({
        path: '/',
        query: {
          ...othersQuery,
        },
      })
    }
    Message.success('欢迎使用')
  } catch (error) {
    console.error(error)
    getCaptcha()
    form.captcha = ''
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getCaptcha()
})
</script>

<style scoped lang="scss">
.arco-input-wrapper,
:deep(.arco-select-view-single) {
  height: 42px;
  border-radius: 10px;
  font-size: 13px;
}

.arco-input-wrapper.arco-input-error {
  background-color: rgb(var(--danger-1));
  border-color: rgb(var(--danger-3));
}

.arco-input-wrapper.arco-input-error:hover {
  background-color: rgb(var(--danger-1));
  border-color: rgb(var(--danger-6));
}

.arco-input-wrapper :deep(.arco-input) {
  font-size: 13px;
  color: var(--color-text-1);
}

.arco-input-wrapper:hover {
  border-color: #1fa97a;
}

.captcha {
  width: 108px;
  height: 40px;
  border-radius: 8px;
  object-fit: cover;
}

.btn {
  height: 42px;
  border-radius: 10px;
}

.captcha-container {
  position: relative;
  display: flex;
  margin-left: 10px;
  overflow: hidden;
  border-radius: 8px;
  border: 1px solid rgba(11, 110, 110, 0.12);
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.captcha-container:hover {
  border-color: rgba(31, 169, 122, 0.45);
  box-shadow: 0 0 0 3px rgba(31, 169, 122, 0.1);
}

.overlay {
  position: absolute;
  inset: 0;
  background-color: rgba(18, 58, 56, 0.72);
  display: flex;
  justify-content: center;
  align-items: center;
}

.overlay p {
  margin: 0;
  font-size: 12px;
  color: white;
}
</style>
