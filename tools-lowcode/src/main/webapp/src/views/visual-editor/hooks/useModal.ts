//模态弹框
import {defineComponent, reactive, ComponentInternalInstance, PropType,getCurrentInstance} from 'vue'
import {ElButton, ElDialog} from 'element-plus'


//模态配置接口
interface ModalOptions {
    title?: string,//标题
    footer?: null | (() => JSX.Element),//弹框底部
    content: ComponentInternalInstance | (() => JSX.Element)//弹框内容
    onConfirm?: () => void//确认按钮点击事件
    onCancel?: () => void//取消按钮点击事件
    props?: {
        [propName: string]: any
    }
}

//定义模态弹框组件
const Modal = defineComponent({
    props: {
        options: {
            type: Object as PropType<ModalOptions>,
            default: () => ({})
        }
    },
    setup(props) {
        const instance = getCurrentInstance()!
        const state = reactive({
            options:props.options,
            visible:true
        })

    }
})