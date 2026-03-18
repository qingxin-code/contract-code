<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { useUserStore } from '../store/user'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const isLogin = ref(true)
const loading = ref(false)

const form = ref({
  username: '',
  password: '',
  nickname: ''
})

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    message.warning('请输入用户名和密码')
    return
  }
  
  loading.value = true
  try {
    if (isLogin.value) {
      // 登录
      const res: any = await request.post('/auth/login', {
        username: form.value.username,
        password: form.value.password
      })
      userStore.setToken(res.data.token)
      userStore.setNickname(res.data.nickname || res.data.username)
      message.success('登录成功')
      const redirect = route.query.redirect as string || '/'
      router.push(redirect)
    } else {
      // 注册
      await request.post('/auth/register', form.value)
      message.success('注册成功，请登录')
      isLogin.value = true
    }
  } catch (error) {
    // 错误在拦截器已统一提示
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-primary-50 flex flex-col justify-center items-center p-4">
    <div class="max-w-md w-full bg-white rounded-2xl shadow-pink-soft overflow-hidden">
      <!-- Header -->
      <div class="bg-gradient-to-r from-primary-500 to-primary-600 p-8 text-center">
        <div class="w-16 h-16 bg-white/20 backdrop-blur-md rounded-2xl mx-auto flex items-center justify-center text-white text-3xl mb-4 shadow-pink-glow">
          <n-icon>
            <i class="fa-solid fa-file-signature"></i>
          </n-icon>
        </div>
        <h2 class="text-2xl font-bold text-white tracking-wide">CMS Pro</h2>
        <p class="text-primary-100 mt-2 text-sm">企业级合同管理系统原型</p>
      </div>

      <!-- Form -->
      <div class="p-8">
        <h3 class="text-xl font-bold text-gray-800 mb-6 text-center">
          {{ isLogin ? '账号登录' : '注册新账号' }}
        </h3>

        <form @submit.prevent="handleSubmit" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">用户名</label>
            <input 
              v-model="form.username" 
              type="text" 
              class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-primary-400 focus:ring-2 focus:ring-primary-100 transition-all text-gray-700 placeholder-gray-400"
              placeholder="请输入用户名"
            >
          </div>

          <div v-if="!isLogin">
            <label class="block text-sm font-medium text-gray-700 mb-2">昵称 (可选)</label>
            <input 
              v-model="form.nickname" 
              type="text" 
              class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-primary-400 focus:ring-2 focus:ring-primary-100 transition-all text-gray-700 placeholder-gray-400"
              placeholder="您的显示名称"
            >
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">密码</label>
            <input 
              v-model="form.password" 
              type="password" 
              class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-primary-400 focus:ring-2 focus:ring-primary-100 transition-all text-gray-700 placeholder-gray-400"
              placeholder="请输入密码"
            >
          </div>

          <button 
            type="submit" 
            :disabled="loading"
            class="w-full py-3 px-4 bg-gradient-to-r from-primary-500 to-primary-600 hover:from-primary-600 hover:to-primary-700 text-white font-bold rounded-xl shadow-pink-soft transition-all mt-4 flex items-center justify-center"
          >
            <span v-if="loading" class="mr-2"><i class="fa-solid fa-spinner fa-spin"></i></span>
            {{ isLogin ? '登录系统' : '立即注册' }}
          </button>
        </form>

        <div class="mt-6 text-center text-sm">
          <span class="text-gray-500">
            {{ isLogin ? '还没有账号？' : '已有账号？' }}
          </span>
          <a href="#" @click.prevent="isLogin = !isLogin" class="text-primary-500 hover:text-primary-600 font-medium ml-1">
            {{ isLogin ? '免费注册' : '返回登录' }}
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
