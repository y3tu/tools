package com.y3tu.tools.lowcode.report.repository;


import com.y3tu.tools.lowcode.base.BaseRepository;
import com.y3tu.tools.lowcode.report.entity.domain.ReportAttachment;

import java.util.List;

/**
 * @author y3tu
 */
public interface ReportAttachmentRepository extends BaseRepository<ReportAttachment> {

    /**
     * 根据报表id获取附件记录
     *
     * @param reportId
     * @return
     */
    List<ReportAttachment> getByReportId(int reportId);

    /**
     * 根据报表id删除附件记录
     *
     * @param reportId
     */
    void deleteByReportId(int reportId);
}
