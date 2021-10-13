package com.y3tu.tools.kit.text;


import com.y3tu.tools.kit.collection.ArrayUtil;
import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.lang.func.Filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


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
     * 当给定字符串为null时，转换为Empty
     *
     * @param str 被转换的字符串
     * @return 转换后的字符串
     */
    public static String nullToEmpty(CharSequence str) {
        return nullToDefault(str, EMPTY);
    }

    /**
     * 如果字符串是null，则返回指定默认字符串，否则返回字符串本身。
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或指定的默认字符串
     */
    public static String nullToDefault(CharSequence str, String defaultStr) {
        return (str == null) ? defaultStr : str.toString();
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

    /**
     * 去掉字符串指定前缀
     *
     * @param str        字符串
     * @param prefix     前缀
     * @param ignoreCase 是否忽略大小写
     * @return 切掉后的字符串，若前缀不是 prefix， 返回原字符串
     */
    public static String removePrefix(CharSequence str, CharSequence prefix, boolean ignoreCase) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return String.valueOf(str);
        }
        final String str2 = str.toString();
        if (ignoreCase) {
            //忽略大小写
            if (str2.toLowerCase().startsWith(prefix.toString().toLowerCase())) {
                // 截取后半段
                return str2.substring(prefix.length());
            }
        } else {
            if (str2.startsWith(prefix.toString())) {
                return str2.substring(prefix.length());
            }
        }
        return str2;
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串
     *
     * @param str     字符串
     * @param testStr 检查的字符
     * @return 是否包含
     */
    public static boolean containsAny(CharSequence str, CharSequence... testStr) {
        if (isEmpty(str) || ArrayUtil.isEmpty(testStr)) {
            return false;
        }
        for (CharSequence checkStr : testStr) {
            if (str.toString().contains(checkStr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串
     * 忽略大小写
     *
     * @param str     字符串
     * @param testStr 检查的字符
     * @return 是否包含
     */
    public static boolean containsAnyIgnoreCase(CharSequence str, CharSequence... testStr) {
        if (isEmpty(str) || ArrayUtil.isEmpty(testStr)) {
            return false;
        }
        for (CharSequence checkStr : testStr) {
            if (str.toString().toLowerCase().contains(checkStr.toString().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 清理空白字符
     *
     * @param str 被清理的字符串
     * @return 清理后的字符串
     */
    public static String cleanBlank(CharSequence str) {
        return filter(str, c -> !CharUtil.isBlankChar(c));
    }

    /**
     * 过滤字符串
     *
     * @param str    字符串
     * @param filter 过滤器，{@link Filter#accept(Object)}返回为{@code true}的保留字符
     * @return 过滤后的字符串
     */
    public static String filter(CharSequence str, final Filter<Character> filter) {
        if (str == null || filter == null) {
            return str.toString();
        }

        int len = str.length();
        final StringBuilder sb = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (filter.accept(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 根据正则获取到字符串匹配正则的次数
     *
     * @param str     字符串
     * @param pattern 正则
     * @return 正则匹配次数
     */
    public static int getPatternMatchCount(CharSequence str, Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * 处理Gizp压缩的数据.
     *
     * @param str
     * @return
     */
    public static String conventFromGzip(String str, String charset) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in;
            GZIPInputStream gunzip = null;

            in = new ByteArrayInputStream(str.getBytes(charset));
            gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString();
        } catch (Exception e) {
            throw new ToolException(e);
        }
    }

    /**
     * 将对象转为字符串
     * <pre>
     * 	 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 	 2、对象数组会调用Arrays.toString方法
     * </pre>
     *
     * @param obj     对象
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            if (null == charset) {
                return new String((byte[]) obj);
            }
            return new String((byte[]) obj, charset);
        } else if (obj instanceof Byte[]) {
            Byte[] data = (Byte[]) obj;
            byte[] bytes = new byte[data.length];
            Byte dataByte;
            for (int i = 0; i < data.length; i++) {
                dataByte = data[i];
                bytes[i] = (null == dataByte) ? -1 : dataByte;
            }
            return str(bytes, charset);
        } else if (obj instanceof ByteBuffer) {
            if (null == charset) {
                charset = Charset.defaultCharset();
            }
            return charset.decode((ByteBuffer) obj).toString();
        } else if (ArrayUtil.isArray(obj)) {
            return ArrayUtil.toString(obj);
        }

        return obj.toString();
    }

    /**
     * 将对象转为字符串<br>
     *
     * <pre>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 2、对象数组会调用Arrays.toString方法
     * </pre>
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String utf8Str(Object obj) {
        return str(obj, StandardCharsets.UTF_8);
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示，如果模板为null，返回"null"
     * @param params   参数值
     * @return 格式化后的文本，如果模板为null，返回"null"
     */
    public static String format(CharSequence template, Object... params) {
        if (null == template) {
            return NULL;
        }
        if (ArrayUtil.isEmpty(params) || isBlank(template)) {
            return template.toString();
        }
        return StrFormatter.format(template.toString(), params);
    }

    /**
     * 重复某个字符串并通过分界符连接
     *
     * <pre>
     * StrUtil.repeatAndJoin("?", 5, ",")   = "?,?,?,?,?"
     * StrUtil.repeatAndJoin("?", 0, ",")   = ""
     * StrUtil.repeatAndJoin("?", 5, null) = "?????"
     * </pre>
     *
     * @param str         被重复的字符串
     * @param count       数量
     * @param conjunction 分界符
     * @return 连接后的字符串
     */
    public static String repeatAndJoin(CharSequence str, int count, CharSequence conjunction) {
        if (count <= 0) {
            return EMPTY;
        }
        final StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        while (count-- > 0) {
            if (isFirst) {
                isFirst = false;
            } else if (isNotEmpty(conjunction)) {
                builder.append(conjunction);
            }
            builder.append(str);
        }
        return builder.toString();
    }
}
