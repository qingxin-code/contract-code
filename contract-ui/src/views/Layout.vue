<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store/user'
import { useAppStore } from '../store/app'
import { NIcon, useDialog, useMessage } from 'naive-ui'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()
const dialog = useDialog()
const message = useMessage()

const globalKeyword = ref('')
const globalSearching = ref(false)

onMounted(() => {
  appStore.fetchConfigs()
})

const activeMenu = computed(() => {
  const path = route.path
  if (path.includes('contract/create')) return 'contract-create'
  if (path.includes('contract')) return 'contract'
  if (path.includes('merchant')) return 'merchant'
  if (path.includes('settings')) return 'settings'
  return 'dashboard'
})

const goPage = (name: string) => {
  router.push({ name })
}

const handleGlobalSearch = async () => {
  const keyword = globalKeyword.value.trim()
  if (!keyword || globalSearching.value) {
    return
  }

  globalSearching.value = true
  try {
    const [contractRes, merchantRes] = await Promise.all([
      request.get('/contracts', {
        params: {
          page: 1,
          size: 10,
          searchKey: keyword
        }
      }),
      request.get('/merchant/list', {
        params: {
          current: 1,
          size: 10,
          name: keyword
        }
      })
    ])

    const contractTotal = Number(contractRes.data?.records?.length || 0)
    const merchantTotal = Number(merchantRes.data?.records?.length || 0)

    if (contractTotal > 0) {
      router.push({ name: 'contract', query: { keyword } })
      return
    }

    if (merchantTotal > 0) {
      router.push({ name: 'merchant', query: { keyword } })
      return
    }

    message.warning('未找到匹配的合同或客商')
  } catch (error) {
    //
  } finally {
    globalSearching.value = false
  }
}

const handleLogout = () => {
  dialog.warning({
    title: '确认退出',
    content: '您确定要退出当前的合同管理系统吗？',
    positiveText: '确认退出',
    negativeText: '取消',
    onPositiveClick: () => {
      userStore.clearStore()
      router.push('/login')
    }
  })
}
</script>

