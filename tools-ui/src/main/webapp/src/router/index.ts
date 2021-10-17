import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router'
//进度条
//@ts-ignore
import NProgress from 'nprogress'
//进度条样式
import 'nprogress/css/nprogress.css'
//进度条配置
NProgress.configure({showSpinner: false})

const routes: Array<RouteRecordRaw> = [
    {
        path: '/:pathMatch(.*)*',
        component: () => import('@/views/visual-editor/index.vue')
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

router.beforeEach(() => {
    // start progress bar
    NProgress.start()
    return true
})

router.afterEach(() => {
    // finish progress bar
    NProgress.done()
})

export default router