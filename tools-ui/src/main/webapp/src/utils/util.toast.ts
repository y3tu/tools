import {ElMessage} from 'element-plus'

interface Toast {
    message: (msg: string, type: "info" | 'success' | 'warning' | 'error', duration: number) => void
}

const toast: Toast = {
    message: (msg: string, type: "info" | 'success' | 'warning' | 'error', duration: number): void => {
        ElMessage({
            message: msg,
            type: type,
            duration: duration
        })
    }
}

export default toast;