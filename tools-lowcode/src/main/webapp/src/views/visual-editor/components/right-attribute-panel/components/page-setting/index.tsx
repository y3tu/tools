import {defineComponent} from 'vue'
import {ElForm, ElFormItem, ElInput, ElUpload, ElColorPicker, ElSwitch,ElIcon} from 'element-plus'
import {Plus} from '@element-plus/icons-vue'
import {useVisualData} from "@/views/visual-editor/hooks/useVisualData";
import styles from './styles.module.scss'

export const PageSetting = defineComponent({
    setup() {
        const {currentPage} = useVisualData()
        const pageConfig = currentPage.value.config

        const beforeUpload = (file: File) => {
            console.log(file, '要上传的文件')
            const fileReader = new FileReader()
            fileReader.onload = (event) => {
                pageConfig.bgImage = event.target?.result as string
            }
            fileReader.readAsDataURL(file)
        }

        return () => (
            <>
                <ElForm>
                    <ElFormItem label="路由切换时缓存本页面">
                        <ElSwitch v-model={pageConfig.keepAlive}/>
                    </ElFormItem>
                    <ElFormItem label="背景颜色">
                        <ElColorPicker v-model={pageConfig.bgColor}/>
                    </ElFormItem>
                    <ElFormItem label="背景图片">
                        <ElInput v-model={pageConfig.bgImage} placeholder={'图片地址'} clearable/>
                    </ElFormItem>
                    <ElUpload action={''} beforeUpload={beforeUpload} class={styles.upload}>
                        {pageConfig.bgImage ? (
                            <img src={pageConfig.bgImage}/>
                        ) : (
                            <ElIcon size="30">
                                <Plus></Plus>
                            </ElIcon>

                        )}
                    </ElUpload>
                </ElForm>
            </>
        )
    }
})