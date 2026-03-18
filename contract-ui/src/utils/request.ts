import axios from 'axios'
import { createDiscreteApi } from 'naive-ui'

// 处理脱离 Vue 组件实例的 Naive UI 消息提示
const { message } = createDiscreteApi(['message'])

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器
request.interceptors.response.use(
  response => {
    let res = response.data;
    if (res.code === 200) {
      return res;
    } else {
      // 业务逻辑错误
      message.error(res.message || '系统异常')
      if (res.code === 401) {
        localStorage.removeItem('token')
        // 跳转到登录并刷新状态
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message))
    }
  },
  error => {
    if (error.response && error.response.status === 401) {
       message.error('登录失效')
       localStorage.removeItem('token')
       window.location.href = '/login'
    } else {
       message.error('网络连接异常')
    }
    return Promise.reject(error)
  }
)

export default request;
