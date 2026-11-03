import { defineStore } from 'pinia'
import { computed, reactive, toRefs } from 'vue'
import { generate, getRgbStr } from '@arco-design/color'
import { type BasicConfig, listSiteOptionDict } from '@/apis/system'
import { BRAND_THEME_COLOR, getSettings, LEGACY_THEME_COLOR } from '@/config/setting'

const storeSetup = () => {
  // App配置
  const settingConfig = reactive({ ...getSettings() }) as App.AppSettings
  // 页面切换动画类名
  const transitionName = computed(() => (settingConfig.animate ? settingConfig.animateMode : ''))

  // 深色菜单主题色变量
  const themeCSSVar = computed<Record<string, string>>(() => {
    const obj: Record<string, string> = {}
    const list = generate(settingConfig.themeColor, { list: true, dark: true }) as string[]
    list.forEach((color, index) => {
      obj[`--primary-${index + 1}`] = getRgbStr(color)
    })
    return obj
  })

  // 设置主题色
  const setThemeColor = (color: string) => {
    if (!color) return
    settingConfig.themeColor = color
    // gi-demo 主题里 @primary-* 映射到 --arcoblue-*，两边都要写，否则链接/图标仍是蓝
    const list = generate(settingConfig.themeColor, { list: true, dark: settingConfig.theme === 'dark' }) as string[]
    list.forEach((item, index) => {
      const rgbStr = getRgbStr(item)
      const n = index + 1
      document.body.style.setProperty(`--primary-${n}`, rgbStr)
      document.body.style.setProperty(`--arcoblue-${n}`, rgbStr)
    })
  }

  // 切换主题 暗黑模式|简白模式
  const toggleTheme = (dark: boolean) => {
    if (dark) {
      settingConfig.theme = 'dark'
      document.body.setAttribute('arco-theme', 'dark')
    } else {
      settingConfig.theme = 'light'
      document.body.removeAttribute('arco-theme')
    }
    setThemeColor(settingConfig.themeColor)
  }

  // 初始化主题
  const initTheme = () => {
    // 本地仍是旧默认蓝时，迁移为天泽品牌色（用户自定义色不受影响）
    if (!settingConfig.themeColor || settingConfig.themeColor.toUpperCase() === LEGACY_THEME_COLOR) {
      settingConfig.themeColor = BRAND_THEME_COLOR
    }
    setThemeColor(settingConfig.themeColor)
  }

  // 设置左侧菜单折叠状态
  const setMenuCollapse = (collapsed: boolean) => {
    settingConfig.menuCollapse = collapsed
  }

  // 系统配置配置
  const siteConfig = reactive({}) as BasicConfig
  // 初始化系统配置
  const initSiteConfig = () => {
    listSiteOptionDict().then((res) => {
      const resMap = new Map()
      res.data.forEach((item) => {
        resMap.set(item.label, item.value)
      })
      siteConfig.SITE_FAVICON = resMap.get('SITE_FAVICON')
      siteConfig.SITE_LOGO = resMap.get('SITE_LOGO')
      siteConfig.SITE_TITLE = resMap.get('SITE_TITLE')
      siteConfig.SITE_COPYRIGHT = resMap.get('SITE_COPYRIGHT')
      siteConfig.SITE_BEIAN = resMap.get('SITE_BEIAN')
      document.title = resMap.get('SITE_TITLE') || '天泽'
      const favicon = resMap.get('SITE_FAVICON') || '/favicon.ico?v=deer6'
      document.querySelectorAll('link[rel="shortcut icon"], link[rel="icon"]').forEach((el) => {
        el.setAttribute('href', favicon)
      })
    })
  }

  // 设置系统配置
  const setSiteConfig = (config: BasicConfig) => {
    Object.assign(siteConfig, config)
    document.title = config.SITE_TITLE || '天泽'
    const favicon = config.SITE_FAVICON || '/favicon.ico?v=deer6'
    document.querySelectorAll('link[rel="shortcut icon"], link[rel="icon"]').forEach((el) => {
      el.setAttribute('href', favicon)
    })
  }
  // 监听 色弱模式 和 哀悼模式
  watch([
    () => settingConfig.enableMourningMode,
    () => settingConfig.enableColorWeaknessMode,
  ], ([mourningMode, colorWeaknessMode]) => {
    const filters = [] as string[]
    if (mourningMode) {
      filters.push('grayscale(100%)')
    }
    if (colorWeaknessMode) {
      filters.push('invert(80%)')
    }
    // 如果没有任何滤镜条件，移除 `filter` 样式
    if (filters.length === 0) {
      document.documentElement.style.removeProperty('filter')
    } else {
      document.documentElement.style.setProperty('filter', filters.join(' '))
    }
  }, {
    immediate: true,
  })

  const getFavicon = () => {
    return siteConfig.SITE_FAVICON
  }

  const getLogo = () => {
    return siteConfig.SITE_LOGO
  }

  const getTitle = () => {
    return siteConfig.SITE_TITLE
  }

  const getCopyright = () => {
    return siteConfig.SITE_COPYRIGHT
  }

  const getForRecord = () => {
    return siteConfig.SITE_BEIAN
  }
  return {
    ...toRefs(settingConfig),
    ...toRefs(siteConfig),
    transitionName,
    themeCSSVar,
    toggleTheme,
    setThemeColor,
    initTheme,
    setMenuCollapse,
    initSiteConfig,
    setSiteConfig,
    getFavicon,
    getLogo,
    getTitle,
    getCopyright,
    getForRecord,
  }
}

export const useAppStore = defineStore('app', storeSetup, { persist: true })
