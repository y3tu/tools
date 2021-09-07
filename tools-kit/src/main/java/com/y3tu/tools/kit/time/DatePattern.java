package com.y3tu.tools.kit.time;

import java.time.format.DateTimeFormatter;

/**
 * 日期格式化类,提供常用的日期格式化对象
 *
 * @author y3tu
 */
public interface DatePattern {
    /**
     * 年月格式：yyyy-MM
     */
    String NORM_MONTH_PATTERN = "yyyy-MM";
    DateTimeFormatter NORM_MONTH_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(NORM_MONTH_PATTERN);

    /**
     * 简单年月格式：yyyyMM
     */
    String SIMPLE_MONTH_PATTERN = "yyyyMM";
    DateTimeFormatter SIMPLE_MONTH_FORMATTER = DateTimeFormatter.ofPattern(SIMPLE_MONTH_PATTERN);


    /**
     * 标准日期格式：yyyy-MM-dd
     */
    String NORM_DATE_PATTERN = "yyyy-MM-dd";
    DateTimeFormatter NORM_DATE_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(NORM_DATE_PATTERN);


    /**
     * 标准时间格式：HH:mm:ss
     */
    String NORM_TIME_PATTERN = "HH:mm:ss";
    DateTimeFormatter NORM_TIME_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(NORM_TIME_PATTERN);


    /**
     * 标准日期时间格式，精确到分：yyyy-MM-dd HH:mm
     */
    String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    DateTimeFormatter NORM_DATETIME_MINUTE_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(NORM_DATETIME_MINUTE_PATTERN);


    /**
     * 标准日期时间格式，精确到秒：yyyy-MM-dd HH:mm:ss
     */
    String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter NORM_DATETIME_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);

    /**
     * 标准日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss.SSS
     */
    String NORM_DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    DateTimeFormatter NORM_DATETIME_MS_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(NORM_DATETIME_MS_PATTERN);

    /**
     * ISO8601日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss,SSS
     */
    String ISO8601_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";
    DateTimeFormatter ISO8601_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(ISO8601_PATTERN);

    /**
     * 标准日期格式：yyyy年MM月dd日
     */
    String CHINESE_DATE_PATTERN = "yyyy年MM月dd日";
    DateTimeFormatter CHINESE_DATE_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(CHINESE_DATE_PATTERN);
    /**
     * 标准日期格式：yyyy年MM月dd日 HH时mm分ss秒
     */
    String CHINESE_DATE_TIME_PATTERN = "yyyy年MM月dd日HH时mm分ss秒";
    DateTimeFormatter CHINESE_DATE_TIME_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(CHINESE_DATE_TIME_PATTERN);

    /**
     * 标准日期格式：yyyyMMdd
     */
    String PURE_DATE_PATTERN = "yyyyMMdd";
    DateTimeFormatter PURE_DATE_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(PURE_DATE_PATTERN);


    /**
     * 标准日期格式：HHmmss
     */
    String PURE_TIME_PATTERN = "HHmmss";
    DateTimeFormatter PURE_TIME_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(PURE_TIME_PATTERN);

    /**
     * 标准日期格式：yyyyMMddHHmmss
     */
    String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";
    DateTimeFormatter PURE_DATETIME_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(PURE_DATETIME_PATTERN);

    /**
     * 标准日期格式：yyyyMMddHHmmssSSS
     */
    String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";
    DateTimeFormatter PURE_DATETIME_MS_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(PURE_DATETIME_MS_PATTERN);

    /**
     * HTTP头中日期时间格式：EEE, dd MMM yyyy HH:mm:ss z
     */
    String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    DateTimeFormatter HTTP_DATETIME_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(HTTP_DATETIME_PATTERN);

    /**
     * JDK中日期时间格式：EEE MMM dd HH:mm:ss zzz yyyy
     */
    String JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";
    DateTimeFormatter JDK_DATETIME_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(JDK_DATETIME_PATTERN);

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss
     */
    String UTC_SIMPLE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    DateTimeFormatter UTC_SIMPLE_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(UTC_SIMPLE_PATTERN);

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss.SSS
     */
    String UTC_SIMPLE_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    DateTimeFormatter UTC_SIMPLE_MS_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(UTC_SIMPLE_MS_PATTERN);

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss'Z'
     */
    String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    DateTimeFormatter UTC_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(UTC_PATTERN);

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ssZ
     */
    String UTC_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    DateTimeFormatter UTC_WITH_ZONE_OFFSET_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(UTC_WITH_ZONE_OFFSET_PATTERN);

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ssXXX
     */
    String UTC_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
    DateTimeFormatter UTC_WITH_XXX_OFFSET_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(UTC_WITH_XXX_OFFSET_PATTERN);

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    String UTC_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    DateTimeFormatter UTC_MS_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(UTC_MS_PATTERN);

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ssZ
     */
    String UTC_MS_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    DateTimeFormatter UTC_MS_WITH_ZONE_OFFSET_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(UTC_MS_WITH_ZONE_OFFSET_PATTERN);

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss.SSSXXX
     */
    String UTC_MS_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    DateTimeFormatter UTC_MS_WITH_XXX_OFFSET_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(UTC_MS_WITH_XXX_OFFSET_PATTERN);

}