<template>
  <div class="bg-primary-50 text-gray-800 font-sans antialiased overflow-hidden flex h-screen w-screen">
    <aside class="w-64 bg-white shadow-pink-soft z-20 flex flex-col transition-all duration-300">
      <div class="h-20 flex items-center justify-center border-b border-primary-100">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-primary-600 to-primary-400 flex items-center justify-center text-white shadow-pink-glow">
            <n-icon size="20"><i class="fa-solid fa-file-signature"></i></n-icon>
          </div>
          <h1 class="text-xl font-bold text-gradient tracking-wide">{{ appStore.systemName }}</h1>
        </div>
      </div>

      <nav class="flex-1 px-4 py-6 space-y-2 overflow-y-auto">
        <p class="px-4 text-xs font-semibold text-primary-300 uppercase tracking-wider mb-2">主菜单</p>

        <a href="#" @click.prevent="goPage('dashboard')"
           :class="[
             'flex items-center gap-3 px-4 py-3 rounded-xl transition-all font-medium border-l-4',
             activeMenu === 'dashboard' ? 'text-primary-600 bg-primary-50 border-primary-500' : 'text-gray-500 hover:text-primary-600 hover:bg-primary-50 border-transparent hover:border-primary-300'
           ]">
          <n-icon size="20" class="w-5 text-center"><i class="fa-solid fa-chart-pie"></i></n-icon>
          仪表盘
        </a>

        <a href="#" @click.prevent="goPage('contract')"
           :class="[
             'flex items-center gap-3 px-4 py-3 rounded-xl transition-all font-medium border-l-4',
             activeMenu === 'contract' ? 'text-primary-600 bg-primary-50 border-primary-500' : 'text-gray-500 hover:text-primary-600 hover:bg-primary-50 border-transparent hover:border-primary-300'
           ]">
          <n-icon size="20" class="w-5 text-center"><i class="fa-solid fa-file-contract"></i></n-icon>
          合同管理
        </a>

        <a href="#" @click.prevent="goPage('contract-create')"
           :class="[
             'flex items-center gap-3 px-4 py-3 rounded-xl transition-all font-medium border-l-4',
             activeMenu === 'contract-create' ? 'text-primary-600 bg-primary-50 border-primary-500' : 'text-gray-500 hover:text-primary-600 hover:bg-primary-50 border-transparent hover:border-primary-300'
           ]">
          <n-icon size="20" class="w-5 text-center"><i class="fa-solid fa-plus-circle"></i></n-icon>
          新建合同
        </a>

        <p class="px-4 text-xs font-semibold text-primary-300 uppercase tracking-wider mb-2 mt-6">其他</p>

        <a href="#" @click.prevent="goPage('merchant')"
           :class="[
             'flex items-center gap-3 px-4 py-3 rounded-xl transition-all font-medium border-l-4',
             activeMenu === 'merchant' ? 'text-primary-600 bg-primary-50 border-primary-500' : 'text-gray-500 hover:text-primary-600 hover:bg-primary-50 border-transparent hover:border-primary-300'
           ]">
          <n-icon size="20" class="w-5 text-center"><i class="fa-solid fa-users"></i></n-icon>
          客商管理
        </a>

        <a href="#" @click.prevent="goPage('settings')"
           :class="[
             'flex items-center gap-3 px-4 py-3 rounded-xl transition-all font-medium border-l-4',
             activeMenu === 'settings' ? 'text-primary-600 bg-primary-50 border-primary-500' : 'text-gray-500 hover:text-primary-600 hover:bg-primary-50 border-transparent hover:border-primary-300'
           ]">
          <n-icon size="20" class="w-5 text-center"><i class="fa-solid fa-gear"></i></n-icon>
          系统设置
        </a>
      </nav>

      <div class="p-4 border-t border-primary-100">
        <div class="flex items-center gap-3 p-2 rounded-xl hover:bg-primary-50 transition-colors cursor-pointer" @click="handleLogout" title="退出登录">
          <img :src="`https://ui-avatars.com/api/?name=${userStore.nickname || 'Admin'}&background=fbcfe8&color=be185d`" alt="User" class="w-10 h-10 rounded-full border-2 border-primary-200">
          <div class="flex-1 min-w-0">
            <p class="text-sm font-bold text-gray-800 truncate">{{ userStore.nickname || '管理员' }}</p>
            <p class="text-xs text-primary-500 truncate">退出系统</p>
          </div>
          <n-icon class="text-gray-400 hover:text-primary-500"><i class="fa-solid fa-right-from-bracket"></i></n-icon>
        </div>
      </div>
    </aside>

    <main class="flex-1 flex flex-col h-screen overflow-hidden">
      <header class="h-20 bg-white/80 backdrop-blur-md shadow-sm flex items-center justify-between px-8 z-10 sticky top-0 shrink-0">
        <div class="flex items-center bg-primary-50 rounded-full px-4 py-2 w-96 border border-primary-100 focus-within:border-primary-400 focus-within:ring-2 focus-within:ring-primary-100 transition-all">
          <n-icon class="text-primary-400 cursor-pointer" @click="handleGlobalSearch"><i class="fa-solid fa-search"></i></n-icon>
          <input
            v-model="globalKeyword"
            type="text"
            placeholder="全局搜索合同、客商名称..."
            class="bg-transparent border-none outline-none w-full ml-3 text-sm text-gray-700 placeholder-primary-300"
            @keyup.enter="handleGlobalSearch"
          >
        </div>

        <div class="flex items-center gap-6">
          <button class="relative text-gray-400 hover:text-primary-500 transition-colors">
            <n-icon size="24"><i class="fa-regular fa-bell"></i></n-icon>
            <span class="absolute -top-1 -right-1 w-2.5 h-2.5 bg-red-500 rounded-full border-2 border-white"></span>
          </button>
          <button @click="goPage('contract-create')" class="w-10 h-10 rounded-full bg-gradient-to-r from-primary-500 to-primary-700 text-white shadow-pink-soft hover:shadow-pink-glow transition-all transform hover:-translate-y-0.5 flex items-center justify-center">
            <n-icon><i class="fa-solid fa-plus"></i></n-icon>
          </button>
        </div>
      </header>

      <div class="flex-1 overflow-y-auto p-8 relative">
        <router-view></router-view>
      </div>
    </main>
  </div>
</template>

<style scoped>
</style>
