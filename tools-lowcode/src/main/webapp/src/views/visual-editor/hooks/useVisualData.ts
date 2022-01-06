import {reactive,readonly, inject, computed} from 'vue'
import type {InjectionKey} from 'vue'
import type {
    VisualEditorPage,
    VisualEditorModelValue,
    VisualEditorBlockData
} from '../visual-editor.utils'
import {visualConfig} from '../visual.config'

import {CacheEnum} from '@/enums'
// 保存到本地JSON数据的key
export const localKey = CacheEnum.PAGE_DATA_KEY

// 注入jsonData的key
export const injectKey: InjectionKey<ReturnType<typeof initVisualData>> = Symbol()

interface IState {
    currentBlock: VisualEditorBlockData // 当前正在操作的组件
    currentPage: VisualEditorPage // 当前正在操作的页面
    jsonData: VisualEditorModelValue // 整棵JSON树
}

const page = {
    title: 'test',
    path: '/testVisual',
    config: {
        bgColor: '',
        bgImage: '',
        keepAlive: false
    },
    blocks: []
}

const defaultValue: VisualEditorModelValue = {
    models: [], // 模型实体集合
    actions: {
        // 动作集合
        fetch: {
            name: '接口请求',
            apis: []
        },
        dialog: {
            name: '对话框',
            handlers: []
        }
    }
}

export const initVisualData = () => {
    const localData = JSON.parse(sessionStorage.getItem(localKey) as string)
    const jsonData: VisualEditorModelValue = Object.keys(localData?.pages || {}).length
        ? localData
        : defaultValue

    const state: IState = reactive({
        jsonData,
        currentPage: page,
        currentBlock: page?.blocks?.find((item) => item.focus) ?? ({} as VisualEditorBlockData)
    })

    // 设置当前被操作的组件
    const setCurrentBlock = (block: VisualEditorBlockData) => {
        state.currentBlock = block
    }

    // 更新pages下面的blocks
    const updatePageBlock = (path = '', blocks: VisualEditorBlockData[] = []) => {
        state.jsonData.pages[path].blocks = blocks
    }

    return{
        visualConfig:visualConfig,
        jsonData: readonly(state.jsonData), // 保护JSONData避免直接修改
        currentPage: computed(() => state.currentPage),
        currentBlock: computed(() => state.currentBlock),
        setCurrentBlock,
        updatePageBlock
    }
}

export const useVisualData = () => inject<ReturnType<typeof initVisualData>>(injectKey)!
