const common = {
    /**
     * 获取到随机整数
     * @param min 最小值
     * @param max 最大值
     * @returns {number}
     */
    getRandomInt(min: number, max: number) {
        min = Math.ceil(min);
        max = Math.floor(max);
        //含最大值，含最小值
        return Math.floor(Math.random() * (max - min + 1)) + min;
    },

    /**
     * 改变网站标题
     * @param title 标题
     */
    changeTitle(title: string) {
        document.title = title;
    },

    /**
     * 获取dom元素
     * @param selector
     */
    $(selector: string) {
        return document.querySelector(selector)
    },
    /**
     * 深拷贝
     * @param target
     */
    deepCopy(target: any) {
        if (typeof target == 'object') {
            const result = Array.isArray(target) ? [] : {}
            for (const key in target) {
                if (typeof target[key] == 'object') {
                    // @ts-ignore
                    result[key] = this.deepCopy(target[key])
                } else {
                    // @ts-ignore
                    result[key] = target[key]
                }
            }
            return result
        }
        return target
    },
    /**
     * 生成uuid方法
     * @returns {string}
     */
    createUUID() {
        let d = new Date().getTime();
        if (window.performance && typeof window.performance.now === "function") {
            d += performance.now(); //use high-precision timer if available
        }
        let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            let r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
        });
        return uuid;
    }
}

export default common