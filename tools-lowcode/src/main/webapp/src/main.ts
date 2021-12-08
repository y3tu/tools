import {createApp} from 'vue'
import App from './App.vue'


const app = createApp(App)

//路由和状态管理
import router from './router'
import store from './store'

app.use(router).use(store)

// 使用element-plus插件
import {setupElementPlus} from './plugins/element-plus'

setupElementPlus(app)

// 路由准备完毕再挂载
router.isReady().then(() => app.mount('#app'))
