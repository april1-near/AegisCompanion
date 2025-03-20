import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import { router } from './router/index'
import { createPinia } from 'pinia'
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'



const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
 
// if (typeof window.global === 'undefined') {
//     window.global = window;
//   }
  

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }

app.use(router)
app.use(ElementPlus)
app.use(pinia)
app.mount('#app')
