/**
 * 快照
 */
import {reactive} from "vue";
import util from '@utils'

import {state as baseState, method as baseMethod} from './base'

const state = reactive({
    snapshotData: [], // 编辑器快照数据
    snapshotIndex: -1, // 快照索引
})

const method = {
    //撤销
    undo() {
        if (state.snapshotIndex >= 0) {
            state.snapshotIndex--
            const componentData = util.common.deepCopy(state.snapshotData[state.snapshotIndex]) || []
            if (baseState.curComponent) {
                // 如果当前组件不在 componentData 中，则置空
                let needClean = !componentData.find(component => baseState.curComponent.id === component.id)

                if (needClean) {
                    baseMethod.setCurComponent(null, null);
                }
            }
            baseMethod.setComponentData(componentData)
        }
    },

    redo(state) {
        if (state.snapshotIndex < state.snapshotData.length - 1) {
            state.snapshotIndex++
            baseMethod.setComponentData(util.common.deepCopy(state.snapshotData[state.snapshotIndex]))
        }
    },

    recordSnapshot(state) {
        // 添加新的快照
        state.snapshotData[++state.snapshotIndex] = util.common.deepCopy(baseState.componentData)
        // 在 undo 过程中，添加新的快照时，要将它后面的快照清理掉
        if (state.snapshotIndex < state.snapshotData.length - 1) {
            state.snapshotData = state.snapshotData.slice(0, state.snapshotIndex + 1)
        }
    },
}
