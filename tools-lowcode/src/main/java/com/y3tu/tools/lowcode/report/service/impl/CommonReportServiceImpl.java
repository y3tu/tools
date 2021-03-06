package com.y3tu.tools.lowcode.report.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.y3tu.tools.kit.lang.R;
import com.y3tu.tools.kit.text.StrUtil;
import com.y3tu.tools.lowcode.base.PageInfo;
import com.y3tu.tools.lowcode.common.entity.domain.DataSource;
import com.y3tu.tools.lowcode.common.service.DataSourceService;
import com.y3tu.tools.lowcode.common.util.DataSourceUtil;
import com.y3tu.tools.lowcode.exception.LowCodeException;
import com.y3tu.tools.lowcode.report.entity.dto.ReportDto;
import com.y3tu.tools.lowcode.report.service.CommonReportService;
import com.y3tu.tools.web.excel.ExcelPageData;
import com.y3tu.tools.web.excel.ExcelUtil;
import com.y3tu.tools.web.db.DataSourceType;
import com.y3tu.tools.web.db.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author y3tu
 */
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@Service
public class CommonReportServiceImpl implements CommonReportService {

    @Autowired
    DataSourceService dataSourceService;

    @Override
    public R parseSqlForHeader(String sql, int dsId) {
        DataSource dataSource = dataSourceService.getById(dsId);
        //获取数据源
        javax.sql.DataSource ds = DataSourceUtil.getDataSource(dataSource);
        try {
            PreparedStatement ps = null;
            ResultSet rs = null;
            Connection connection = ds.getConnection();

            //如果sql包含where条件，需要截取
            int index = StrUtil.indexOf(sql, "where", 0, false);
            if (index > 0) {
                sql = sql.substring(0, index);
            }

            ps = connection.prepareStatement(sql);
            ResultSetMetaData metaData = ps.getMetaData();
            int count = metaData.getColumnCount();
            List<Map<String, String>> fieldNameList = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                String fieldName = metaData.getColumnName(i);
                Map<String, String> fieldNameMap = new HashMap<>();
                fieldNameMap.put("field", fieldName);
                fieldNameMap.put("label", fieldName);
                //默认宽度100
                fieldNameMap.put("width", "100");
                fieldNameList.add(fieldNameMap);
            }
            return R.success(fieldNameList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new LowCodeException("解析SQL异常:" + e.getMessage());
        }
    }

    @Override
    public PageInfo reportHtml(ReportDto reportDto) {
        int dsId = reportDto.getDsId();
        String sql = reportDto.getQuerySql();
        PageInfo pageInfo = reportDto.getPageInfo();
        DataSource dataSource = dataSourceService.getById(dsId);
        javax.sql.DataSource ds = DataSourceUtil.getDataSourceByDsId(dsId);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        //首选查询数据总数
        int count = SqlUtil.count(sql, jdbcTemplate);
        pageInfo.setTotal(count);
        if (count > 0) {
            //如果有数据再分页查询
            if (dataSource.getDbType().equals(DataSource.TYPE_MYSQL)) {
                //mysql
                sql = SqlUtil.buildPageSql(DataSourceType.MYSQL, sql, pageInfo.getCurrent() - 1, pageInfo.getSize());
            } else if (dataSource.getDbType().equals(DataSource.TYPE_ORACLE)) {
                //oracle
                sql = SqlUtil.buildPageSql(DataSourceType.ORACLE, sql, pageInfo.getCurrent() - 1, pageInfo.getSize());
            }

            List<Map<String, Object>> list = SqlUtil.queryList(sql, null, jdbcTemplate);

            pageInfo.setRecords(list);
        } else {
            pageInfo.setRecords(null);
        }

        return pageInfo;
    }

    @Override
    public void exportExcel(ReportDto reportDto, HttpServletResponse response) {
        Map<String, Object> map = buildExcel(reportDto);
        List<List<String>> headerList = (List<List<String>>) map.get("headerList");
        ExcelPageData excelPageData = (ExcelPageData) map.get("excelPageData");
        ExcelUtil.downExcelByThreadAndPage(20, reportDto.getName(), reportDto.getName(), null, headerList, ExcelTypeEnum.XLSX, excelPageData, response);
    }


