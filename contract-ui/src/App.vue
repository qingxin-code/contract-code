<script setup lang="ts">
import { computed } from 'vue'
import { NConfigProvider, NDialogProvider, NMessageProvider } from 'naive-ui'
import type { GlobalThemeOverrides } from 'naive-ui'
import { storeToRefs } from 'pinia'
import { useAppStore } from './store/app'
import { buildThemePalette } from './utils/theme'

const appStore = useAppStore()
const { theme } = storeToRefs(appStore)

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  const palette = buildThemePalette(theme.value)

  return {
    common: {
      primaryColor: palette[500],
      primaryColorHover: palette[400],
      primaryColorPressed: palette[600],
      primaryColorSuppl: palette[500],
      warningColor: palette[500],
      warningColorHover: palette[400],
      warningColorPressed: palette[600],
      borderRadius: '12px'
    },
    Button: {
      borderRadiusMedium: '12px'
    }
  }
})
</script>

<template>
  <n-config-provider :theme-overrides="themeOverrides">
    <n-message-provider>
      <n-dialog-provider>
        <router-view></router-view>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<style scoped>
</style>
