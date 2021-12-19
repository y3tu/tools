/**
 * 基础数据和方法
 */

import {reactive} from "vue";

export const state = reactive({
    editMode: 'edit', // 编辑器模式 edit preview
    canvasStyleData: { // 页面全局数据
        width: 1200,
        height: 740,
        scale: 100,
    },
    componentData: [], // 画布组件数据
    curComponent: null,
    curComponentIndex: null,
    // 点击画布时是否点中组件，主要用于取消选中组件用。
    // 如果没点中组件，并且在画布空白处弹起鼠标，则取消当前组件的选中状态
    isClickComponent: false,
})

export const method = {
    //设置组件被点中
    setClickComponentStatus(status) {
        state.isClickComponent = status
    },
    //设置编辑器模式
    setEditMode(mode) {
        state.editMode = mode
    },
    //设置画布数据
    setCanvasStyle(style) {
        state.canvasStyleData = style
    },
    //设置当前选中的组件数据
    setCurComponent(component, index) {
        state.curComponent = component
        state.curComponentIndex = index
    },
    //设置组件形状
    setShapeStyle({top, left, width, height, rotate}) {
        if (top) state.curComponent.style.top = top
        if (left) state.curComponent.style.left = left
        if (width) state.curComponent.style.width = width
        if (height) state.curComponent.style.height = height
        if (rotate) state.curComponent.style.rotate = rotate
    },
    //设置组件数据
    setComponentData(componentData = []) {
        state.componentData = componentData;
    },
    //添加组件
    addComponent(component, index) {
        if (index !== undefined) {
            state.componentData.splice(index, 0, component)
        } else {
            state.componentData.push(component)
        }
    },
    //删除组件
    deleteComponent(index) {
        if (index === undefined) {
            index = state.curComponentIndex
        }

        if (index == state.curComponentIndex) {
            state.curComponentIndex = null
            state.curComponent = null
        }

        state.componentData.splice(index, 1)
    },
}