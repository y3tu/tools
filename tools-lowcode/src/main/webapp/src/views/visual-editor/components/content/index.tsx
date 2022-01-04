import {defineComponent,ref,reactive,computed} from 'vue'
import {useVisualData} from "@/views/visual-editor/hooks/useVisualData";
import {VisualEventBusProvider,VisualEditorComponent,createNewBlock,VisualEditorBlockData,VisualEditorMarkLines} from "@/views/visual-editor/visual-editor.utils";
import './index.scss';
import VisualEditorBlock from "@/views/visual-editor/components/block";
import {visualConfig} from "@/views/visual-editor/visual.config";


export const VisualEditorContent= defineComponent({
    components: {
        VisualEditorBlock
    },
    setup(props, ctx) {

        let { dataModel,focusData,visualConfig,updateBlocks,clearFocus,resetSelectIndex} = useVisualData()

        // ref获取画布元素
        const containerRef = ref({} as HTMLDivElement);
        // 画布样式

        const canvasStyle = computed(() => ({
            width: `${dataModel.container.width}px`,
            height: `${dataModel.container.height}px`
        }))

        /*** 事件监听，左侧菜单拖拽组件时，要求给容器绑定事件 ***/
        const { eventBus } = VisualEventBusProvider.inject();
        let dragComponent = null as null | VisualEditorComponent;

        const containerHandler = {
            // 鼠标设置为可放置状态
            dragenter: (e: DragEvent) => { e.dataTransfer!.dropEffect = 'move'; },
            dragover: (e: DragEvent) => { e.preventDefault(); },
            // 鼠标设置为不可放置状态
            dragleave: (e: DragEvent) => { e.dataTransfer!.dropEffect = 'none'; },
            drop: (e: DragEvent) => {
                // 单独拷贝一份，不能直接用，避免污染源数据
                let blocks = [...dataModel.blocks || []];
                // 新增当前拖拽元素
                blocks.push(createNewBlock({
                    component: dragComponent!,
                    top: e.offsetY,
                    left: e.offsetX
                }));
                //同步容器中展示的最新blocks
                updateBlocks(blocks);
            }
        };

        // 绑定事件监听
        eventBus.on((dragComp: VisualEditorComponent) => {
            dragComponent = dragComp; // 当前拖拽的组件
            containerRef.value.addEventListener('dragenter', containerHandler.dragenter);
            containerRef.value.addEventListener('dragover', containerHandler.dragover);
            containerRef.value.addEventListener('dragleave', containerHandler.dragleave);
            containerRef.value.addEventListener('drop', containerHandler.drop);
        })

        // 工作区，聚焦
        const focusHandler = {
            container: {
                onMousedown: (e: MouseEvent) => {
                    // 点击的不是block元素，则忽略
                    if (e.currentTarget !== e.target) return;
                    e.preventDefault();
                    // 如果是预览态，点击不生效
                    if (state.preview) return;
                    // 清除所有选中的block元素
                    if (!e.shiftKey) {
                        clearFocus();
                        resetSelectIndex(-1);
                    }
                }
            },
            block: {
                onMousedown: (e: MouseEvent, block: VisualEditorBlockData, index: number) => {
                    // 如果是预览态，点击不生效
                    if (state.preview) return;
                    // 按下shift键，则切换当前block的选中态，否则清除除当前block外其他所有block的选中态
                    if (e.shiftKey) {
                        block.focus = !block.focus;
                    } else {
                        if (!block.focus) { // 多选拖拽
                            block.focus = true;
                            clearFocus(block);
                        }
                    }
                    // 设置当前操作的block
                    resetSelectIndex(index);
                    // 记录当前位置
                    blockDraggier.mousedown(e);
                }
            }
        }

        // 工作区拖拽
        const blockDraggier = (() => {
            const mark = reactive({
                x: null as null | number,
                y: null as null | number
            })

            let dragState = {
                startX: 0,
                startY: 0,
                startLeft: 0,
                startTop: 0,
                startPos: [] as { left: number, top: number }[],
                dragging: false, // 是否正在拖拽（move的时候才知道）
                markLines: {} as VisualEditorMarkLines // {x:[{left:"", markX: ''}], y:[{top: "", markY: ''}]} 拖拽元素到达未聚焦元素的top时，引导线显示，引导线坐标为markY
            }
            const mousedown = (e: MouseEvent) => {
                dragState = {
                    startX: e.clientX, // 鼠标按下的开始位置
                    startY: e.clientY,
                    startLeft: state.selectBlock!.left, // 拖拽元素的初始位置
                    startTop: state.selectBlock!.top,
                    startPos: focusData.value.focus.map(({ top, left }) => ({ top, left })),
                    dragging: false, // 是否正在拖拽
                    markLines: (() => {
                        const { focus, unFocus } = focusData.value;
                        const { top, left, width, height } = state.selectBlock!;
                        let lines: VisualEditorMarkLines = { x: [], y: [] };
                        [...unFocus, { // 画布内元素对齐、画布对齐
                            top: 0,
                            left: 0,
                            width: dataModel.container.width,
                            height: dataModel.container.height
                        }].forEach(block => {
                            const { top: t, left: l, width: w, height: h } = block;
                            lines.y.push({ top: t, markY: t }); // 拖拽元素顶部对齐其他元素顶部
                            lines.y.push({ top: t + h, markY: t + h }); // 顶部对齐底部
                            lines.y.push({ top: t + h / 2 - height / 2, markY: t + h / 2 }); // 中间对齐（垂直）
                            lines.y.push({ top: t - height, markY: t }); // 底部对齐顶部
                            lines.y.push({ top: t + h - height, markY: t + h }); // 底部对齐底部

                            lines.x.push({ left: l, markX: l }); // 左侧对齐左侧
                            lines.x.push({ left: l + w, markX: l + w }); // 左侧对齐右侧
                            lines.x.push({ left: l + w / 2 - width / 2, markX: l + w / 2 }); // 中间对齐（水平）
                            lines.x.push({ left: l - width, markX: l }); // 右侧对齐左侧
                            lines.x.push({ left: l + w - width, markX: l + w }); // 右侧对齐右侧
                        })
                        return lines;
                    })()
                }
                document.addEventListener('mousemove', mousemove);
                document.addEventListener('mouseup', mouseup);
            }
            const mousemove = (e: MouseEvent) => {
                // 修改拖拽标识
                if (!dragState.dragging) {
                    dragState.dragging = true;
                    //dragStart.emit();
                }

                // 获取当前鼠标的位置，以及鼠标按下时的初始位置，以及拖拽元素的offsetLeft和offsetTop
                let { clientX: moveX, clientY: moveY } = e;
                const { startX, startY, startLeft, startTop } = dragState; // 鼠标按下开始移动时候记录的点击位置

                // 拖拽过程中如果按下shift键，则只允许一个方向移动
                if (e.shiftKey) {
                    if (Math.abs(moveX - startX) > Math.abs(moveY - startY)) {
                        moveY = startY;
                    } else {
                        moveX = startX;
                    }
                }
                // 当前拖拽元素距离左边和上面的距离（当前位置）
                const currentLeft = startLeft + moveX - startX;
                const currentTop = startTop + moveY - startY;

                const currentMark = {
                    x: null as null | number,
                    y: null as null | number
                };
                for (let i = 0; i < dragState.markLines.y.length; i++) {
                    const { top, markY } = dragState.markLines.y[i];
                    if (Math.abs(top - currentTop) < 5) {
                        moveY = top + startY - startTop;
                        currentMark.y = markY;
                        break;
                    }
                }
                for (let i = 0; i < dragState.markLines.x.length; i++) {
                    const { left, markX } = dragState.markLines.x[i];
                    if (Math.abs(left - currentLeft) < 5) {
                        moveX = left + startX - startLeft;
                        currentMark.x = markX;
                        break;
                    }
                }
                mark.x = currentMark.x;
                mark.y = currentMark.y;

                // 修改拖拽元素的位置
                const durX = moveX - startX;
                const durY = moveY - startY;
                focusData.value.focus.forEach((block, index) => {
                    block.top = dragState.startPos[index].top + durY;
                    block.left = dragState.startPos[index].left + durX;
                })
            }
            const mouseup = (e: MouseEvent) => {
                document.removeEventListener('mousemove', mousemove);
                document.removeEventListener('mouseup', mouseup);
                // 鼠标抬起，移除引导线
                mark.x = null;
                mark.y = null;
                // 鼠标抬起，如果是拖拽状态，则触发更新
                if (dragState.dragging) {
                   // dragEnd.emit();
                }
            }
            return { mark, mousedown }
        })();

        // 其他事件处理，比如右键
        const handler = {
            onContextmenuBlock: (e: MouseEvent, block: VisualEditorBlockData) => {
                // 如果是预览态，点击不生效
                if (state.preview) return;
                e.preventDefault();
                e.stopPropagation();

            }
        }

        return () => <>
            <div class="visual-editor-content">
                <div class="visual-editor-canvas"
                     style={canvasStyle.value}
                     ref={containerRef}
                     {...focusHandler.container}
                >
                    {
                        (dataModel.blocks || []).map((block, index) => (
                            <visual-editor-block
                                key={index}
                                block={block}
                                config={visualConfig}
                                {...{
                                    onMousedown: (e: MouseEvent) => focusHandler.block.onMousedown(e, block, index),
                                    onContextmenu: (e: MouseEvent) => handler.onContextmenuBlock(e, block)
                                }}
                            />
                        ))
                    }
                    {blockDraggier.mark.y !== null && (
                        <div class="visual-editor-mark-line-y" style={{ top: `${blockDraggier.mark.y}px` }}></div>
                    )}
                    {blockDraggier.mark.x !== null && (
                        <div class="visual-editor-mark-line-x" style={{ left: `${blockDraggier.mark.x}px` }}></div>
                    )}
                </div>
            </div>
        </>
    }
})