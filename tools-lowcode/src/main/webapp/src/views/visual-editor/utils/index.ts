/**
 * 部署应用时的基本URL
 */
// @ts-ignore
export const BASE_URL = import.meta.env.BASE_URL


/**
 * 生成UUID
 * @param {boolean} [noSymbol=false] 是否需要 - 分隔符
 * @returns {string}
 */
export function generateUUID(noSymbol = false) {
    let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = (Math.random() * 16) | 0,
            v = c == 'x' ? r : (r & 0x3) | 0x8
        return v.toString(16)
    })
    if (noSymbol) {
        uuid = uuid.replace(/-/g, '')
    }
    return uuid
}