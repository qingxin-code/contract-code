<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import request from '../utils/request'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const summary = ref({
  contractTotal: 0,
  pendingCount: 0,
  monthlyAmount: 0,
  expiringCount: 0,
  merchantTotal: 0
})

const monthlyAmountText = computed(() => {
  const amount = Number(summary.value.monthlyAmount || 0)
  return `¥ ${amount.toLocaleString()}`
})

const goCreate = () => {
  router.push({ name: 'contract-create' })
}

const fetchSummary = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/dashboard/summary')
    summary.value = {
      contractTotal: Number(res.data.contractTotal || 0),
      pendingCount: Number(res.data.pendingCount || 0),
      monthlyAmount: Number(res.data.monthlyAmount || 0),
      expiringCount: Number(res.data.expiringCount || 0),
      merchantTotal: Number(res.data.merchantTotal || 0)
    }
  } catch (error) {
    //
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchSummary()
})
</script>

<template>
  <div class="space-y-8 animate-[fadeIn_0.5s_ease-out]">
    <div class="flex justify-between items-end">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">欢迎回来，{{ userStore.nickname || '管理员' }}</h2>
        <p class="text-sm text-gray-500 mt-1">这是您今日的合同数据概览。</p>
      </div>
      <button @click="goCreate" class="px-6 py-2.5 bg-gradient-to-r from-primary-500 to-primary-600 hover:from-primary-600 hover:to-primary-700 text-white font-medium rounded-xl shadow-pink-soft transition-all">
        <i class="fa-solid fa-pen-nib mr-2"></i> 起草新合同
      </button>
    </div>

    <n-spin :show="loading">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-primary-50 relative overflow-hidden group hover:shadow-pink-soft transition-shadow">
          <div class="absolute -right-6 -top-6 w-24 h-24 bg-primary-50 rounded-full opacity-50 group-hover:scale-110 transition-transform"></div>
          <div class="flex justify-between items-start relative z-10">
            <div>
              <p class="text-sm font-medium text-gray-500 mb-1">合同总数</p>
              <h3 class="text-3xl font-bold text-gray-800">{{ summary.contractTotal.toLocaleString() }}</h3>
            </div>
            <div class="w-12 h-12 rounded-xl bg-primary-100 text-primary-600 flex items-center justify-center text-xl">
              <i class="fa-solid fa-layer-group"></i>
            </div>
          </div>
          <div class="mt-4 flex items-center text-sm">
            <span class="text-gray-400">当前账号下的合同数量</span>
          </div>
        </div>

        <div class="bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl p-6 shadow-pink-soft text-white relative overflow-hidden transform hover:-translate-y-1 transition-transform">
          <div class="absolute right-0 bottom-0 opacity-10 text-7xl"><i class="fa-solid fa-file-signature"></i></div>
          <div class="flex justify-between items-start relative z-10">
            <div>
              <p class="text-sm font-medium text-primary-100 mb-1">待我处理</p>
              <h3 class="text-3xl font-bold text-white">{{ summary.pendingCount.toLocaleString() }}</h3>
            </div>
            <div class="w-12 h-12 rounded-xl bg-white/20 backdrop-blur-sm flex items-center justify-center text-xl">
              <i class="fa-solid fa-clock-rotate-left"></i>
            </div>
          </div>
          <div class="mt-4 flex items-center text-sm">
            <span class="bg-white/20 px-2 py-0.5 rounded-md">待签署合同</span>
          </div>
        </div>

        <div class="bg-white rounded-2xl p-6 shadow-sm border border-primary-50 relative overflow-hidden group hover:shadow-pink-soft transition-shadow">
          <div class="flex justify-between items-start relative z-10">
            <div>
              <p class="text-sm font-medium text-gray-500 mb-1">本月签署金额 (元)</p>
              <h3 class="text-3xl font-bold text-gray-800">{{ monthlyAmountText }}</h3>
            </div>
            <div class="w-12 h-12 rounded-xl bg-green-50 text-green-500 flex items-center justify-center text-xl">
              <i class="fa-solid fa-sack-dollar"></i>
            </div>
          </div>
          <div class="mt-4 flex items-center text-sm">
            <span class="text-gray-400">按签署日期统计当月金额</span>
          </div>
        </div>

        <div class="bg-white rounded-2xl p-6 shadow-sm border border-primary-50 relative overflow-hidden group hover:shadow-pink-soft transition-shadow">
          <div class="flex justify-between items-start relative z-10">
            <div>
              <p class="text-sm font-medium text-gray-500 mb-1">即将过期</p>
              <h3 class="text-3xl font-bold text-gray-800">{{ summary.expiringCount.toLocaleString() }}</h3>
            </div>
            <div class="w-12 h-12 rounded-xl bg-orange-50 text-orange-500 flex items-center justify-center text-xl">
              <i class="fa-solid fa-triangle-exclamation"></i>
            </div>
          </div>
          <div class="mt-4 flex items-center text-sm">
            <span class="text-gray-400">当前未配置合同到期日字段</span>
          </div>
        </div>
      </div>
    </n-spin>

    <div class="bg-white p-6 rounded-2xl shadow-sm border border-primary-50">
      <h3 class="text-lg font-bold text-gray-800 mb-4 border-l-4 border-primary-500 pl-3">快捷通道</h3>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <button class="flex flex-col items-center justify-center p-4 rounded-xl border border-gray-100 hover:border-primary-200 hover:bg-primary-50 text-gray-600 hover:text-primary-600 transition-all group">
          <div class="w-12 h-12 rounded-full bg-primary-100 flex items-center justify-center text-primary-500 mb-3 group-hover:scale-110 transition-transform">
            <i class="fa-solid fa-stamp text-xl"></i>
          </div>
          <span class="text-sm font-medium">用印申请</span>
        </button>
        <button class="flex flex-col items-center justify-center p-4 rounded-xl border border-gray-100 hover:border-primary-200 hover:bg-primary-50 text-gray-600 hover:text-primary-600 transition-all group">
          <div class="w-12 h-12 rounded-full bg-primary-50 flex items-center justify-center text-primary-400 mb-3 group-hover:scale-110 transition-transform">
            <i class="fa-solid fa-file-invoice-dollar text-xl"></i>
          </div>
          <span class="text-sm font-medium">付款申请</span>
        </button>
        <button class="flex flex-col items-center justify-center p-4 rounded-xl border border-gray-100 hover:border-primary-200 hover:bg-primary-50 text-gray-600 hover:text-primary-600 transition-all group">
          <div class="w-12 h-12 rounded-full bg-primary-50 flex items-center justify-center text-primary-500 mb-3 group-hover:scale-110 transition-transform">
            <i class="fa-solid fa-handshake text-xl"></i>
          </div>
          <span class="text-sm font-medium">客商准入</span>
        </button>
        <button class="flex flex-col items-center justify-center p-4 rounded-xl border border-gray-100 hover:border-primary-200 hover:bg-primary-50 text-gray-600 hover:text-primary-600 transition-all group">
          <div class="w-12 h-12 rounded-full bg-primary-100 flex items-center justify-center text-primary-600 mb-3 group-hover:scale-110 transition-transform">
            <i class="fa-solid fa-magnifying-glass-chart text-xl"></i>
          </div>
          <span class="text-sm font-medium">数据报表</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
