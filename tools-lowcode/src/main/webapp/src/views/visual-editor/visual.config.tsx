import { createVisualEditorConfig } from './visual-editor.utils'

import baseWidgets from './packages/base-widgets'

export const visualConfig = createVisualEditorConfig()
// 注册基础控件
Object.keys(baseWidgets).forEach((name: string) =>
    visualConfig.registry('baseWidgets', name, baseWidgets[name])
)

console.log(
    '%c成功加载组件数量:' + Object.keys(visualConfig.componentMap).length,
    'color:#409EFF;background-color:#ecf5ff;padding:0 10px;line-height:2;margin-bottom:4px;'
)
