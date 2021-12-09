import {service, download} from '@/plugin/axios'

/**
 * 获取报表生成列表
 */
export function page(params) {
    return service({
        url: 'tools-lowcode/reportDownload/page',
        method: 'post',
        data: params
    })
}

export function handleAgain(id) {
    return service({
        url: `tools-lowcode/reportDownload/handleAgain/${id}`,
        method: 'get',
    })
}

export function downloadFile(id, fileName) {
    download(`tools-lowcode/reportDownload/download/${id}`, fileName);
}







