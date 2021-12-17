const state = {
    // 编辑器模式 edit preview
    editMode: 'edit',
    // 页面全局数据
    canvasStyleData: {
        width: 1200,
        height: 740,
        scale: 100,
    },
    // 画布组件数据
    componentData: [],
    // 当前选中组件
    curComponent: null,
    // 当前选中组件index
    curComponentIndex: null,
    // 点击画布时是否点中组件，主要用于取消选中组件用。
    // 如果没点中组件，并且在画布空白处弹起鼠标，则取消当前组件的选中状态
    isClickComponent: false,

}

const mutations = {
    //设置组件被点中
    setClickComponentStatus(state, status) {
        state.isClickComponent = status
    },
    //设置编辑器模式
    setEditMode(state, mode) {
        state.editMode = mode
    },
    //设置画布数据
    setCanvasStyle(state, style) {
        state.canvasStyleData = style
    },
    //设置当前选中的组件数据
    setCurComponent(state, { component, index }) {
        state.curComponent = component
        state.curComponentIndex = index
    },
    //设置组件形状
    setShapeStyle({ curComponent }, { top, left, width, height, rotate }) {
        if (top) curComponent.style.top = top
        if (left) curComponent.style.left = left
        if (width) curComponent.style.width = width
        if (height) curComponent.style.height = height
        if (rotate) curComponent.style.rotate = rotate
    },
    //设置组件数据
    setComponentData(state, componentData = []) {
        state.componentData = componentData;
    },
    //添加组件
    addComponent(state, { component, index }) {
        if (index !== undefined) {
            state.componentData.splice(index, 0, component)
        } else {
            state.componentData.push(component)
        }
    },
    //删除组件
    deleteComponent(state, index) {
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

const actions ={

}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}