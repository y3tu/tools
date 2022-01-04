import {provide, inject} from 'vue';

import {VisualEditorProps} from './visual-editor.props'

//全局数据
export interface VisualEditorModelValue {
    //页面数据
    container: VisualEditorContainer;
    //当前所有组件数据
    blocks?: VisualEditorBlockData[];
    //当前选中的组件
    selectBlock: VisualEditorBlockData,
    //选中组件索引
    selectIndex: number,
    //快照
    snapshotData: VisualEditorBlockData[],
    //快照索引
    snapshotIndex: number,
}

//页面类型声明
export interface VisualEditorContainer {
    //页面宽度
    width: number;
    //页面高度
    height: number;
    //页面缩放比例
    scale: number;
    //背景图片
    bgImage: string
    //背景颜色
    bgColor: string
}

// block类型声明
export interface VisualEditorBlockData {
    componentKey: string; // text, input ...
    width: number; // block的宽度
    height: number;// block的高度
    top: number;  // 位置
    left: number; // 位置
    adjustPosition?: boolean; // 鼠标抬起时是否需要调整位置（为true时，鼠标抬起，组件的中间位置位于鼠标的位置）
    focus?: boolean; // 是否为选中状态
    zIndex: number; // 权重，用于置顶置底
    hasResize?: boolean; // 是否调整过宽度，用于展示block的时候，如果调整过宽度则渲染数据中的width和height
    props: Record<string, any>; // 组件的设计属性
    model: Record<string, string>; // 绑定的字段
    slotName?: string; // slotName -组件唯一标识
}


export interface VisualEditorComponent {
    key: string, // text、input、button、select...
    label: string, // 文本、输入框、按钮、下拉框...
    preview: () => JSX.Element, // 默认渲染
    render: (data: { // 画布渲染，给当前block添加属性
        props: any, // 属性
        model: any, // 绑定值
        size: { width?: number, height?: number }, // 宽高
        custom: Record<string, any> // 自定义属性
    }) => JSX.Element,
    props?: Record<string, VisualEditorProps>, // 绑定属性（用于右侧）
    model?: Record<string, string>, // 用于右侧绑定值的标识（用于右侧）
    resize?: { width?: boolean, height?: boolean } // 是否可拖拽改变宽高
}

// focusData
export interface VisualEditorFocusData {
    //选中的组件
    focus: VisualEditorBlockData[];
    //未选中的组件
    unFocus: VisualEditorBlockData[];
}

// state
export interface VisualEditorState {
    //当前选中的组件
    selectBlock: VisualEditorBlockData,
    preview: boolean,
    editFlag: boolean
}

/**
 * 组件模块
 */
export interface ComponentModules {
    baseWidgets: VisualEditorComponent[] // 基础组件
    containerComponents: VisualEditorComponent[] // 容器组件
}

// 创建可视化编辑器配置
// 返回组件列表、组件映射、注册函数
export function createVisualEditorConfig() {
    const componentModules: ComponentModules = {
        baseWidgets: [],
        containerComponents: []
    }
    const componentMap: Record<string, VisualEditorComponent> = {}
    return {
        componentModules,
        componentMap,
        registry: <Props extends Record<string, VisualEditorProps> = {},
            Model extends Record<string, string> = {}>(
            moduleName: keyof ComponentModules,
            key: string,
            componentOptions: {
                label: string,
                preview: () => JSX.Element,
                render: (data: {
                    props: { [k in keyof Props]: any },
                    model: Partial<{ [k in keyof Model]: any }>,
                    size: { width?: number, height?: number },
                    custom: Record<string, any>
                }) => JSX.Element,
                props?: Props,
                model?: Model,
                resize?: { width?: boolean, height?: boolean }
            }) => {
            let comp = {...componentOptions, key, moduleName};
            componentModules[moduleName].push(comp)
            componentMap[key] = comp;
        }
    }
}

// 创建block
export function createNewBlock({component, top, left}: { component: VisualEditorComponent, top: number, left: number }): VisualEditorBlockData {
    return {
        componentKey: component!.key, // input、text...
        width: 0,
        height: 0,
        top,  // 距离顶部位置
        left, // 距离左侧位置
        adjustPosition: true, // 鼠标松开是否调整block位置居中
        focus: false, // 是否为选中态
        zIndex: 0, // 权重，用于置顶置底
        hasResize: false, // 是否已经改变了block的宽高，改变了则使用数据中保存的宽高
        props: {}, // 配置的属性
        model: {}  // 绑定的字段
    }
}


// 当前操作block的标志线
export interface VisualEditorMarkLines {
    x: { left: number, markX: number }[],
    y: { top: number, markY: number }[]
}

//-----------------------------------------------事件-----------------------------------------------

// 拖拽事件类型定义
export interface VisualDragEvent {
    dragStart: {
        on: (cb: () => void) => void,
        off: (cb: () => void) => void,
        emit: () => void,
    },
    dragEnd: {
        on: (cb: () => void) => void,
        off: (cb: () => void) => void,
        emit: () => void,
    }
}

// 左侧菜单和中间容器通信
export interface VisualEventBus {
    eventBus: { // 左侧组件开始拖拽， 给容器绑定事件
        on: (cb: (dragComponent?: any) => void) => void,
        off: (cb: (dragComponent?: any) => void) => void,
        emit: (dragComponent?: any) => void
    }
}

// 用于拖拽的 provide inject
export const VisualDragProvider = (() => {
    const VISUAL_DRAG_PROVIDER = '@@VISUAL_DRAG_PROVIDER';
    return {
        provide: (data: VisualDragEvent) => {
            provide(VISUAL_DRAG_PROVIDER, data);
        },
        inject: () => {
            return inject(VISUAL_DRAG_PROVIDER) as VisualDragEvent;
        }
    }
})();

// 用于左侧菜单组件和容器组件通信的 provide inject
export const VisualEventBusProvider = (() => {
    const VISUAL_EVENTBUS_PROVIDER = '@@VISUAL_EVENTBUS_PROVIDER';
    return {
        provide: (data: VisualEventBus) => {
            provide(VISUAL_EVENTBUS_PROVIDER, data);
        },
        inject: () => {
            return inject(VISUAL_EVENTBUS_PROVIDER) as VisualEventBus;
        }
    }
})();