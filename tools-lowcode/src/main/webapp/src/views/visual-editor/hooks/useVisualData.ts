import {CacheEnum} from '@/enums'
// 注入jsonData的key
export const injectKey: InjectionKey<ReturnType<typeof initVisualData>> = Symbol()

// 保存到本地JSON数据的key
export const localKey = CacheEnum.PAGE_DATA_KEY

export const initVisualData = () => {
    const localData = JSON.parse(sessionStorage.getItem(localKey) as string)
    return {}
}