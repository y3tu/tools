package com.y3tu.tools.kit.time;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class DateUtilTest {

    @Test
    public void strToLdt() {
        String dateStr = "2021-01-01 12:12:12";
        LocalDateTime localDateTime = DateUtil.strToLdt(dateStr, DateUtil.NORM_DATETIME_PATTERN);
        Assert.assertEquals("2021年01月01日", localDateTime.format(DateUtil.CHINESE_DATE_PATTERN_FORMATTER));
    }

    @Test
    public void strToDate() {
        String dateStr = "2021-01-01 12:12:12";
        Date date = DateUtil.strToDate(dateStr, DatePattern.NORM_DATETIME_PATTERN);
        Assert.assertEquals("2021-01", DateUtil.dateToStr(date, DatePattern.NORM_MONTH_PATTERN));

        String result = DateUtil.strToStr(dateStr, DatePattern.NORM_DATETIME_PATTERN, DatePattern.CHINESE_DATE_PATTERN);
        Assert.assertEquals("2021年01月01日", result);
    }

    @Test
    public void between() {
        String startTimeStr = "2021-01-01 12:12:12";
        String endTimeStr = "2021-07-27 15:14:13";
        Date startDate = DateUtil.strToDate(startTimeStr, DateUtil.NORM_DATETIME_PATTERN);
        Date endDate = DateUtil.strToDate(endTimeStr, DateUtil.NORM_DATETIME_PATTERN);
        long month = DateUtil.between(DateUtil.dateToLdt(startDate), DateUtil.dateToLdt(endDate), DateUnit.MONTH);
        Assert.assertEquals(6, month);
    }

    @Test
    public void offset() {
        String dateStr = "2021-01-01 12:12:12";
        Date date = DateUtil.strToDate(dateStr, DateUtil.NORM_DATETIME_PATTERN);
        LocalDateTime localDateTime = DateUtil.offset(DateUtil.dateToLdt(date), -1, DateUnit.MINUTE);
        date = DateUtil.ldtToDate(localDateTime);
        dateStr = DateUtil.dateToStr(date, DateUtil.NORM_DATETIME_PATTERN);
        Assert.assertEquals("2021-01-01 12:11:12", dateStr);
    }
}