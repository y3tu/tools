package com.y3tu.tools.lowcode.report.repository;

import com.y3tu.tools.lowcode.base.BaseRepository;
import com.y3tu.tools.lowcode.report.entity.domain.Report;

/**
 * @author y3tu
 */
public interface ReportRepository extends BaseRepository<Report> {
    /**
     * 根据报表名称获取报表
     *
     * @param name
     * @return
     */
    Report getByName(String name);
}
