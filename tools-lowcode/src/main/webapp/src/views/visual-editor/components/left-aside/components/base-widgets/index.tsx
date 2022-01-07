import {defineComponent, ref} from 'vue'
import {cloneDeep} from 'lodash'
import {visualConfig} from "@/views/visual-editor/visual.config";
import {createNewBlock, VisualEditorComponent} from '../../../../visual-editor.utils'
import styles from './index.module.scss'
import DraggableTransitionGroup from '../../../simulator-editor/draggable-transition-group.vue'


export default defineComponent({
    name: 'BaseWidgets',
    label: '基本组件',
    order: 3,
    icon: 'el-icon-edit',
    setup() {
        const baseWidgets = ref(visualConfig.componentModules.baseWidgets)

        // 克隆组件
        const cloneDog = (comp: VisualEditorComponent) => {
            console.log('当前拖拽的组件：', comp)
            const newComp = cloneDeep(comp)
            return createNewBlock(newComp)
        }

        const log = (evt: Event) => {
            window.console.log('onChange:', evt)
        }

        return () => (
            <>
                <DraggableTransitionGroup
                    class={styles.listGroup}
                    v-model={baseWidgets.value}
                    group={{name: 'components', pull: 'clone', put: false}}
                    clone={cloneDog}
                    onChange={log}
                    itemKey={'key'}
                >
                    {{
                        item: ({element}) => (
                            <div class={styles.listGroupItem} data-label={element.label}>
                                {element.preview()}
                            </div>
                        )
                    }}
                </DraggableTransitionGroup>
            </>
        )
    }
})
