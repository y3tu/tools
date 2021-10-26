import ElementPlus from 'element-plus'
import 'element-plus/lib/theme-chalk/index.css'
import type {App} from 'vue'

export const setupElementPlus = (app: App) => {
    app.use(ElementPlus)

    const option = {
        size: 'medium',
        zIndex: 2000
    }
    app.config.globalProperties.$ELEMENT = option
}
