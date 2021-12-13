import { createApp } from 'vue'
import App from './App.vue'


import router from './router/'
import store from './store/'

const app = createApp(App)

// 使用element-plus插件
import { setupElementPlus } from './plugins/element-plus'
setupElementPlus(app)

//背景粒子效果
import Particles from 'particles.vue3'
app.use(Particles)

app.use(router).use(store)
// 路由准备完毕再挂载
router.isReady().then(() => app.mount('#app'))
