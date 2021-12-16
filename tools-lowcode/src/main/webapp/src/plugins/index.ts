import type {App} from 'vue'

// 使用element-plus
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'

//背景粒子效果
import Particles from 'particles.vue3'
import util from '@/utils'

export default function installPlugins(app: App) {
    //使用element-plus
    app.use(ElementPlus)
    //使用背景粒子效果
    app.use(Particles)

    //工具对象
    app.config.globalProperties.$util = util
}