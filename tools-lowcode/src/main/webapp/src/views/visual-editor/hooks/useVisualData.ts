import {reactive, inject, computed} from 'vue'
import type {InjectionKey} from 'vue'
import type {
    VisualEditorModelValue,
    VisualEditorBlockData
} from '../visual-editor.utils'

//加载组件配置
import {visualConfig} from '../visual.config'
//注入jsonData的key
export const injectKey: InjectionKey<ReturnType<typeof initVisualData>> = Symbol()


export const initVisualData = () => {
    //全局数据
    const dataModel: VisualEditorModelValue = reactive({
        //页面宽高
        container: {
            width: 1000,
            height: 1000,
            scale: 100,
            bgColor: '',
            bgImage: ''
        },
        //页面组件集合
        blocks: [] as VisualEditorBlockData[],
        //当前选中的组件
        selectBlock: {} as VisualEditorBlockData,
        //选中组件索引
        selectIndex: -1,
        //快照
        snapshotData: [],
        //快照索引
        snapshotIndex: -1,
    })

    // 计算选中与未选中的block
    const focusData = computed(() => {
        let focus: VisualEditorBlockData[] = [];
        let unFocus: VisualEditorBlockData[] = [];
        (dataModel.blocks || []).forEach(block => (block.focus ? focus : unFocus).push(block))
        return {
            focus,
            unFocus
        }
    })

    //公共方法
    const publicMethods = {
        //更新dataModel
        updateDataModel: (modelValue: VisualEditorModelValue) => {
            dataModel.container = modelValue.container;
            dataModel.blocks = modelValue.blocks
            dataModel.selectBlock = modelValue.selectBlock;
            dataModel.selectIndex = modelValue.selectIndex;
            dataModel.snapshotData = modelValue.snapshotData;
            dataModel.snapshotIndex = modelValue.snapshotIndex;
        },

        //清除选中的block
        clearFocus: (block?: VisualEditorBlockData) => {
            let blocks = (dataModel.blocks || []);
            if (!blocks.length) return;
            if (!!blocks) {
                blocks = blocks.filter(item => item !== block);
            }
            blocks.forEach(block => block.focus = false);
        },

        //更新block，渲染最新数据
        updateBlocks: (blocks?: VisualEditorBlockData[]) => {
            dataModel.blocks = blocks
        },

        //更新单个block
        updateBlock: (newBlock: VisualEditorBlockData, oldBlock: VisualEditorBlockData) => {
            let oldBlocks = dataModel.blocks || [];
            // 找到位置，替换成新的block
            let blocks = [...oldBlocks];
            const index = oldBlocks.indexOf(oldBlock);
            if (index > -1) {
                blocks.splice(index, 1, newBlock);
            }
            publicMethods.updateBlocks(blocks);
        },

        // 重置selectIndex
        resetSelectIndex: (index: number) => {
            dataModel.selectIndex = index;
        },

    }

    return {
        visualConfig,
        dataModel,
        focusData,
        ...publicMethods
    }
}

export const useVisualData = () => inject<ReturnType<typeof initVisualData>>(injectKey)!

