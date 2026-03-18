<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import request from '../../utils/request'

type ContractFormState = {
  contractNo: string
  contractName: string
  contractType: string
  counterparty: string
  amount: number | null
  signDate: string
  expireDate: string
  status: string
  attachment: string
}

const route = useRoute()
const router = useRouter()
const message = useMessage()

const statusOptions = [
  { label: '待签署', value: '待签署' },
  { label: '待审批', value: '待审批' },
  { label: '履行中', value: '履行中' },
  { label: '已归档', value: '已归档' },
  { label: '已完成', value: '已完成' },
  { label: '将过期', value: '将过期' }
]

const typeOptions = [
  { label: '采购合同', value: '采购合同' },
  { label: '外包合同', value: '外包合同' },
  { label: '租赁合同', value: '租赁合同' },
  { label: '运维合同', value: '运维合同' },
  { label: '服务合同', value: '服务合同' },
  { label: '其他合同', value: '其他合同' }
]

const createEmptyForm = (): ContractFormState => ({
  contractNo: '',
  contractName: '',
  contractType: '采购合同',
  counterparty: '',
  amount: null,
  signDate: '',
  expireDate: '',
  status: '待签署',
  attachment: ''
})

const contractId = computed(() => Number(route.params.id || 0))
const renewSourceId = computed(() => Number(route.query.renewFrom || 0))
const isEditMode = computed(() => Boolean(contractId.value))
const isRenewMode = computed(() => !isEditMode.value && Boolean(renewSourceId.value))

const loading = ref(false)
const detailLoading = ref(false)
const currentStep = ref(1)
const confirmSubmit = ref(false)
const form = ref<ContractFormState>(createEmptyForm())

const pageTitle = computed(() => {
  if (isEditMode.value) return '处理合同'
  if (isRenewMode.value) return '续约合同'
  return '新建合同'
})

const submitText = computed(() => {
  if (isEditMode.value) return '确认更新合同'
  if (isRenewMode.value) return '确认创建续约合同'
  return '确认创建合同'
})

const amountText = computed(() => Number(form.value.amount || 0).toLocaleString('zh-CN', {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2
}))

const goBack = () => {
  router.push({ name: 'contract' })
}

const fillForm = (data: any, renew = false) => {
  form.value = {
    contractNo: renew ? '' : (data.contractNo || ''),
    contractName: renew ? `${data.contractName || ''}-续约` : (data.contractName || ''),
    contractType: data.contractType || '采购合同',
    counterparty: data.counterparty || '',
    amount: data.amount == null ? null : Number(data.amount),
    signDate: renew ? '' : (data.signDate || ''),
    expireDate: renew ? '' : (data.expireDate || ''),
    status: renew ? '待签署' : (data.status || '待签署'),
    attachment: data.attachment || ''
  }
}

const fetchDetail = async (id: number, renew = false) => {
  detailLoading.value = true
  try {
    const res: any = await request.get(`/contracts/${id}`)
    if (res.data) {
      fillForm(res.data, renew)
    }
  } finally {
    detailLoading.value = false
  }
}

const validateStepOne = () => {
  if (!form.value.contractName.trim()) {
    message.warning('请输入合同名称')
    return false
  }
  if (!form.value.contractNo.trim()) {
    message.warning('请输入合同编号')
    return false
  }
  if (!form.value.contractType) {
    message.warning('请选择合同类型')
    return false
  }
  if (!form.value.counterparty.trim()) {
    message.warning('请输入相对方')
    return false
  }
  if (form.value.amount == null || Number(form.value.amount) <= 0) {
    message.warning('请输入正确的合同金额')
    return false
  }
  if (!form.value.signDate) {
    message.warning('请选择签订日期')
    return false
  }
  if (form.value.expireDate && form.value.expireDate < form.value.signDate) {
    message.warning('到期日期不能早于签订日期')
    return false
  }
  return true
}

const nextStep = () => {
  if (currentStep.value === 1 && !validateStepOne()) {
    return
  }
  if (currentStep.value < 3) {
    currentStep.value += 1
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value -= 1
  }
}

