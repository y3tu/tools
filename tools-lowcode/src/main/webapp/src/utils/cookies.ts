// @ts-ignore
import {Cookies, CookieAttributes} from 'js-cookie'

const cookies = {
    /**
     * 存储cookies值
     * @param key 键
     * @param value 值
     * @param cookieSetting cookies设置
     */
    set(key: string = 'default', value: any, cookieSetting?: CookieAttributes): void {
        let currentCookieSetting = {
            expires: 1
        }
        Object.assign(currentCookieSetting, cookieSetting)
        Cookies.set(`tools-low-code-${key}`, value, currentCookieSetting)
    },

    /**
     * 获取cookies里存的值
     * @param key 键
     */
    get(key: string = 'default'): any {
        return Cookies.get(`tools-low-code--${key}`)
    },

    /**
     * 获取到cookies里面存的所有值
     */
    getAll(): any {
        return Cookies.get()
    },

    /**
     * 删除cookies里面的值
     * @param key 键
     */
    remove(key: string = 'default'): void {
        return Cookies.remove(`tools-low-code--${key}`)
    }
}

export default cookies
