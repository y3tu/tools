import {Store} from 'vuex'

import defaultSettings from '@/settings'
import util from '@/utils'

import {useDynamicTitle} from '@/utils/dynamicTitle'


const {sideTheme, showSettings, topNav, tagsView, fixedHeader, sidebarLogo, dynamicTitle} = defaultSettings

const storageSetting = JSON.parse(util.db.get('settings')) || ''

const state = {
    title: '',
    theme: storageSetting.theme || '#409EFF',
    sideTheme: storageSetting.sideTheme || sideTheme,
    showSettings: showSettings,
    topNav: storageSetting.topNav === undefined ? topNav : storageSetting.topNav,
    tagsView: storageSetting.tagsView === undefined ? tagsView : storageSetting.tagsView,
    fixedHeader: storageSetting.fixedHeader === undefined ? fixedHeader : storageSetting.fixedHeader,
    sidebarLogo: storageSetting.sidebarLogo === undefined ? sidebarLogo : storageSetting.sidebarLogo,
    dynamicTitle: storageSetting.dynamicTitle === undefined ? dynamicTitle : storageSetting.dynamicTitle
}

const mutations = {

    /**
     * 改变配置的值
     */
    CHANGE_SETTING: (state: any, {key, value}: { key: string, value: any }) => {
        if (state.hasOwnProperty(key)) {
            state[key] = value
        }
    }
}

const actions = {
    // 修改设置
    changeSetting({commit}: Store<any>, data: any) {
        commit('CHANGE_SETTING', data)
    },
    // 设置网页标题
    setTitle({commit}: Store<any>, title: string) {
        state.title = title
        useDynamicTitle();
    }

}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

