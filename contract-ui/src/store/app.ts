import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'
import { applyThemeColor, DEFAULT_THEME_COLOR, sanitizeThemeColor } from '../utils/theme'

export const useAppStore = defineStore('app', () => {
  const systemName = ref(localStorage.getItem('systemName') || 'CMS - 合同管理系统')
  const theme = ref(sanitizeThemeColor(localStorage.getItem('theme') || DEFAULT_THEME_COLOR))

  if (typeof window !== 'undefined') {
    applyThemeColor(theme.value)
  }

  async function fetchConfigs() {
    try {
      const res: any = await request.get('/config/all')
      const configs = res.data || []
      const nameCfg = configs.find((c: any) => c.configKey === 'sys.name')
      const themeCfg = configs.find((c: any) => c.configKey === 'sys.theme')

      if (nameCfg) {
        systemName.value = nameCfg.configValue
        localStorage.setItem('systemName', nameCfg.configValue)
      }
      if (themeCfg) {
        theme.value = sanitizeThemeColor(themeCfg.configValue)
        localStorage.setItem('theme', theme.value)
        applyThemeColor(theme.value)
      }
    } catch (error) {
      console.error('Failed to fetch system configs:', error)
    }
  }

  function setConfigs(name: string, newTheme: string) {
    systemName.value = name
    theme.value = sanitizeThemeColor(newTheme)
    localStorage.setItem('systemName', name)
    localStorage.setItem('theme', theme.value)
    applyThemeColor(theme.value)
  }

  return { systemName, theme, fetchConfigs, setConfigs }
})
