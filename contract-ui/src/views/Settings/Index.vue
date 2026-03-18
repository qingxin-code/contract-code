<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useMessage } from 'naive-ui'
import request from '../../utils/request'
import { useAppStore } from '../../store/app'
import { DEFAULT_THEME_COLOR, THEME_PRESETS, sanitizeThemeColor } from '../../utils/theme'

const message = useMessage()
const appStore = useAppStore()
const loading = ref(false)
const configs = ref<any[]>([])
const customThemeColor = ref(DEFAULT_THEME_COLOR)

const nameConfig = computed(() => configs.value.find((item) => item.configKey === 'sys.name'))
const themeConfig = computed(() => configs.value.find((item) => item.configKey === 'sys.theme'))

const fetchConfigs = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/config/all')
    configs.value = res.data || []

    if (!nameConfig.value) {
      configs.value.push({
        id: null,
        configKey: 'sys.name',
        configValue: appStore.systemName,
        description: '系统运行名称'
      })
    }

    if (!themeConfig.value) {
      configs.value.push({
        id: null,
        configKey: 'sys.theme',
        configValue: appStore.theme,
        description: '系统主色调'
      })
    }

    customThemeColor.value = sanitizeThemeColor(themeConfig.value?.configValue || appStore.theme)
  } finally {
    loading.value = false
  }
}

const selectPresetTheme = (color: string) => {
  if (themeConfig.value) {
    themeConfig.value.configValue = color
  }
  customThemeColor.value = color
}

const applyCustomTheme = () => {
  const color = sanitizeThemeColor(customThemeColor.value)
  customThemeColor.value = color
  if (themeConfig.value) {
    themeConfig.value.configValue = color
  }
}

const handleSave = async () => {
  loading.value = true
  try {
    applyCustomTheme()
    await request.put('/config/update', configs.value)

    appStore.setConfigs(
      nameConfig.value?.configValue || appStore.systemName,
      themeConfig.value?.configValue || DEFAULT_THEME_COLOR
    )

    message.success('保存设置成功')
    await appStore.fetchConfigs()
  } finally {
    loading.value = false
  }
}

onMounted(fetchConfigs)
</script>

<template>
  <div class="max-w-4xl">
    <h2 class="text-2xl font-bold text-gray-800 mb-6">系统设置</h2>

    <n-card class="rounded-3xl border-none shadow-pink-soft p-4">
      <template #header>
        <div class="flex items-center gap-2">
          <i class="fas fa-sliders-h text-primary-500"></i>
          <span>全局参数配置</span>
        </div>
      </template>

      <n-spin :show="loading">
        <div class="space-y-8 mt-4">
          <div class="grid grid-cols-[140px_1fr] gap-6 items-center">
            <label class="text-base text-gray-700">系统运行名称</label>
            <n-input
              v-if="nameConfig"
              v-model:value="nameConfig.configValue"
              placeholder="请输入系统运行名称"
              class="rounded-xl"
            />
          </div>

          <div class="grid grid-cols-[140px_1fr] gap-6 items-start">
            <label class="text-base text-gray-700 pt-2">系统主色调</label>
            <div class="space-y-5">
              <div>
                <div class="text-sm text-gray-500 mb-3">预设颜色</div>
                <div class="flex flex-wrap gap-3">
                  <button
                    v-for="preset in THEME_PRESETS"
                    :key="preset.value"
                    type="button"
                    @click="selectPresetTheme(preset.value)"
                    class="flex items-center gap-2 px-3 py-2 rounded-xl border transition-all"
                    :class="themeConfig?.configValue === preset.value ? 'border-primary-500 bg-primary-50 text-primary-700' : 'border-gray-200 bg-white text-gray-600 hover:border-primary-300'"
                  >
                    <span class="w-5 h-5 rounded-full border border-white shadow-sm" :style="{ backgroundColor: preset.value }"></span>
                    <span class="text-sm font-medium">{{ preset.name }}</span>
                  </button>
                </div>
              </div>

              <div>
                <div class="text-sm text-gray-500 mb-3">自定义颜色</div>
                <div class="flex items-center gap-4 rounded-2xl border border-gray-200 bg-white px-4 py-3">
                  <input v-model="customThemeColor" type="color" class="w-14 h-10 p-0 border-none bg-transparent cursor-pointer">
                  <n-input
                    v-model:value="customThemeColor"
                    placeholder="#ec4899"
                    class="max-w-xs rounded-xl"
                    @blur="applyCustomTheme"
                  />
                  <button
                    type="button"
                    @click="applyCustomTheme"
                    class="px-4 py-2 rounded-xl border border-primary-200 text-primary-600 hover:bg-primary-50 transition-colors"
                  >
                    应用颜色
                  </button>
                </div>
              </div>

              <div class="rounded-2xl border border-primary-100 bg-primary-50/50 p-4">
                <div class="text-sm text-gray-500 mb-3">当前预览</div>
                <div class="flex flex-wrap gap-3">
                  <div class="h-10 w-28 rounded-xl shadow-pink-soft flex items-center justify-center text-sm font-medium text-white" :style="{ backgroundColor: themeConfig?.configValue || DEFAULT_THEME_COLOR }">
                    主按钮
                  </div>
                  <div class="h-10 w-28 rounded-xl border flex items-center justify-center text-sm font-medium" :style="{ borderColor: themeConfig?.configValue || DEFAULT_THEME_COLOR, color: themeConfig?.configValue || DEFAULT_THEME_COLOR }">
                    边框色
                  </div>
                  <div class="h-10 w-28 rounded-xl flex items-center justify-center text-sm font-medium" :style="{ backgroundColor: `${themeConfig?.configValue || DEFAULT_THEME_COLOR}22`, color: themeConfig?.configValue || DEFAULT_THEME_COLOR }">
                    浅色块
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="flex justify-end">
            <n-button type="primary" class="rounded-xl px-8" @click="handleSave">
              保存修改
            </n-button>
          </div>
        </div>
      </n-spin>
    </n-card>

    <n-card class="rounded-3xl border-none shadow-pink-soft p-4 mt-8 opacity-60">
      <template #header>
        <div class="flex items-center gap-2">
          <i class="fas fa-shield-alt text-primary-500"></i>
          <span>高级权限设置</span>
        </div>
      </template>
      <div class="p-4 text-center text-gray-400 text-sm">
        当前版本暂不支持修改高级安全策略
      </div>
    </n-card>
  </div>
</template>
