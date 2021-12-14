import util from '@/utils'
import {Store} from 'vuex'

export interface AppState {
    sidebar: AppSidebar,
    device: string,
    size: string
}

export interface AppSidebar {
    opened: boolean,
    withoutAnimation: boolean
}

const state: AppState = {
    //侧边栏
    sidebar: {
        opened: util.cookies.get('sidebarStatus') ? !!+util.cookies.get('sidebarStatus') : true,
        withoutAnimation: false
    },
    //页面设备
    device: 'desktop',
    //大小
    size: util.cookies.get('size') || 'medium'
}

const mutations = {
    //侧边栏开关
    TOGGLE_SIDEBAR: (state: AppState) => {
        state.sidebar.opened = !state.sidebar.opened
        state.sidebar.withoutAnimation = false
        if (state.sidebar.opened) {
            util.cookies.set('sidebarStatus', 1)
        } else {
            util.cookies.set('sidebarStatus', 0)
        }
    },
    //关闭侧边栏
    CLOSE_SIDEBAR: (state: AppState, withoutAnimation: boolean) => {
        util.cookies.set('sidebarStatus', 0)
        state.sidebar.opened = false
        state.sidebar.withoutAnimation = withoutAnimation
    },
    //设置设备
    TOGGLE_DEVICE: (state: AppState, device: string) => {
        state.device = device
    },
    //设置大小
    SET_SIZE: (state: AppState, size: string) => {
        state.size = size
        util.cookies.set('size', size)
    }
}

const actions = {
    toggleSideBar({commit}: Store<AppSidebar>) {
        commit('TOGGLE_SIDEBAR')
    },
    closeSideBar({commit}: Store<AppState>, {withoutAnimation}: AppSidebar) {
        commit('CLOSE_SIDEBAR', withoutAnimation)
    },
    toggleDevice({commit}: Store<AppState>, device: string) {
        commit('TOGGLE_DEVICE', device)
    },
    setSize({commit}: Store<AppState>, size: string) {
        commit('SET_SIZE', size)
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}