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
    }
}

export default common