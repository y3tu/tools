import { defineComponent, PropType, Slot, ref, onMounted, computed } from "vue";
import './index.scss';
import {VisualEditorBlockData} from "@/views/visual-editor/visual-editor.utils";
import {VisualEditorConfig} from "@/views/visual-editor/visual.config";

const VisualEditorBlock = defineComponent({
    props: {
        block: {
            type: Object as PropType<VisualEditorBlockData>,
            required: true
        },
        config: {
            type: Object as PropType<VisualEditorConfig>,
            required: true
        },
        formData: {
            type: Object as PropType<Record<string, any>>,
        },
        slots: {
            type: Object as PropType<Record<string, Slot | undefined>>,
        },
        customProps: {
            type: Object as PropType<Record<string, any>>
        }
    },
    setup(props, ctx) {
        console.log(333444, props.block.slotName, props.slots);
        // 鼠标抬起，元素以鼠标位置为中心居中
        const blockEl = ref({} as HTMLDivElement);
        onMounted(() => {
            const block = props.block;
            if (block.adjustPosition) {
                const { offsetWidth, offsetHeight } = blockEl.value;
                block.left = block.left - offsetWidth / 2;
                block.top = block.top - offsetHeight / 2;
                block.width = offsetWidth;
                block.height = offsetHeight;
                block.adjustPosition = false; // 已经放到了画布，设置为false
            }
        });
        // 样式
        const blockStyle = computed(() => ({
            left: `${props.block.left}px`,
            top: `${props.block.top}px`,
            zIndex: props.block.zIndex
        }));
        const classes = computed(() => ([
            'visual-editor-block',
            {
                'visual-editor-block-focus': props.block.focus
            }
        ]))

        return () => {
            // 渲染
            const dragComp = props.config.componentMap[props.block.componentKey];
            const formData = props.formData as Record<string, any>;
            let renderContent: any;
            // 有自定义slot，则渲染slot的数据
            if (!!props.block.slotName && !!props.slots[props.block.slotName]) {
                renderContent = props.slots[props.block.slotName]!(); // 执行渲染函数renderFnWithContext，得到Vitual-DOM
            } else {
                renderContent = dragComp.render({
                    props: props.block.props || {},
                    size: props.block.hasResize ? {
                        width: props.block.width,
                        height: props.block.height
                    } : {},
                    model: Object.keys(dragComp.model || {}).reduce((prev, propName) => {
                        const modelName = props.block.model ? props.block.model[propName] : null;
                        prev[propName] = {
                            [propName === 'default' ? 'modelValue' : propName]: !!modelName ? formData[modelName] : null,
                            [propName === 'default' ? 'onUpdate:modelValue' : 'onChange']: (val: any) => {
                                !!modelName && (formData[modelName] = val);
                            }
                        }
                        return prev;
                    }, {} as Record<string, any>),
                    custom: (!props.block.slotName || !props.customProps) ? {} : (props.customProps[props.block.slotName] || {})
                });
            }

            // 当前拖拽block的宽高
            const { width, height } = dragComp.resize || {};
            return (
                <div class={classes.value} style={blockStyle.value} ref={blockEl}>
                    {renderContent}
                    {
                        !!props.block.focus
                        && (!!width || !!height)
                        // && <BlockResize block={props.block} component={dragComp} />
                    }
                </div>
            )
        }
    }
})

export default VisualEditorBlock;
