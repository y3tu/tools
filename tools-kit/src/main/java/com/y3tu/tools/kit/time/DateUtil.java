package com.y3tu.tools.kit.time;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author y3tu
 */
public class DateUtil implements DatePattern {

    /**
     * 当前时间，默认时区
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Date转换LocalDateTime
     *
     * @param date Date时间对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime dateToLdt(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime LocalDateTime对象
     * @return Date时间对象
     */
    public static Date ldtToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 时间字符串转换成时间
     *
     * @param dateStr 时间字符串
     * @param pattern 时间格式
     * @return 时间
     */
    public static Date strToDate(String dateStr, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, dateTimeFormatter);
        return ldtToDate(localDateTime);
    }

    /**
     * 时间转换成字符串
     *
     * @param date    时间
     * @param pattern 时间格式
     * @return 时间字符串
     */
    public static String dateToStr(Date date, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * 格式化时间格式
     *
     * @param dateStr     时间字符串
     * @param fromPattern 转换前的时间格式
     * @param toPattern   转换后的时间格式
     * @return 格式化后的时间字符串
     */
    public static String strToStr(String dateStr, String fromPattern, String toPattern) {
        Date date = strToDate(dateStr, fromPattern);
        return dateToStr(date, toPattern);
    }

    /**
     * 时间字符串转换为LocalDateTime对象
     *
     * @param dateStr 时间字符串
     * @param pattern 时间格式
     * @return LocalDateTime对象
     */
    public static LocalDateTime strToLdt(String dateStr, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateStr, dateTimeFormatter);
    }

    /**
     * LocalDateTime对象转换成字符串
     *
     * @param localDateTime LocalDateTime对象
     * @param pattern       时间格式
     * @return 时间字符串
     */
    public static String LdtToStr(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * 获取两个时间之间的差值
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param dateUnit  差值的单位 年月日时分秒毫秒
     * @return
     */
    public static long between(LocalDateTime startTime, LocalDateTime endTime, DateUnit dateUnit) {
        Duration duration = Duration.between(startTime, endTime);
        Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));
        ChronoUnit chronoUnit = DateUnit.toChronoUnit(dateUnit);
        switch (chronoUnit) {
            case YEARS:
                return period.getYears();
            case MONTHS:
                return period.getMonths();
            case DAYS:
                return duration.toDays();
            case HOURS:
                return duration.toHours();
            case MINUTES:
                return duration.toMinutes();
            case SECONDS:
                return duration.getSeconds();
            case MILLIS:
                return duration.toMillis();
            default:
                return duration.toNanos();
        }
    }

    /**
     * 日期偏移 根据单元不同加不同值（偏移会修改传入的对象）
     *
     * @param time     时间
     * @param number   偏移量，正数为向后偏移，负数为向前偏移
     * @param dateUnit 偏移单位，不能为null
     * @return 偏移后的日期时间
     */
    public static Date offset(Date time, long number, DateUnit dateUnit) {
        LocalDateTime localDateTime = dateToLdt(time);
        LocalDateTime offsetTime = offset(localDateTime, number, dateUnit);
        return ldtToDate(offsetTime);
    }

    /**
     * 日期偏移,根据单元不同加不同值（偏移会修改传入的对象）
     *
     * @param time     {@link LocalDateTime}
     * @param number   偏移量，正数为向后偏移，负数为向前偏移
     * @param dateUnit 偏移单位，见{@link ChronoUnit}，不能为null
     * @return 偏移后的日期时间
     */
    public static LocalDateTime offset(LocalDateTime time, long number, DateUnit dateUnit) {
        if (null == time) {
            return null;
        }
        return time.plus(number, DateUnit.toChronoUnit(dateUnit));
    }

    /**
     * 计时器<br>
     * 计算某个过程花费的时间，精确到毫秒
     *
     * @return Timer
     */
    public static TimeInterval timer() {
        return new TimeInterval();
    }

    /**
     * 计时器<br>
     * 计算某个过程花费的时间，精确到毫秒
     *
     * @param isNano 是否使用纳秒计数，false则使用毫秒
     * @return Timer
     */
    public static TimeInterval timer(boolean isNano) {
        return new TimeInterval(isNano);
    }

    /**
     * 一天的开始时间 例如：2020-02-02 00:00:00,000
     *
     * @param time 日期时间
     * @return 一天的开始时间
     */
    public static LocalDateTime beginOfDay(LocalDateTime time) {
        return time.with(LocalTime.MIN);
    }

    /**
     * 一天的结束时间 例如：2020-02-02 23:59:59,999
     *
     * @param time 日期时间
     * @return 一天的结束时间
     */
    public static LocalDateTime endOfDay(LocalDateTime time) {
        return time.with(LocalTime.MAX);
    }

    /**
     * 某个月的第一天 例如：2020-02-02 00:00:00,000
     *
     * @param time 日期时间
     * @return 某个月的第一天
     */
    public static LocalDateTime beginOfMonth(LocalDateTime time) {
        return beginOfDay(time.with(TemporalAdjusters.firstDayOfMonth()));
    }

    /**
     * 某个月的最后一天 2020-02-02 23:59:59,999
     *
     * @param time 日期时间
     * @return 某个月的最后一天
     */
    public static LocalDateTime endOfMonth(LocalDateTime time) {
        return endOfDay(time.with(TemporalAdjusters.lastDayOfMonth()));
    }

    /**
     * 打印用户友好的，与当前时间相比的时间差，如刚刚，5分钟前，今天XXX，昨天XXX
     *
     * @param timeMillis 传入时间
     * @return 友好时间输出
     */
    public static String formatFriendlyTimeSpanByNow(long timeMillis) {
        //当前时间
        long now = System.currentTimeMillis();
        //时间跨度
        long span = now - timeMillis;
        if (span < 0) {
            //如果传入时间比当前时间大，直接返回传入时间
            Date date = new Date(timeMillis);
            return DateUtil.dateToStr(date, DateUtil.NORM_DATETIME_PATTERN);
        }
        if (span < DateUnit.SECOND.getMillis()) {
            return "刚刚";
        } else if (span < DateUnit.MINUTE.getMillis()) {
            return String.format("%d秒前", span / DateUnit.SECOND.getMillis());
        } else if (span < DateUnit.HOUR.getMillis()) {
            return String.format("%d分钟前", span / DateUnit.MINUTE.getMillis());
        }
        //默认时区
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = beginOfDay(now());
        long today = localDateTime.atZone(zoneId).toEpochSecond() * 1000;

        if (timeMillis >= today) {
            return String.format("今天%tR", timeMillis);
        } else if (timeMillis >= today - DateUnit.DAY.getMillis()) {
            return String.format("昨天%tR", timeMillis);
        } else {
            // 'F' ISO 8601 格式的完整日期，被格式化为 "%tY-%tm-%td"。
            return String.format("%tF", timeMillis);
        }
    }

}
