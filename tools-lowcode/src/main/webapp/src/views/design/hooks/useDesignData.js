import {inject} from "vue";

import {state as baseState, method as baseMethod} from './base'
import {method as layerMethod} from './layer'

//初始化编辑器数据
export const initDesignData = () => {
    return {
        ...baseState,
        ...baseMethod,
        ...layerMethod
    }
}

export const injectKey = 'designData';

export const useDesignData = () => inject(injectKey)
