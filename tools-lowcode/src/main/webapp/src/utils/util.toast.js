import {ElMessage} from 'element-plus'

const toast = {}

toast.msg = function (msg = '', type, duration) {
    const data = {
        message: msg,
    }
    if (type) data.type = type
    if (duration) data.duration = duration
    ElMessage(data)
}

export default toast;