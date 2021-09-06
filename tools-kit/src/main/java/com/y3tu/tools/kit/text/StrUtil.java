package com.y3tu.tools.kit.text;

/**
 * 字符串工具类
 *
 * @author y3tu
 */
public class StrUtil {
    /**
     * 字符串是否为空
     * 空的定义:null;"";
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 字符串是否为非空
     *
     * @param str 被检测的字符串
     * @return 是否为非空
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * 字符串是否为空白
     * 空白的定义:null;"";空格、全角空格、制表符、换行符，等不可见字符;
     *
     * @param str 被检测的字符串
     * @return 是否为空白
     */
    public static boolean isBlank(CharSequence str) {
        int length;
        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            //只要有一个非空字符即为非空字符串
            if (!CharUtil.isBlankChar(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串是否为非空白
     *
     * @param str 被检测的字符串
     * @return 是否非空白
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 替换字符串中的指定字符串
     *
     * @param str 字符串
     * @param fromIndex 开始位置(包括)
     * @param searchStr 被查找的字符串
     * @param replacement 被替换的字符串
     * @param ignoreCase 是否忽略大小写
     * @return 替换后的字符串
     */
    public static String replace(CharSequence str, int fromIndex, CharSequence searchStr, CharSequence replacement, boolean ignoreCase) {
        if (isEmpty(str) || isEmpty(searchStr)) {
            //如果字符串为空或者被查询找字符串为空直接返回原字符串
            return String.valueOf(str);
        }
        if (null == replacement) {
            replacement = "";
        }
        final int strLength = str.length();
        return null;
    }
}
