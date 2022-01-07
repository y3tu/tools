import {defineComponent, reactive, watch} from 'vue'
import {ElTabPane, ElTabs} from 'element-plus'
import {ArrowLeft, ArrowRight} from '@element-plus/icons-vue'
import {useVisualData} from "@/views/visual-editor/hooks/useVisualData";
import {AttrEditor, PageSetting} from './components'
import styles from './index.module.scss'

export default defineComponent({
    name: 'RightAttributePanel',
    setup() {
        const {currentBlock} = useVisualData()
        const state = reactive({
            activeName: 'attr',
            isOpen: true
        })

        watch(
            () => currentBlock.value.label,
            (newLabel) => {
                if (!newLabel?.startsWith('表单') && state.activeName == 'form-rule') {
                    state.activeName = 'attr'
                }
            }
        )
        return () => (
            <>
                <div class={[styles.drawer, {[styles.isOpen]: state.isOpen}]}>
                    <div class={styles.floatingActionBtn} onClick={() => (state.isOpen = !state.isOpen)}>
                        {state.isOpen ? <ArrowRight></ArrowRight> : <ArrowLeft></ArrowLeft>}
                    </div>
                    <div class={styles.attrs}>
                        <ElTabs
                            v-model={state.activeName}
                            type="border-card"
                            stretch={true}
                            class={styles.tabs}
                        >
                            <ElTabPane label="属性" name="attr">
                                <AttrEditor/>
                            </ElTabPane>
                            <ElTabPane label="动画" name="animate" lazy>

                            </ElTabPane>
                            <ElTabPane label="事件" name="events">

                            </ElTabPane>
                            {currentBlock.value.label?.startsWith('表单') ? (
                                <ElTabPane label="规则" name="form-rule" lazy>

                                </ElTabPane>
                            ) : null}
                            <ElTabPane label="页面设置" name="page-setting">
                                <PageSetting/>
                            </ElTabPane>
                        </ElTabs>
                    </div>
                </div>
            </>
        )
    }
})