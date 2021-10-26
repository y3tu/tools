// 保存到本地JSON数据的key
import {CacheEnum} from '@/enums'

export const localKey = CacheEnum.PAGE_DATA_KEY

//初始化数据
export const initVisualData = () => {
    const localData = JSON.parse(sessionStorage.getItem(localKey) as string);


}