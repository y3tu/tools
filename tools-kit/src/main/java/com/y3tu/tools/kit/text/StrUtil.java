package com.y3tu.tools.kit.text;


/**
 * 字符串工具类
 *
 * @author y3tu
 */
public class StrUtil implements StrPool {

    public static final int INDEX_NOT_FOUND = -1;

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
     * @param str         字符串
     * @param fromIndex   开始位置(包括) 起始位0
     * @param searchStr   被查找的字符串
     * @param replacement 被替换的字符串
     * @param ignoreCase  是否忽略大小写
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
        final int searchStrLength = searchStr.length();
        if (fromIndex > strLength) {
            //如果开始位置大于字符串长度，直接返回原字符串
            return String.valueOf(str);
        } else if (fromIndex < 0) {
            //从位置0开始
            fromIndex = 0;
        }
        StringBuilder result = new StringBuilder(strLength + 16);
        if (0 != fromIndex) {
            result.append(str.subSequence(0, fromIndex));
        }
        int preIndex = fromIndex;
        int index;
        while ((index = indexOf(str, searchStr, preIndex, ignoreCase)) > -1) {
            result.append(str.subSequence(preIndex, index));
            result.append(replacement);
            preIndex = index + searchStrLength;
        }

        if (preIndex < strLength) {
            // 结尾部分
            result.append(str.subSequence(preIndex, strLength));
        }
        return result.toString();

    }

    /**
     * 指定范围内查找字符串
     *
     * @param str        字符串
     * @param searchStr  需要查找位置的字符串
     * @param fromIndex  起始位置 如果小于0，从0开始查找
     * @param ignoreCase 是否忽略大小写
     * @return 位置
     */
    public static int indexOf(final CharSequence str, CharSequence searchStr, int fromIndex, boolean ignoreCase) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        final int endLimit = str.length() - searchStr.length() + 1;
        if (fromIndex > endLimit) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return fromIndex;
        }
        if (!ignoreCase) {
            // 不忽略大小写调用JDK方法
            return str.toString().indexOf(searchStr.toString(), fromIndex);
        }
        for (int i = fromIndex; i < endLimit; i++) {

            if (str.toString().regionMatches(true, i, searchStr.toString(), 0, searchStr.length())) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 重复某个字符串
     *
     * @param str   被重复的字符
     * @param count 重复的数目
     * @return 重复字符字符串
     */
    public static String repeat(CharSequence str, int count) {
        if (null == str) {
            return null;
        }
        if (count <= 0 || str.length() == 0) {
            return "";
        }
        if (count == 1) {
            return str.toString();
        }

        // 检查
        final int len = str.length();
        final long longSize = (long) len * (long) count;
        final int size = (int) longSize;
        if (size != longSize) {
            throw new ArrayIndexOutOfBoundsException("Required String length is too large: " + longSize);
        }

        final char[] array = new char[size];
        str.toString().getChars(0, len, array, 0);
        int n;
        for (n = len; n < size - n; n <<= 1) {
            // n <<= 1相当于n *2
            System.arraycopy(array, 0, array, n, n);
        }
        System.arraycopy(array, 0, array, n, size - n);
        return new String(array);
    }


}
