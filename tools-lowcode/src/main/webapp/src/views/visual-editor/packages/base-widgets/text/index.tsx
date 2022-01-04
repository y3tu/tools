import {useGlobalProperties} from '@/hooks/useGlobalProperties'
import {
    createEditorColorProp,
    createEditorInputProp,
    createEditorSelectProp
} from '../../../visual-editor.props'

import type {VisualEditorComponent} from '../../../visual-editor.utils'
import {fontArr} from './fontArr'

export default {
    key: 'text',
    moduleName: 'baseWidgets',
    label: '文本',
    preview: () => <span>预览文本</span>,
    render: ({ props }) => <span style={{ color: props.color, fontSize: props.size }}>{props.text || '默认文本'}</span>,

    props: {
        text: createEditorInputProp({label: '显示文本'}),
        font: createEditorSelectProp({label: '字体设置', options: fontArr}),
        color: createEditorColorProp({label: '字体颜色'})
    }
} as VisualEditorComponent