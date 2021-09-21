package com.y3tu.tools.kit.lang;

import com.y3tu.tools.kit.regex.RegexUtil;

import java.util.regex.Pattern;

/**
 * 验证器
 *
 * @author y3tu
 */
public class Validator {

    /**
     * 通过正则表达式验证
     *
     * @param pattern 正则模式
     * @param value   值
     * @return 是否匹配正则
     */
    public static boolean isMatchRegex(Pattern pattern, CharSequence value) {
        return RegexUtil.isMatch(pattern, value);
    }

    /**
     * 通过正则表达式验证
     *
     * @param regex 正则
     * @param value 值
     * @return 是否匹配正则
     */
    public static boolean isMatchRegex(String regex, CharSequence value) {
        return RegexUtil.isMatch(regex, value);
    }


}
