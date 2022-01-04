import {defineComponent} from 'vue'
import {useVisualData} from '../../../../hooks/useVisualData'
import {VisualEventBusProvider, VisualEditorComponent} from '../../../../visual-editor.utils'
import styles from './index.module.scss'

export default defineComponent({
    name: 'BaseWidgets',
    label: '基本组件',
    order: 3,
    icon: 'el-icon-edit',
    setup() {
        const baseWidgets = useVisualData().visualConfig.componentModules.baseWidgets;

        const {eventBus} = VisualEventBusProvider.inject();

        // 左侧菜单拖拽
        const menuDraggier = (() => {
            const blockHandler = {
                dragstart: (e: DragEvent, component: VisualEditorComponent) => {
                    eventBus.emit(component);
                }
            };
            return {
                blockHandler
            }
        })();

        return () => (
            <>
                <section>
                    {
                        (baseWidgets || []).map(component => (
                            <div draggable={true}
                                 onDragstart={(e) => menuDraggier.blockHandler.dragstart(e, component)}
                                 class={styles.componentItem}
                                 data-label={component.label}>
                                {component.preview()}
                            </div>
                        ))
                    }
                </section>
            </>
        )
    }
})
