import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
// @ts-ignore
import NProgress from 'nprogress' // progress bar
import 'nprogress/css/nprogress.css' // 进度条样式

NProgress.configure({ showSpinner: false }) // NProgress Configuration

const routes: Array<RouteRecordRaw> = [

    {
        path: '/',
        name: '登录页',
        component: () => import('@/views/home/login.vue'),
    },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

router.beforeEach(() => {
    NProgress.start() // start progress bar
    return true
})

router.afterEach(() => {
    NProgress.done() // finish progress bar
})

export default router