import axios from 'axios'
import util from '@/utils'

// 请求超时时间，10s
const requestTimeOut = 30 * 1000;
// 成功状态
const success = 200;

export const service = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API+'',
    timeout: requestTimeOut,
    // 跨域请求，允许保存cookie
    withCredentials: true,
    responseType: 'json',
    validateStatus(status) {
        return status === success
    }
});

//请求拦截
service.interceptors.request.use(
    config => {
        let _config = config;
        try {
            //请求拦截处理
        } catch (e) {
            console.error(e)
        }
        return _config
    },
    error => {
        console.log(error);
        return Promise.reject(error)
    }
);

//响应拦截
service.interceptors.response.use(response => {
    if (response.data !== undefined && response.data !== null && response.data !== '') {
        let status = response.data.status;
        if (status !== undefined && status === "ERROR") {
            util.modal.msgError(response.data.message)
            throw new Error(response.data.message);
        }

        if (status !== undefined && status === 'WARN') {
            util.modal.msgWarning(response.data.message)
            throw new Error(response.data.message);
        }
    }
    return response.data;
}, (error) => {
    if (error) {
        if (error.toString().indexOf('Error: timeout') !== -1) {

            util.modal.msgError('网络请求超时')
            return Promise.reject(error)
        }
        if (error.toString().indexOf('Error: Network Error') !== -1) {

            util.modal.msgError('网络请求错误')
            return Promise.reject(error)
        }

        if (error.toString().indexOf('503') !== -1) {

            util.modal.msgError('服务暂时不可用，请稍后再试!')
            return Promise.reject(error)
        }

        if (error.response.data instanceof Blob) {
            return Promise.reject(error)
        }

        const errorMessage = error.response.data === null ? '系统内部异常，请联系网站管理员' : error.response.data.message;
        switch (error.response.status) {
            case 404:
                util.modal.msgError('很抱歉，资源未找到!')
                break;
            case 403:
                util.modal.msgError('很抱歉，您暂无该操作权限!')
                break;
            case 401:
                util.modal.msgError('很抱歉，您没有权限!')
                break;
            default:
                util.modal.msgError(errorMessage)
                break
        }
    }
    return Promise.reject(error)
});


export default service