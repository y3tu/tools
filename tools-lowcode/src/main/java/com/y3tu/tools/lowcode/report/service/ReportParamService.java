package com.y3tu.tools.lowcode.report.service;

import com.y3tu.tools.lowcode.base.BaseService;
import com.y3tu.tools.lowcode.report.entity.domain.ReportParam;

import java.util.List;

/**
 * @author y3tu
 */
public interface ReportParamService extends BaseService<ReportParam> {
    /**
     * 根据reportId获取报表参数
     *
     * @param reportId
     * @return
     */
    List<ReportParam> getByReportId(int reportId);

    /**
     * 根据reportId删除参数
     *
     * @param reportId
     */
    void deleteByReportId(int reportId);
}
