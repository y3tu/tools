import 'element-plus/packages/theme-chalk/src/base.scss'

import type {App} from 'vue'
import {ElInfiniteScroll} from "element-plus";

export const setupElementPlus = (app: App) => {
    app.use(ElInfiniteScroll)
}