    @Override
    public void exportExcel(ReportDto reportDto, OutputStream outputStream) {
        Map<String, Object> map = buildExcel(reportDto);
        List<List<String>> headerList = (List<List<String>>) map.get("headerList");
        ExcelPageData excelPageData = (ExcelPageData) map.get("excelPageData");
        ExcelUtil.downExcelByThreadAndPage(20, reportDto.getName(), reportDto.getName(), null, headerList, ExcelTypeEnum.XLSX, excelPageData, outputStream);
    }

    private Map<String, Object> buildExcel(ReportDto reportDto) {
        //组装excel表头数据
        String tableHeader = reportDto.getTableHeader();
        String sql = reportDto.getQuerySql();
        int dsId = reportDto.getDsId();
        //Map<String, Object> headerMap = getHeader(JSONObject.parseArray(tableHeader));
        Map<String, Object> headerMap = null;
        List<List<String>> headerList = (List<List<String>>) headerMap.get("headerList");
        List<String> fieldList = (List<String>) headerMap.get("fieldList");

        DataSource dataSource = dataSourceService.getById(dsId);
        javax.sql.DataSource ds = DataSourceUtil.getDataSourceByDsId(dsId);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        ExcelPageData excelPageData = (ExcelPageData<List<Object>>) (startNum, pageSize) -> {
            StringBuilder selectSql = new StringBuilder();
            if (dataSource.getDbType().equals(DataSource.TYPE_MYSQL)) {
                //mysql
                selectSql.append(sql + " limit ?,? ");
            } else if (dataSource.getDbType().equals(DataSource.TYPE_ORACLE)) {
                //oracle
                selectSql.append("SELECT * FROM ( SELECT row_.*, rownum rownum_ from (").append(sql)
                        .append(" ) row_ where rownum <=").append(startNum + pageSize).append(") table_alias where table_alias.rownum_ >")
                        .append(startNum);
            }

            Map<String, Object> paramMap = new LinkedHashMap(2);
            paramMap.put("startNum", startNum);
            paramMap.put("pageSize", pageSize);
            List<Map<String, Object>> dataList = SqlUtil.queryList(selectSql.toString(), paramMap, jdbcTemplate);

            List<List<Object>> resultList = new ArrayList<>();
            for (Map<String, Object> data : dataList) {
                List<Object> result = new ArrayList<>();
                for (String field : fieldList) {
                    result.add(data.get(field));
                }
                resultList.add(result);
            }
            return resultList;
        };

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("headerList", headerList);
        resultMap.put("excelPageData", excelPageData);
        return resultMap;
    }

    /**
     * 装置excel表头
     */
//    private Map<String, Object> getHeader(JSONArray jsonArray) {
//        Map<String, Object> result = new HashMap<>();
//        List<List<String>> headerList = new ArrayList();
//        List<String> fieldList = new ArrayList<>();
//        int size = jsonArray.size();
//        for (int i = 0; i < size; i++) {
//            List<String> heads = new ArrayList<>();
//            JSONObject object = jsonArray.getJSONObject(i);
//            handleHeaderChildren(headerList, heads, fieldList, object);
//        }
//        result.put("headerList", headerList);
//        result.put("fieldList", fieldList);
//        return result;
//    }

//    private void handleHeaderChildren(List<List<String>> headerList, List<String> list, List<String> fieldList, JSONObject object) {
//        JSONArray jsonArray = object.getJSONArray("children");
//        if (jsonArray != null && !jsonArray.isEmpty()) {
//            int size = jsonArray.size();
//            list.add(object.getString("label"));
//            for (int i = 0; i < size; i++) {
//                handleHeaderChildren(headerList, list, fieldList, jsonArray.getJSONObject(i));
//            }
//        } else {
//            fieldList.add(object.getString("field"));
//            List<String> tempList = new ArrayList<>();
//            tempList.addAll(list);
//            tempList.add(object.getString("label"));
//            headerList.add(tempList);
//        }
//    }
}
