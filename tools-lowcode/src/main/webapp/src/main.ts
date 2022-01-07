import {createApp} from 'vue'
import App from './App.vue'

import router from './router/'
import store from './store/'

const app = createApp(App)
//插件
import plugins from './plugins'

app.use(plugins)


app.config.globalProperties.$$refs = {}
window.$$refs = app.config.globalProperties.$$refs

//解决chrome下的passive警告问题
import "default-passive-events"

app.use(router).use(store)
// 路由准备完毕再挂载
router.isReady().then(() => app.mount('#app'))
