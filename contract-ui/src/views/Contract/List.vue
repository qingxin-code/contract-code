<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDialog, useMessage } from 'naive-ui'
import request from '../../utils/request'

type ContractItem = {
  id: number
  contractNo: string
  contractName: string
  contractType: string
  counterparty: string
  amount: number
  signDate: string
  expireDate?: string
  status: string
  attachment?: string
  userId: number
}

const route = useRoute()
const router = useRouter()
const dialog = useDialog()
const message = useMessage()

const statusOptions = [
  { label: '所有状态', value: '' },
  { label: '待签署', value: '待签署' },
  { label: '待审批', value: '待审批' },
  { label: '履行中', value: '履行中' },
  { label: '已归档', value: '已归档' },
  { label: '已完成', value: '已完成' },
  { label: '将过期', value: '将过期' }
]

const typeOptions = [
  { label: '所有类型', value: '' },
  { label: '采购合同', value: '采购合同' },
  { label: '外包合同', value: '外包合同' },
  { label: '租赁合同', value: '租赁合同' },
  { label: '运维合同', value: '运维合同' },
  { label: '服务合同', value: '服务合同' },
  { label: '其他合同', value: '其他合同' }
]

const searchKey = ref('')
const selectedStatus = ref<string | null>('')
const selectedType = ref<string | null>('')
const loading = ref(false)
const contractList = ref<ContractItem[]>([])
const pageInfo = ref({
  current: 1,
  size: 8,
  total: 0
})

const pageCount = computed(() => Math.ceil(pageInfo.value.total / pageInfo.value.size))

const syncFiltersFromRoute = () => {
  searchKey.value = typeof route.query.keyword === 'string' ? route.query.keyword : ''
  selectedStatus.value = typeof route.query.status === 'string' ? route.query.status : ''
  selectedType.value = typeof route.query.type === 'string' ? route.query.type : ''
}

const fetchList = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/contracts', {
      params: {
        page: pageInfo.value.current,
        size: pageInfo.value.size,
        searchKey: searchKey.value.trim(),
        status: selectedStatus.value || '',
        contractType: selectedType.value || ''
      }
    })
    contractList.value = res.data.records || []
    pageInfo.value.total = Number(res.data.total || 0)
  } finally {
    loading.value = false
  }
}

const applyQueryToRoute = () => {
  const query: Record<string, string> = {}
  if (searchKey.value.trim()) query.keyword = searchKey.value.trim()
  if (selectedStatus.value) query.status = selectedStatus.value
  if (selectedType.value) query.type = selectedType.value

  router.replace({ name: 'contract', query })
}

const handleSearch = () => {
  pageInfo.value.current = 1
  applyQueryToRoute()
  fetchList()
}

const handlePageChange = (page: number) => {
  pageInfo.value.current = page
  fetchList()
}

const goCreate = () => {
  router.push({ name: 'contract-create' })
}

const goDetail = (id: number) => {
  router.push({ name: 'contract-detail', params: { id } })
}

const handleRenew = (item: ContractItem) => {
  router.push({ name: 'contract-create', query: { renewFrom: String(item.id) } })
}

const handleDelete = (item: ContractItem) => {
  dialog.warning({
    title: '确认删除',
    content: `确定删除合同“${item.contractName}”吗？删除后不可恢复。`,
    positiveText: '确认删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      await request.delete(`/contracts/${item.id}`)
      message.success('合同删除成功')
      if (contractList.value.length === 1 && pageInfo.value.current > 1) {
        pageInfo.value.current -= 1
      }
      fetchList()
    }
  })
}

const getTypeMeta = (type: string) => {
  const typeMap: Record<string, { icon: string, iconClass: string }> = {
    '采购合同': { icon: 'fa-cloud', iconClass: 'bg-blue-50 text-blue-500' },
    '外包合同': { icon: 'fa-code', iconClass: 'bg-violet-50 text-violet-500' },
    '租赁合同': { icon: 'fa-building', iconClass: 'bg-pink-50 text-pink-500' },
    '运维合同': { icon: 'fa-server', iconClass: 'bg-amber-50 text-amber-500' },
    '服务合同': { icon: 'fa-handshake', iconClass: 'bg-emerald-50 text-emerald-500' }
  }
  return typeMap[type] || { icon: 'fa-file-contract', iconClass: 'bg-primary-50 text-primary-500' }
}

