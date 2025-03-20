// /src/utils/request.ts

import axios from 'axios'
import JSONBig from 'json-bigint'
export const getAuthToken = () => localStorage.getItem('token')

// 初始化大数解析器
const JSONParser = JSONBig({
  storeAsString: true,  // 自动将大数转为字符串
  useNativeBigInt: true // 启用BigInt支持（可选）
})

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  
  transformResponse: [data => {
    try {
      return JSONParser.parse(data, (key: string, value: any) => {
        // 类型明确的转换逻辑
        if (/_?id$/i.test(key) && typeof value === 'number') {
          return value.toString()
        }
        return value
      })
    } catch (e) {
      console.error('JSON解析失败:', e)
      return data
    }
  }]
})


service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 增强的响应拦截器
service.interceptors.response.use(
  response => {
    // 调试输出转换后的数据
    console.debug('[API Response]', response.data)
    return response.data
  },
  error => {
    // 统一错误格式处理
    const errData = error.response?.data || { message: error.message }
    console.error('[API Error]', error.config.url, errData)
    
    // 特殊处理大数精度错误
    if (errData.message.includes('BigInt')) {
      return Promise.reject(new Error('数据转换异常，请检查ID格式'))
    }
    return Promise.reject(errData)
  }
)

export default service
