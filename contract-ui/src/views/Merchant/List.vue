<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { NButton, NCard, NInput, NModal, NForm, NFormItem, NSpace, useDialog, useMessage } from 'naive-ui'
import request from '../../utils/request'

const route = useRoute()
const message = useMessage()
const dialog = useDialog()

const STATUS_ENABLED = '启用'
const STATUS_DISABLED = '禁用'

const loading = ref(false)
const list = ref<any[]>([])
const searchName = ref('')

const showModal = ref(false)
const form = ref({
  id: null as number | null,
  merchantName: '',
  merchantCode: '',
  contactPerson: '',
  contactPhone: '',
  address: '',
  status: STATUS_ENABLED
})

const fetchList = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/merchant/list', {
      params: {
        name: searchName.value.trim()
      }
    })
    list.value = res.data.records || []
  } finally {
    loading.value = false
  }
}

const syncSearchFromRoute = () => {
  const queryKeyword = typeof route.query.keyword === 'string' ? route.query.keyword : ''
  searchName.value = queryKeyword
}

const handleAdd = () => {
  form.value = {
    id: null,
    merchantName: '',
    merchantCode: 'MS-' + Date.now().toString().slice(-6),
    contactPerson: '',
    contactPhone: '',
    address: '',
    status: STATUS_ENABLED
  }
  showModal.value = true
}

const handleEdit = (item: any) => {
  form.value = { ...item }
  showModal.value = true
}

const handleSubmit = async () => {
  if (form.value.id) {
    await request.put('/merchant/update', form.value)
    message.success('更新成功')
  } else {
    await request.post('/merchant/add', form.value)
    message.success('添加成功')
  }
  showModal.value = false
  fetchList()
}

const handleDelete = (item: any) => {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除客商“${item.merchantName}”吗？删除后不可恢复。`,
    positiveText: '确定删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      await request.delete(`/merchant/delete/${item.id}`)
      message.success('删除成功')
      fetchList()
    }
  })
}

const handleSearch = () => {
  fetchList()
}

const getStatusClass = (status: string) => {
  if (status === STATUS_ENABLED) {
    return 'bg-emerald-50 text-emerald-600 border-emerald-200'
  }
  return 'bg-rose-50 text-rose-600 border-rose-200'
}

const getStatusDotClass = (status: string) => {
  if (status === STATUS_ENABLED) {
    return 'bg-emerald-500'
  }
  return 'bg-rose-500'
}

const modalTitle = computed(() => (form.value.id ? '编辑客商' : '新增客商'))

onMounted(() => {
  syncSearchFromRoute()
  fetchList()
})

watch(() => route.query.keyword, () => {
  syncSearchFromRoute()
  fetchList()
})
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">客商管理</h2>
      <n-button type="primary" secondary @click="handleAdd" class="rounded-xl px-6">
        <template #icon><i class="fas fa-plus"></i></template>
        新增客商
      </n-button>
    </div>

    <div class="bg-white/50 backdrop-blur-sm p-4 rounded-3xl border border-primary-100 flex items-center gap-4">
      <n-input
        v-model:value="searchName"
        placeholder="搜索客商名称、编号、联系人、电话、地址..."
        class="max-w-3xl rounded-2xl"
        @keyup.enter="handleSearch"
      >
        <template #prefix><i class="fas fa-search text-primary-300"></i></template>
      </n-input>
      <n-button type="primary" ghost @click="handleSearch" class="rounded-xl">查询</n-button>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-if="!loading && list.length === 0"
        class="col-span-full flex flex-col items-center justify-center rounded-3xl border border-dashed border-primary-200 bg-white/70 py-20 text-center"
      >
        <div class="w-16 h-16 rounded-full bg-primary-50 text-primary-500 flex items-center justify-center text-2xl mb-4">
          <i class="fa-solid fa-users"></i>
        </div>
        <h3 class="text-lg font-semibold text-gray-700 mb-2">
          {{ searchName.trim() ? '未找到匹配的客商' : '暂无客商' }}
        </h3>
        <p class="text-sm text-gray-400">
          {{ searchName.trim() ? '请尝试更换搜索关键词后重新查询' : '当前账号还没有添加客商，点击右上角“新增客商”开始创建' }}
        </p>
      </div>

      <n-card
        v-for="item in list"
        :key="item.id"
        class="rounded-3xl border-none shadow-pink-soft hover:shadow-pink-glow transition-all duration-300 group overflow-hidden"
      >
        <template #cover>
          <div class="h-2 bg-gradient-to-r from-primary-400 to-primary-600"></div>
        </template>

        <div class="flex justify-between items-start mb-4">
          <div>
            <h3 class="text-lg font-bold text-gray-800 group-hover:text-primary-600 transition-colors">{{ item.merchantName }}</h3>
            <p class="text-xs text-gray-400 mt-1">{{ item.merchantCode }}</p>
          </div>
          <span
            class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full border text-xs font-semibold"
            :class="getStatusClass(item.status)"
          >
            <span class="w-1.5 h-1.5 rounded-full" :class="getStatusDotClass(item.status)"></span>
            {{ item.status }}
          </span>
        </div>

        <div class="space-y-3 py-4 border-y border-primary-50">
          <div class="flex justify-between text-sm">
            <span class="text-gray-400">联系人</span>
            <span class="text-gray-700 font-medium">{{ item.contactPerson || '-' }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-400">联系电话</span>
            <span class="text-gray-700 font-medium font-mono text-primary-500">{{ item.contactPhone || '-' }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-400">地址</span>
            <span class="text-gray-700 truncate ml-4">{{ item.address || '-' }}</span>
          </div>
        </div>

        <div class="flex justify-end gap-3 mt-4">
          <n-button size="small" ghost @click="handleEdit(item)" class="rounded-lg">编辑</n-button>
          <n-button size="small" type="error" ghost @click="handleDelete(item)" class="rounded-lg">删除</n-button>
        </div>
      </n-card>
    </div>

    <n-modal v-model:show="showModal" preset="card" :title="modalTitle" class="max-w-md rounded-3xl overflow-hidden">
      <n-form :model="form">
        <n-form-item label="客商名称" path="merchantName">
          <n-input v-model:value="form.merchantName" placeholder="请输入客商名称" />
        </n-form-item>
        <n-form-item label="客商编号" path="merchantCode">
          <n-input v-model:value="form.merchantCode" placeholder="请输入客商编号" />
        </n-form-item>
        <n-form-item label="联系人" path="contactPerson">
          <n-input v-model:value="form.contactPerson" placeholder="请输入联系人" />
        </n-form-item>
        <n-form-item label="联系电话" path="contactPhone">
          <n-input v-model:value="form.contactPhone" placeholder="请输入联系电话" />
        </n-form-item>
        <n-form-item label="地址" path="address">
          <n-input v-model:value="form.address" type="textarea" placeholder="请输入地址" />
        </n-form-item>
        <n-form-item label="状态" path="status">
          <n-space>
            <n-button :type="form.status === STATUS_ENABLED ? 'primary' : 'default'" @click="form.status = STATUS_ENABLED">启用</n-button>
            <n-button :type="form.status === STATUS_DISABLED ? 'primary' : 'default'" @click="form.status = STATUS_DISABLED">禁用</n-button>
          </n-space>
        </n-form-item>
      </n-form>
      <template #footer>
        <div class="flex justify-end gap-4">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" @click="handleSubmit">确定</n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>
