package com.y3tu.tools.lowcode.report.service;


import com.y3tu.tools.kit.lang.R;
import com.y3tu.tools.lowcode.base.PageInfo;
import com.y3tu.tools.lowcode.report.entity.dto.ReportDto;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 通用报表服务
 *
 * @author y3tu
 */
public interface CommonReportService {
    /**
     * 解析sql
     *
     * @param sql  sql
     * @param dsId 数据源
     * @return
     */
    R parseSqlForHeader(String sql, int dsId);

    /**
     * 查询报表数据
     *
     * @param reportDto
     * @return
     */
    PageInfo reportHtml(ReportDto reportDto);

    /**
     * 导出报表数据到浏览器
     *
     * @param reportDto
     * @return
     */
    void exportExcel(ReportDto reportDto, HttpServletResponse response);

    /**
     * 导出报表数据到流
     *
     * @param reportDto
     * @param outputStream
     */
    void exportExcel(ReportDto reportDto, OutputStream outputStream);
}