const handleSubmit = async () => {
  if (!validateStepOne()) {
    currentStep.value = 1
    return
  }
  if (!confirmSubmit.value) {
    message.warning('请先勾选确认信息后再提交')
    return
  }

  const payload = {
    ...form.value,
    contractNo: form.value.contractNo.trim(),
    contractName: form.value.contractName.trim(),
    contractType: form.value.contractType.trim(),
    counterparty: form.value.counterparty.trim(),
    expireDate: form.value.expireDate || null,
    attachment: form.value.attachment.trim() || null
  }

  loading.value = true
  try {
    if (isEditMode.value) {
      await request.put(`/contracts/${contractId.value}`, payload)
      message.success('合同更新成功')
    } else {
      await request.post('/contracts', payload)
      message.success(isRenewMode.value ? '续约合同创建成功' : '合同创建成功')
    }
    goBack()
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (isEditMode.value) {
    await fetchDetail(contractId.value)
    return
  }
  if (isRenewMode.value) {
    await fetchDetail(renewSourceId.value, true)
  }
})
</script>

<template>
  <div class="animate-[fadeIn_0.5s_ease-out]">
    <div class="mx-auto max-w-5xl">
      <div class="mb-6">
        <div class="mb-2 flex items-center text-sm text-gray-500">
          <a href="#" class="transition-colors hover:text-primary-500" @click.prevent="goBack">合同管理</a>
          <i class="fa-solid fa-chevron-right mx-2 text-xs"></i>
          <span class="font-medium text-gray-800">{{ pageTitle }}</span>
        </div>
        <h2 class="text-3xl font-bold text-gray-800">{{ pageTitle }}</h2>
      </div>

      <div class="rounded-3xl border border-primary-100 bg-white p-8 shadow-sm">
        <div class="mb-10 flex items-center border-b border-primary-50 pb-8">
          <div class="relative flex w-1/3 flex-col items-center">
            <div :class="[
              'z-10 flex h-12 w-12 items-center justify-center rounded-full text-lg font-bold',
              currentStep >= 1 ? 'bg-primary-500 text-white shadow-pink-soft' : 'border border-gray-200 bg-white text-gray-300'
            ]">1</div>
            <span :class="['mt-3 text-sm font-medium', currentStep >= 1 ? 'text-primary-600' : 'text-gray-400']">基本信息</span>
            <div class="absolute left-1/2 top-6 h-1 w-full" :class="currentStep >= 2 ? 'bg-primary-100' : 'bg-gray-100'"></div>
          </div>
          <div class="relative flex w-1/3 flex-col items-center">
            <div :class="[
              'z-10 flex h-12 w-12 items-center justify-center rounded-full text-lg font-bold',
              currentStep >= 2 ? 'bg-primary-500 text-white shadow-pink-soft' : 'border border-gray-200 bg-white text-gray-300'
            ]">2</div>
            <span :class="['mt-3 text-sm font-medium', currentStep >= 2 ? 'text-primary-600' : 'text-gray-400']">正文与附件</span>
            <div class="absolute left-1/2 top-6 h-1 w-full" :class="currentStep >= 3 ? 'bg-primary-100' : 'bg-gray-100'"></div>
          </div>
          <div class="flex w-1/3 flex-col items-center">
            <div :class="[
              'z-10 flex h-12 w-12 items-center justify-center rounded-full text-lg font-bold',
              currentStep >= 3 ? 'bg-primary-500 text-white shadow-pink-soft' : 'border border-gray-200 bg-white text-gray-300'
            ]">3</div>
            <span :class="['mt-3 text-sm font-medium', currentStep >= 3 ? 'text-primary-600' : 'text-gray-400']">提交确认</span>
          </div>
        </div>

        <n-spin :show="detailLoading">
          <form class="space-y-8" @submit.prevent="handleSubmit">
            <div v-if="currentStep === 1" class="grid grid-cols-1 gap-6 md:grid-cols-2">
              <div class="space-y-2 md:col-span-2">
                <label class="block text-sm font-medium text-gray-700">合同名称 <span class="text-red-500">*</span></label>
                <input
                  v-model="form.contractName"
                  type="text"
                  placeholder="例如：阿里云服务器采购合同"
                  class="w-full rounded-2xl border border-gray-200 px-4 py-3 text-gray-700 transition-all focus:border-primary-400 focus:outline-none focus:ring-2 focus:ring-primary-100"
                >
              </div>

              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700">合同编号 <span class="text-red-500">*</span></label>
                <input
                  v-model="form.contractNo"
                  type="text"
                  placeholder="例如：HT-20260318-001"
                  class="w-full rounded-2xl border border-gray-200 px-4 py-3 text-gray-700 transition-all focus:border-primary-400 focus:outline-none focus:ring-2 focus:ring-primary-100"
                >
              </div>

              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700">合同类型 <span class="text-red-500">*</span></label>
                <n-select
                  v-model:value="form.contractType"
                  :options="typeOptions"
                  placeholder="请选择合同类型"
                  size="large"
                />
              </div>

              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700">相对方 <span class="text-red-500">*</span></label>
                <input
                  v-model="form.counterparty"
                  type="text"
                  placeholder="例如：阿里云计算有限公司"
                  class="w-full rounded-2xl border border-gray-200 px-4 py-3 text-gray-700 transition-all focus:border-primary-400 focus:outline-none focus:ring-2 focus:ring-primary-100"
                >
              </div>

              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700">合同状态</label>
                <n-select
                  v-model:value="form.status"
                  :options="statusOptions"
                  placeholder="请选择合同状态"
                  size="large"
                />
              </div>

              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700">合同金额（元） <span class="text-red-500">*</span></label>
                <div class="relative">
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">¥</span>
                  <input
                    v-model.number="form.amount"
                    type="number"
                    min="0.01"
                    step="0.01"
                    placeholder="0.00"
                    class="w-full rounded-2xl border border-gray-200 py-3 pl-9 pr-4 text-gray-700 transition-all focus:border-primary-400 focus:outline-none focus:ring-2 focus:ring-primary-100"
                  >
                </div>
              </div>

              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700">签订日期 <span class="text-red-500">*</span></label>
                <input
                  v-model="form.signDate"
                  type="date"
                  class="w-full rounded-2xl border border-gray-200 px-4 py-3 text-gray-700 transition-all focus:border-primary-400 focus:outline-none focus:ring-2 focus:ring-primary-100"
                >
              </div>

              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700">到期日期</label>
                <input
                  v-model="form.expireDate"
                  type="date"
                  class="w-full rounded-2xl border border-gray-200 px-4 py-3 text-gray-700 transition-all focus:border-primary-400 focus:outline-none focus:ring-2 focus:ring-primary-100"
                >
              </div>
            </div>

            <div v-else-if="currentStep === 2" class="space-y-5">
              <div class="rounded-2xl border border-primary-100 bg-primary-50/50 p-4 text-sm leading-6 text-gray-500">
                这里可以填写合同正文摘要、附件链接、网盘地址，或者补充说明。没有附件也可以直接进入下一步。
              </div>

              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700">正文或附件说明</label>
                <textarea
                  v-model="form.attachment"
                  rows="10"
                  placeholder="请输入附件链接、正文摘要或补充说明"
                  class="w-full resize-none rounded-2xl border border-gray-200 px-4 py-3 text-gray-700 transition-all focus:border-primary-400 focus:outline-none focus:ring-2 focus:ring-primary-100"
                ></textarea>
              </div>
            </div>

            <div v-else class="space-y-6">
              <div class="rounded-3xl border border-primary-100 bg-primary-50/40 p-6">
                <h3 class="mb-4 text-lg font-semibold text-gray-800">合同确认</h3>
                <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                  <div class="rounded-2xl border border-white bg-white p-4">
                    <div class="mb-1 text-gray-400">合同名称</div>
                    <div class="font-medium text-gray-800">{{ form.contractName || '-' }}</div>
                  </div>
                  <div class="rounded-2xl border border-white bg-white p-4">
                    <div class="mb-1 text-gray-400">合同编号</div>
                    <div class="font-medium text-gray-800">{{ form.contractNo || '-' }}</div>
                  </div>
                  <div class="rounded-2xl border border-white bg-white p-4">
                    <div class="mb-1 text-gray-400">合同类型</div>
                    <div class="font-medium text-gray-800">{{ form.contractType || '-' }}</div>
                  </div>
                  <div class="rounded-2xl border border-white bg-white p-4">
                    <div class="mb-1 text-gray-400">相对方</div>
                    <div class="font-medium text-gray-800">{{ form.counterparty || '-' }}</div>
                  </div>
                  <div class="rounded-2xl border border-white bg-white p-4">
                    <div class="mb-1 text-gray-400">合同状态</div>
                    <div class="font-medium text-gray-800">{{ form.status }}</div>
                  </div>
                  <div class="rounded-2xl border border-white bg-white p-4">
                    <div class="mb-1 text-gray-400">合同金额</div>
                    <div class="font-medium text-primary-600">¥ {{ amountText }}</div>
                  </div>
                  <div class="rounded-2xl border border-white bg-white p-4">
                    <div class="mb-1 text-gray-400">签订日期</div>
                    <div class="font-medium text-gray-800">{{ form.signDate || '-' }}</div>
                  </div>
                  <div class="rounded-2xl border border-white bg-white p-4">
                    <div class="mb-1 text-gray-400">到期日期</div>
                    <div class="font-medium text-gray-800">{{ form.expireDate || '-' }}</div>
                  </div>
                  <div class="rounded-2xl border border-white bg-white p-4 md:col-span-2">
                    <div class="mb-1 text-gray-400">正文或附件说明</div>
                    <div class="break-all font-medium text-gray-800">{{ form.attachment || '未填写' }}</div>
                  </div>
                </div>
              </div>

              <label class="flex cursor-pointer items-center gap-3 rounded-2xl border border-gray-200 p-4 transition-colors hover:border-primary-300">
                <input v-model="confirmSubmit" type="checkbox" class="h-4 w-4 accent-pink-500">
                <span class="text-sm text-gray-700">我已确认以上合同信息无误，并同意提交</span>
              </label>
            </div>

            <div class="mt-8 flex justify-end gap-4 border-t border-gray-100 pt-6">
              <button
                type="button"
                class="rounded-2xl border border-gray-200 bg-white px-6 py-3 font-medium text-gray-600 transition-colors hover:bg-gray-50"
                @click="goBack"
              >
                返回取消
              </button>

              <button
                v-if="currentStep > 1"
                type="button"
                class="rounded-2xl border border-primary-200 bg-white px-6 py-3 font-medium text-primary-600 transition-colors hover:bg-primary-50"
                @click="prevStep"
              >
                上一步
              </button>

              <button
                v-if="currentStep < 3"
                type="button"
                class="flex items-center rounded-2xl bg-gradient-to-r from-primary-500 to-primary-600 px-8 py-3 font-medium text-white shadow-pink-soft transition-all hover:from-primary-600 hover:to-primary-700"
                @click="nextStep"
              >
                下一步
                <i class="fa-solid fa-arrow-right ml-2"></i>
              </button>

              <button
                v-else
                type="submit"
                :disabled="loading || detailLoading"
                class="flex items-center rounded-2xl bg-gradient-to-r from-primary-500 to-primary-600 px-8 py-3 font-medium text-white shadow-pink-soft transition-all hover:from-primary-600 hover:to-primary-700 disabled:cursor-not-allowed disabled:opacity-60"
              >
                <i v-if="loading" class="fa-solid fa-spinner mr-2 animate-spin"></i>
                {{ submitText }}
                <i v-if="!loading" class="fa-solid fa-arrow-right ml-2"></i>
              </button>
            </div>
          </form>
        </n-spin>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.n-base-selection) {
  --n-height: 48px !important;
  border-radius: 1rem;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
