import type {App} from 'vue'

// 使用element-plus
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'

//背景粒子效果
import Particles from 'particles.vue3'

import modal from '../utils/modal'


export default function installPlugins(app: App) {
    //使用element-plus
    app.use(ElementPlus)
    //使用背景粒子效果
    app.use(Particles)

    // 模态框对象
    app.config.globalProperties.$modal = modal
}