const getStatusMeta = (status: string) => {
  const statusMap: Record<string, { className: string, icon: string, primaryAction: string, secondaryAction?: string }> = {
    '待签署': {
      className: 'bg-amber-50 text-amber-600 border-amber-100',
      icon: 'fa-pen-nib',
      primaryAction: '处理'
    },
    '待审批': {
      className: 'bg-orange-50 text-orange-600 border-orange-100',
      icon: 'fa-hourglass-half',
      primaryAction: '去审批'
    },
    '履行中': {
      className: 'bg-emerald-50 text-emerald-600 border-emerald-100',
      icon: 'fa-circle-check',
      primaryAction: '处理'
    },
    '已归档': {
      className: 'bg-slate-100 text-slate-600 border-slate-200',
      icon: 'fa-folder',
      primaryAction: '查看详情'
    },
    '已完成': {
      className: 'bg-green-50 text-green-600 border-green-100',
      icon: 'fa-seal-check',
      primaryAction: '查看详情'
    },
    '将过期': {
      className: 'bg-rose-50 text-rose-600 border-rose-100',
      icon: 'fa-circle-exclamation',
      primaryAction: '处理',
      secondaryAction: '续约'
    }
  }
  return statusMap[status] || {
    className: 'bg-primary-50 text-primary-600 border-primary-100',
    icon: 'fa-file-contract',
    primaryAction: '处理'
  }
}

const formatAmount = (amount: number) => Number(amount || 0).toLocaleString('zh-CN', {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2
})

onMounted(() => {
  syncFiltersFromRoute()
  fetchList()
})

watch(() => route.query, () => {
  syncFiltersFromRoute()
})
</script>

