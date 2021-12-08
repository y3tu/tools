package com.y3tu.tools.lowcode.report.repository;


import com.y3tu.tools.lowcode.base.BaseRepository;
import com.y3tu.tools.lowcode.report.entity.domain.ReportParam;

import java.util.List;

/**
 * @author y3tu
 */
public interface ReportParamRepository extends BaseRepository<ReportParam> {
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
