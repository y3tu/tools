package com.y3tu.tools.kit.lang;

import com.y3tu.tools.kit.text.StrUtil;

import static java.lang.System.out;

/**
 * 命令行（控制台）工具方法类
 *
 * @author Looly
 */
public class Console {
    /**
     * 打印进度条
     *
     * @param showChar 进度条提示字符，例如“#”
     * @param totalLen 总长度
     * @param rate     总长度所占比取值0~1
     */
    public static void printProgress(String showChar, int totalLen, double rate) {
        Assert.isTrue(rate >= 0 && rate <= 1, "Rate must between 0 and 1 (both include)");
        printProgress(showChar, (int) (totalLen * rate));
    }

    /**
     * 打印进度条
     *
     * @param showChar 进度条提示字符，例如“#”
     * @param len      打印长度
     */
    public static void printProgress(String showChar, int len) {
        print("%s%s", StrUtil.CR, StrUtil.repeat(showChar, len));
    }

    /**
     * 同 System.out.println()方法，打印控制台日志
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param values   值
     */
    public static void print(String template, Object... values) {
        String str = String.format(template, values);
        out.print(str);
    }

}