<template>
  <div class="space-y-6 animate-[fadeIn_0.5s_ease-out]">
    <div class="flex flex-col gap-4 rounded-3xl border border-primary-100 bg-white px-6 py-5 shadow-sm xl:flex-row xl:items-center xl:justify-between">
      <div class="flex flex-col gap-3 md:flex-row md:items-center">
        <n-select
          v-model:value="selectedStatus"
          :options="statusOptions"
          placeholder="所有状态"
          clearable
          size="large"
          class="contract-filter-select"
          @update:value="handleSearch"
        />
        <n-select
          v-model:value="selectedType"
          :options="typeOptions"
          placeholder="所有类型"
          clearable
          size="large"
          class="contract-filter-select"
          @update:value="handleSearch"
        />
      </div>

      <div class="flex flex-col gap-3 md:flex-row md:items-center">
        <div class="relative">
          <i class="fa-solid fa-search absolute left-4 top-1/2 -translate-y-1/2 text-gray-400"></i>
          <input
            v-model="searchKey"
            type="text"
            placeholder="搜索合同编号/名称/相对方..."
            class="w-full rounded-2xl border border-gray-200 py-3 pl-11 pr-4 text-sm text-gray-700 transition-all focus:border-primary-400 focus:outline-none focus:ring-2 focus:ring-primary-100 md:w-80"
            @keyup.enter="handleSearch"
          >
        </div>
        <button
          class="rounded-2xl border border-primary-200 px-5 py-3 text-sm font-medium text-primary-600 transition-colors hover:bg-primary-50"
          @click="handleSearch"
        >
          查询
        </button>
        <button
          class="flex items-center justify-center rounded-2xl bg-gradient-to-r from-primary-500 to-primary-600 px-5 py-3 text-sm font-medium text-white shadow-pink-soft transition-all hover:from-primary-600 hover:to-primary-700"
          @click="goCreate"
        >
          <i class="fa-solid fa-plus mr-2"></i>
          新建
        </button>
      </div>
    </div>

    <n-spin :show="loading">
      <div class="grid min-h-[420px] grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-4">
        <div
          v-if="!loading && contractList.length === 0"
          class="col-span-full flex flex-col items-center justify-center rounded-3xl border border-dashed border-primary-200 bg-white/70 py-20 text-center"
        >
          <div class="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-primary-50 text-2xl text-primary-500">
            <i class="fa-solid fa-file-circle-plus"></i>
          </div>
          <h3 class="mb-2 text-lg font-semibold text-gray-700">
            {{ searchKey.trim() || selectedStatus || selectedType ? '未找到匹配的合同' : '暂无合同' }}
          </h3>
          <p class="text-sm text-gray-400">
            {{ searchKey.trim() || selectedStatus || selectedType ? '请调整筛选条件后重新查询' : '当前账号还没有创建合同，点击右上角“新建”开始录入' }}
          </p>
        </div>

        <div
          v-for="item in contractList"
          :key="item.id"
          class="group flex h-full flex-col overflow-hidden rounded-[28px] border border-primary-100 bg-white shadow-sm transition-all duration-300 hover:-translate-y-1 hover:shadow-pink-soft"
        >
          <div class="h-2 bg-gradient-to-r from-primary-400 to-primary-600"></div>

          <div class="relative flex-1 p-7">
            <div class="mb-6 flex items-start justify-between gap-4">
              <div :class="['flex h-14 w-14 items-center justify-center rounded-2xl text-xl', getTypeMeta(item.contractType).iconClass]">
                <i class="fa-solid" :class="getTypeMeta(item.contractType).icon"></i>
              </div>
              <span :class="['inline-flex items-center gap-2 rounded-full border px-3 py-1 text-sm font-semibold', getStatusMeta(item.status).className]">
                <i class="fa-solid text-xs" :class="getStatusMeta(item.status).icon"></i>
                {{ item.status }}
              </span>
            </div>

            <h3 class="mb-2 line-clamp-2 min-h-[64px] text-[22px] font-bold leading-8 text-gray-800">
              {{ item.contractName }}
            </h3>
            <p class="mb-6 text-sm font-medium tracking-wide text-gray-400">{{ item.contractNo }}</p>

            <div class="space-y-4 border-t border-primary-50 pt-6 text-[15px]">
              <div class="flex items-center justify-between gap-4">
                <span class="text-gray-400">合同类型</span>
                <span class="font-medium text-gray-700">{{ item.contractType || '-' }}</span>
              </div>
              <div class="flex items-center justify-between gap-4">
                <span class="text-gray-400">相对方</span>
                <span class="text-right font-medium text-gray-700">{{ item.counterparty || '-' }}</span>
              </div>
              <div class="flex items-center justify-between gap-4">
                <span class="text-gray-400">合同金额</span>
                <span class="text-right text-xl font-bold text-primary-600">¥ {{ formatAmount(item.amount) }}</span>
              </div>
              <div class="flex items-center justify-between gap-4">
                <span class="text-gray-400">签订日期</span>
                <span class="font-medium text-gray-700">{{ item.signDate || '-' }}</span>
              </div>
              <div class="flex items-center justify-between gap-4">
                <span class="text-gray-400">到期日期</span>
                <span :class="item.status === '将过期' ? 'font-medium text-rose-500' : 'font-medium text-gray-700'">
                  {{ item.expireDate || '-' }}
                </span>
              </div>
            </div>
          </div>

          <div class="flex items-center justify-end gap-2 border-t border-primary-50 px-6 py-5">
            <button
              class="rounded-xl px-4 py-2 text-sm font-medium text-gray-500 transition-colors hover:bg-gray-50 hover:text-gray-700"
              @click="handleDelete(item)"
            >
              删除
            </button>
            <button
              class="rounded-xl px-4 py-2 text-sm font-medium text-gray-500 transition-colors hover:bg-gray-50 hover:text-gray-700"
              @click="goDetail(item.id)"
            >
              查看
            </button>
            <button
              v-if="getStatusMeta(item.status).secondaryAction"
              class="rounded-xl border border-primary-200 px-4 py-2 text-sm font-medium text-primary-600 transition-colors hover:bg-primary-50"
              @click="handleRenew(item)"
            >
              {{ getStatusMeta(item.status).secondaryAction }}
            </button>
            <button
              class="rounded-xl bg-gradient-to-r from-primary-500 to-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all hover:from-primary-600 hover:to-primary-700"
              @click="goDetail(item.id)"
            >
              {{ getStatusMeta(item.status).primaryAction }}
            </button>
          </div>
        </div>
      </div>
    </n-spin>

    <div v-if="pageInfo.total > 0" class="flex justify-center pb-4 pt-2">
      <n-pagination
        v-model:page="pageInfo.current"
        :page-count="pageCount"
        @update:page="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.contract-filter-select {
  width: 168px;
}

:deep(.n-base-selection) {
  --n-height: 44px !important;
  height: 44px;
  border-radius: 1rem;
  box-shadow: none;
}

:deep(.n-base-selection-label),
:deep(.n-base-selection-input),
:deep(.n-base-selection-placeholder),
:deep(.n-base-suffix) {
  min-height: 44px;
  display: flex;
  align-items: center;
}

:deep(.n-base-selection:hover) {
  border-color: rgb(var(--primary-400-rgb) / 1);
}

:deep(.n-base-selection.n-base-selection--active) {
  border-color: rgb(var(--primary-400-rgb) / 1);
  box-shadow: 0 0 0 3px rgb(var(--primary-100-rgb) / 0.9);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
