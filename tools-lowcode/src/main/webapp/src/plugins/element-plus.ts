import 'element-plus/dist/index.css'
import type {App} from 'vue'
import ElementPlus from 'element-plus'

export const setupElementPlus = (app: App) => {
    app.use(ElementPlus)
}
