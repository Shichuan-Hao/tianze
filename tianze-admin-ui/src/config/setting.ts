/** 天泽品牌主色（与登录页青绿一致） */
export const BRAND_THEME_COLOR = '#0B6E6E'
/** 历史默认蓝，仅用于本地持久化迁移 */
export const LEGACY_THEME_COLOR = '#165DFF'

export const defaultSettings: App.AppSettings = {
  theme: 'light',
  themeColor: BRAND_THEME_COLOR,
  tab: true,
  tabMode: 'card-gutter',
  animate: false,
  animateMode: 'zoom-fade',
  menuCollapse: false,
  menuAccordion: true,
  menuDark: false,
  copyrightDisplay: true,
  layout: 'mix',
  enableColorWeaknessMode: false,
  enableMourningMode: false,
}
// 根据环境返回配置
export const getSettings = (): App.AppSettings => {
  return defaultSettings
}
