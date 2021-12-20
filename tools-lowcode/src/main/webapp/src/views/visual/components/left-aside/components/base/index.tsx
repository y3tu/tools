import {defineComponent, ref} from 'vue'
import {Edit} from "@element-plus/icons-vue";

export default defineComponent({
    name: 'base',
    label: '基本组件',
    order: 3,
    icon: Edit,
    setup() {
        return () => {
            <div>222</div>
        }
    }
})