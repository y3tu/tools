/**
 * 编辑器左侧组件列表
 *
 * 说明：
 * component:在vue中注册的组件名称
 * label:组件列表展示名称
 * style:组件样式
 */
const list = [
    {
        component: 'v-text',
        label:'文字',
        style:{
            width: 200,
            height: 22,
            fontSize: 14,
            fontWeight: 500,
            lineHeight: '',
            letterSpacing: 0,
            textAlign: '',
            color: '',
        }
    },
    {
        component: 'v-button',
        label: '按钮',
        style: {
            width: 100,
            height: 34,
            borderWidth: 1,
            borderColor: '',
            borderRadius: '',
            fontSize: 14,
            fontWeight: 500,
            lineHeight: '',
            letterSpacing: 0,
            textAlign: '',
            color: '',
            backgroundColor: '',
        },
    },
]

export default list