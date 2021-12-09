/* eslint-disable */
const validate = {
    /**
     * 判断是否为空
     */
    isEmpty(val) {
        if (val instanceof Array) {
            if (val.length === 0) return true
        } else if (val instanceof Object) {
            if (JSON.stringify(val) === '{}') return true
        } else {
            if (val === 'null' || val == null || val === 'undefined' || val === undefined || val === '') return true;
            return false
        }
        return false
    },

    /**
     * 判断是否不为空
     */
    isNotEmpty(val) {
        return !validate.isEmpty(val);
    },

    /**
     * @param {string} path
     * @returns {Boolean}
     */
    isExternal(path) {
        return /^(https?:|mailto:|tel:)/.test(path)
    },

    /**
     * 判断是否是URL
     */
    isURL(url) {
        const reg = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
        return reg.test(url)
    },

    /**
     * 判断是否为小写字母
     */
    isLowerCase(str) {
        const reg = /^[a-z]+$/;
        return reg.test(str)
    },

    /**
     * 判断是否为大写字母
     */
    isUpperCase(str) {
        const reg = /^[A-Z]+$/;
        return reg.test(str)
    },

    /**
     * 判断是否为邮件地址
     */
    isEmail(email) {
        const reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;;
        return reg.test(email)
    },

    /**
     * 判断是否电话号码
     */
    isMobile(mobile) {
        const reg = /^0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$/;
        return reg.test(mobile)
    },

    /**
     * @param {string} str
     * @returns {Boolean}
     */
    isString(str) {
        if (typeof str === 'string' || str instanceof String) {
            return true
        }
        return false
    },

    /**
     * @param {Array} arg
     * @returns {Boolean}
     */
    isArray(arg) {
        if (typeof Array.isArray === 'undefined') {
            return Object.prototype.toString.call(arg) === '[object Array]'
        }
        return Array.isArray(arg)
    },

    isIntegerGreaterThanZero(arg) {
        return /(^[1-9]\d*$)/.test(arg)
    }
}

export default validate