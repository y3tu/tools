import axios from 'axios'
import util from '@/utils'

// 请求超时时间，10s
const requestTimeOut = 30 * 1000;
// 成功状态
const success = 200;
// 提示信息显示时长 3秒
const messageDuration = 3 * 1000;

export const service = axios.create({
    baseURL: import.meta.env.BASE_URL,
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
            util.toast.error(response.data.message,  5 * 1000)
            throw new Error(response.data.message);
        }

        if (status !== undefined && status === 'WARN') {
            util.toast.warning(response.data.message, 5 * 1000)
            throw new Error(response.data.message);
        }
    }
    return response.data;
}, (error) => {
    if (error) {
        if (error.toString().indexOf('Error: timeout') !== -1) {

            util.toast.error('网络请求超时', 5 * 1000)
            return Promise.reject(error)
        }
        if (error.toString().indexOf('Error: Network Error') !== -1) {

            util.toast.error('网络请求错误', 5 * 1000)
            return Promise.reject(error)
        }

        if (error.toString().indexOf('503') !== -1) {

            util.toast.error('服务暂时不可用，请稍后再试!', 5 * 1000)
            return Promise.reject(error)
        }

        if (error.response.data instanceof Blob) {
            return Promise.reject(error)
        }

        const errorMessage = error.response.data === null ? '系统内部异常，请联系网站管理员' : error.response.data.message;
        switch (error.response.status) {
            case 404:
                util.toast.error('很抱歉，资源未找到!', 5 * 1000)
                break;
            case 403:
                util.toast.error('很抱歉，您暂无该操作权限!', 5 * 1000)
                break;
            case 401:
                util.toast.error('很抱歉，您没有权限!', 5 * 1000)
                break;
            default:
                util.toast.error(errorMessage, messageDuration)
                break
        }
    }
    return Promise.reject(error)
});


export default service