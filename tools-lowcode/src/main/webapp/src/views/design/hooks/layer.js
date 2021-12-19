/**
 * 组件层级
 */
import util from '@/utils'
import {state} from './base'

export const method = {
    upComponent(curComponentIndex) {
        // 上移图层 index，表示元素在数组中越往后
        if (curComponentIndex < state.componentData.length - 1) {
            util.common.swap(state.componentData, curComponentIndex, curComponentIndex + 1)
        } else {
            util.modal.msgWarning('已经到顶了')
        }
    },

    downComponent(curComponentIndex) {
        // 下移图层 index，表示元素在数组中越往前
        if (curComponentIndex > 0) {
            util.common.swap(state.componentData, curComponentIndex, curComponentIndex - 1)
        } else {
            util.modal.msgWarning('已经到底了')
        }
    },

    topComponent(curComponentIndex, curComponent) {
        // 置顶
        if (curComponentIndex < baseState.componentData.length - 1) {
            state.componentData.splice(curComponentIndex, 1)
            state.componentData.push(curComponent)
        } else {
            util.modal.msgWarning('已经到顶了')
        }
    },

    bottomComponent(curComponentIndex, curComponent) {
        // 置底
        if (curComponentIndex > 0) {
            state.componentData.splice(curComponentIndex, 1)
            state.componentData.unshift(curComponent)
        } else {
            util.modal.msgWarning('已经到底了')
        }
    },
}