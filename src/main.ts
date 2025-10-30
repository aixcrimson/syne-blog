import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'
import { useAppStore } from './stores/app'

// 引入 iconfont
import '@/assets/iconfont/iconfont.js'

// 引入 SvgIcon 组件
import { SvgIcon } from '@/components/Icon'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 注册全局组件
app.component('SvgIcon', SvgIcon)

app.mount('#app')

// 初始化应用状态（主题等）
const appStore = useAppStore()
appStore.init()

