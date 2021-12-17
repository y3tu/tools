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
        redirect: '/',
        component: () => import('@/views/layout/index.vue'),
        children: [
            {
                path: '/',
                name: 'home',
                component: () => import('@/views/home/index.vue'),
                meta: {
                    keepAlive: true,
                }
            }
        ]
    },
    {
        path: '/ds',
        name: '数据源',
        redirect: '/ds',
        component: () => import('@/views/layout/index.vue'),
        children: [
            {
                path: '/ds',
                name: '数据源',
                component: () => import('@/views/dataSource/index.vue'),
                meta: {
                    keepAlive: true,
                }
            }
        ]
    },
    {
        path: '/codeGen',
        name: '代码生成',
        redirect: '/codeGen',
        component: () => import('@/views/layout/index.vue'),
        children: [
            {
                path: '/codeGen',
                name: '代码生成',
                component: () => import('@/views/codeGen/index.vue'),
                meta: {
                    keepAlive: true,
                }
            }
        ]
    },
    {
        path: '/design',
        name: '设计器',
        redirect: '/design',
        component: () => import('@/views/layout/index.vue'),
        children: [
            {
                path: '/design',
                name: '设计器',
                component: () => import('@/views/design/index.vue'),
                meta: {
                    keepAlive: true,
                }
            },
            {
                path: '/designEditor',
                name: '编辑器',
                component: () => import('@/views/design/home.vue'),
                meta: {
                    keepAlive: true,
                }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHashHistory(import.meta.env.VITE_APP_BASE_API+''),
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