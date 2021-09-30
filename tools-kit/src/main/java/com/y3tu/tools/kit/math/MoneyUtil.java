package com.y3tu.tools.kit.math;

import com.y3tu.tools.kit.text.StrUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * 货币工具
 *
 * @author y3tu
 */
public class MoneyUtil {

    public static final String DEFAULT_FORMAT = "0.00";
    public static final String PRETTY_FORMAT = "#,##0.00";

    /**
     * 人民币金额单位转换，分转换成元，取两位小数 例如：150 => 1.5
     *
     * @param num 金额分
     * @return 金额元
     */
    public static BigDecimal fen2yuan(Number num) {
        BigDecimal bigDecimal = NumberUtil.toBigDecimal(num);
        return NumberUtil.div(num, 100, 2, RoundingMode.HALF_UP);
    }

    /**
     * 人民币金额单位转换，分转换成元，取两位小数 例如：150 => 1.5
     *
     * @param num 金额分
     * @return 金额元
     */
    public static BigDecimal fen2yuan(String num) {
        return fen2yuan(NumberUtil.toBigDecimal(num));
    }

    /**
     * 人民币金额单位转换，元转换成分，例如：1 => 100
     *
     * @param y 元
     * @return 分
     */
    public static BigDecimal yuan2fen(String y) {
        return new BigDecimal(Math.round(new BigDecimal(y).multiply(new BigDecimal(100)).doubleValue()));
    }

    /**
     * 人民币金额单位转换，元转换成分，例如：1 => 100
     *
     * @param y 元
     * @return 分
     */
    public static BigDecimal yuan2fen(BigDecimal y) {
        if (y != null) {
            return yuan2fen(y.toString());
        } else {
            return new BigDecimal(0);
        }
    }

    /**
     * 格式化金额，当pattern为空时，pattern默认为#,##0.00
     *
     * @param number  金额
     * @param pattern 格式
     * @return 转换后的金额字符串
     */
    public static String format(Number number, String pattern) {
        DecimalFormat df = null;
        if (StrUtil.isEmpty(pattern)) {
            df = new DecimalFormat(PRETTY_FORMAT);
        } else {
            df = (DecimalFormat) DecimalFormat.getInstance();
            df.applyPattern(pattern);
        }

        return df.format(number);
    }

    /**
     * 按格式分析字符串，当pattern为空时，pattern默认为#,##0.00
     *
     * @param numberStr 金额
     * @param pattern   格式
     * @return 转换后的金额 {@link BigDecimal}
     */
    public static BigDecimal parseString(String numberStr, String pattern) throws ParseException {
        DecimalFormat df = null;
        if (StrUtil.isEmpty(pattern)) {
            df = new DecimalFormat(PRETTY_FORMAT);
        } else {
            df = (DecimalFormat) DecimalFormat.getInstance();
            df.applyPattern(pattern);
        }

        return new BigDecimal(df.parse(numberStr).doubleValue());
    }
}
