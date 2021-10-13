package com.y3tu.tools.kit.lang;

import com.y3tu.tools.kit.collection.ArrayUtil;
import com.y3tu.tools.kit.text.StrUtil;

import java.util.Scanner;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * 命令行（控制台）工具方法类
 *
 * @author Looly
 */
public class Console {

    /**
     * 同 System.out.println()方法，打印控制台日志<br>
     * 如果传入打印对象为{@link Throwable}对象，那么同时打印堆栈
     *
     * @param obj 要打印的对象
     */
    public static void log(Object obj) {
        if (obj instanceof Throwable) {
            final Throwable e = (Throwable) obj;
            log(e, e.getMessage());
        } else {
            log(null, "{}", obj);
        }
    }

    /**
     * 同 System.out.println()方法，打印控制台日志<br>
     * 如果传入打印对象为{@link Throwable}对象，那么同时打印堆栈
     *
     * @param obj1      第一个要打印的对象
     * @param otherObjs 其它要打印的对象
     */
    public static void log(Object obj1, Object... otherObjs) {
        if (ArrayUtil.isEmpty(otherObjs)) {
            log(obj1);
        } else {
            log(null, StrUtil.repeatAndJoin("{}", otherObjs.length + 1, StrUtil.SPACE), ArrayUtil.insert(otherObjs, 0, obj1));
        }
    }

    /**
     * 同 System.out.println()方法，打印控制台日志
     *
     * @param t        异常对象
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param values   值
     */
    public static void log(Throwable t, String template, Object... values) {
        out.println(StrUtil.format(template, values));
        if (null != t) {
            //noinspection CallToPrintStackTrace
            t.printStackTrace();
            out.flush();
        }
    }

    /**
     * 同 System.err.println()方法，打印控制台日志
     *
     * @param obj 要打印的对象
     */
    public static void error(Object obj) {
        if (obj instanceof Throwable) {
            Throwable e = (Throwable) obj;
            error(e, e.getMessage());
        } else {
            error("{}", obj);
        }
    }

    /**
     * 同 System.out.println()方法，打印控制台日志<br>
     * 如果传入打印对象为{@link Throwable}对象，那么同时打印堆栈
     *
     * @param obj1      第一个要打印的对象
     * @param otherObjs 其它要打印的对象
     */
    public static void error(Object obj1, Object... otherObjs) {
        if (ArrayUtil.isEmpty(otherObjs)) {
            error(obj1);
        } else {
            error(null, StrUtil.repeatAndJoin("{}", otherObjs.length + 1, StrUtil.SPACE), ArrayUtil.insert(otherObjs, 0, obj1));
        }
    }


    /**
     * 同 System.err.println()方法，打印控制台日志
     *
     * @param t        异常对象
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param values   值
     */
    public static void error(Throwable t, String template, Object... values) {
        err.println(StrUtil.format(template, values));
        if (null != t) {
            t.printStackTrace(err);
            err.flush();
        }
    }

    /**
     * 创建从控制台读取内容的{@link Scanner}
     *
     * @return {@link Scanner}
     */
    public static Scanner scanner() {
        return new Scanner(System.in);
    }

    /**
     * 读取用户输入的内容（在控制台敲回车前的内容）
     *
     * @return 用户输入的内容
     */
    public static String input() {
        return scanner().nextLine();
    }
}
