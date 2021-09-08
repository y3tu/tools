package com.y3tu.tools.web.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.y3tu.tools.kit.time.DateUtil;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Timestamp 数据类型转换
 *
 * @author y3tu
 */
public class TimestampConverter implements Converter<Timestamp> {

    @Override
    public Class supportJavaTypeKey() {
        return Timestamp.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Timestamp convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
        Date date = DateUtil.strToDate(cellData.getStringValue(), DateUtil.NORM_DATETIME_PATTERN);
        return new Timestamp(date.getTime());
    }

    @Override
    public CellData convertToExcelData(Timestamp value, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new CellData<>(sdf.format(value));
    }
}
