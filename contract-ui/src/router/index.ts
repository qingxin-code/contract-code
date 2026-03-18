import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      component: () => import('../views/Layout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('../views/Dashboard.vue'),
          meta: { title: '仪表盘', requireAuth: true }
        },
        {
          path: 'contract',
          name: 'contract',
          component: () => import('../views/Contract/List.vue'),
          meta: { title: '合同管理', requireAuth: true }
        },
        {
          path: 'contract/create',
          name: 'contract-create',
          component: () => import('../views/Contract/Form.vue'),
          meta: { title: '新建合同', requireAuth: true }
        },
        {
          path: 'contract/:id',
          name: 'contract-detail',
          component: () => import('../views/Contract/Form.vue'),
          meta: { title: '合同详情', requireAuth: true }
        },
        {
          path: 'merchant',
          name: 'merchant',
          component: () => import('../views/Merchant/List.vue'),
          meta: { title: '客商管理', requireAuth: true }
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('../views/Settings/Index.vue'),
          meta: { title: '系统设置', requireAuth: true }
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title} - CMS Pro` || 'CMS Pro'

  const userStore = useUserStore()
  if (to.meta.requireAuth) {
    if (userStore.token) {
      next()
    } else {
      next({ path: '/login', query: { redirect: to.fullPath } })
    }
  } else {
    next()
  }
})

export default router
