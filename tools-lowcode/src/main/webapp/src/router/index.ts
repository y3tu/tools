import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
// @ts-ignore
import NProgress from 'nprogress' // progress bar
import 'nprogress/css/nprogress.css' // 进度条样式
import util from "@/utils";

NProgress.configure({ showSpinner: false }) // NProgress Configuration

const routes: Array<RouteRecordRaw> = [

    {
        path: '/login',
        name: '登录页',
        component: () => import('@/views/home/login.vue'),
    },
    {
        path: '/',
        name: '首页',
        redirect: '/home',
        component: () => import('@/views/layout/index.vue'),
        children: [
            {
                path: '/home',
                name: 'home',
                component: () => import('@/views/home/index.vue'),
                meta: {
                    keepAlive: true,
                }
            }
        ]
    },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

//白名单
const whiteList = ['/login', '/blog'];


router.beforeEach((to, from, next) => {
    NProgress.start() // start progress bar
    if (to.meta.title) {
        // @ts-ignore
        document.title = to.meta.title
    }

    let whiteFlag = false;

    whiteList.forEach(function (white) {
        if (to.path.indexOf(white) !== -1) {
            //白名单直接放行
            whiteFlag = true;
        }
    });

    if (whiteFlag) {
        next()
    } else {
        const token = util.cookies.get('ACCESS_TOKEN');
        if (token && token.length) {
            next()
        } else {
            //如果没有登录，跳转到登录页面
            if (to.path === '/login') {
                next()
            } else {
                next('/login')
            }
        }
    }
});

router.afterEach(() => {
    NProgress.done() // finish progress bar
})

export default router